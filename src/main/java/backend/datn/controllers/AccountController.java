package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.CustomerAddressRequest;
import backend.datn.dto.request.UpdateCustomerInfomationRequest;
import backend.datn.dto.request.UpdateCustomerPasswordRequest;
import backend.datn.dto.response.CustomerAddressResponse;
import backend.datn.dto.response.CustomerResponse;
import backend.datn.services.UpdateAccountService;
import backend.datn.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private UpdateAccountService updateAccountService;

    @PutMapping("/update-info")
    public ResponseEntity<ApiResponse> updateCustomerInfo(@RequestBody UpdateCustomerInfomationRequest request) {
        try {
            CustomerResponse response = updateAccountService.updateCustomerInfo(request);
            ApiResponse apiResponse = new ApiResponse("success", "Cập nhật thông tin khách hàng thành công", response);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Không tìm thấy khách hàng", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (BadCredentialsException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Thông tin xác thực không hợp lệ", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse> updateCustomerPassword(@RequestBody UpdateCustomerPasswordRequest request) {
        try {
            updateAccountService.updateCustomerPassword(request);
            ApiResponse apiResponse = new ApiResponse("success", "Cập nhật mật khẩu thành công", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Không tìm thấy khách hàng", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (BadCredentialsException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Thông tin xác thực không hợp lệ", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse> getCustomerAddresses() {
        try {
            List<CustomerAddressResponse> addresses = updateAccountService.getCurrentCustomerAddresses();
            ApiResponse apiResponse = new ApiResponse("success", "Lấy danh sách địa chỉ thành công", addresses);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Không tìm thấy khách hàng", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse> createAddress(@RequestBody CustomerAddressRequest request) {
        try {
            CustomerAddressResponse response = updateAccountService.createAddress(request);
            ApiResponse apiResponse = new ApiResponse("success", "Tạo địa chỉ thành công", response);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Không tìm thấy khách hàng", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (BadCredentialsException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Thông tin xác thực không hợp lệ", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable Long addressId) {
        try {
            updateAccountService.deleteAddress(addressId);
            ApiResponse apiResponse = new ApiResponse("success", "Xóa địa chỉ thành công", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Không tìm thấy địa chỉ", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (BadCredentialsException ex) {
            ApiResponse apiResponse = new ApiResponse("error", "Không có quyền xóa địa chỉ này", null);
            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
    }
}
