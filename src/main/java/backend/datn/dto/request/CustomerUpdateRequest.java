package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUpdateRequest {
    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    String fullname;

    @Size(max = 255, message = "Email cannot exceed 255 characters")
    String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phone;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    String address;

}