package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
public class EmployeeResponse implements Serializable {
    Integer id;
    String employeeCode;
    RoleResponse role;
    String fullname;
    String username;
    String email;
    String phone;
    String address;
    String photo;
    Integer status;
    Instant createDate;
    Instant updateDate;
    Boolean forgetPassword;
    Boolean gender;
}