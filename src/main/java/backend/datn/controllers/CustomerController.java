package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.CustomerCreateRequest;
import backend.datn.dto.request.CustomerUpdateRequest;
import backend.datn.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", "Lấy danh sách khách hàng thành công",
                    customerService.getAllCustomers(search, page, size, sortBy, sortDir)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", "Lấy thông tin khách hàng thành công",
                    customerService.getCustomerById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi xác thực", errorMessages));
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("success", "Tạo khách hàng thành công",
                            customerService.createCustomer(request)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCustomer(@PathVariable int id, @Valid @RequestBody CustomerUpdateRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi xác thực", errorMessages));
        }
        try {
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật khách hàng thành công",
                    customerService.updateCustomer(id, request)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleStatusCustomer(@PathVariable int id) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", "Thay đổi trạng thái khách hàng thành công",
                    customerService.toggleStatusCustomer(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }
}