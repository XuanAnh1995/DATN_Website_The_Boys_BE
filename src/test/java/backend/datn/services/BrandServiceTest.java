package backend.datn.services;

import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;
import backend.datn.entities.Product;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.repositories.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand brand;
    private BrandResponse brandResponse;

    @BeforeEach
    public void setUp() {
        brand = new Brand();
        brand.setId(1L);
        brand.setBrandName("Nike");
        brand.setStatus(true);
        brand.setListProducts(Collections.emptyList());

        brandResponse = new BrandResponse();
        brandResponse.setId(1L);
        brandResponse.setBrandName("Nike");
        brandResponse.setStatus(true);
    }

    @Test
    public void testGetAllBrand_Success() {
        Page<Brand> brandPage = new PageImpl<>(List.of(brand));
        when(brandRepository.searchBrand(anyString(), any())).thenReturn(brandPage);

        Page<BrandResponse> result = brandService.getAllBrand("Nike", 0, 10, "id", "asc");

        assertEquals(1, result.getContent().size());
        assertEquals("Nike", result.getContent().get(0).getBrandName());
        verify(brandRepository).searchBrand("Nike", PageRequest.of(0, 10, Sort.by("id").ascending()));
    }

    @Test
    public void testGetBrandById_Success() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        BrandResponse result = brandService.getBrandById(1L);

        assertEquals("Nike", result.getBrandName());
        verify(brandRepository).findById(1L);
    }

    @Test
    public void testGetBrandById_NotFound() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> brandService.getBrandById(1L));
        verify(brandRepository).findById(1L);
    }

    @Test
    public void testCreateBrand_Success() {
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Nike").build();
        when(brandRepository.existsByBrandName("Nike")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
            Brand saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        BrandResponse result = brandService.createBrand(request);

        assertEquals("Nike", result.getBrandName());
        verify(brandRepository).existsByBrandName("Nike");
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    public void testCreateBrand_DuplicateName() {
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Nike").build();
        when(brandRepository.existsByBrandName("Nike")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> brandService.createBrand(request));
        verify(brandRepository).existsByBrandName("Nike");
        verify(brandRepository, never()).save(any());
    }

    @Test
    public void testUpdateBrand_Success() {
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Adidas");
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByBrandName("Adidas")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandResponse result = brandService.updateBrand(1L, request);

        assertNotNull(result);
        verify(brandRepository).findById(1L);
        verify(brandRepository).existsByBrandName("Adidas");
        verify(brandRepository).save(brand);
    }

    @Test
    public void testUpdateBrand_DuplicateName() {
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Adidas");
        brand.setBrandName("Nike");
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByBrandName("Adidas")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> brandService.updateBrand(1L, request));
    }

    @Test
    public void testToggleStatusBrand_Success() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandResponse result = brandService.toggleStatusBrand(1L);

        assertFalse(brand.getStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    public void testToggleStatusBrand_HasProducts() {
        brand.setListProducts(List.of(new Product()));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        assertThrows(IllegalStateException.class, () -> brandService.toggleStatusBrand(1L));
    }

    @Test
    public void testSoftDeleteBrand_Success() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        brandService.softDeleteBrand(1L);

        assertFalse(brand.getStatus());
        verify(brandRepository).save(brand);
    }

    @Test
    public void testSoftDeleteBrand_HasProducts() {
        brand.setListProducts(List.of(new Product()));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        assertThrows(IllegalStateException.class, () -> brandService.softDeleteBrand(1L));
    }

    @Test
    public void testGetProductsWithActiveStatusByBrandId_Success() {
        Product product = new Product();
        product.setStatus(true);
        brand.setListProducts(List.of(product));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        List<Product> result = brandService.getProductsWithActiveStatusByBrandId(1L, true);

        assertEquals(1, result.size());
    }

    @Test
    public void testGetProductCountWithActiveStatusByBrandId_Success() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        long count = brandService.getProductCountWithActiveStatusByBrandId(1L, false);

        assertEquals(0, count);
    }
}