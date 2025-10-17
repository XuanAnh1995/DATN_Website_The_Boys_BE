package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class CustomerAddressRequest {
    @NotNull
    private Long provinceId;

    @NotNull
    @Size(max = 50)
    private String provinceName;

    @NotNull
    private Long districtId;

    @NotNull
    @Size(max = 50)
    private String districtName;

    @NotNull
    private Long wardId;

    @NotNull
    @Size(max = 50)
    private String wardName;

    @NotNull
    @Size(max = 255)
    private String addressDetail;
}
