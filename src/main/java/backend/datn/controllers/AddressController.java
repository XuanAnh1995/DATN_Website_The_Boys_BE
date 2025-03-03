package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.AddressCreateRequest;
import backend.datn.dto.response.AddressResponse;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Integer id) {
        try {
            AddressResponse addressResponse = addressService.getById(id);
            return ResponseEntity.ok(new ApiResponse("success", "Địa chỉ khách hàng được tìm thấy", addressResponse));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse> getByCustomerId(@PathVariable String customerId) {
        List<AddressResponse> addressResponses = addressService.getByCustomerId(customerId);
        return ResponseEntity.ok(new ApiResponse("success", "Danh sách địa chỉ khách hàng", addressResponses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody AddressCreateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(buildValidationErrorResponse(bindingResult));
        }
        try {
            AddressResponse addressResponse = addressService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("success", "Địa chỉ khách hàng được tạo thành công", addressResponse));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @Valid @RequestBody AddressCreateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(buildValidationErrorResponse(bindingResult));
        }
        try {
            AddressResponse addressResponse = addressService.update(id, request);
            return ResponseEntity.ok(new ApiResponse("success", "Địa chỉ khách hàng được cập nhật thành công", addressResponse));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        try {
            addressService.delete(id);
            return ResponseEntity.ok(new ApiResponse("success", "Địa chỉ khách hàng được xóa thành công", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("error", e.getMessage(), null));
        }
    }

    private ApiResponse buildValidationErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
        return new ApiResponse("error", "Dữ liệu không hợp lệ", errors);
    }
}

