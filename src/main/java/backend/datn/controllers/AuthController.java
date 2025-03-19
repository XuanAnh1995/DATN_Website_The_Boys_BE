package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.LoginRequest;
import backend.datn.dto.response.LoginResponse;
import backend.datn.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Dữ liệu đầu vào không hợp lệ", errors));
        }

        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse("success", "Đăng nhập thành công", response));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse("error", "Tên đăng nhập hoặc mật khẩu không đúng"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-temp-accounts")
    public ResponseEntity<ApiResponse> resetTempAccounts() {
        boolean isUpdated = authService.resetTempAccounts();

        if (isUpdated) {
            return ResponseEntity.ok(new ApiResponse("success", "Mật khẩu cho admin, staff và user đã được đặt lại thành công!"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Không tìm thấy tài khoản nào để đặt lại mật khẩu."));
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<ApiResponse> verifyToken(@RequestParam String token) {
        try {
            Map<String, Object> claims = authService.verifyToken(token);
            return ResponseEntity.ok(new ApiResponse("success", "Token hợp lệ", claims));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse("error", "Token không hợp lệ hoặc đã hết hạn"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage()));
        }
    }
}
