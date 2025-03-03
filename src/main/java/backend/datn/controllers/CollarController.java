package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.CollarCreateRequest;
import backend.datn.dto.request.CollarUpdateRequest;
import backend.datn.dto.response.CollarResponse;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.services.CollarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collars")
public class CollarController {
    @Autowired
    private CollarService collarService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCollarById(@PathVariable Integer id) {
        try {
            CollarResponse collarResponse = collarService.getCollarById(id);
            ApiResponse response = new ApiResponse("success", "Lấy tay áo theo id thành công", collarResponse);
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
    public ResponseEntity<ApiResponse> createCollar(@RequestBody CollarCreateRequest collarCreateRequest) {
        try {
            CollarResponse collarResponse = collarService.createCollar(collarCreateRequest);
            ApiResponse response = new ApiResponse("success", "Thêm mới tay áo thành công", collarResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi thêm mới tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCollars(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<CollarResponse> collars = collarService.getAllCollars(search, page, size, sortBy, sortDir);
            ApiResponse response = new ApiResponse("success", "Lấy được danh sách tay áo thành công", collars);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi truy xuất danh sách tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCollar(@PathVariable Integer id, @RequestBody CollarUpdateRequest collarUpdateRequest) {
        try {
            CollarResponse collarResponse = collarService.updateCollar(id, collarUpdateRequest);
            ApiResponse response = new ApiResponse("success", "Cập nhật tay áo thành công", collarResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi cập nhật tay áo", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCollar(@PathVariable Integer id) {
        try {
            collarService.deleteCollar(id);
            ApiResponse response = new ApiResponse("success", "Collar deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "An error occurred while deleting the collar", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleStatusCollar(@PathVariable Integer id){
        try {
            CollarResponse collarResponse = collarService.toggleCollarStatus(id);
            ApiResponse response = new ApiResponse("success", "Chuyển đổi trạng thái tay áo thành công", collarResponse);
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
