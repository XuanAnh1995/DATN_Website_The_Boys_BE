package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.OrderOnlineRequest;
import backend.datn.dto.response.OrderOnlineResponse;
import backend.datn.services.OrderOnlineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderOnlineController {

    @Autowired
    private OrderOnlineService orderOnlineService;

    /**
     * API tạo đơn hàng online
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderOnlineRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Dữ liệu không hợp lệ");

            return ResponseEntity.badRequest().body(new ApiResponse("error", errorMessage));
        }

        try {
            OrderOnlineResponse orderResponse = orderOnlineService.createOrder(request);
            return ResponseEntity.ok(new ApiResponse("success", "Đơn hàng đã được tạo thành công", orderResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }
}

