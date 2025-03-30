package backend.datn.controllers;

import backend.datn.dto.request.CartItemRequest;
import backend.datn.dto.response.CartItemResponse;
import backend.datn.dto.ApiResponse;
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
        List<CartItemResponse> response = cartService.getAllCartItems();
        return ResponseEntity.ok(new ApiResponse("success", "Lấy tất cả sản phẩm trong giỏ hàng thành công", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addProductToCart(@RequestBody CartItemRequest request) {
        CartItemResponse response = cartService.addProductToCart(request);
        return ResponseEntity.ok(new ApiResponse("success", "Thêm sản phẩm vào giỏ hàng thành công", response));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> removeProductFromCart(@PathVariable Integer cartItemId) {
        cartService.removeProductFromCart(cartItemId);
        return ResponseEntity.ok(new ApiResponse("success", "Xóa sản phẩm khỏi giỏ hàng thành công"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok(new ApiResponse("success", "Xóa tất cả sản phẩm trong giỏ hàng thành công"));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> updateCartItemQuantity(@PathVariable Integer cartItemId, @RequestParam Integer quantity) {
        CartItemResponse response = cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(new ApiResponse("success", "Cập nhật số lượng sản phẩm trong giỏ hàng thành công", response));
    }
}