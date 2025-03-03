package backend.datn.services;

import backend.datn.dto.request.CategoryCreateRequest;
import backend.datn.dto.request.CategoryUpdateRequest;
import backend.datn.dto.response.CategoryResponse;
import backend.datn.entities.Category;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.CategoryMapper;
import backend.datn.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public Page<CategoryResponse> getAllCategories(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categories = categoryRepository.searchCategories(search, pageable);

        return categories.map(CategoryMapper::toCategoryResponse);
    }

    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại có id: " + id));
        return CategoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        if (categoryRepository.existsByCategoryName(categoryCreateRequest.getName())) {
            throw new ResourceNotFoundException("Thể loại có tên: " + categoryCreateRequest.getName() + " đã tồn tại");
        }
        Category category = new Category();
        category.setCategoryName(categoryCreateRequest.getName());
        category = categoryRepository.save(category);
        return CategoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại có id: " + id));

        if(category.getCategoryName().equalsIgnoreCase(categoryUpdateRequest.getName()) && categoryRepository.existsByCategoryName(categoryUpdateRequest.getName())) {
            throw new EntityAlreadyExistsException("Thể loại có tên: " + categoryUpdateRequest.getName() + " đã tồn tại");
        }
        category.setCategoryName(categoryUpdateRequest.getName());
        category = categoryRepository.save(category);
        return CategoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse toggleCategoryStatus(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thể loại có id: " + id));
        category.setStatus(!category.getStatus());
        category = categoryRepository.save(category);
        return CategoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại có id: " + id));
        categoryRepository.delete(category);
    }
}