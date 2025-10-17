package backend.datn.dto.request;


import lombok.Data;

@Data
public class OrderOnlineDetailRequest  {
    Long productDetailId;
    Integer quantity;
}