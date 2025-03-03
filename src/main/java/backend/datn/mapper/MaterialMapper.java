package backend.datn.mapper;


import backend.datn.dto.response.MaterialResponse;
import backend.datn.entities.Material;

public class MaterialMapper {

    public static MaterialResponse toMaterialResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .materialName(material.getMaterialName())
                .status(material.getStatus())
                .build();
    }

}
