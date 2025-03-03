package backend.datn.services;


import backend.datn.dto.request.PromotionCreateRequest;
import backend.datn.dto.request.PromotionUpdateRequest;
import backend.datn.dto.response.PromotionResponse;
import backend.datn.entities.Promotion;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.PromotionMapper;
import backend.datn.repositories.PromotionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;
    public Page<PromotionResponse> getAllPromotion(String search, LocalDate startDate, LocalDate endDate,
                                                   int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Promotion> promotions = promotionRepository.searchPromotions(search, startDate, endDate, pageable);


        return promotions.map(PromotionMapper::toPromotionResponse);
    }

    public PromotionResponse getPromotionById(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion không tồn tại với ID: " + id));
        return PromotionMapper.toPromotionResponse(promotion);
    }
    public PromotionResponse createPromotion(PromotionCreateRequest promotionCreateRequest) {
        if (promotionCreateRequest.getStartDate().isAfter(promotionCreateRequest.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }
        if (promotionCreateRequest.getPromotionPercent() < 0 || promotionCreateRequest.getPromotionPercent() > 100) {
            throw new IllegalArgumentException("Phần trăm khuyến mãi phải từ 0 đến 100!");
        }

        Promotion promotion = new Promotion();
        promotion.setPromotionName(promotionCreateRequest.getPromotionName());
        promotion.setPromotionPercent(promotionCreateRequest.getPromotionPercent());
        promotion.setStartDate(promotionCreateRequest.getStartDate());
        promotion.setEndDate(promotionCreateRequest.getEndDate());
        promotion.setDescription(promotionCreateRequest.getDescription());
        promotion.setStatus(promotionCreateRequest.getStatus());

        promotion = promotionRepository.save(promotion);

        return PromotionMapper.toPromotionResponse(promotion);
    }

    public PromotionResponse updatePromotion(PromotionUpdateRequest updateRequest, Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion không tồn tại với ID: " + id));

        if (updateRequest.getStartDate().isAfter(updateRequest.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }

        if (updateRequest.getPromotionPercent() < 0 || updateRequest.getPromotionPercent() > 100) {
            throw new IllegalArgumentException("Phần trăm khuyến mãi phải từ 0 đến 100!");
        }

        promotion.setPromotionName(updateRequest.getPromotionName());
        promotion.setPromotionPercent(updateRequest.getPromotionPercent());
        promotion.setStartDate(updateRequest.getStartDate());
        promotion.setEndDate(updateRequest.getEndDate());
        promotion.setDescription(updateRequest.getDescription());
        promotion.setStatus(updateRequest.getStatus());

        promotion = promotionRepository.save(promotion);

        return PromotionMapper.toPromotionResponse(promotion);
    }

    @Transactional
    public void deletePromotion(Integer id) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Promotion không tồn tại với ID: " + id));
        promotionRepository.delete(promotion);
    }
    @Transactional
    public PromotionResponse toggleStatusPromotionResponse(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion khong co id: " + id));
        promotion.setStatus(!promotion.getStatus());
        Promotion newPromotion = promotionRepository.save(promotion);
        return PromotionMapper.toPromotionResponse(newPromotion);
    }
}
