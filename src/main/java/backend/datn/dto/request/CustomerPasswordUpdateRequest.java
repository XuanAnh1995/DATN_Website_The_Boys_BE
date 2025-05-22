package backend.datn.dto.request;

import lombok.Data;

@Data
public class CustomerPasswordUpdateRequest {
    private String newPassword;
    private String confirmPassword;
}

