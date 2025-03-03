package backend.datn.services;

import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.BrandMapper;
import backend.datn.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public Page<BrandResponse> getAllBrand(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Brand> brandPage = brandRepository.searchBrand(search, pageable);

        return brandPage.map(BrandMapper::toBrandResponse);

    }

    public BrandResponse getBrandById(int id) {
        Brand brand = brandRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu có id: " + id));
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse createBrand(BrandCreateRequest brandCreateRequest) {
        if (brandRepository.existsByBrandName(brandCreateRequest.getBrandName())) {
            throw new EntityAlreadyExistsException("Thương hiệu có tên: " + brandCreateRequest.getBrandName() + " đã tồn tại");
        }

        Brand brand = new Brand();
        brand.setBrandName(brandCreateRequest.getBrandName());

        brand = brandRepository.save(brand);
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse updateBrand(Integer id, BrandUpdateRequest brandUpdateRequest) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu có id: " + id));

        if (!brand.getBrandName().equalsIgnoreCase(brandUpdateRequest.getBrandName()) && brandRepository.existsByBrandName(brandUpdateRequest.getBrandName())) {
            throw new EntityAlreadyExistsException("Thương hiệu có tên: " + brandUpdateRequest.getBrandName() + " đã tồn tại");
        }

        brand.setBrandName(brandUpdateRequest.getBrandName());
        brand = brandRepository.save(brand);
        return BrandMapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse toggleStatusBrand(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu có id: " + id));

        brand.setStatus(!brand.getStatus());
        brand = brandRepository.save(brand);
        return BrandMapper.toBrandResponse(brand);
    }


}
