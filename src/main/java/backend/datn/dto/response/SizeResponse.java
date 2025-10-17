package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SizeResponse {
    private Long id;
    private String name;
    private Boolean status;
}