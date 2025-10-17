package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerAddressResponse {
    private Long id;
    private Long provinceId;
    private String provinceName;
    private Long districtId;
    private String districtName;
    private Long wardId;
    private String wardName;
    private String addressDetail;
}
