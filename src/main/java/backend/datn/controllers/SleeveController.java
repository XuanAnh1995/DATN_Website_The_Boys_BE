package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.SleeveCreateRequest;
import backend.datn.dto.request.SleeveUpdateRequest;
import backend.datn.dto.response.SleeveResponse;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.services.SleeveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sleeve")
public class SleeveController {

    @Autowired
    private SleeveService sleeveService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllSleeve(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<SleeveResponse> sleeveResponsePage = sleeveService.getAllSleeves(search, page, size, sortBy, sortDir);
            ApiResponse response = new ApiResponse("success", "Lấy được danh sách tay áo thành công", sleeveResponsePage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi truy xuất danh sách tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getSleeveById(@PathVariable int id) {
        try {
            SleeveResponse sleeveResponse = sleeveService.getSleeveById(id);
            ApiResponse response = new ApiResponse("success", "Lấy tay áo theo id thành công", sleeveResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi truy xuất tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createSleeve(@RequestBody SleeveCreateRequest sleeveCreateRequest) {
        try {
            SleeveResponse sleeveResponse = sleeveService.createSleeve(sleeveCreateRequest);
            ApiResponse response = new ApiResponse("success", "Thêm mới tay áo thành công", sleeveResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi thêm mới tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateSleeve(@PathVariable int id, @RequestBody SleeveUpdateRequest sleeveUpdateRequest) {
        try {
            SleeveResponse sleeveResponse = sleeveService.updateSleeve(id, sleeveUpdateRequest);
            ApiResponse response = new ApiResponse("success", "Cập nhật tay áo thành công", sleeveResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(EntityNotFoundException e){
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi cập nhật tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleStatusSleeve(@PathVariable Integer id){
        try {
            SleeveResponse sleeveResponse = sleeveService.toggleSleeveStatus(id);
            ApiResponse response = new ApiResponse("success", "Chuyển đổi trạng thái tay áo thành công", sleeveResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi chuyển đổi trạng thái của tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
