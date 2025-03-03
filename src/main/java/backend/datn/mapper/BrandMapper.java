package backend.datn.mapper;


import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;

public class BrandMapper {

    public static BrandResponse toBrandResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .brandName(brand.getBrandName())
                .status(brand.getStatus())
                .build();
    }
}
