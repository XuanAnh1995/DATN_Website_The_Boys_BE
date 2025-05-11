package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class CustomerAddressRequest {
    @NotNull
    private Integer provinceId;

    @NotNull
    @Size(max = 50)
    private String provinceName;

    @NotNull
    private Integer districtId;

    @NotNull
    @Size(max = 50)
    private String districtName;

    @NotNull
    private Integer wardId;

    @NotNull
    @Size(max = 50)
    private String wardName;

    @NotNull
    @Size(max = 255)
    private String addressDetail;
}
