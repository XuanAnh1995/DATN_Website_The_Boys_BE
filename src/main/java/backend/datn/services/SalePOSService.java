package backend.datn.services;

import backend.datn.dto.request.OrderDetailCreateRequest;
import backend.datn.dto.request.OrderPOSCreateRequest;
import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.*;
import backend.datn.mapper.OrderMapper;
import backend.datn.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

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
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private VoucherService voucherService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private VoucherRepository voucherRepository;

    /**
     * Tạo mới đơn hàng rỗng cho POS
     * Được sử dụng khi bắt đầu một giao dịch bán hàng tại quầy.
     */
    @Transactional
    public Order createEmptyOrder(Customer customer, Employee employee, Voucher voucher, Integer paymentMethod) {
        logger.info("Bắt đầu tạo đơn hàng. Customer ID: {}, Employee ID: {}, Voucher ID: {}, Payment Method: {}",
                (customer != null) ? customer.getId() : "Khách vãng lai",
                employee.getId(),
                (voucher != null) ? voucher.getId() : "Không có voucher",
                paymentMethod);

        if (employee == null || employeeService.findById(employee.getId()).isEmpty()) {
            throw new IllegalArgumentException("Nhân viên không tồn tại.");
        }

        if (voucher != null && voucherService.findById(voucher.getId()).isEmpty()) {
            throw new IllegalArgumentException("Voucher không tồn tại.");
        }

        if (paymentMethod == null || paymentMethod < 0) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
        }

        Order order = new Order();
        order.setCustomer(customer); // Có thể null (khách vãng lai)
        order.setEmployee(employee);
        order.setVoucher(voucher);
        order.setPaymentMethod(paymentMethod);
        order.setStatusOrder(1);
        order.setKindOfOrder(true);
        order.setOrderDetails(new ArrayList<>());
        order.setTotalAmount(0);
        order.setTotalBill(BigDecimal.ZERO);
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCreateDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        logger.info("Đã tạo đơn hàng. Order ID: {}, Order Code: {}", savedOrder.getId(), savedOrder.getOrderCode());
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
    @Transactional
    public Order thanhToan(OrderPOSCreateRequest request) {
        if (request.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID không được để trống.");
        }

        // Tìm đơn hàng đã tồn tại
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn."));

        // Cập nhật trạng thái và tổng tiền
        order.setTotalAmount(request.getTotalAmount());
        order.setStatusOrder(request.getStatusOrder());

        // Xử lý danh sách sản phẩm
        BigDecimal totalBill = BigDecimal.ZERO;
        for (OrderDetailCreateRequest detailRequest : request.getOrderDetails()) {
            ProductDetail productDetail = productDetailRepository.findById(detailRequest.getProductDetailId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));

            if (productDetail.getQuantity() < detailRequest.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + productDetail.getProductDetailCode() + " không đủ số lượng tồn kho.");
            }

            // Cập nhật số lượng tồn kho của sản phẩm
            productDetail.setQuantity(productDetail.getQuantity() - detailRequest.getQuantity());
            productDetailRepository.save(productDetail);  // Lưu vào DB

            // Kiểm tra nếu chi tiết đơn hàng đã tồn tại, cập nhật số lượng thay vì tạo mới
            OrderDetail orderDetail = orderDetailRepository.findByOrderAndProductDetail(order, productDetail)
                    .orElse(new OrderDetail());

            orderDetail.setOrder(order);
            orderDetail.setProductDetail(productDetail);

            // Xử lý trường hợp quantity bị null
            int currentQuantity = (orderDetail.getQuantity() == null) ? 0 : orderDetail.getQuantity();
            orderDetail.setQuantity(currentQuantity + detailRequest.getQuantity());

            orderDetailRepository.save(orderDetail);  // Lưu vào DB

            // Tính tổng tiền
            totalBill = totalBill.add(getDiscountedPrice(productDetail).multiply(BigDecimal.valueOf(detailRequest.getQuantity())));
        }

        // Cập nhật tổng tiền đơn hàng
        order.setTotalBill(totalBill);

        // Lưu đơn hàng vào DB và trả về
        return orderRepository.save(order);  // Lưu vào DB
    }


    private BigDecimal getDiscountedPrice(ProductDetail productDetail) {
        BigDecimal salePrice = productDetail.getSalePrice();

        if (productDetail.getPromotion() != null) {
            BigDecimal discountPercent = BigDecimal.valueOf(100 - productDetail.getPromotion().getPromotionPercent())
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            return salePrice.multiply(discountPercent);
        }

        return salePrice; // Nếu không có khuyến mãi, trả về giá gốc
    }
}
