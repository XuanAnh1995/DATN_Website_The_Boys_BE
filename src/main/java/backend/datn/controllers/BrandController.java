package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.dto.response.PagedResponse;
import backend.datn.dto.response.ProductResponse;
import backend.datn.entities.Product;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.ProductMapper;
import backend.datn.services.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Brand Controller", description = "Quản lý thương hiệu trong hệ thống ")
@RestController
@RequestMapping("/api/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Operation(summary = "Lấy danh sách thương hiệu", description = "Trả về danh sách thương hiệu có hỗ trợ tìm kiếm, phân trang, sắp xếp")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBrand(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<BrandResponse> brandPage = brandService.getAllBrand(search, page, size, sortBy, sortDir);

            // Bọc dữ liệu vào PagedResponse
            PagedResponse<BrandResponse> responseData = new PagedResponse<>(brandPage);

            ApiResponse response = new ApiResponse("success", "Lấy được danh sách thương hiệu thành công", responseData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi truy xuất danh sách thương hiệu", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy thương hiệu theo ID", description = "Trả về thông tin chi tiết thương hiệu theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBrandById(@PathVariable Long id) {
        try {
            BrandResponse brandResponse = brandService.getBrandById(id);
            ApiResponse response = new ApiResponse("success", "Lấy thương hiệu theo id thành công", brandResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi truy xuất thương hiệu", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Tạo mới thương hiệu")
    @PostMapping
    public ResponseEntity<ApiResponse> createBrand(@Valid @RequestBody BrandCreateRequest brandCreateRequest) {
        try {
            BrandResponse brandResponse = brandService.createBrand(brandCreateRequest);
            ApiResponse response = new ApiResponse("success", "Thêm mới thương hiệu thành công", brandResponse);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi thêm mới thương hiệu", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Cập nhật thương hiệu")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBrand(@PathVariable Long id,@Valid @RequestBody BrandUpdateRequest brandUpdateRequest) {
        try {
            BrandResponse brandResponse = brandService.updateBrand(id, brandUpdateRequest);
            ApiResponse response = new ApiResponse("success", "Cập nhật thương hiệu thành công", brandResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(EntityNotFoundException e){
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi cập nhật thương hiệu", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Chuyển đổi trạng thái thương hiệu")
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleStatusBrand(@PathVariable Long id) {
        try {
            BrandResponse brandResponse = brandService.toggleStatusBrand(id);
            ApiResponse response = new ApiResponse("success", "Chuyển đổi trạng thái thương hiệu thành công", brandResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi chuyển đổi trạng thái của thương hiệu", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Xóa mềm thương hiệu")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> softDeleteBrand(@PathVariable Long id) {
        try {
            brandService.softDeleteBrand(id);
            ApiResponse response = new ApiResponse("success", "Xóa mềm thương hiệu thành công", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException | IllegalStateException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi xóa mềm thương hiệu", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy danh sách sản phẩm theo thương hiệu")
    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse> getProductsByBrandId(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean onlyActive) {
        try {
            List<Product> products = brandService.getProductsWithActiveStatusByBrandId(id, onlyActive);
            List<ProductResponse> productResponses = products.stream()
                    .map(ProductMapper::toProductResponse)
                    .collect(Collectors.toList());
            ApiResponse response = new ApiResponse("success", "Lấy danh sách sản phẩm thành công", productResponses);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException | EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi lấy danh sách sản phẩm", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lấy tổng số lượng sản phẩm của thương hiệu")
    @GetMapping("/{id}/product-count")
    public ResponseEntity<ApiResponse> getProductCountByBrandId(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean onlyActive) {
        try {
            long productCount = brandService.getProductCountWithActiveStatusByBrandId(id, onlyActive);
            ApiResponse response = new ApiResponse("success", "Lấy số lượng sản phẩm thành công", productCount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException | EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi lấy số lượng sản phẩm", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
