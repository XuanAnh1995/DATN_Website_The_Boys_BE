package backend.datn.services;

import backend.datn.dto.request.MaterialCreateRequest;
import backend.datn.dto.request.MaterialUpdateRequest;
import backend.datn.dto.response.MaterialResponse;
import backend.datn.entities.Material;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.MaterialMapper;
import backend.datn.repositories.CategoryRepository;
import backend.datn.repositories.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public Page<MaterialResponse> getAllMaterials(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Material> materialPage = materialRepository.searchBrand(search, pageable);

        return materialPage.map(MaterialMapper::toMaterialResponse);
    }

    public MaterialResponse getMaterialById(int id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chất liệu có id " + id));
        return MaterialMapper.toMaterialResponse(material);
    }

    @Transactional
    public MaterialResponse createMaterial(MaterialCreateRequest materialCreateRequest) {
        if (materialRepository.existsByMaterialName(materialCreateRequest.getMaterialName())) {
            throw new EntityAlreadyExistsException("Chất liệu có tên " + materialCreateRequest.getMaterialName() + " đã tồn tại");
        }
        Material material = new Material();
        material.setMaterialName(materialCreateRequest.getMaterialName());
        material = materialRepository.save(material);
        return MaterialMapper.toMaterialResponse(material);
    }

    @Transactional
    public MaterialResponse updateMaterial(Integer id, MaterialUpdateRequest materialUpdateRequest) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chất liệu có id " + id));

        if (material.getMaterialName().equalsIgnoreCase(materialUpdateRequest.getMaterialName())
                && materialRepository.existsByMaterialName(materialUpdateRequest.getMaterialName())) {
            throw new EntityAlreadyExistsException("Chất liệu có tên " + materialUpdateRequest.getMaterialName() + " đã tồn tại");
        }

        material.setMaterialName(materialUpdateRequest.getMaterialName());

        material = materialRepository.save(material);

        return MaterialMapper.toMaterialResponse(material);
    }

    @Transactional
    public MaterialResponse toggleMaterialStatus(Integer id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chất liệu có id: " + id));
        material.setStatus(!material.getStatus());
        material = materialRepository.save(material);
        return MaterialMapper.toMaterialResponse(material);
    }



}
