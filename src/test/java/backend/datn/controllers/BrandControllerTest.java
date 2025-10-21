package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.dto.response.PagedResponse;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.services.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;
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

    @Test
    public void testGetAllBrand_Success() throws Exception {
        // Arrange
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike").status(true).build();
        Page<BrandResponse> page = new PageImpl<>(List.of(response));
        when(brandService.getAllBrand(any(), anyInt(), anyInt(), any(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/brand")
                        .param("search", "Nike")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content[0].brandName").value("Nike"));
    }

    @Test
    public void testGetBrandById_Success() throws Exception {
        // Arrange
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike").status(true).build();
        when(brandService.getBrandById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.brandName").value("Nike"));
    }

    @Test
    public void testCreateBrand_Success() throws Exception {
        // Arrange
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Nike").build();
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike").status(true).build();
        when(brandService.createBrand(any(BrandCreateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.brandName").value("Nike"));
    }

    @Test
    public void testCreateBrand_Duplicate() throws Exception {
        // Arrange
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Nike").build();
        when(brandService.createBrand(any())).thenThrow(new EntityAlreadyExistsException("Duplicate"));

        // Act & Assert
        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    public void testUpdateBrand_Success() throws Exception {
        // Arrange
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Adidas");
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Adidas").status(true).build();
        when(brandService.updateBrand(eq(1L), any(BrandUpdateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/brand/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.brandName").value("Adidas"));
    }

    @Test
    public void testToggleStatusBrand_Success() throws Exception {
        // Arrange
        BrandResponse response = BrandResponse.builder().id(1L).brandName("Nike").status(false).build();
        when(brandService.toggleStatusBrand(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/brand/1/toggle-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.status").value(false));
    }

    @Test
    public void testSoftDeleteBrand_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
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
}