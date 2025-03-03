package backend.datn.services;

import backend.datn.dto.request.SizeCreateRequest;
import backend.datn.dto.request.SizeUpdateRequest;
import backend.datn.dto.response.SizeResponse;
import backend.datn.entities.Size;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.SizeMapper;
import backend.datn.repositories.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SizeService {
    @Autowired
    SizeRepository sizeRepository;

    public Page<SizeResponse> getAllSizes(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Size> sizes = sizeRepository.searchSizes(search, pageable);

        return sizes.map(SizeMapper::toSizeResponse);
    }

    public SizeResponse getSizeById(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kích thước có id: " + id));
        return SizeMapper.toSizeResponse(size);
    }

    @Transactional
    public SizeResponse createSize(SizeCreateRequest sizeCreateRequest) {
        if (sizeRepository.existsBySizeName(sizeCreateRequest.getName())) {
            throw new ResourceNotFoundException("Kích thước có tên: " + sizeCreateRequest.getName() + " đã tồn tại");
        }
        Size size = new Size();
        size.setSizeName(sizeCreateRequest.getName());
        size = sizeRepository.save(size);
        return SizeMapper.toSizeResponse(size);
    }

    @Transactional
    public SizeResponse updateSize(Integer id, SizeUpdateRequest sizeUpdateRequest) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kích thước có id: " + id));

        if(size.getSizeName().equalsIgnoreCase(sizeUpdateRequest.getName()) && sizeRepository.existsBySizeName(sizeUpdateRequest.getName())) {
            throw new EntityAlreadyExistsException("Kích thước có tên: " + sizeUpdateRequest.getName() + " đã tồn tại");
        }
        size.setSizeName(sizeUpdateRequest.getName());
        size = sizeRepository.save(size);
        return SizeMapper.toSizeResponse(size);
    }

    @Transactional
    public void deleteSize(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kích thước có id: " + id));
        sizeRepository.delete(size);
    }

    @Transactional
    public SizeResponse toggleSizeStatus(Integer id) {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy kích thước có id: " + id));
        size.setStatus(!size.getStatus());
        size = sizeRepository.save(size);
        return SizeMapper.toSizeResponse(size);
    }
}
