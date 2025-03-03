package backend.datn.mapper;


import backend.datn.dto.response.SleeveResponse;
import backend.datn.entities.Sleeve;

public class SleeveMapper {

    public static SleeveResponse toSleeveResponse(Sleeve sleeve) {
        return SleeveResponse.builder()
                .id(sleeve.getId())
                .sleeveName(sleeve.getSleeveName())
                .status(sleeve.getStatus())
                .build();
    }

}
