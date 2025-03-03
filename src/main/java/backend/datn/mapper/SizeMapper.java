package backend.datn.mapper;

import backend.datn.dto.response.SizeResponse;
import backend.datn.entities.Size;


public class SizeMapper {
    public static SizeResponse toSizeResponse(Size size){
        return SizeResponse.builder()
                .id(size.getId())
                .name(size.getSizeName())
                .status(size.getStatus())
                .build();
    }
}
