package backend.datn.services;

import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;
import backend.datn.entities.Product;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.InvalidDataException;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandService {

    private static final Logger logger = LoggerFactory.getLogger(BrandService.class);

    @Autowired
    private BrandRepository brandRepository;

    /**
     * Lấy danh sách thương hiệu với phân trang và tìm kiếm.
     *
     * @param search  Chuỗi tìm kiếm (có thể rỗng).
     * @param page    Số trang (mặc định 0 nếu âm).
     * @param size    Kích thước trang (mặc định 10 nếu <= 0).
     * @param sortBy  Trường sắp xếp.
     * @param sortDir Hướng sắp xếp (asc/desc).
     * @return Trang chứa danh sách BrandResponse.
     */
    @Transactional(readOnly = true)
    public Page<BrandResponse> getAllBrand(String search, int page, int size, String sortBy, String sortDir) {
        logger.info("Fetching brands with search: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                search, page, size, sortBy, sortDir);
        page = Math.max(page, 0); // Xử lý page âm
        size = size <= 0 ? 10 : size; // Xử lý size không hợp lệ
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Brand> brandPage = brandRepository.searchBrand(search, pageable);

        return brandPage.map(BrandMapper::toBrandResponse);

    }

    /**
     * Lấy thương hiệu theo ID.
     *
     * @param id ID thương hiệu.
     * @return BrandResponse.
     * @throws InvalidDataException    nếu ID không hợp lệ.
     * @throws EntityNotFoundException nếu không tìm thấy.
     */
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        logger.info("Fetching brand with id: {}", id);
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID thương hiệu không hợp lệ");
        }
        Brand brand = brandRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));
        return BrandMapper.toBrandResponse(brand);
    }

    /**
     * Tạo thương hiệu mới.
     *
     * @param request Yêu cầu tạo thương hiệu (đã được validate bởi @Valid).
     * @return BrandResponse.
     * @throws EntityAlreadyExistsException nếu tên đã tồn tại.
     */
    @Transactional
    public BrandResponse createBrand(BrandCreateRequest request) {
        logger.info("Creating brand with name: {}", request.getBrandName());
        String name = request.getBrandName(); // Đã được validate bởi @NotBlank và @Size
        if (brandRepository.existsByBrandName(name)) {
            logger.warn("Brand with name {} already exists", name);
            throw new EntityAlreadyExistsException("Thương hiệu có tên: " + name + " đã tồn tại");
        }
        Brand brand = new Brand();
        brand.setBrandName(name);
        brand.setStatus(true);
        brand.setListProducts(new ArrayList<>());

        brand = brandRepository.save(brand);    // Hibernate Validator sẽ kiểm tra @NotBlank và @Size
        logger.info("Brand created successfully with id: {}", brand.getId());
        return BrandMapper.toBrandResponse(brand);
    }

    /**
     * Cập nhật thương hiệu.
     *
     * @param id      ID thương hiệu.
     * @param request Yêu cầu cập nhật (đã được validate bởi @Valid).
     * @return BrandResponse.
     * @throws InvalidDataException         nếu ID không hợp lệ.
     * @throws EntityNotFoundException      nếu không tìm thấy.
     * @throws EntityAlreadyExistsException nếu tên mới đã tồn tại.
     */
    @Transactional
    public BrandResponse updateBrand(Long id, BrandUpdateRequest request) {
        logger.info("Updating brand with id: {}", id);
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID thương hiệu không hợp lệ");
        }
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));
        String newName = request.getBrandName(); // Đã được validate bởi @NotBlank và @Size
        if (!newName.equalsIgnoreCase(brand.getBrandName()) && brandRepository.existsByBrandName(newName)) {
            logger.warn("Brand with name {} already exists", newName);
            throw new EntityAlreadyExistsException("Thương hiệu có tên: " + newName + " đã tồn tại");
        }
        brand.setBrandName(newName);
        brand = brandRepository.save(brand); // Hibernate Validator sẽ kiểm tra @NotBlank và @Size
        logger.info("Brand updated successfully with id: {}", id);
        return BrandMapper.toBrandResponse(brand);
    }

    /**
     * Thay đổi trạng thái thương hiệu (bật/tắt).
     *
     * @param id ID thương hiệu.
     * @return BrandResponse.
     * @throws InvalidDataException    nếu ID không hợp lệ.
     * @throws EntityNotFoundException nếu không tìm thấy.
     * @throws InvalidDataException    nếu có sản phẩm liên quan khi tắt.
     */
    @Transactional
    public BrandResponse toggleStatusBrand(Long id) {
        logger.info("Toggling status for brand with id: {}", id);
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID thương hiệu không hợp lệ");
        }
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));
        if (!brand.getStatus() && !brand.getListProducts().isEmpty()) {
            logger.warn("Cannot deactivate brand with id {} because it has associated products", id);
            throw new InvalidDataException("Không thể vô hiệu hóa thương hiệu đang có sản phẩm liên kết");
        }
        brand.setStatus(!brand.getStatus());
        brand = brandRepository.save(brand);
        logger.info("Brand status toggled successfully for id: {}", id);
        return BrandMapper.toBrandResponse(brand);
    }

    /**
     * Xóa mềm thương hiệu (đặt trạng thái thành false).
     *
     * @param id ID thương hiệu.
     * @throws InvalidDataException    nếu ID không hợp lệ.
     * @throws EntityNotFoundException nếu không tìm thấy.
     * @throws InvalidDataException    nếu có sản phẩm liên quan.
     */
    @Transactional
    public void softDeleteBrand(Long id) {
        logger.info("Soft deleting brand with id: {}", id);
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID thương hiệu không hợp lệ");
        }
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));
        if (!brand.getListProducts().isEmpty()) {
            logger.warn("Cannot soft delete brand with id {} because it has associated products", id);
            throw new InvalidDataException("Không thể xóa mềm thương hiệu đang có sản phẩm liên kết");
        }
        brand.setStatus(false);
        brandRepository.save(brand);
        logger.info("Brand soft deleted successfully with id: {}", id);
    }

    /**
     * Lấy danh sách sản phẩm theo trạng thái của thương hiệu.
     *
     * @param id         ID thương hiệu.
     * @param onlyActive Lọc sản phẩm active.
     * @return Danh sách sản phẩm.
     * @throws InvalidDataException    nếu ID không hợp lệ.
     * @throws EntityNotFoundException nếu không tìm thấy.
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsWithActiveStatusByBrandId(Long id, boolean onlyActive) {
        logger.info("Fetching product for brand with id: {} (onlyActive : {})", id, onlyActive);

        if (id == null || id <= 0) {
            throw new InvalidDataException("ID thương hiệu không hợp lệ");
        }
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));
        return brand.getAllProductsInBrand(onlyActive);
    }

    /**
     * Đếm số lượng sản phẩm của thương hiệu.
     *
     * @param id         ID thương hiệu.
     * @param onlyActive Đếm sản phẩm active.
     * @return Số lượng sản phẩm.
     * @throws InvalidDataException    nếu ID không hợp lệ.
     * @throws EntityNotFoundException nếu không tìm thấy.
     */
    @Transactional(readOnly = true)
    public long getProductCountWithActiveStatusByBrandId(Long id, boolean onlyActive) {
        logger.info("Fetching product count for brand with id: {}", id);
        if (id == null || id <= 0) {
            throw new InvalidDataException("ID thương hiệu không hợp lệ");
        }
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thương hiệu có id: " + id));
        return brand.getAllProductsInBrand(onlyActive).size();
    }
}
