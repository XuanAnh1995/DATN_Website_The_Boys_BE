package backend.datn.mapper;

import backend.datn.dto.response.CategoryResponse;
import backend.datn.entities.Category;


public class CategoryMapper {
    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getCategoryName())
                .status(category.getStatus())
                .build();
    }
}
