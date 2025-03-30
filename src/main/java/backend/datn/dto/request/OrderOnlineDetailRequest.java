package backend.datn.dto.request;


import lombok.Data;

@Data
public class OrderOnlineDetailRequest  {
    Integer productDetailId;
    Integer quantity;
}