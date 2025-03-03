package backend.datn.dto.request;

import backend.datn.entities.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmployeeCreateRequest implements Serializable {
    @NotNull(message = "ID vai trò không được để trống")
    String roleId;

    @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
    String fullname;

    @NotNull(message = "Tên đăng nhập không được để trống")
    @Size(max = 100, message = "Tên đăng nhập không được vượt quá 100 ký tự")
    String username;

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