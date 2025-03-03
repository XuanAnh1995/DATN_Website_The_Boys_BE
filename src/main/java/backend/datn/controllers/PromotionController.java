package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.PromotionCreateRequest;
import backend.datn.dto.request.PromotionUpdateRequest;
import backend.datn.dto.response.PromotionResponse;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.services.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPromotion(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Page<PromotionResponse> promotionResponses = promotionService.getAllPromotion(search, start, end, page, size, sortBy, sortDir);
            ApiResponse response = new ApiResponse("success", "Lấy danh sách promotion thành công", promotionResponses);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "An error occurred while getting the promotion list", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPromotion(@Valid  @RequestBody PromotionCreateRequest promotionCreateRequest) {
        try {
            PromotionResponse promotionResponse = promotionService.createPromotion(promotionCreateRequest);
            ApiResponse response = new ApiResponse("success", "Tạo promotion thành công", promotionResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "An error occurred while creating the brand", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePromotion(@RequestBody PromotionUpdateRequest updateRequest, @PathVariable Integer id) {
        try {
            PromotionResponse promotionResponse = promotionService.updatePromotion(updateRequest, id);
            ApiResponse response = new ApiResponse("success", "Cập nhật promotion thành công", promotionResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "An error occurred while updating the brand", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePromotion(@PathVariable Integer id) {
        try {
            promotionService.deletePromotion(id);
            ApiResponse response = new ApiResponse("success", "Xóa promotion thành công", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "An error occurred while deleting the brand", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleStatus(@PathVariable Integer id) {
        try {
            PromotionResponse promotionResponse = promotionService.toggleStatusPromotionResponse(id);
            ApiResponse response = new ApiResponse("success", "Cập nhật trạng thái promotion thành công", promotionResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "An error occurred while updating the brand", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
