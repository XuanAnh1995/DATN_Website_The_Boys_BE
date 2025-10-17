package backend.datn.services;

import backend.datn.dto.request.OrderOnlineDetailRequest;
import backend.datn.dto.request.OrderOnlineRequest;
import backend.datn.dto.response.OrderDetailResponse;
import backend.datn.dto.response.OrderOnlineResponse;
import backend.datn.entities.*;
import backend.datn.exceptions.BadRequestException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.helpers.CodeGeneratorHelper;
import backend.datn.mapper.OrderDetailMapper;
import backend.datn.mapper.OrderOnlineMapper;
import backend.datn.repositories.*;
import backend.datn.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private OrderDetailRepository repository;

    @Autowired
    CartRepository cartRepository;

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
        order.setStatusOrder(0); // Chờ xác nhận
        order.setCreateDate(LocalDateTime.now());

        // Lưu order trước để có ID
        order = orderRepository.save(order);

        // Xử lý danh sách chi tiết đơn hàng
        List<OrderOnlineDetail> orderDetails = processOrderOnlineDetails(orderOnlineRequest.getOrderOnlineDetails(), order);

        // Tính tổng tiền hàng
        BigDecimal totalAmount = orderDetails.stream()
                .map(od -> od.getPrice().multiply(BigDecimal.valueOf(od.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // Xử lý voucher nếu có
        Voucher voucher = null;
        if (orderOnlineRequest.getVoucherId() != null) {
            Long voucherId = Long.parseLong(orderOnlineRequest.getVoucherId());
            if (checkVoucher(voucherId, totalAmount)) {
                voucher = voucherRepository.findById(voucherId).orElse(null);
            }
        }
        order.setVoucher(voucher);

        // Tính tổng tiền sau giảm giá
        BigDecimal totalBill = calculateTotal(totalAmount, voucher, order.getShipfee());
        order.setTotalBill(totalBill);

        // Cập nhật order
        order = orderRepository.save(order);

        List<Long> productDetailIds = orderDetails.stream()
                .map(detail -> detail.getProductDetail().getId())
                .collect(Collectors.toList());
        cartRepository.deleteByCustomerAndProductDetailIds(order.getCustomer().getId(), productDetailIds);

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
            Promotion promotion = productDetail.getPromotion();

            if (promotion != null
                    && promotion.getStatus()
                    && promotion.getStartDate() != null
                    && promotion.getEndDate() != null
                    && LocalDateTime.now().isAfter(promotion.getStartDate())
                    && LocalDateTime.now().isBefore(promotion.getEndDate())) {

                salePrice = applyPromotionDiscount(salePrice, promotion.getPromotionPercent());
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

        // KHÔNG cập nhật số lượng tồn kho tại đây (sẽ xử lý khi chuyển sang trạng thái "Đã xác nhận")

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
    public boolean checkVoucher(Long voucherId, BigDecimal totalAmount) {
        return voucherRepository.findById(voucherId)
                .map(voucher -> {
                    LocalDateTime now = LocalDateTime.now();
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
     * Tính giá sản phẩm sau khi áp dụng khuyến mãi
     */
    public BigDecimal applyPromotionDiscount(BigDecimal price, int discountPercent) {
        return price.multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    }

    /**
     * Kiểm tra số lượng sản phẩm còn trong kho
     */
    public boolean checkQuantity(Long productId, Integer quantity) {
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
        if (order.getStatusOrder() == 0) { // Từ "Chờ xác nhận" sang "Đã xác nhận"
            updateOrderStatus(order.getId(), 2, null); // Gọi phương thức updateOrderStatus để xử lý trừ số lượng
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

    /**
     * Lấy danh sách đơn hàng online với tìm kiếm và phân trang
     */
    public Page<OrderOnlineResponse> getAllOnlineOrders(
            String search, int page, int size, String sortKey, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortKey).ascending() :
                Sort.by(sortKey).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        String formattedSearch = (search == null || search.isEmpty()) ? null : "%" + search.toLowerCase() + "%";

        Page<OrderOnline> onlineOrdersPage = orderRepository.findAllByKindOfOrderWithSearchAndJoin(
                false, formattedSearch, pageable);

        return onlineOrdersPage.map(OrderOnlineMapper::toOrderOnlineResponse);
    }

    /**
     * Tìm đơn hàng online theo ID
     */
    public OrderOnlineResponse findOrderOnlineByIdWithKindOfOrder(Long id) {
        OrderOnline order = orderRepository.findOrderOnlineByIdWithKindOfOrder(id, false)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng online với ID: " + id));

        return OrderOnlineMapper.toOrderOnlineResponse(order);
    }

    /**
     * Lấy chi tiết đơn hàng online kèm danh sách sản phẩm
     */
    @Transactional
    public OrderOnlineResponse getOrderOnlineDetails(Long orderId) {
        OrderOnline order = orderRepository.findOrderOnlineByIdWithKindOfOrder(orderId, false)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng online với ID: " + orderId));

        List<OrderDetail> orderDetails = repository.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailMapper::toOrderDetailResponse)
                .collect(Collectors.toList());

        OrderOnlineResponse response = OrderOnlineMapper.toOrderOnlineResponse(order);
        response.setOrderDetails(orderDetailResponses);

        return response;
    }

    /**
     * Cập nhật trạng thái đơn hàng online
     */
    @Transactional
    public OrderOnlineResponse updateOrderStatus(Long id, Integer newStatus, String note) {
        OrderOnline order = orderRepository.findOrderOnlineByIdWithKindOfOrder(id, false)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng online với ID: " + id));

        List<Integer> validStatuses = Arrays.asList(-1, 0, 2, 3, 4, 5);
        if (!validStatuses.contains(newStatus)) {
            throw new BadRequestException("Trạng thái không hợp lệ");
        }

        if (newStatus == -1 && (note == null || note.trim().isEmpty())) {
            throw new BadRequestException("Lý do hủy đơn hàng là bắt buộc");
        }

        // Trừ số lượng sản phẩm khi chuyển sang trạng thái "Đã xác nhận" (status 2)
        if (newStatus == 2 && order.getStatusOrder() != 2) {
            List<OrderOnlineDetail> orderDetails = orderDetailRepository.findByOrder(order);
            for (OrderOnlineDetail detail : orderDetails) {
                ProductDetail productDetail = detail.getProductDetail();
                if (productDetail.getQuantity() < detail.getQuantity()) {
                    throw new BadRequestException("Số lượng sản phẩm " + productDetail.getProduct().getProductName() + " không đủ");
                }
                productDetail.setQuantity(productDetail.getQuantity() - detail.getQuantity());
                productDetailRepository.save(productDetail);
                // Ghi log
                System.out.println("Trừ kho: Sản phẩm " + productDetail.getProduct().getProductName() + ", Số lượng: " + detail.getQuantity());
            }
        }

        // Nếu hủy đơn hàng (newStatus = -1), hoàn trả số lượng sản phẩm nếu đã trừ trước đó (từ trạng thái 2 trở đi)
        if (newStatus == -1 && order.getStatusOrder() != -1 && order.getStatusOrder() >= 2) {
            List<OrderOnlineDetail> orderDetails = orderDetailRepository.findByOrder(order);
            for (OrderOnlineDetail detail : orderDetails) {
                ProductDetail productDetail = detail.getProductDetail();
                int newQuantity = productDetail.getQuantity() + detail.getQuantity();
                if (newQuantity < 0) {
                    throw new BadRequestException("Số lượng hoàn trả không hợp lệ cho sản phẩm " + productDetail.getProduct().getProductName());
                }
                productDetail.setQuantity(newQuantity);
                productDetailRepository.save(productDetail);
                // Ghi log
                System.out.println("Hoàn trả kho: Sản phẩm " + productDetail.getProduct().getProductName() + ", Số lượng: " + detail.getQuantity());
            }
        }

        order.setStatusOrder(newStatus);
        order.setNote(note);
        order = orderRepository.save(order);

        return OrderOnlineMapper.toOrderOnlineResponse(order);
    }
}