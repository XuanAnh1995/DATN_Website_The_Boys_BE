package backend.datn.mapper;

import backend.datn.dto.response.CollarResponse;
import backend.datn.entities.Collar;

public class CollarMapper {
    public static CollarResponse toCollarResponse(Collar collar) {
        return CollarResponse.builder()
                .id(collar.getId())
                .name(collar.getCollarName())
                .status(collar.getStatus())
                .build();
    }
}
