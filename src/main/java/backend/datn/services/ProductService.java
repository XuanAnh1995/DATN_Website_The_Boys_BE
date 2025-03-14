package backend.datn.services;

import backend.datn.dto.request.ProductCreateRequest;
import backend.datn.dto.request.ProductUpdateRequest;
import backend.datn.dto.response.ProductDetailResponse;
import backend.datn.dto.response.ProductResponse;
import backend.datn.dto.response.UserProductResponse;
import backend.datn.entities.Product;
import backend.datn.entities.ProductDetail;
import backend.datn.helpers.CodeGeneratorHelper;
import backend.datn.mapper.ProductDetailMapper;
import backend.datn.mapper.ProductMapper;
import backend.datn.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


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

    @Autowired
    ProductDetailRepository productDetailRepository;
    

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
        product.setProductName(request.getProductName());
        product.setStatus(true);
        product.setProductCode(CodeGeneratorHelper.generateCode7("PRO"));
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
        product.setProductName(request.getProductName());
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

    public List<ProductDetailResponse> getProductDetailsByProductCode(String productCode) {
        List<ProductDetail> productDetails = productDetailRepository.findByProductCode(productCode);
        if (productDetails.isEmpty()) {
            throw new EntityNotFoundException("No product details found for product code: " + productCode);
        }
        return productDetails.stream()
                .map(ProductDetailMapper::toProductDetailResponse)
                .collect(Collectors.toList());
    }
    public Page<UserProductResponse> getAllProductUser(
            String search, List<Integer> brandIds, List<Integer> categoryIds,
            List<Integer> materialIds, List<Integer> collarIds, List<Integer> sleeveIds,
            List<Integer> colorIds, List<Integer> sizeIds, BigDecimal minPrice,
            BigDecimal maxPrice, String sortBy, String sortDir, int page, int size
    ) {
        // Kiểm tra giá trị hợp lệ của sortBy
        List<String> allowedSortFields = List.of("id", "productName", "quantity", "salePrice");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "id"; // Mặc định nếu không hợp lệ
        }

        // Tạo Sort object từ sortBy và sortDir
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAllWithFilters(
                search, brandIds, categoryIds, materialIds, collarIds, sleeveIds, colorIds, sizeIds, minPrice, maxPrice, pageable
        );
    }



}
