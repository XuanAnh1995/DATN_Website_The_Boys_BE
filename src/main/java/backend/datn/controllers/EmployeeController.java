package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.EmployeeCreateRequest;
import backend.datn.dto.request.EmployeePasswordUpdateRequest;
import backend.datn.dto.request.EmployeeUpdateRequest;
import backend.datn.dto.response.EmployeeResponse;
import backend.datn.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", "Lấy danh sách nhân viên thành công",
                    employeeService.getAllEmployees(search, page, size, sortBy, sortDir)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmployeeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", "Lấy thông tin nhân viên thành công",
                    employeeService.getEmployeeById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi xác thực", errorMessages));
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("success", "Tạo nhân viên thành công",
                            employeeService.createEmployee(request)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Long id,
                                                      @Valid @RequestBody EmployeeUpdateRequest request,
                                                      BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi xác thực", errorMessages));
        }

        try {
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật nhân viên thành công",
                    employeeService.updateEmployee(id, request)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Đã xảy ra lỗi: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable Long id,
                                                      @Valid @RequestBody EmployeePasswordUpdateRequest request,
                                                      BindingResult result) {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi xác thực", errorMessages));
        }

        try {
            EmployeeResponse response = employeeService.updatePassword(id, request);
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật mật khẩu thành công", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Lỗi máy chủ: " + e.getMessage()));
        }
    }



    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleStatusEmployee(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", "Thay đổi trạng thái nhân viên thành công",
                    employeeService.toggleStatusEmployee(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }
}
