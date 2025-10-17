package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AddressResponse implements Serializable {
    Long id;
    CustomerResponse customer;
    Long provinceId;
    String provinceName;
    Long districtId;
    String districtName;
    Long wardId;
    String wardName;
    String addressDetail;
}