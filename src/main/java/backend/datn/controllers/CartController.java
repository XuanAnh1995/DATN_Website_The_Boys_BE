package backend.datn.controllers;

import backend.datn.dto.request.CartItemRequest;
import backend.datn.dto.response.CartItemResponse;
import backend.datn.dto.ApiResponse;
import backend.datn.exceptions.BadRequestException;
import backend.datn.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCartItems() {
        try {
            List<CartItemResponse> response = cartService.getAllCartItems();
            return ResponseEntity.ok(new ApiResponse("success", "Lấy tất cả sản phẩm trong giỏ hàng thành công", response));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addProductToCart(@RequestBody CartItemRequest request) {
        try {
            CartItemResponse response = cartService.addProductToCart(request);
            return ResponseEntity.ok(new ApiResponse("success", "Thêm sản phẩm vào giỏ hàng thành công", response));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> removeProductFromCart(@PathVariable Long cartItemId) {
        try {
            cartService.removeProductFromCart(cartItemId);
            return ResponseEntity.ok(new ApiResponse("success", "Xóa sản phẩm khỏi giỏ hàng thành công"));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", ex.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart() {
        try {
            cartService.clearCart();
            return ResponseEntity.ok(new ApiResponse("success", "Xóa tất cả sản phẩm trong giỏ hàng thành công"));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", ex.getMessage()));
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
        try {
            CartItemResponse response = cartService.updateCartItemQuantity(cartItemId, quantity);
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật số lượng sản phẩm trong giỏ hàng thành công", response));
        } catch (BadRequestException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", ex.getMessage()));
        }
    }
}