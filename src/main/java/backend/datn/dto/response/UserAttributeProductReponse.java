package backend.datn.dto.response;

import backend.datn.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributeProductReponse {
    List<Category> categories;
    List<Collar> collars;
    List<Size> sizes;
    List<Sleeve> sleeves;
    List<Brand> brands;
    List<Material> materials;
    List<Color> colors;
}
