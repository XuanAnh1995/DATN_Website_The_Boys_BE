package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.dto.response.PagedResponse;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.services.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(brandController).build();
        objectMapper = new ObjectMapper();
    }

    // region ===== GET ALL BRANDS =====
    @Test
    @DisplayName("Get all brands with valid parameters should return success")
    public void testGetAllBrand_Success() throws Exception {
        // Arrange
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike Inc").status(true).build();
        Page<BrandResponse> page = new PageImpl<>(List.of(response));
        when(brandService.getAllBrand(anyString(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/brand")
                        .param("search", "Nike")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content[0].brandName").value("Nike Inc"));
    }

    @Test
    @DisplayName("Get all brands with exception should return internal server error")
    public void testGetAllBrand_Failure() throws Exception {
        // Arrange
        when(brandService.getAllBrand(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/brand")
                        .param("search", "Nike")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Đã xảy ra lỗi khi truy xuất danh sách thương hiệu"));
    }
    // endregion

    // region ===== GET BRAND BY ID =====
    @Test
    @DisplayName("Get brand by ID with valid ID should return success")
    public void testGetBrandById_Success() throws Exception {
        // Arrange
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike Inc").status(true).build();
        when(brandService.getBrandById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.brandName").value("Nike Inc"));
    }

    @Test
    @DisplayName("Get brand by ID with non-existing ID should return not found")
    public void testGetBrandById_NotFound() throws Exception {
        // Arrange
        when(brandService.getBrandById(1L)).thenThrow(new EntityNotFoundException("Brand not found"));

        // Act & Assert
        mockMvc.perform(get("/api/brand/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand not found"));
    }
    // endregion

    // region ===== CREATE BRAND =====
    @Test
    @DisplayName("Create brand with valid request should return success")
    public void testCreateBrand_Success() throws Exception {
        // Arrange
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Nike Inc").build();
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike Inc").status(true).build();
        when(brandService.createBrand(any(BrandCreateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.brandName").value("Nike Inc"));
    }

    @Test
    @DisplayName("Create brand with duplicate name should return conflict")
    public void testCreateBrand_Duplicate() throws Exception {
        // Arrange
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Nike Inc").build();
        when(brandService.createBrand(any(BrandCreateRequest.class)))
                .thenThrow(new EntityAlreadyExistsException("Brand already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand already exists"));
    }

//    @Test
//    @DisplayName("Create brand with invalid name (too short) should return bad request")
//    public void testUpdateBrand_InvalidName_Short() throws Exception {
//        // Arrange
//        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Adid");
//
//        // Act & Assert
//        mockMvc.perform(put("/api/brand/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value("error"))
//                .andExpect(jsonPath("$.message").value("Tên thương hiệu phải có độ dài từ 5 đến 100 kí tự"));
//    }
//
//    @Test
//    @DisplayName("Create brand with invalid name (too long) should return bad request")
//    public void testUpdateBrand_InvalidName_Long() throws Exception {
//        // Arrange
//        BrandUpdateRequest request = new BrandUpdateRequest(1L, "A".repeat(101));
//
//        // Act & Assert
//        mockMvc.perform(put("/api/brand/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value("error"))
//                .andExpect(jsonPath("$.message").value("Tên thương hiệu phải có độ dài từ 5 đến 100 kí tự"));
//    }

    // endregion

    // region ===== UPDATE BRAND =====
    @Test
    @DisplayName("Update brand with valid request should return success")
    public void testUpdateBrand_Success() throws Exception {
        // Arrange
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Adidas Inc");
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Adidas Inc").status(true).build();
        when(brandService.updateBrand(eq(1L), any(BrandUpdateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/brand/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.brandName").value("Adidas Inc"));
    }

    @Test
    @DisplayName("Update brand with non-existing ID should return not found")
    public void testUpdateBrand_NotFound() throws Exception {
        // Arrange
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Adidas Inc");
        when(brandService.updateBrand(eq(1L), any(BrandUpdateRequest.class)))
                .thenThrow(new EntityNotFoundException("Brand not found"));

        // Act & Assert
        mockMvc.perform(put("/api/brand/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand not found"));
    }

    @Test
    @DisplayName("Update brand with duplicate name should return conflict")
    public void testUpdateBrand_Duplicate() throws Exception {
        // Arrange
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Adidas Inc");
        when(brandService.updateBrand(eq(1L), any(BrandUpdateRequest.class)))
                .thenThrow(new EntityAlreadyExistsException("Brand already exists"));

        // Act & Assert
        mockMvc.perform(put("/api/brand/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand already exists"));
    }
    // endregion

    // region ===== TOGGLE STATUS =====
    @Test
    @DisplayName("Toggle brand status with valid ID should return success")
    public void testToggleStatusBrand_Success() throws Exception {
        // Arrange
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike Inc").status(false).build();
        when(brandService.toggleStatusBrand(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/brand/1/toggle-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.status").value(false));
    }

    @Test
    @DisplayName("Toggle brand status with non-existing ID should return not found")
    public void testToggleStatusBrand_NotFound() throws Exception {
        // Arrange
        when(brandService.toggleStatusBrand(1L))
                .thenThrow(new ResourceNotFoundException("Brand not found"));

        // Act & Assert
        mockMvc.perform(put("/api/brand/1/toggle-status"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand not found"));
    }

    @Test
    @DisplayName("Toggle brand status with invalid state should return bad request")
    public void testToggleStatusBrand_InvalidState() throws Exception {
        // Arrange
        when(brandService.toggleStatusBrand(1L))
                .thenThrow(new IllegalStateException("Cannot toggle status"));

        // Act & Assert
        mockMvc.perform(put("/api/brand/1/toggle-status"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Cannot toggle status"));
    }
    // endregion

    // region ===== SOFT DELETE BRAND =====
    @Test
    @DisplayName("Soft delete brand with valid ID should return success")
    public void testSoftDeleteBrand_Success() throws Exception {
        // Arrange
        doNothing().when(brandService).softDeleteBrand(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Xóa mềm thương hiệu thành công"));
    }

    @Test
    @DisplayName("Soft delete brand with non-existing ID should return bad request")
    public void testSoftDeleteBrand_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Brand not found")).when(brandService).softDeleteBrand(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/brand/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand not found"));
    }
    // endregion

    // region ===== GET PRODUCTS BY BRAND ID =====
    @Test
    @DisplayName("Get products by brand ID with valid ID should return success")
    public void testGetProductsByBrandId_Success() throws Exception {
        // Arrange
        when(brandService.getProductsWithActiveStatusByBrandId(1L, false)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/brand/1/products")
                        .param("onlyActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Get products by brand ID with non-existing ID should return not found")
    public void testGetProductsByBrandId_NotFound() throws Exception {
        // Arrange
        when(brandService.getProductsWithActiveStatusByBrandId(1L, false))
                .thenThrow(new EntityNotFoundException("Brand not found"));

        // Act & Assert
        mockMvc.perform(get("/api/brand/1/products")
                        .param("onlyActive", "false"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand not found"));
    }
    // endregion

    // region ===== GET PRODUCT COUNT BY BRAND ID =====
    @Test
    @DisplayName("Get product count by brand ID with valid ID should return success")
    public void testGetProductCountByBrandId_Success() throws Exception {
        // Arrange
        when(brandService.getProductCountWithActiveStatusByBrandId(1L, false)).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/brand/1/product-count")
                        .param("onlyActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    @DisplayName("Get product count by brand ID with non-existing ID should return not found")
    public void testGetProductCountByBrandId_NotFound() throws Exception {
        // Arrange
        when(brandService.getProductCountWithActiveStatusByBrandId(1L, false))
                .thenThrow(new EntityNotFoundException("Brand not found"));

        // Act & Assert
        mockMvc.perform(get("/api/brand/1/product-count")
                        .param("onlyActive", "false"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Brand not found"));
    }
    // endregion
}