package backend.datn.controllers;

import backend.datn.dto.response.ProductDetailResponse;
import backend.datn.services.UserProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-details")
@RequiredArgsConstructor
public class UserProductDetailController {

    private final UserProductDetailService userProductDetailService;

    @GetMapping("/{productCode}/variants")
    public ResponseEntity<List<ProductDetailResponse>> getProductVariants(@PathVariable String productCode) {
        List<ProductDetailResponse> variants = userProductDetailService.getProductVariantsBySanPhamId(productCode);

        if (variants == null || variants.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(variants);
    }
}