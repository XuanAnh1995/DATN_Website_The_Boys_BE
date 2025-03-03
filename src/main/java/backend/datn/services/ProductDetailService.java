package backend.datn.services;

import backend.datn.dto.request.ProductDetailCreateRequest;
import backend.datn.dto.request.ProductDetailUpdateRequest;
import backend.datn.dto.response.ProductDetailResponse;
import backend.datn.entities.ProductDetail;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.mapper.ProductDetailMapper;
import backend.datn.repositories.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductDetailService {

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private CollarRepository collarRepository;

    @Autowired
    private SleeveRepository sleeveRepository;

    public Page<ProductDetailResponse> getAllProductDetails(
            String search,
            List<Integer> sizeIds, List<Integer> colorIds,
            List<Integer> collarIds, List<Integer> sleeveIds,
            Double minPrice, Double maxPrice, Pageable pageable) {

        Page<ProductDetail> productDetails = productDetailRepository.findBySearchAndFilter(
                search, sizeIds, colorIds, collarIds, sleeveIds, minPrice, maxPrice, pageable);

        return productDetails.map(ProductDetailMapper::toProductDetailResponse);
    }

    @Transactional
    public ProductDetailResponse getById(Integer id) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết sản phẩm"));
        return ProductDetailMapper.toProductDetailResponse(productDetail);
    }

    @Transactional
    public List<ProductDetailResponse> createProductDetails(List<ProductDetailCreateRequest> requests) {
        List<ProductDetailResponse> result = new ArrayList<>();



        for (ProductDetailCreateRequest request : requests) {
            validateExistence(request);
            checkUniqueProductDetail(request);

            for (Integer sizeId : request.getSizeId()) {
                for (Integer colorId : request.getColorId()) {
                    for (Integer collarId : request.getCollarId()) {
                        for (Integer sleeveId : request.getSleeveId()) {
                            ProductDetail productDetail = new ProductDetail();
                            mapToEntity(request, productDetail, sizeId, colorId, collarId, sleeveId);
                            productDetailRepository.save(productDetail);
                            result.add(ProductDetailMapper.toProductDetailResponse(productDetail));
                        }
                    }
                }
            }
        }
        return result;
    }

    @Transactional
    public ProductDetailResponse updateProductDetail(Integer id, ProductDetailUpdateRequest request) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết sản phẩm"));

        mapToEntity(request, productDetail);
        productDetailRepository.save(productDetail);
        return ProductDetailMapper.toProductDetailResponse(productDetail);
    }

    private void validateExistence(ProductDetailCreateRequest request) {
        if(request.getPromotionId()!=null){
            promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khuyến mãi"));
        }

        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));

        for (Integer size : request.getSizeId()) {
            sizeRepository.findById(size)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kích thước"));
        }

        for (Integer color : request.getColorId()) {
            colorRepository.findById(color)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy màu sắc"));
        }
    }

    private void checkUniqueProductDetail(ProductDetailCreateRequest request) {
        for (Integer size : request.getSizeId()) {
            for (Integer color : request.getColorId()) {
                for (Integer collar : request.getCollarId()) {
                    for (Integer sleeve : request.getSleeveId()) {
                        boolean exists = productDetailRepository.existsByProductAndSizeAndColorAndCollarAndSleeve(
                                request.getProductId(), size, color, collar, sleeve);
                        if (exists) {
                            throw new EntityAlreadyExistsException("Chi tiết sản phẩm đã tồn tại");
                        }
                    }
                }
            }
        }
    }

    private void mapToEntity(ProductDetailCreateRequest request, ProductDetail entity,
                             Integer sizeId, Integer colorId, Integer collarId, Integer sleeveId) {
        if(request.getPromotionId()!=null){
            entity.setPromotion(promotionRepository.findById(request.getPromotionId()).orElse(null));
        }
        entity.setProduct(productRepository.findById(request.getProductId()).orElse(null));
        entity.setSize(sizeRepository.findById(sizeId).orElse(null));
        entity.setColor(colorRepository.findById(colorId).orElse(null));
        entity.setCollar(collarRepository.findById(collarId).orElse(null));
        entity.setSleeve(sleeveRepository.findById(sleeveId).orElse(null));
        entity.setQuantity(request.getQuantity());
        entity.setImportPrice(request.getImportPrice());
        entity.setSalePrice(request.getSalePrice());
        entity.setPhoto(request.getPhoto());
        entity.setStatus(true);
    }

    private void mapToEntity(ProductDetailUpdateRequest request, ProductDetail entity) {
        entity.setProduct(productRepository.findById(request.getProductId()).orElse(null));
        entity.setSize(sizeRepository.findById(request.getSizeId()).orElse(null));
        entity.setColor(colorRepository.findById(request.getColorId()).orElse(null));
        entity.setCollar(collarRepository.findById(request.getCollarId()).orElse(null));
        entity.setSleeve(sleeveRepository.findById(request.getSleeveId()).orElse(null));
        entity.setQuantity(request.getQuantity());
        entity.setImportPrice(request.getImportPrice());
        entity.setSalePrice(request.getSalePrice());
        entity.setPhoto(request.getPhoto());
    }

    public ProductDetailResponse toggleProductDetailStatus(Integer id) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết sản phẩm"));

        productDetail.setStatus(!productDetail.getStatus());
        productDetailRepository.save(productDetail);
        return ProductDetailMapper.toProductDetailResponse(productDetail);
    }

    public Optional<ProductDetail> findById(@NotNull Integer productDetailId) {
        return productDetailRepository.findById(productDetailId);
    }
}
