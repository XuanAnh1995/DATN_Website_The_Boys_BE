package backend.datn.dto.request;

import lombok.Data;

@Data
public class EmployeePasswordUpdateRequest {
    private String newPassword;
    private String confirmPassword;
}

