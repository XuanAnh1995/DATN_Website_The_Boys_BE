package backend.datn.dto.request;


import backend.datn.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class EmployeeUpdateRequest  {
    @Size(max = 50, message = "Mã nhân viên không được vượt quá 50 ký tự")
    String employeeCode;

    @NotNull(message = "ID vai trò không được để trống")
    String roleId;

    @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
    String fullname;

    @Email(message = "Email phải hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    String email;

    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    String phone;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    String address;

    @Size(max = 255, message = "URL ảnh không được vượt quá 255 ký tự")
    String photo;

    @NotNull(message = "Giới tính không được để trống")
    Boolean gender;

}