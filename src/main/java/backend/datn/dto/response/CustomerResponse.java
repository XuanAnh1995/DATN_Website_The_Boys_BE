package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
public class CustomerResponse implements Serializable {
    Integer id;
    String customerCode;
    String fullname;
    String username;
    String email;
    String phone;
    Instant createDate;
    Instant updateDate;
    Boolean forgetPassword;
    Boolean status;
}