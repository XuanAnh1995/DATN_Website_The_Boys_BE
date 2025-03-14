package backend.datn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserProductResponse {
    Integer id ;
    String codeProduct;
    String nameProduct;
    Long quantity;
    Long quantitySaled;
    String photo;
    BigDecimal salePrice;
    String description;
}
