package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.OrderDetailCreateRequest;
import backend.datn.dto.request.OrderPOSCreateRequest;
import backend.datn.dto.response.CheckoutResponse;
import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.*;
import backend.datn.mapper.OrderMapper;
import backend.datn.services.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> createEmptyOrder(@RequestBody OrderPOSCreateRequest request) {
        try {

            System.out.println("📌 [CREATE ORDER] Nhận yêu cầu tạo đơn hàng:");
            System.out.println("👤 Khách hàng ID: " + request.getCustomerId());
            System.out.println("💳 Phương thức thanh toán: " + request.getPaymentMethod());
            System.out.println("🎟 Voucher ID: " + request.getVoucherId());

            Customer customer = (request.getCustomerId() == null ||
                    request.getCustomerId().toString().trim().isEmpty() ||
                    request.getCustomerId() == -1)
                    ? customerService.findById(-1).orElse(null)
                    : resolveCustomer(request.getCustomerId());


            Employee employee = resolveEmployee(request.getEmployeeId());
            Voucher voucher = (request.getVoucherId() != null)
                    ? voucherService.findById(request.getVoucherId()).orElse(null)
                    : null;

            // Gọi createEmptyOrder trong SalePOSService
            Order order = salePOSService.createEmptyOrder(customer, employee, voucher, request.getPaymentMethod());

            // 🔍 Kiểm tra order sau khi tạo
            System.out.println("✅ [CREATE ORDER] Đơn hàng được tạo thành công: " + order.getId());

            return ResponseEntity.ok(new ApiResponse("success", "Tạo hóa đơn mới thành công", OrderMapper.toOrderResponse(order)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", e.getMessage(), null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi khi tạo hóa đơn: " + e.getMessage(), null));
        }
    }


    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/orders/{orderId}/products")
    public ResponseEntity<ApiResponse> addProductToCart(
            @PathVariable Integer orderId,
            @RequestBody OrderDetailCreateRequest request) {
        try {

            System.out.println("📌 [ADD PRODUCT] Đang thêm sản phẩm vào đơn hàng #" + orderId);
            System.out.println("🔍 Sản phẩm ID: " + request.getProductDetailId() + ", SL: " + request.getQuantity());

            // Kiểm tra đơn hàng có tồn tại không
            Order order = salePOSService.findOrderById(orderId);
            if (order == null) {
                System.err.println("❌ [ADD PRODUCT] Không tìm thấy đơn hàng #" + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("error", "Không tìm thấy đơn hàng", null));
            }

            // Kiểm tra sản phẩm tồn tại và còn hàng không
            ProductDetail productDetail = productDetailService.findById(request.getProductDetailId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));

            if (productDetail.getQuantity() < request.getQuantity()) {
                System.err.println("⚠ [ADD PRODUCT] Sản phẩm hết hàng! Còn lại: " + productDetail.getQuantity());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("error", "Sản phẩm không đủ hàng", null));
            }

            // Thêm sản phẩm vào đơn hàng
            OrderResponse response = salePOSService.addProductToCart(orderId, request);
            System.out.println("✅ [ADD PRODUCT] Đã thêm sản phẩm vào đơn hàng #" + orderId);
            return ResponseEntity.ok(new ApiResponse("success", "Thêm sản phẩm thành công", response));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Cập nhật trạng thái đơn hàng sau khi thanh toán
     */
    @PutMapping("/orders/{orderId}/payment")
    public ResponseEntity<ApiResponse> updateOrderStatusAfterPayment(
            @PathVariable Integer orderId,
            @RequestBody Map<String, Integer> requestBody) {
        try {
            // 🔍 Kiểm tra đơn hàng trước khi thanh toán
            Integer customerId = requestBody.get("customerId");
            Integer voucherId = requestBody.get("voucherId");
            OrderResponse response = salePOSService.updateOrderStatusAfterPayment(orderId, customerId, voucherId);

            System.out.println("📌 Xác nhận thanh toán cho đơn hàng #" + orderId);

            // 🔍 Log totalBill sau khi cập nhật
            System.out.println("✅ [PAYMENT] Đơn hàng #" + orderId + " đã được thanh toán. Tổng tiền: " + response.getTotalBill());

            return ResponseEntity.ok(new ApiResponse("success", "Thanh toán thành công", response));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", "Không tìm thấy đơn hàng", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", "Đơn hàng không hợp lệ để thanh toán", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", "Dữ liệu đầu vào không hợp lệ", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi hệ thống: " + e.getMessage(), null));
        }
    }


    // Hỗ trợ lấy thông tin khách hàng
    private Customer resolveCustomer(Integer customerId) {
        if (customerId != null && customerId > 0) {
            return customerService.findById(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + customerId));
        }
        return customerService.getWalkInCustomer();
    }


    // Hỗ trợ lấy thông tin nhân viên
    private Employee resolveEmployee(Integer employeeId) {
        return employeeService.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId));
    }


//    @PostMapping("/checkout")
//    public ResponseEntity<?> checkout(@Valid @RequestBody OrderPOSCreateRequest request, BindingResult result) {
//        if (result.hasErrors()) {
//            return ResponseEntity.badRequest().body(result.getAllErrors());
//        }
//        try {
//            Order order = salePOSService.thanhToan(request);
//            return ResponseEntity.ok(order);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody OrderPOSCreateRequest request) {
        try {
            Order order = salePOSService.thanhToan(request);
            return ResponseEntity.ok(new CheckoutResponse(order.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    /**
     * Lấy chi tiết đơn hàng theo ID
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Integer orderId) {
        try {
            System.out.println("📌 [GET ORDER] Lấy chi tiết đơn hàng #" + orderId);

            // Tìm đơn hàng theo ID
            Order order = salePOSService.findOrderById(orderId);
            if (order == null) {
                System.err.println("❌ [GET ORDER] Không tìm thấy đơn hàng #" + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("error", "Không tìm thấy đơn hàng", null));
            }

            // Chuyển đổi Order thành OrderResponse
            OrderResponse response = OrderMapper.toOrderResponse(order);
            System.out.println("✅ [GET ORDER] Lấy chi tiết đơn hàng thành công: " + order.getId());
            return ResponseEntity.ok(new ApiResponse("success", "Lấy chi tiết đơn hàng thành công", response));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage(), null));
        }
    }

}

