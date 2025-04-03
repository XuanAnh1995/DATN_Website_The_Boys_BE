package backend.datn.services;

import backend.datn.dto.response.ProductDetailResponse;
import backend.datn.repositories.ProductDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProductDetailService {

    private final ProductDetailRepository productDetailRepository;

    public List<ProductDetailResponse> getProductVariantsBySanPhamId(String productCode) {
        return productDetailRepository.getProductVariantsByProductCode(productCode);
    }
}