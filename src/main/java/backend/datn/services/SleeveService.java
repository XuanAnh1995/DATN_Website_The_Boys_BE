package backend.datn.services;

import backend.datn.dto.request.SleeveCreateRequest;
import backend.datn.dto.request.SleeveUpdateRequest;
import backend.datn.dto.response.SleeveResponse;
import backend.datn.entities.Sleeve;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.SleeveMapper;
import backend.datn.repositories.SleeveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SleeveService {

    @Autowired
    private SleeveRepository sleeveRepository;

    public Page<SleeveResponse> getAllSleeves(String search, int page, int size, String sortBy, String sortDri) {

        Sort sort = sortDri.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Sleeve> sleevePage = sleeveRepository.searchSleeve(search, pageable);

        return sleevePage.map(SleeveMapper::toSleeveResponse);
    }

    public SleeveResponse getSleeveById(Integer id){
        Sleeve sleeve = sleeveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tay áo có id : " + id));
        return SleeveMapper.toSleeveResponse(sleeve);
    }

    @Transactional
    public SleeveResponse createSleeve(SleeveCreateRequest sleeveCreateRequest){
        if(sleeveRepository.existsBySleeveName(sleeveCreateRequest.getSleeveName())){
            throw  new EntityAlreadyExistsException("Tay áo có tên " + sleeveCreateRequest.getSleeveName() + " đã tồn tại");
        }

        Sleeve sleeve = new Sleeve();
        sleeve.setSleeveName(sleeveCreateRequest.getSleeveName());

        sleeve = sleeveRepository.save(sleeve);
        return SleeveMapper.toSleeveResponse(sleeve);
    }

    @Transactional
    public SleeveResponse updateSleeve(Integer id, SleeveUpdateRequest sleeveUpdateRequest){
        Sleeve sleeve = sleeveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tay áo có id: " + id));

        if (sleeve.getSleeveName().equalsIgnoreCase(sleeveUpdateRequest.getSleeveName()) && sleeveRepository.existsBySleeveName(sleeveUpdateRequest.getSleeveName())){
            throw new EntityAlreadyExistsException("Tay áo có tên: " + sleeveUpdateRequest.getSleeveName() + " đã tồn tại");
        }

        sleeve.setSleeveName(sleeveUpdateRequest.getSleeveName());

        sleeve = sleeveRepository.save(sleeve);
        return SleeveMapper.toSleeveResponse(sleeve);
    }

    @Transactional
    public SleeveResponse toggleSleeveStatus(Integer id){
        Sleeve sleeve = sleeveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tay áo có id: " + id));

        sleeve.setStatus(!sleeve.getStatus());
        sleeve = sleeveRepository.save(sleeve);
        return SleeveMapper.toSleeveResponse(sleeve);
    }


}
