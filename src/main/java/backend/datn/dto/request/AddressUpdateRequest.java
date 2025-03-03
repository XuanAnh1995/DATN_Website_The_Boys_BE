package backend.datn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressUpdateRequest {
    @NotNull(message = "ID tỉnh không được để trống")
    Integer provinceId;

    @NotBlank(message = "Tên tỉnh không được để trống")
    String provinceName;

    @NotNull(message = "ID huyện không được để trống")
    Integer districtId;

    @NotBlank(message = "Tên huyện không được để trống")
    String districtName;

    @NotNull(message = "ID xã không được để trống")
    Integer wardId;

    @NotBlank(message = "Tên xã không được để trống")
    String wardName;

    @NotBlank(message = "Chi tiết địa chỉ không được để trống")
    String addressDetail;
}