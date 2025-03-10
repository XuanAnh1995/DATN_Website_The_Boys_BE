package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ProductDetailGroupReponse implements Serializable {
    Integer productId;
    String ColorName;
    String productName;
    List<ProductDetailGenerateResponse> productDetails;
}