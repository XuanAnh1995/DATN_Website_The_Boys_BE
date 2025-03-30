package backend.datn.services;

import backend.datn.dto.request.OrderOnlineDetailRequest;
import backend.datn.dto.request.OrderOnlineRequest;
import backend.datn.dto.response.OrderOnlineResponse;
import backend.datn.entities.*;
import backend.datn.exceptions.BadRequestException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.helpers.CodeGeneratorHelper;
import backend.datn.helpers.RandomHelper;
import backend.datn.mapper.OrderOnlineMapper;
import backend.datn.repositories.*;
import backend.datn.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class OrderOnlineService {

    @Autowired
    private OrderOnlineRepository orderRepository;

    @Autowired
    private OrderOnlineDetailRepository orderDetailRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Tạo đơn hàng online
     */
    @Transactional
    public OrderOnlineResponse createOrder(OrderOnlineRequest orderOnlineRequest) {
        OrderOnline order = new OrderOnline();
        order.setOrderCode(CodeGeneratorHelper.generateCode("INV"));
        order.setKindOfOrder(false);
        order.setCustomer(getCurrentCustomer());
        order.setEmployee(null);
        order.setPhone(orderOnlineRequest.getPhone());
        order.setAddress(orderOnlineRequest.getAddress());
        order.setPaymentMethod(orderOnlineRequest.getPaymentMethod());
        order.setShipfee(orderOnlineRequest.getShipfee() != null ? orderOnlineRequest.getShipfee() : BigDecimal.ZERO);
        order.setStatusOrder(orderOnlineRequest.getPaymentMethod() == 0 ? 0 : 1);
        order.setCreateDate(LocalDateTime.now());

        // *** Lưu order trước để có ID ***
        order = orderRepository.save(order);

        // Xử lý danh sách chi tiết đơn hàng sau khi order đã được lưu
        List<OrderOnlineDetail> orderDetails = processOrderOnlineDetails(orderOnlineRequest.getOrderOnlineDetails(), order);

        // Tính tổng tiền hàng
        BigDecimal totalAmount = orderDetails.stream()
                .map(od -> od.getPrice().multiply(BigDecimal.valueOf(od.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // Xử lý voucher nếu có
        Voucher voucher = null;
        if (orderOnlineRequest.getVoucherId() != null) {
            int voucherId = Integer.parseInt(orderOnlineRequest.getVoucherId());
            if (checkVoucher(voucherId, totalAmount)) {
                voucher = voucherRepository.findById(voucherId).orElse(null);
            }
        }
        order.setVoucher(voucher);

        // Tính tổng tiền sau giảm giá
        BigDecimal totalBill = calculateTotal(totalAmount, voucher, order.getShipfee());
        order.setTotalBill(totalBill);

        // *** Cập nhật order sau khi đã có đầy đủ thông tin ***
        order = orderRepository.save(order);

        return OrderOnlineMapper.toOrderOnlineResponse(order);
    }


    /**
     * Xử lý danh sách chi tiết đơn hàng
     */
    @Transactional
    protected List<OrderOnlineDetail> processOrderOnlineDetails(List<OrderOnlineDetailRequest> orderOnlineDetails, OrderOnline order) {
        List<OrderOnlineDetail> orderDetails = orderOnlineDetails.stream().map(detailRequest -> {
            ProductDetail productDetail = productDetailRepository.findById(detailRequest.getProductDetailId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));

            if (!checkQuantity(detailRequest.getProductDetailId(), detailRequest.getQuantity())) {
                throw new BadRequestException("Số lượng sản phẩm không đủ");
            }

            // Tính giá khuyến mãi (nếu có)
            BigDecimal salePrice = productDetail.getSalePrice();
            if (productDetail.getPromotion() != null && productDetail.getPromotion().getStatus()) {
                salePrice = applyPromotionDiscount(salePrice, productDetail.getPromotion().getPromotionPercent());
            }

            // Tạo chi tiết đơn hàng
            OrderOnlineDetail detail = new OrderOnlineDetail();
            detail.setProductDetail(productDetail);
            detail.setQuantity(detailRequest.getQuantity());
            detail.setPrice(salePrice);
            detail.setOrder(order);

            return detail;
        }).collect(Collectors.toList());

        // Lưu tất cả chi tiết đơn hàng
        orderDetailRepository.saveAll(orderDetails);

        // Cập nhật số lượng tồn kho
        orderDetails.forEach(detail -> {
            ProductDetail productDetail = detail.getProductDetail();
            productDetail.setQuantity(productDetail.getQuantity() - detail.getQuantity());
            productDetailRepository.save(productDetail);
        });

        return orderDetails;
    }

    /**
     * Tính tổng tiền đơn hàng (bao gồm voucher và ship fee)
     */
    public BigDecimal calculateTotal(BigDecimal totalAmount, Voucher voucher, BigDecimal shipfee) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (voucher != null && checkVoucher(voucher.getId(), totalAmount)) {
            discountAmount = calculateVoucherDiscount(totalAmount, voucher);
        }

        return totalAmount.subtract(discountAmount).add(shipfee).max(BigDecimal.ZERO);
    }

    /**
     * Kiểm tra voucher có hợp lệ không
     */
    public boolean checkVoucher(Integer voucherId, BigDecimal totalAmount) {
        return voucherRepository.findById(voucherId)
                .map(voucher -> {
                    Instant now = Instant.now();
                    return voucher.getStatus() &&
                            totalAmount.compareTo(voucher.getMinCondition()) >= 0 &&
                            now.isAfter(voucher.getStartDate()) &&
                            now.isBefore(voucher.getEndDate());
                })
                .orElse(false);
    }

    /**
     * Tính số tiền giảm giá từ voucher
     */
    public BigDecimal calculateVoucherDiscount(BigDecimal totalAmount, Voucher voucher) {
        if (voucher == null || !voucher.getStatus()) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = totalAmount.multiply(BigDecimal.valueOf(voucher.getReducedPercent()))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        return discount.min(voucher.getMaxDiscount());
    }

    /**
     * Tính giá sản phẩm sau khi áp dụng khuyến mãi (nếu có)
     */
    public BigDecimal applyPromotionDiscount(BigDecimal price, int discountPercent) {
        return price.multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    /**
     * Kiểm tra số lượng sản phẩm còn trong kho
     */
    public boolean checkQuantity(Integer productId, Integer quantity) {
        return productDetailRepository.findById(productId)
                .map(product -> product.getQuantity() >= quantity)
                .orElse(false);
    }

    /**
     * Cập nhật trạng thái đơn hàng sau khi thanh toán thành công
     */
    @Transactional
    public void updateOrderStatusAfterPayment(String orderCode) {
        OrderOnline order = orderRepository.findByOrderCode(orderCode);
        if (order == null) {
            throw new EntityNotFoundException("Không tìm thấy đơn hàng");
        }
        if (order.getStatusOrder() == 1) {
            order.setStatusOrder(2); // Đã xác nhận
            orderRepository.save(order);
        }
    }

    /**
     * Lấy thông tin khách hàng hiện tại từ phiên đăng nhập
     */
    private Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new BadRequestException("Người dùng chưa đăng nhập");
        }

        Customer customer = customerRepository.findByUsername(userDetails.getUsername());
        if (customer == null) {
            throw new EntityNotFoundException("Không tìm thấy khách hàng");
        }
        return customer;
    }
}
