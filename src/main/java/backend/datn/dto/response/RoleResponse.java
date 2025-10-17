package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RoleResponse implements Serializable {
    Long id;
    String name;
}