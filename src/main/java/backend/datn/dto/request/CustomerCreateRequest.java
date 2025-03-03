package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerCreateRequest implements Serializable {
    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    String fullname;

    @NotNull(message = "Username cannot be empty")
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    String username;

    @Size(max = 255, message = "Email cannot exceed 255 characters")
    String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phone;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    String address;
}