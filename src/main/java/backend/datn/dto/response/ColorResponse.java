package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColorResponse {
    private Integer id;
    private String name;
    private Boolean status;
}