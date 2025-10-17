package backend.datn.services;

import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.mapper.BrandMapper;
import backend.datn.repositories.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {
    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandMapper brandMapper;

    @InjectMocks
    private BrandService brandService;

    private Brand brand;
    private BrandResponse brandResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        brand = new Brand();
        brand.setBrandName("1");

    }
}
