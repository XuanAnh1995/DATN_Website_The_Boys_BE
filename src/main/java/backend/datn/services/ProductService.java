package backend.datn.services;

import backend.datn.dto.request.ProductCreateRequest;
import backend.datn.dto.request.ProductUpdateRequest;
import backend.datn.dto.response.ProductResponse;
import backend.datn.entities.Product;
import backend.datn.mapper.ProductMapper;
import backend.datn.repositories.BrandRepository;
import backend.datn.repositories.CategoryRepository;
import backend.datn.repositories.MaterialRepository;
import backend.datn.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    CategoryRepository categoryRepository;

    public Page<ProductResponse> getAllProducts(String keyword, Boolean status, int page, int size, String sortBy, String sortDirection) {
        sortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "id" : sortBy;
        Sort sort = "asc".equalsIgnoreCase(sortDirection) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAllWithFilters(keyword, status, pageable)
                .map(ProductMapper::toProductResponse);
    }

    public ProductResponse getProductById(Integer id) {
        return ProductMapper.toProductResponse(
                productRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found."))
        );
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setBrand(brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand with ID " + request.getBrandId() + " not found.")));
        product.setCategory(categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + request.getCategoryId() + " not found.")));
        product.setMaterial(materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new EntityNotFoundException("Material with ID " + request.getMaterialId() + " not found.")));
        product.setProductName(request.getProductNameId().toString());
        product.setStatus(true);
        product = productRepository.save(product);
        return ProductMapper.toProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found."));
        product.setBrand(brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand with ID " + request.getBrandId() + " not found.")));
        product.setCategory(categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + request.getCategoryId() + " not found.")));
        product.setMaterial(materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new EntityNotFoundException("Material with ID " + request.getMaterialId() + " not found.")));
        product.setProductName(request.getProductNameId().toString());
        product = productRepository.save(product);
        return ProductMapper.toProductResponse(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product with ID " + id + " not found.");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponse toggleProductStatus(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found."));
        product.setStatus(!product.getStatus());
        product = productRepository.save(product);
        return ProductMapper.toProductResponse(product);
    }
}
