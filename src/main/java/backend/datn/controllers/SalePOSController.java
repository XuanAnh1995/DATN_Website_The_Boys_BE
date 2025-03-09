package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.OrderDetailCreateRequest;
import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.*;
import backend.datn.mapper.OrderMapper;
import backend.datn.services.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // tạo hoá đơn rỗng
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> createEmptyOrder(
            @RequestParam Integer customerId,
            @RequestParam Integer employeeId,
            @RequestParam(required = false) Integer voucherId,
            @RequestParam Integer paymentMethod) {
        try {
            // Lấy thông tin khách hàng
            Customer customer = resolveCustomer(customerId);

            // Lấy thông tin nhân viên
            Employee employee = resolveEmployee(employeeId);

            // Lấy thông tin voucher nếu có
            Voucher voucher = (voucherId != null) ? voucherService.findById(voucherId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy voucher với ID: " + voucherId))
                    : null;

            // Tạo đơn hàng trống
            Order order = salePOSService.createEmptyOrder(customer, employee, voucher, paymentMethod);

            OrderResponse orderResponse = OrderMapper.toOrderResponse(order);
            return ResponseEntity.ok(new ApiResponse("success", "Tạo hóa đơn mới thành công", orderResponse));
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
    public ResponseEntity<OrderResponse> addProductToCart(
            @PathVariable Integer orderId,
            @RequestBody OrderDetailCreateRequest request) {
        try {
            OrderResponse response = salePOSService.addProductToCart(orderId, request);
            return ResponseEntity.ok(response);
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
    public ResponseEntity<ApiResponse> updateOrderStatusAfterPayment(@PathVariable Integer orderId) {
        try {
            OrderResponse response = salePOSService.updateOrderStatusAfterPayment(orderId);
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

}

