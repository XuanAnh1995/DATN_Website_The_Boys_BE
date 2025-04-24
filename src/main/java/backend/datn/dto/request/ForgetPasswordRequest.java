package backend.datn.dto.request;

import lombok.Data;

@Data
public class ForgetPasswordRequest {
    private String usernameOrEmail;
}
