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
    private static final String VIETQR_API_URL = "https://img.vietqr.io/image/";
    private static final String BANK_BIN = "970436"; // BIN của Vietcombank
    private static final String ACCOUNT_NUMBER = "1040023014"; // Số tài khoản của bạn

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

    public Order findOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng #" + orderId));
    }

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

        if (paymentMethod == null || paymentMethod < 0 || paymentMethod > 1) { // Kiểm tra giá trị hợp lệ
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ. Chỉ chấp nhận 0 (Tiền mặt) hoặc 1 (VNPay).");
        }

        // Nếu khách hàng là null, gán khách hàng vãng lai (ID = -1)
        if (customer == null) {
            customer = new Customer();
            customer.setId(-1);
            customer.setFullname("Khách vãng lai");
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
        order.setOriginalTotal(BigDecimal.ZERO);
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCreateDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        logger.info("Đã tạo đơn hàng. Order ID: {}, Order Code: {}", savedOrder.getId(), savedOrder.getOrderCode());
        return savedOrder;

    }

    public String createVietQRPaymentUrl(Integer orderId) {
        logger.info("Đang tạo URL VietQR cho đơn hàng ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Tính tổng tiền cần thanh toán (sau khi giảm giá)
        updateOrderTotal(order);
        BigDecimal amount = order.getTotalBill();
        String transactionId = "HD1-" + order.getOrderCode(); // Mã giao dịch duy nhất

        // Tạo URL VietQR
        String vietQrUrl = String.format(
                "%s%s-%s-qr_only.png?amount=%s&addInfo=%s",
                VIETQR_API_URL, BANK_BIN, ACCOUNT_NUMBER, amount.toString(), transactionId
        );

        logger.info("URL VietQR đã tạo: {}", vietQrUrl);
        return vietQrUrl;
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
        // Kiểm tra xem order có null không
        logger.info("📝 [DEBUG] Order nhận vào: {}", order);
        if (order == null) {
            logger.error("❌ [ERROR] Order bị null!");
            return;
        }

        // Kiểm tra xem danh sách orderDetails có null hoặc rỗng không
        logger.info("📝 [DEBUG] OrderDetails nhận vào: {}", order.getOrderDetails());
        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            logger.error("❌ [ERROR] OrderDetails null hoặc rỗng. Order ID: {}", order.getId());
            return;
        }

        BigDecimal originalTotal = BigDecimal.ZERO; // Tổng tiền chưa áp dụng giảm giá
        BigDecimal totalBill = BigDecimal.ZERO; // Tổng tiền sau khi áp khuyến mãi
        int totalAmount = 0;

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            // Kiểm tra orderDetail có bị null không
            if (orderDetail == null || orderDetail.getProductDetail() == null) {
                logger.error("❌ [ERROR] OrderDetail hoặc ProductDetail bị null. Order ID: {}", order.getId());
                continue;
            }

            // Log thông tin từng sản phẩm
            logger.info("🔎 [CHECK] OrderDetail: productId={}, quantity={}, price={}",
                    orderDetail.getProductDetail().getId(),
                    orderDetail.getQuantity(),
                    orderDetail.getProductDetail().getSalePrice());

            BigDecimal price = orderDetail.getProductDetail().getSalePrice(); // Giá gốc
            BigDecimal originalPrice = price; // Lưu giá gốc để debug
            ProductDetail productDetail = orderDetail.getProductDetail();

            // Tính tổng tiền chưa giảm giá
            originalTotal = originalTotal.add(originalPrice.multiply(BigDecimal.valueOf(orderDetail.getQuantity())));

            // Kiểm tra và áp dụng khuyến mãi (nếu có)
            if (productDetail.getPromotion() != null) {
                Promotion promotion = productDetail.getPromotion();
                if (!promotion.getStatus()) {
                    logger.warn("⚠️ [PROMOTION] Khuyến mãi {} bị vô hiệu hóa", promotion.getPromotionName());
                } else if (promotion.getStartDate().isAfter(LocalDateTime.now()) || promotion.getEndDate().isBefore(LocalDateTime.now())) {
                    logger.warn("⚠️ [PROMOTION] Khuyến mãi {} chưa đến hạn hoặc đã hết hạn", promotion.getPromotionName());
                } else {
                    BigDecimal discountPercentage = BigDecimal.valueOf(promotion.getPromotionPercent()).divide(BigDecimal.valueOf(100));
                    BigDecimal discountAmount = price.multiply(discountPercentage);
                    price = price.subtract(discountAmount);

                    logger.info("✅ [DISCOUNT] Giá gốc: {}, Giá giảm: {}, Giá sau giảm: {}",
                            originalPrice, discountAmount, price);
                }
            }

            totalBill = totalBill.add(price.multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
            totalAmount += orderDetail.getQuantity();
        }

        // Kiểm tra và áp dụng Voucher nếu có
        if (order.getVoucher() != null) {
            Voucher voucher = order.getVoucher();
            if (!voucher.getStatus()) {
                logger.warn("⚠️ [VOUCHER] Voucher {} bị vô hiệu hóa", voucher.getVoucherCode());
            } else if (totalBill.compareTo(voucher.getMinCondition()) < 0) {
                logger.warn("⚠️ [VOUCHER] Đơn hàng không đủ điều kiện áp dụng voucher (yêu cầu: {}, hiện tại: {})",
                        voucher.getMinCondition(), totalBill);
            } else {
                BigDecimal beforeVoucher = totalBill;
                totalBill = voucherService.applyVoucher(voucher, totalBill);
                BigDecimal voucherDiscount = beforeVoucher.subtract(totalBill);

                logger.info("✅ [VOUCHER] Giá trước: {}, Voucher giảm: {}, Giá sau giảm: {}",
                        beforeVoucher, voucherDiscount, totalBill);
            }
        }

        // Gán lại giá trị cho order
        order.setOriginalTotal(originalTotal); // Thêm originalTotal vào order
        order.setTotalBill(totalBill);
        order.setTotalAmount(totalAmount);

        logger.info("✅ [UPDATE ORDER] Order ID: {}, Trước giảm giá (originalTotal): {}, Sau khuyến mãi: {}, Sau voucher: {}, Tổng số lượng: {}",
                order.getId(), originalTotal, totalBill, totalBill, totalAmount);
    }

    /**
     * Cập nhật trạng thái đơn hàng sau khi thanh toán thành công.
     * Kiểm tra tồn kho trước khi trừ số lượng sản phẩm
     */
    @Transactional
    public OrderResponse updateOrderStatusAfterPayment(Integer orderId, Integer customerId, Integer voucherId) {
        logger.info("Bắt đầu cập nhật trạng thái đơn hàng sau thanh toán. Order ID: {}, Customer ID: {}, Voucher ID: {}",
                orderId, customerId, voucherId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra nếu đơn hàng đã hoàn thành
        if (order.getStatusOrder() == 5) {
            throw new IllegalStateException("Đơn hàng đã được thanh toán trước đó!");
        }

        // Cập nhật customer và voucher nếu có giá trị mới
        if (customerId != null && customerId != order.getCustomer().getId()) {
            Customer customer = customerService.findById(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));
            order.setCustomer(customer);
            logger.info("Cập nhật customer_id thành: {}", customerId);
        }
        if (voucherId != null && (order.getVoucher() == null || voucherId != order.getVoucher().getId())) {
            Voucher voucher = voucherService.findById(voucherId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy voucher với ID: " + voucherId));
            order.setVoucher(voucher);
            logger.info("Cập nhật voucher_id thành: {}", voucherId);
        }

        // Kiểm tra tồn kho trước khi trừ
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            ProductDetail productDetail = orderDetail.getProductDetail();
            int quantity = productDetail.getQuantity();
            int orderedQuantity = orderDetail.getQuantity();

            if (quantity < orderedQuantity) {
                throw new IllegalArgumentException("Sản phẩm " + productDetail.getProduct().getProductName() + " không đủ hàng trong kho!");
            }

            productDetail.setQuantity(quantity - orderedQuantity);
            productDetailService.update(productDetail);

            logger.info("Cập nhật tồn kho sản phẩm: {} | Trước: {} | Sau: {} | Đã bán: {}",
                    productDetail.getProduct().getProductName(), quantity, productDetail.getQuantity(), orderDetail.getQuantity());

        }

        // 🔥 Quan trọng: Cập nhật lại tổng tiền trước khi lưu đơn hàng
        updateOrderTotal(order);

        // Cập nhật trạng thái đơn hàng thành "Hoàn thành"
        order.setStatusOrder(5);
        OrderResponse response = OrderMapper.toOrderResponse(orderRepository.save(order));
        logger.info("Thanh toán thành công! Order ID: {}, Tổng tiền: {}, Tổng số lượng: {}",
                order.getId(), order.getTotalBill(), order.getTotalAmount());

        return response;
    }


    @Transactional
    public Order thanhToan(OrderPOSCreateRequest request) {
        if (request.getOrderId() == null) {
            logger.error("Order ID is null in request");
            throw new IllegalArgumentException("Order ID không được để trống.");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn."));


        // Cập nhật customerId
        if (request.getCustomerId() != null && request.getCustomerId() != order.getCustomer().getId()) {
            Customer customer = customerService.findById(request.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + request.getCustomerId()));
            order.setCustomer(customer);
            logger.info("Cập nhật customer_id thành: {}", request.getCustomerId());
        }

        // Cập nhật voucherId
        if (request.getVoucherId() != null && (order.getVoucher() == null || request.getVoucherId() != order.getVoucher().getId())) {
            Voucher voucher = voucherService.findById(request.getVoucherId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy voucher với ID: " + request.getVoucherId()));
            order.setVoucher(voucher);
            logger.info("Cập nhật voucher_id thành: {}", request.getVoucherId());
        }

        // Cập nhật orderDetails
        if (request.getOrderDetails() != null && !request.getOrderDetails().isEmpty()) {
            for (OrderDetailCreateRequest detailReq : request.getOrderDetails()) {
                ProductDetail productDetail = productDetailService.findById(detailReq.getProductDetailId())
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + detailReq.getProductDetailId()));

                if (productDetail.getQuantity() < detailReq.getQuantity()) {
                    throw new IllegalArgumentException("Sản phẩm " + productDetail.getProduct().getProductName() + " không đủ hàng!");
                }

                OrderDetail existingOrderDetail = order.getOrderDetails().stream()
                        .filter(od -> od.getProductDetail().getId().equals(detailReq.getProductDetailId()))
                        .findFirst()
                        .orElse(null);

                if (existingOrderDetail != null) {
                    existingOrderDetail.setQuantity(detailReq.getQuantity());
                    orderDetailRepository.save(existingOrderDetail);
                    logger.info("Cập nhật số lượng sản phẩm trong đơn hàng. Order Detail ID: {}", existingOrderDetail.getId());
                } else {
                    OrderDetail newOrderDetail = new OrderDetail();
                    newOrderDetail.setOrder(order);
                    newOrderDetail.setProductDetail(productDetail);
                    newOrderDetail.setQuantity(detailReq.getQuantity());
                    order.getOrderDetails().add(newOrderDetail);
                    orderDetailRepository.save(newOrderDetail);
                    logger.info("Thêm mới sản phẩm vào đơn hàng. Order Detail ID: {}", newOrderDetail.getId());
                }
            }
        }

        // ✅ Cập nhật lại tổng tiền trước khi lưu đơn hàng
        updateOrderTotal(order);

        // Lưu lại hóa đơn đã cập nhật vào cơ sở dữ liệu
        return orderRepository.save(order);
    }

    private BigDecimal getDiscountedPrice(ProductDetail productDetail) {
        BigDecimal salePrice = productDetail.getSalePrice();
        if (productDetail.getPromotion() != null) {
            BigDecimal discountPercent = BigDecimal.valueOf(100 - productDetail.getPromotion().getPromotionPercent())
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            return salePrice.multiply(discountPercent);
        }
        return salePrice;
    }

    @Transactional
    public OrderResponse updateProductQuantity(Integer orderId, Integer productDetailId, Integer quantity) {
        logger.info("Bắt đầu cập nhật số lượng sản phẩm. Order ID: {}, Product Detail ID: {}, Quantity: {}",
                orderId, productDetailId, quantity);

        // Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Tìm sản phẩm
        ProductDetail productDetail = productDetailService.findById(productDetailId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + productDetailId));

        // Kiểm tra tồn kho
        if (productDetail.getQuantity() < quantity) {
            logger.error("Sản phẩm {} không đủ hàng. Yêu cầu: {}, Còn lại: {}",
                    productDetail.getProduct().getProductName(), quantity, productDetail.getQuantity());
            throw new IllegalArgumentException("Sản phẩm " + productDetail.getProduct().getProductName() + " không đủ hàng!");
        }

        // Tìm OrderDetail
        OrderDetail orderDetail = order.getOrderDetails().stream()
                .filter(od -> od.getProductDetail().getId().equals(productDetailId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Sản phẩm không có trong đơn hàng"));

        // Cập nhật số lượng
        orderDetail.setQuantity(quantity);
        orderDetailRepository.save(orderDetail);
        logger.info("Cập nhật số lượng sản phẩm thành công. Order Detail ID: {}", orderDetail.getId());

        // Cập nhật tổng tiền đơn hàng
        updateOrderTotal(order);

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);
        logger.info("Cập nhật đơn hàng thành công. Order ID: {}", savedOrder.getId());
        return OrderMapper.toOrderResponse(savedOrder);
    }

}
