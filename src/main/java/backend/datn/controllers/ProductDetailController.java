package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.ProductDetailCreateRequest;
import backend.datn.dto.request.ProductDetailUpdateRequest;
import backend.datn.dto.response.ProductDetailResponse;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.services.ProductDetailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product-details")
public class ProductDetailController {

    @Autowired
    private ProductDetailService productDetailService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProductDetails(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Integer> colorIds,
            @RequestParam(required = false) List<Integer> collarIds,
            @RequestParam(required = false) List<Integer> sizeIds,
            @RequestParam(required = false) List<Integer> sleeveIds,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {
        try {
            Page<ProductDetailResponse> productDetails = productDetailService.getAllProductDetails(
                    search, sizeIds, colorIds, collarIds, sleeveIds, minPrice, maxPrice, pageable);
            return ResponseEntity.ok(new ApiResponse("success", "Lấy danh sách chi tiết sản phẩm thành công", productDetails));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("error", "Lỗi khi lấy danh sách chi tiết sản phẩm: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductDetailById(@PathVariable Integer id) {
        try {
            ProductDetailResponse productDetail = productDetailService.getById(id);
            return ResponseEntity.ok(new ApiResponse("success", "Lấy chi tiết sản phẩm thành công", productDetail));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("error", "Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProductDetails(@Valid @RequestBody List<ProductDetailCreateRequest> requests, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi xác thực", errorMessages));
        }
        try {
            List<ProductDetailResponse> productDetails = productDetailService.createProductDetails(requests);
            return ResponseEntity.ok(new ApiResponse("success", "Tạo chi tiết sản phẩm thành công", productDetails));
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(409).body(new ApiResponse("error", "Chi tiết sản phẩm đã tồn tại: " + e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Không tìm thấy thực thể: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("error", "Lỗi khi tạo chi tiết sản phẩm: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProductDetail(@PathVariable Integer id, @Valid @RequestBody ProductDetailUpdateRequest request, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new ApiResponse("error", "Lỗi xác thực", errorMessages));
        }
        try {
            ProductDetailResponse productDetail = productDetailService.updateProductDetail(id, request);
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật chi tiết sản phẩm thành công", productDetail));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Không tìm thấy chi tiết sản phẩm: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("error", "Lỗi khi cập nhật chi tiết sản phẩm: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleProductDetailStatus(@PathVariable Integer id) {
        try {
            ProductDetailResponse productDetail = productDetailService.toggleProductDetailStatus(id);
            return ResponseEntity.ok(new ApiResponse("success", "Toggle trạng thái chi tiết sản phẩm thành công", productDetail));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse("error", "Không tìm thấy chi tiết sản phẩm: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("error", "Lỗi khi toggle trạng thái chi tiết sản phẩm: " + e.getMessage()));
        }
    }
}