package backend.datn.services;

import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.repositories.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    public void testCreateBrand_Success() {
        BrandCreateRequest request = new BrandCreateRequest("New Brand");
        Brand savedBrand = new Brand();
        savedBrand.setId(1);
        savedBrand.setBrandName("New Brand");
        savedBrand.setStatus(true);

        when(brandRepository.existsByBrandName("New Brand")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);

        BrandResponse response = brandService.createBrand(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("New Brand", response.getBrandName());
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    public void testCreateBrand_AlreadyExists() {
        BrandCreateRequest request = new BrandCreateRequest("Existing Brand");
        when(brandRepository.existsByBrandName("Existing Brand")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> {
            brandService.createBrand(request);
        });
        verify(brandRepository, never()).save(any(Brand.class));
    }
}
