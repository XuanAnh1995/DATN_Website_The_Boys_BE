package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.ForgetPasswordRequest;
import backend.datn.dto.request.LoginRequest;
import backend.datn.dto.request.RegisterRequest;
import backend.datn.dto.request.UpdateCurrentUserRequest;
import backend.datn.dto.response.AddressResponse;
import backend.datn.dto.response.LoginResponse;
import backend.datn.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
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

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse> getCurrentUserInfo() {
        try {
            Object userInfo = authService.getCurrentUserInfo();
            return ResponseEntity.ok(new ApiResponse("success", "Lấy thông tin người dùng thành công", userInfo));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Đã xảy ra lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/current-user/addresses")
    public ResponseEntity<ApiResponse> getCurrentUserAddresses() {
        try {
            List<AddressResponse> addresses = authService.getCurrentUserAddresses();

            if (addresses == null) {
                return ResponseEntity.ok(new ApiResponse("success", "Người dùng chưa có địa chỉ nào", null));
            }

            return ResponseEntity.ok(new ApiResponse("success", "Lấy danh sách địa chỉ thành công", addresses));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse("error", "Người dùng chưa đăng nhập"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Đã xảy ra lỗi: " + e.getMessage()));
        }
    }

    @PutMapping("/update-current-user")
    public ResponseEntity<ApiResponse> updateCurrentUser(@Valid @RequestBody UpdateCurrentUserRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Dữ liệu đầu vào không hợp lệ", errors));
        }

        try {
            Object updatedUser = authService.updateCurrentUser(request);
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật thông tin thành công", updatedUser));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi validate", errors));
        }

        try {
            String message = authService.register(request);
            return ResponseEntity.ok(new ApiResponse("success", message, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/confirm")
    public ModelAndView confirmRegister(@RequestParam("token") String token) {
        String message = authService.confirmRegister(token);

        ModelAndView mav = new ModelAndView();
        if (message.contains("thành công")) {
            mav.setViewName("confirm-success");
        } else {
            mav.setViewName("confirm-error");
        }
        mav.addObject("message", message);
        return mav;
    }



    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse> handleForgetPassword(@RequestBody ForgetPasswordRequest request) {
        try {
            authService.handleForgotPassword(request.getUsernameOrEmail());
            return ResponseEntity.ok(new ApiResponse("success", "Yêu cầu quên mật khẩu đã được gửi. Vui lòng kiểm tra email của bạn."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi: " + e.getMessage()));
        }
    }

    @RequestMapping("/confirm-forgot-password")
    public ModelAndView confirmForgotPassword(@RequestParam String token) {
        ModelAndView mav = new ModelAndView();
        try {
            authService.confirmForgotPassword(token);
            mav.setViewName("forgot-password-success");
            mav.addObject("message", "Mật khẩu tạm thời đã được gửi qua email.");
        } catch (Exception e) {
            mav.setViewName("forgot-password-error");
            mav.addObject("message", "Lỗi: " + e.getMessage());
        }
        return mav;
    }


}
