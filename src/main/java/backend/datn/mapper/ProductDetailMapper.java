package backend.datn.mapper;

import backend.datn.dto.response.ProductDetailResponse;
import backend.datn.entities.ProductDetail;

public class ProductDetailMapper {
    public static ProductDetailResponse toProductDetailResponse(ProductDetail productDetail) {
        return ProductDetailResponse.builder()
                .id(productDetail.getId())
                .product(ProductMapper.toProductResponse(productDetail.getProduct()))
                .size(SizeMapper.toSizeResponse(productDetail.getSize()))
                .color(ColorMapper.toColorResponse(productDetail.getColor()))
                .promotion(PromotionMapper.toPromotionResponse(productDetail.getPromotion()))
                .collar(CollarMapper.toCollarResponse(productDetail.getCollar()))
                .sleeve(SleeveMapper.toSleeveResponse(productDetail.getSleeve()))
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
