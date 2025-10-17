package backend.datn.services;

import backend.datn.dto.request.ProductDetailCreateRequest;
import backend.datn.dto.request.ProductDetailUpdateRequest;
import backend.datn.dto.response.*;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private BrandRepository brandRepository;


    public Page<ProductDetailResponse> getAllProductDetails(
            String search,
            List<Long> sizeIds,
            List<Long> colorIds,
            List<Long> collarIds,
            List<Long> sleeveIds,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        search = (search == null || search.trim().isEmpty()) ? null : search;
        sizeIds = (sizeIds == null || sizeIds.isEmpty()) ? null : sizeIds;
        colorIds = (colorIds == null || colorIds.isEmpty()) ? null : colorIds;
        collarIds = (collarIds == null || collarIds.isEmpty()) ? null : collarIds;
        sleeveIds = (sleeveIds == null || sleeveIds.isEmpty()) ? null : sleeveIds;

        Page<ProductDetail> productDetails = productDetailRepository.findBySearchAndFilter(
                search, sizeIds, colorIds, collarIds, sleeveIds, minPrice, maxPrice, pageable);

        return productDetails.map(ProductDetailMapper::toProductDetailResponse);
    }

    public Page<ProductDetailResponse> getAllProductDetailsWithStatusTrue(
            String search,
            List<Long> sizeIds,
            List<Long> colorIds,
            List<Long> collarIds,
            List<Long> sleeveIds,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        search = (search == null || search.trim().isEmpty()) ? null : search;
        sizeIds = (sizeIds == null || sizeIds.isEmpty()) ? null : sizeIds;
        colorIds = (colorIds == null || colorIds.isEmpty()) ? null : colorIds;
        collarIds = (collarIds == null || collarIds.isEmpty()) ? null : collarIds;
        sleeveIds = (sleeveIds == null || sleeveIds.isEmpty()) ? null : sleeveIds;

        Page<ProductDetail> productDetails = productDetailRepository.findBySearchAndFilterWithStatusTrue(
                search, sizeIds, colorIds, collarIds, sleeveIds, minPrice, maxPrice, pageable);

        return productDetails.map(ProductDetailMapper::toProductDetailResponse);
    }

    @Transactional
    public ProductDetailResponse getById(Long id) {
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

            for (Long sizeId : request.getSizeId()) {
                for (Long colorId : request.getColorId()) {
                    for (Long collarId : request.getCollarId()) {
                        for (Long sleeveId : request.getSleeveId()) {
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
    public ProductDetailResponse updateProductDetail(Long id, ProductDetailUpdateRequest request) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết sản phẩm"));

        mapToEntity(request, productDetail);
        productDetailRepository.save(productDetail);
        return ProductDetailMapper.toProductDetailResponse(productDetail);
    }

    // sửa soos lượng sản phẩm
    @Transactional
    public void update(ProductDetail productDetail) {
        productDetailRepository.save(productDetail);
    }

    private void validateExistence(ProductDetailCreateRequest request) {
        if (request.getPromotionId() != null) {
            promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khuyến mãi"));
        }

        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"));

        for (Long size : request.getSizeId()) {
            sizeRepository.findById(size)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kích thước"));
        }

        for (Long color : request.getColorId()) {
            colorRepository.findById(color)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy màu sắc"));
        }
    }

    private void checkUniqueProductDetail(ProductDetailCreateRequest request) {
        for (Long size : request.getSizeId()) {
            for (Long color : request.getColorId()) {
                for (Long collar : request.getCollarId()) {
                    for (Long sleeve : request.getSleeveId()) {
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
                             Long sizeId, Long colorId, Long collarId, Long sleeveId) {
        if (request.getPromotionId() != null) {
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
        entity.setDescription(
                request.getDescription() == null || request.getDescription().isEmpty()
                        ? "Chưa có mô tả"
                        : request.getDescription()
        );
        entity.setProductDetailCode("PD" + entity.getProduct().getId() + "S" + sizeId + "C" + colorId + "CL" + collarId + "SL" + sleeveId);
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
        entity.setPromotion(promotionRepository.findById(request.getPromotionId()).orElse(null));
        entity.setPhoto(request.getPhoto());
    }

    public ProductDetailResponse toggleProductDetailStatus(Long id) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chi tiết sản phẩm"));

        productDetail.setStatus(!productDetail.getStatus());
        productDetailRepository.save(productDetail);
        return ProductDetailMapper.toProductDetailResponse(productDetail);
    }

    public Optional<ProductDetail> findById(@NotNull Long productDetailId) {
        return productDetailRepository.findById(productDetailId);
    }

    @Transactional
    public List<ProductDetailGroupReponse> generateProductDetailsGroupedByColor(ProductDetailCreateRequest generateRequest) {
        // Bước 1: Generate danh sách tất cả các kết hợp chi tiết sản phẩm
        List<ProductDetailGenerateResponse> allCombinations = generateProductDetailList(generateRequest);

        // Bước 2: Lọc ra các sản phẩm chi tiết chưa tồn tại
        List<ProductDetailGenerateResponse> filteredCombinations = allCombinations.stream()
                .filter(this::isUniqueProductDetail)
                .collect(Collectors.toList());

        // Bước 3: Nhóm các sản phẩm đã lọc theo màu sắc
        Map<Long, List<ProductDetailGenerateResponse>> groupedByColor = filteredCombinations.stream()
                .collect(Collectors.groupingBy(ProductDetailGenerateResponse::getColor));

        // Bước 4: Chuyển đổi thành danh sách DTO để trả về
        return groupedByColor.entrySet().stream()
                .map(this::convertEntryToGroupResponse)
                .collect(Collectors.toList());
    }

    private boolean isUniqueProductDetail(ProductDetailGenerateResponse dto) {
        return !productDetailRepository.existsByProductAndSizeAndColorAndCollarAndSleeve(
                dto.getProductId(), dto.getSize(), dto.getColor(), dto.getCollar(), dto.getSleeve()
        );
    }

    private List<ProductDetailGenerateResponse> generateProductDetailList(ProductDetailCreateRequest generateRequest) {
        List<ProductDetailGenerateResponse> result = new ArrayList<>();

        for (Long sizeId : generateRequest.getSizeId()) {
            for (Long colorId : generateRequest.getColorId()) {
                for (Long collarId : generateRequest.getCollarId()) {
                    for (Long sleeveId : generateRequest.getSleeveId()) {
                        ProductDetailGenerateResponse dto = ProductDetailGenerateResponse.builder()
                                .productId(generateRequest.getProductId())
                                .productName(productRepository.findById(generateRequest.getProductId())
                                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"))
                                        .getProductName())
                                .brandName(brandRepository.findById(productRepository.findById(generateRequest.getProductId())
                                                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm"))
                                                .getBrand().getId())
                                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu"))
                                        .getBrandName())
                                .promotion(generateRequest.getPromotionId())
                                .promotionName(generateRequest.getPromotionId() == null ? null : promotionRepository.findById(generateRequest.getPromotionId())
                                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khuyến mãi"))
                                        .getPromotionName())
                                .size(sizeId)
                                .sizeName(sizeRepository.findById(sizeId)
                                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kích thước"))
                                        .getSizeName())
                                .color(colorId)
                                .colorName(colorRepository.findById(colorId)
                                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy màu sắc"))
                                        .getColorName())
                                .collar(collarId)
                                .collarName(collarRepository.findById(collarId)
                                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cổ áo"))
                                        .getCollarName())
                                .sleeve(sleeveId)
                                .sleeveName(sleeveRepository.findById(sleeveId)
                                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tay áo"))
                                        .getSleeveName())
                                .quantity(generateRequest.getQuantity())
                                .salePrice(generateRequest.getSalePrice())
                                .importPrice(generateRequest.getImportPrice())
                                .photo(generateRequest.getPhoto())
                                .build();

                        result.add(dto);
                    }
                }
            }
        }
        return result;
    }

    private ProductDetailGroupReponse convertEntryToGroupResponse(Map.Entry<Long, List<ProductDetailGenerateResponse>> entry) {
        return ProductDetailGroupReponse.builder()
                .productId(entry.getValue().get(0).getProductId())
                .ColorName(entry.getValue().get(0).getColorName())
                .productName(entry.getValue().get(0).getProductName())
                .productDetails(entry.getValue())
                .build();
    }

}
