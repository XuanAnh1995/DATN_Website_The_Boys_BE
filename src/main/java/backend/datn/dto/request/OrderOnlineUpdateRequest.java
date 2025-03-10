package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;

public class OrderOnlineUpdateRequest extends OrderOnlineCreateRequest {

    @NotNull
    private Integer id;

}
