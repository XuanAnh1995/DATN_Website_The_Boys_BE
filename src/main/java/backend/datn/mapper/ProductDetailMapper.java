package backend.datn.mapper;

import backend.datn.dto.response.ProductDetailResponse;
import backend.datn.entities.ProductDetail;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailMapper {
    public static ProductDetailResponse toProductDetailResponse(ProductDetail productDetail) {
        if (productDetail == null) return null;
        return ProductDetailResponse.builder()
                .id(productDetail.getId())
                .product(productDetail.getProduct() != null ? ProductMapper.toProductResponse(productDetail.getProduct()) : null)
                .size(productDetail.getSize() != null ? SizeMapper.toSizeResponse(productDetail.getSize()) : null)
                .color(productDetail.getColor() != null ? ColorMapper.toColorResponse(productDetail.getColor()) : null)
                .promotion(productDetail.getPromotion() != null ? PromotionMapper.toPromotionResponse(productDetail.getPromotion()) : null)
                .collar(productDetail.getCollar() != null ? CollarMapper.toCollarResponse(productDetail.getCollar()) : null)
                .sleeve(productDetail.getSleeve() != null ? SleeveMapper.toSleeveResponse(productDetail.getSleeve()) : null)
                .photo(productDetail.getPhoto())
                .productDetailCode(productDetail.getProductDetailCode())
                .importPrice(productDetail.getImportPrice())
                .salePrice(productDetail.getSalePrice())
                .quantity(productDetail.getQuantity())
                .description(productDetail.getDescription())
                .status(productDetail.getStatus())
                .build();
    }
}
