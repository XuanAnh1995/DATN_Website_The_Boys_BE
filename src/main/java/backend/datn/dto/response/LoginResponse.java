package backend.datn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private Boolean status;
}
