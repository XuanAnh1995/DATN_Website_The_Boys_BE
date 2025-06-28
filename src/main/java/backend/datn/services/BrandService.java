package backend.datn.services;

import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;
import backend.datn.entities.Product;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.BrandMapper;
import backend.datn.repositories.BrandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    private static final Logger logger = LoggerFactory.getLogger(BrandService.class);

    @Autowired
    private BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public Page<BrandResponse> getAllBrand(String search, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching brands with search: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                search, page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Brand> brandPage = brandRepository.searchBrand(search, pageable);

        return brandPage.map(BrandMapper::toBrandResponse);

    }

    @Transactional(readOnly = true)
    public BrandResponse getBrandById(int id) {
        logger.info("Fetching brand with id: {}", id);
        Brand brand = brandRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu có id: " + id));
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse createBrand(BrandCreateRequest brandCreateRequest) {
        logger.info("Creating brand with name: {}", brandCreateRequest.getBrandName());
        if (brandRepository.existsByBrandName(brandCreateRequest.getBrandName())) {
            logger.warn("Brand with name {} already exists", brandCreateRequest.getBrandName());
            throw new EntityAlreadyExistsException("Thương hiệu có tên: " + brandCreateRequest.getBrandName() + " đã tồn tại");
        }

        Brand brand = new Brand();
        brand.setBrandName(brandCreateRequest.getBrandName());

        brand = brandRepository.save(brand);
        logger.info("Brand created successfully with id: {}", brand.getId());
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse updateBrand(Integer id, BrandUpdateRequest brandUpdateRequest) {
        logger.info("Updating brand with id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu có id: " + id));

        if (!brand.getBrandName().equalsIgnoreCase(brandUpdateRequest.getBrandName()) && brandRepository.existsByBrandName(brandUpdateRequest.getBrandName())) {
            logger.warn("Brand with name {} already exists", brandUpdateRequest.getBrandName());
            throw new EntityAlreadyExistsException("Thương hiệu có tên: " + brandUpdateRequest.getBrandName() + " đã tồn tại");
        }

        brand.setBrandName(brandUpdateRequest.getBrandName());
        brand = brandRepository.save(brand);
        logger.info("Brand updated successfully with id: {}", brand.getId());
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse toggleStatusBrand(Integer id) {
        logger.info("Toggling status for brand with id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu có id: " + id));

        boolean newStatus = !brand.getStatus(); // Tính trạng thái mới trước khi thay đổi
        if (newStatus == false && !brand.getListProducts().isEmpty()) { // Ngăn chặn vô hiệu hóa nếu có sản phẩm
            logger.warn("Cannot deactivate brand with id {} because it has associated products", id);
            throw new IllegalStateException("Không thể vô hiệu hóa thương hiệu đang có sản phẩm liên kết");
        }

        brand.setStatus(!brand.getStatus());
        brand = brandRepository.save(brand);
        logger.info("Brand status toggled successfully for id: {}", id);
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public void softDeleteBrand(Integer id) {
        logger.info("Soft deleting brand with id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu có id: " + id));

        if (!brand.getListProducts().isEmpty()) {
            logger.warn("Cannot soft delete brand with id {} because it has associated products", id);
            throw new IllegalStateException("Không thể xóa mềm thương hiệu đang có sản phẩm liên kết");
        }

        brand.setStatus(false);
        brandRepository.save(brand);
        logger.info("Brand soft deleted successfully with id: {}", id);
    }

    // Liệt kê danh sách sản phẩm ở trạng thái đang bán của 1 thương hiệu
    @Transactional(readOnly = true)
    public List<Product> getProductsWithActiveStatusByBrandId(Integer id, boolean onlyActive) {
        logger.info("Fetching product for brand with id: {} (onlyActive : {})", id, onlyActive);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));

        return brand.getAllProductsInBrand(onlyActive);
    }

    // Thống kê số lượng sản phẩm ở trạng thái đang bán của 1 thương hiệu
    @Transactional(readOnly = true)
    public long getProductCountWithActiveStatusByBrandId(Integer id, boolean onlyActive) {
        logger.info("Fetching product count for brand with id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));
        return brand.getAllProductsInBrand(onlyActive).size();
    }
}
