package backend.datn.services;

import backend.datn.dto.request.OrderDetailCreateRequest;
import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.*;
import backend.datn.mapper.OrderMapper;
import backend.datn.repositories.OrderRepository;
import backend.datn.repositories.OrderDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;

@Service
public class SalePOSService {

    private static final Logger logger = LoggerFactory.getLogger(SalePOSService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private VoucherService voucherService;

    /**
     * Tạo mới đơn hàng rỗng cho POS
     * Được sử dụng khi bắt đầu một giao dịch bán hàng tại quầy.
     */
    @Transactional
    public Order createEmptyOrder(Customer customer, Employee employee, Voucher voucher, Integer paymentMethod) {
        logger.info("Bắt đầu tạo đơn hàng trống cho POS. Customer ID: {}, Employee ID: {}, Voucher ID: {}, Payment Method: {}",
                customer != null ? customer.getId() : null, employee != null ? employee.getId() : null,
                voucher != null ? voucher.getId() : null, paymentMethod);

        if (customer == null || customerService.findById(customer.getId()).isEmpty()) {
            logger.error("Khách hàng không tồn tại.");
            throw new IllegalArgumentException("Khách hàng không tồn tại.");
        }

        if (employee == null || employeeService.findById(employee.getId()).isEmpty()) {
            logger.error("Nhân viên không tồn tại.");
            throw new IllegalArgumentException("Nhân viên không tồn tại.");
        }

        if (voucher != null && voucherService.findById(voucher.getId()).isEmpty()) {
            logger.error("Voucher không tồn tại.");
            throw new IllegalArgumentException("Voucher không tồn tại.");
        }

        if (paymentMethod == null || paymentMethod < 0) {
            logger.error("Phương thức thanh toán không hợp lệ.");
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setEmployee(employee);
        order.setVoucher(voucher);
        order.setPaymentMethod(paymentMethod);
        order.setStatusOrder(1);
        order.setKindOfOrder(true);
        order.setOrderDetails(new ArrayList<>());
        order.setTotalAmount(0);
        order.setTotalBill(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);
        logger.info("Đơn hàng trống đã được tạo thành công. Order ID: {}", savedOrder.getId());
        return savedOrder;
    }

    /**
     * Thêm sản phẩm vào giỏ hàng của đơn hàng POS
     * Kiểm tra tồn kho trước khi thêm, cập nhật tổng tiền đơn hàng
     */
    public OrderResponse addProductToCart(Integer orderId, OrderDetailCreateRequest detailReq) {
        logger.info("Bắt đầu thêm sản phẩm vào giỏ hàng. Order ID: {}, Product Detail ID: {}, Quantity: {}",
                orderId, detailReq.getProductDetailId(), detailReq.getQuantity());

        // Tìm đơn hàng theo ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Tìm sản phẩm theo ID
        ProductDetail productDetail = productDetailService.findById(detailReq.getProductDetailId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + detailReq.getProductDetailId()));

        // Kiểm tra tồn kho trước khi thêm vào giỏ hàng (tránh cập nhật sai do giao dịch đồng thời)
        synchronized (productDetail) {
            if (productDetail.getQuantity() < detailReq.getQuantity()) {
                logger.error("Sản phẩm {} không đủ hàng.", productDetail.getProduct().getProductName());
                throw new IllegalArgumentException("Sản phẩm " + productDetail.getProduct().getProductName() + " không đủ hàng!");
            }

            OrderDetail existingOrderDetail = order.getOrderDetails().stream()
                    .filter(od -> od.getProductDetail().getId().equals(detailReq.getProductDetailId()))
                    .findFirst()
                    .orElse(null);

            if (existingOrderDetail != null) {
                existingOrderDetail.setQuantity(existingOrderDetail.getQuantity() + detailReq.getQuantity());
                orderDetailRepository.save(existingOrderDetail);
                logger.info("Cập nhật số lượng sản phẩm trong giỏ hàng thành công. Order Detail ID: {}", existingOrderDetail.getId());
            } else {
                OrderDetail newOrderDetail = new OrderDetail();
                newOrderDetail.setOrder(order);
                newOrderDetail.setProductDetail(productDetail);
                newOrderDetail.setQuantity(detailReq.getQuantity());
                order.getOrderDetails().add(newOrderDetail);
                orderDetailRepository.save(newOrderDetail);
                logger.info("Thêm mới sản phẩm vào giỏ hàng thành công. Order Detail ID: {}", newOrderDetail.getId());
            }

            // Cập nhật tổng tiền (totalBill) và tổng số lượng (totalAmount) của đơn hàng sau khi thêm sản phẩm vào giỏ hàng.
            updateOrderTotal(order);

            // Lưu đơn hàng sau khi cập nhật giỏ hàng
            OrderResponse orderResponse = OrderMapper.toOrderResponse(orderRepository.save(order));
            logger.info("Cập nhật tổng tiền và tổng số lượng thành công. Order ID: {}", order.getId());
            return orderResponse;
        }
    }

    private void updateOrderTotal(Order order) {
        BigDecimal totalBill = BigDecimal.ZERO;
        int totalAmount = 0;
        for (OrderDetail orderDetail : order.getOrderDetails()) {

            if (orderDetail == null || orderDetail.getProductDetail() == null) {
                logger.error("Order Detail hoặc Product Detail bị null. Order ID: {}", order.getId());
                continue;
            }

            BigDecimal price = orderDetail.getProductDetail().getSalePrice(); // Sử dụng sale_price từ product_detail
            ProductDetail productDetail = orderDetail.getProductDetail();

            // Kiểm tra và áp dụng khuyến mãi (Promotion) nếu có
            if (productDetail.getPromotion() != null && productDetail.getPromotion().getStatus()) {
                Promotion promotion = productDetail.getPromotion();
                if (promotion.getStartDate().isBefore(Instant.now()) && promotion.getEndDate().isAfter(Instant.now())) {
                    // Khuyến mãi đang hoạt động
                    BigDecimal discountPercentage = BigDecimal.valueOf(promotion.getPromotionPercent()).divide(new BigDecimal(100));
                    BigDecimal discountAmount = price.multiply(discountPercentage);
                    price = price.subtract(discountAmount); // Tính giá đã giảm
                }
            }

            totalBill = totalBill.add(price.multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
            totalAmount += orderDetail.getQuantity();
        }

        // Kiểm tra và áp dụng Voucher nếu có
        if (order.getVoucher() != null && order.getVoucher().getStatus()) {
            totalBill = voucherService.applyVoucher(order.getVoucher(), totalBill);
        }

        order.setTotalBill(totalBill);
        order.setTotalAmount(totalAmount);
    }

    /**
     * Cập nhật trạng thái đơn hàng sau khi thanh toán thành công.
     * Kiểm tra tồn kho trước khi trừ số lượng sản phẩm
     */
    @Transactional
    public OrderResponse updateOrderStatusAfterPayment(Integer orderId) {
        logger.info("Bắt đầu cập nhật trạng thái đơn hàng sau thanh toán. Order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra nếu đơn hàng đã hoàn thành
        if (order.getStatusOrder() == 5) {
            logger.error("Đơn hàng đã được thanh toán trước đó. Order ID: {}", order.getId());
            throw new IllegalStateException("Đơn hàng đã được thanh toán trước đó!");
        }

        // Kiểm tra tồn kho trước khi trừ
        for (OrderDetail orderDetail : order.getOrderDetails()) {

            if (orderDetail == null || orderDetail.getProductDetail() == null || orderDetail.getProductDetail().getProduct() == null) {
                logger.error("Order Detail, Product Detail hoặc Product bị null. Order ID: {}", order.getId());
                continue;
            }

            ProductDetail productDetail = orderDetail.getProductDetail();
            int quantity = productDetail.getQuantity();
            int orderedQuantity = orderDetail.getQuantity();

            if (quantity < orderedQuantity) {
                throw new IllegalArgumentException("Sản phẩm " + productDetail.getProduct().getProductName() + " không đủ hàng trong kho!");
            }
        }

        // Trừ tồn kho
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            ProductDetail productDetail = orderDetail.getProductDetail();
            productDetail.setQuantity(productDetail.getQuantity() - orderDetail.getQuantity());
            productDetailService.update(productDetail);
        }

        // Cập nhật trạng thái đơn hàng thành "Hoàn thành"
        order.setStatusOrder(5);
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }

}
