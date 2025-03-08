package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.OrderDetailCreateRequest;
import backend.datn.dto.request.OrderPOSCreateRequest;
import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.*;
import backend.datn.services.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sale-pos")
public class SalePOSController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private VoucherService voucherService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SalePOSService salePOSService;


    // thanh toán tại POS
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse> checkoutPOS(@RequestBody OrderPOSCreateRequest request) {
        try {
            // Lấy thông tin khách hàng
            Customer customer = customerService.findById(request.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + request.getCustomerId()));

            // Lấy thông tin nhân viên
            Employee employee = employeeService.findById(request.getEmployeeId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với ID: " + request.getEmployeeId()));

            // Lấy thông tin voucher nếu có
            Voucher voucher = null;
            if (request.getVoucherId() != null) {
                voucher = voucherService.findById(request.getVoucherId())
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy voucher với ID: " + request.getVoucherId()));
            }

            // Tạo danh sách chi tiết đơn hàng từ request
            List<OrderDetail> orderDetails = request.getOrderDetails().stream().map(detailReq -> {
                ProductDetail productDetail = productDetailService.findById(detailReq.getProductDetailId())
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + detailReq.getProductDetailId()));

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setProductDetail(productDetail);
                orderDetail.setQuantity(detailReq.getQuantity());
                return orderDetail;
            }).collect(Collectors.toList());

            // Gọi service để tạo đơn hàng
            Order order = orderService.createOrder(customer, employee, voucher, orderDetails, request.getPaymentMethod());

            // Cập nhật trạng thái sau thanh toán
            OrderResponse updatedOrder = orderService.updateOrderStatusAfterPayment(order.getId());

            // Trả về phản hồi thành công
            ApiResponse response = new ApiResponse("success", "Thanh toán POS thành công", updatedOrder);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
         } catch (Exception e) {
        e.printStackTrace(); // In lỗi ra console để kiểm tra
        ApiResponse response = new ApiResponse("error", "Lỗi khi thanh toán tại POS: " + e.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
    // tạo hoá đơn rỗng
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> createEmptyOrder(@RequestBody OrderPOSCreateRequest request) {
        try {
            // Lấy thông tin khách hàng
            Customer customer = resolveCustomer(request.getCustomerId());

            // Lấy thông tin nhân viên
            Employee employee = resolveEmployee(request.getEmployeeId());

            // Tạo đơn hàng trống với giá trị mặc định
            Order order = new Order();

            // Thiết lập các giá trị mặc định cho các trường bắt buộc
            order.setCreateDate(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());  // Chuyển LocalDateTime thành Instant
            order.setTotalAmount(0);  // Tổng sản phẩm ban đầu là 0
            order.setTotalBill(BigDecimal.ZERO);  // Tổng hóa đơn ban đầu là 0
            order.setOrderCode(UUID.randomUUID().toString());  // Tạo mã đơn hàng ngẫu nhiên
            order.setCustomer(customer);
            order.setEmployee(employee);
            order.setPaymentMethod(request.getPaymentMethod());

            // Lưu đơn hàng vào cơ sở dữ liệu
            Order savedOrder = orderService.createEmptyOrder(customer, employee, order.getPaymentMethod());

            // Gán dữ liệu vào OrderResponse từ savedOrder
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderCode(savedOrder.getOrderCode());
            orderResponse.setTotalAmount(savedOrder.getTotalAmount());
            orderResponse.setTotalBill(savedOrder.getTotalBill());
            orderResponse.setCreateDate(savedOrder.getCreateDate());

            // Trả về phản hồi thành công
            return ResponseEntity.ok(new ApiResponse("success", "Tạo hóa đơn mới thành công", orderResponse));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi khi tạo hóa đơn: " + e.getMessage(), null));
        }
    }





    // Phương thức hỗ trợ giải quyết khách hàng từ ID
    private Customer resolveCustomer(Integer customerId) {
        if (customerId != null && customerId > 0) {
            return customerService.findById(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));
        }
        // Nếu không có ID, trả về khách vãng lai
        return customerService.getWalkInCustomer();
    }

    // Phương thức hỗ trợ giải quyết nhân viên từ ID
    private Employee resolveEmployee(Integer employeeId) {
        return employeeService.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId));
    }


    // thêm sản phẩm vào giỏ hàng
    @PostMapping("/orders/{orderId}/products")
    public ResponseEntity<OrderResponse> addProductToCart(
            @PathVariable Integer orderId,
            @RequestBody OrderDetailCreateRequest request) {
        OrderResponse response = salePOSService.addProductToCart(orderId, request);
        return ResponseEntity.ok(response);
    }

    // thêm phương thức thanh toán
    @PutMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderResponse> updateOrderStatusAfterPayment(@PathVariable Integer orderId) {
        OrderResponse response = salePOSService.updateOrderStatusAfterPayment(orderId);
        return ResponseEntity.ok(response);
    }
}

