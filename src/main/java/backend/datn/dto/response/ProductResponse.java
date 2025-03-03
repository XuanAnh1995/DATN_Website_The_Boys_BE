package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link backend.datn_hn37.entities.Product}
 */
@Data
@Builder
public class ProductResponse implements Serializable {
    private final Integer id;
    private final BrandResponse brand;
    private final CategoryResponse category;
    private final MaterialResponse material;
    private final String productName;
    private final Boolean status;
}