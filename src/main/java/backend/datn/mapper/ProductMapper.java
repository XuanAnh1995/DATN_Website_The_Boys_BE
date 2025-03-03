package backend.datn.mapper;


import backend.datn.dto.response.ProductResponse;
import backend.datn.entities.Product;

public class ProductMapper {
    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .brand(BrandMapper.toBrandResponse(product.getBrand()))
                .category(CategoryMapper.toCategoryResponse(product.getCategory()))
                .material(MaterialMapper.toMaterialResponse(product.getMaterial()))
                .productName(product.getProductName())
                .status(product.getStatus())
                .build();
    }
}
