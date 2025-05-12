package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerAddressResponse {
    private Integer id;
    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private Integer wardId;
    private String wardName;
    private String addressDetail;
}
