package backend.datn.services;

import backend.datn.dto.request.BrandCreateRequest;
import backend.datn.dto.request.BrandUpdateRequest;
import backend.datn.dto.response.BrandResponse;
import backend.datn.entities.Brand;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.exceptions.InvalidDataException;
import backend.datn.repositories.BrandRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.Set;
import java.util.stream.Stream;

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
    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Initialize common test data
        brand = new Brand();
        brand.setId(1L);
        brand.setBrandName("Nike");
        brand.setStatus(true);
        brand.setListProducts(Collections.emptyList());

        brandResponse = new BrandResponse();
        brandResponse.setId(1L);
        brandResponse.setBrandName("Nike");
        brandResponse.setStatus(true);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // TC01: createBrand - Equivalence Partitioning - Valid input partition
    @Test
    public void testCreateBrand_ValidName_NotExists_Success() {
        // Test case: TC01 - Kiểm tra tạo thương hiệu hợp lệ với tên chưa tồn tại
        // Arrange: Set up input and mock behavior
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Nike").build();
        when(brandRepository.existsByBrandName("Nike")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
            Brand saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act: Call the service method
        BrandResponse result = brandService.createBrand(request);

        // Assert: Verify the result and interactions
        assertEquals("Nike", result.getBrandName());
        verify(brandRepository).existsByBrandName("Nike");
        verify(brandRepository).save(any(Brand.class));
    }

    // TC02: createBrand - Equivalence Partitioning - Invalid partition (duplicate name)
    @Test
    public void testCreateBrand_DuplicateName_ThrowsException() {
        // Test case: TC02 - Kiểm tra tạo thương hiệu với tên đã tồn tại
        // Arrange: Set up input and mock behavior
        BrandCreateRequest request = BrandCreateRequest.builder().brandName("Adidas").build();
        when(brandRepository.existsByBrandName("Adidas")).thenReturn(true);

        // Act & Assert: Verify that the correct exception is thrown
        assertThrows(EntityAlreadyExistsException.class, () -> brandService.createBrand(request));
        verify(brandRepository).existsByBrandName("Adidas");
        verify(brandRepository, never()).save(any());
    }

    // TC03: createBrand - Boundary Value Analysis - Minimum length name
    @Test
    public void testCreateBrand_MinLengthName_Success() {
        // Test case: TC03 - Kiểm tra tạo thương hiệu với độ dài tên tối thiểu (5 ký tự)
        // Arrange: Set up input with minimum valid name length
        String minName = "Brand"; // 5 characters
        BrandCreateRequest request = BrandCreateRequest.builder().brandName(minName).build();
        when(brandRepository.existsByBrandName(minName)).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
            Brand saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act: Call the service method
        BrandResponse result = brandService.createBrand(request);

        // Assert: Verify the result and interactions
        assertEquals(minName, result.getBrandName());
        verify(brandRepository).existsByBrandName(minName);
        verify(brandRepository).save(any(Brand.class));
    }

    // TC04: createBrand - Boundary Value Analysis - Maximum length name
    @Test
    public void testCreateBrand_MaxLengthName_Success() {
        // Test case: TC04 - Kiểm tra tạo thương hiệu với độ dài tên tối đa (100 ký tự)
        // Arrange: Set up input with maximum valid name length
        String maxName = "A".repeat(100);
        BrandCreateRequest request = BrandCreateRequest.builder().brandName(maxName).build();
        when(brandRepository.existsByBrandName(maxName)).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
            Brand saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act: Call the service method
        BrandResponse result = brandService.createBrand(request);

        // Assert: Verify the result and interactions
        assertEquals(maxName, result.getBrandName());
        verify(brandRepository).existsByBrandName(maxName);
        verify(brandRepository).save(any(Brand.class));
    }

    // TC05: createBrand - Boundary Value Analysis - Exceed maximum length name
    @Test
    public void testCreateBrand_ExceededMaxLengthName_ThrowsValidationException() {
        // Test case: TC05 - Kiểm tra tạo thương hiệu với tên vượt quá độ dài tối đa (101 ký tự)
        // Arrange: Set up input with name exceeding maximum length
        String exceededName = "A".repeat(101);
        BrandCreateRequest request = BrandCreateRequest.builder().brandName(exceededName).build();

        // Act: Validate the request
        Set<ConstraintViolation<BrandCreateRequest>> violations = validator.validate(request);

        // Assert: Verify that validation fails
        assertFalse(violations.isEmpty());
    }

    // TC06 & TC07: createBrand - Decision Table
    @ParameterizedTest
    @MethodSource("provideDecisionTableForCreateBrand")
    public void testCreateBrand_DecisionTable(String brandName, boolean exists, Class<? extends Exception> expectedException) {
        // Test case: TC06 & TC07 - Kiểm tra bảng quyết định cho tạo thương hiệu (null/rỗng, hợp lệ, trùng tên)
        // Arrange: Set up input and mock behavior
        BrandCreateRequest request = BrandCreateRequest.builder().brandName(brandName).build();

        if (brandName == null || brandName.isBlank()) {
            // Act: Validate the request
            Set<ConstraintViolation<BrandCreateRequest>> violations = validator.validate(request);
            // Assert: Verify validation failure for null or empty name
            assertFalse(violations.isEmpty());
            return;
        }

        when(brandRepository.existsByBrandName(brandName)).thenReturn(exists);

        if (expectedException != null) {
            // Act & Assert: Verify that the correct exception is thrown
            assertThrows(expectedException, () -> brandService.createBrand(request));
            verify(brandRepository, never()).save(any());
        } else {
            // Arrange: Mock successful save
            when(brandRepository.save(any(Brand.class))).thenAnswer(invocation -> {
                Brand saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });
            // Act: Call the service method
            BrandResponse result = brandService.createBrand(request);
            // Assert: Verify the result
            assertEquals(brandName, result.getBrandName());
            verify(brandRepository).save(any(Brand.class));
        }
    }

    private static Stream<Arguments> provideDecisionTableForCreateBrand() {
        return Stream.of(
                Arguments.of(null, false, InvalidDataException.class), // TC06: Rule 1 - null
                Arguments.of("", false, InvalidDataException.class), // TC06: Rule 1 - empty
                Arguments.of("ValidName", false, null), // TC07: Rule 2 - valid, not exists
                Arguments.of("ValidName", true, EntityAlreadyExistsException.class) // Additional: valid but exists
        );
    }

    // TC08: updateBrand - Equivalence Partitioning - Valid input partition
    @Test
    public void testUpdateBrand_ValidIdAndName_Success() {
        // Test case: TC08 - Kiểm tra cập nhật thương hiệu hợp lệ với id tồn tại
        // Arrange: Set up input and mock behavior
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "Puma");
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByBrandName("Puma")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        // Act: Call the service method
        BrandResponse result = brandService.updateBrand(1L, request);

        // Assert: Verify the result and interactions
        assertNotNull(result);
        verify(brandRepository).findById(1L);
        verify(brandRepository).existsByBrandName("Puma");
        verify(brandRepository).save(any(Brand.class));
    }

    // TC09: updateBrand - Equivalence Partitioning - Invalid partition (id not exists)
    @Test
    public void testUpdateBrand_IdNotExists_ThrowsException() {
        // Test case: TC09 - Kiểm tra cập nhật thương hiệu với id không tồn tại
        // Arrange: Set up input and mock behavior
        BrandUpdateRequest request = new BrandUpdateRequest(9999L, "Puma");
        when(brandRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert: Verify that the correct exception is thrown
        assertThrows(EntityNotFoundException.class, () -> brandService.updateBrand(9999L, request));
        verify(brandRepository).findById(9999L);
        verify(brandRepository, never()).save(any());
    }

    // TC10: updateBrand - Boundary Value Analysis - Empty name
    @Test
    public void testUpdateBrand_EmptyName_ThrowsValidationException() {
        // Test case: TC10 - Kiểm tra cập nhật thương hiệu với tên rỗng
        // Arrange: Set up input with empty name
        BrandUpdateRequest request = new BrandUpdateRequest(1L, "");

        // Act: Validate the request
        Set<ConstraintViolation<BrandUpdateRequest>> violations = validator.validate(request);

        // Assert: Verify that validation fails
        assertFalse(violations.isEmpty());
    }

    // TC11: updateBrand - Decision Table
    @ParameterizedTest
    @MethodSource("provideDecisionTableForUpdateBrand")
    public void testUpdateBrand_DecisionTable(Long id, String brandName, boolean idExists, boolean nameExists, Class<? extends Exception> expectedException) {
        // Test case: TC11 - Kiểm tra bảng quyết định cho cập nhật thương hiệu
        // Arrange: Set up input and mock behavior
        BrandUpdateRequest request = new BrandUpdateRequest(id, brandName);

        if (id == null || id <= 0) {
            // Act & Assert: Verify InvalidDataException for invalid id
            assertThrows(InvalidDataException.class, () -> brandService.updateBrand(id, request));
            return;
        }

        if (!idExists) {
            when(brandRepository.findById(id)).thenReturn(Optional.empty());
            // Act & Assert: Verify EntityNotFoundException for non-existent id
            assertThrows(EntityNotFoundException.class, () -> brandService.updateBrand(id, request));
            return;
        }

        when(brandRepository.findById(id)).thenReturn(Optional.of(brand));
        when(brandRepository.existsByBrandName(brandName)).thenReturn(nameExists);

        if (expectedException != null) {
            // Act & Assert: Verify that the correct exception is thrown
            assertThrows(expectedException, () -> brandService.updateBrand(id, request));
            verify(brandRepository, never()).save(any());
        } else {
            // Arrange: Mock successful save
            when(brandRepository.save(any(Brand.class))).thenReturn(brand);
            // Act: Call the service method
            BrandResponse result = brandService.updateBrand(id, request);
            // Assert: Verify the result
            assertNotNull(result);
            verify(brandRepository).save(any(Brand.class));
        }
    }

    private static Stream<Arguments> provideDecisionTableForUpdateBrand() {
        return Stream.of(
                Arguments.of(1L, "Puma", true, false, null), // TC11: Rule 1 - id exists, name valid
                Arguments.of(1L, "Nike", false, false, EntityNotFoundException.class) // TC11: Rule 2 - id not exists
        );
    }

    // TC12: deleteBrand (softDeleteBrand) - Equivalence Partitioning - Valid partition
    @Test
    public void testSoftDeleteBrand_ValidId_Success() {
        // Test case: TC12 - Kiểm tra xóa mềm thương hiệu hợp lệ với id tồn tại
        // Arrange: Set up mock behavior
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        // Act: Call the service method
        brandService.softDeleteBrand(1L);

        // Assert: Verify the brand status and interactions
        assertFalse(brand.getStatus());
        verify(brandRepository).save(brand);
    }

    // TC13: deleteBrand (softDeleteBrand) - Equivalence Partitioning - Invalid partition
    @Test
    public void testSoftDeleteBrand_IdNotExists_ThrowsException() {
        // Test case: TC13 - Kiểm tra xóa mềm thương hiệu với id không tồn tại
        // Arrange: Set up mock behavior
        when(brandRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert: Verify that the correct exception is thrown
        assertThrows(EntityNotFoundException.class, () -> brandService.softDeleteBrand(999L));
        verify(brandRepository).findById(999L);
        verify(brandRepository, never()).save(any());
    }

    // TC14: deleteBrand (softDeleteBrand) - Decision Table
    @ParameterizedTest
    @MethodSource("provideDecisionTableForSoftDeleteBrand")
    public void testSoftDeleteBrand_DecisionTable(boolean idExists, Class<? extends Exception> expectedException) {
        // Test case: TC14 - Kiểm tra bảng quyết định cho xóa mềm thương hiệu
        // Arrange: Set up mock behavior
        if (!idExists) {
            when(brandRepository.findById(1L)).thenReturn(Optional.empty());
            // Act & Assert: Verify EntityNotFoundException for non-existent id
            assertThrows(EntityNotFoundException.class, () -> brandService.softDeleteBrand(1L));
            verify(brandRepository, never()).save(any());
        } else {
            when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
            when(brandRepository.save(any(Brand.class))).thenReturn(brand);
            // Act: Call the service method
            brandService.softDeleteBrand(1L);
            // Assert: Verify the brand status
            assertFalse(brand.getStatus());
            verify(brandRepository).save(brand);
        }
    }

    private static Stream<Arguments> provideDecisionTableForSoftDeleteBrand() {
        return Stream.of(
                Arguments.of(true, null), // TC14: Rule 1 - id exists -> delete
                Arguments.of(false, EntityNotFoundException.class) // TC14: Rule 2 - id not exists -> throw
        );
    }

    // TC15: getBrandById - Equivalence Partitioning - Valid partition
    @Test
    public void testGetBrandById_ValidId_Success() {
        // Test case: TC15 - Kiểm tra lấy thương hiệu hợp lệ theo ID
        // Arrange: Set up mock behavior
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        // Act: Call the service method
        BrandResponse result = brandService.getBrandById(1L);

        // Assert: Verify the result and interactions
        assertEquals("Nike", result.getBrandName());
        verify(brandRepository).findById(1L);
    }

    // TC16: getBrandById - Equivalence Partitioning - Invalid partition
    @Test
    public void testGetBrandById_IdNotExists_ThrowsException() {
        // Test case: TC16 - Kiểm tra lấy thương hiệu với ID không tồn tại
        // Arrange: Set up mock behavior
        when(brandRepository.findById(1000L)).thenReturn(Optional.empty());

        // Act & Assert: Verify that the correct exception is thrown
        assertThrows(EntityNotFoundException.class, () -> brandService.getBrandById(1000L));
        verify(brandRepository).findById(1000L);
    }

    // TC17: getAllBrands (getAllBrand) - Equivalence Partitioning - Valid partition (has data)
    @Test
    public void testGetAllBrand_HasData_ReturnsList() {
        // Test case: TC17 - Kiểm tra truy xuất tất cả thương hiệu khi có dữ liệu
        // Arrange: Set up mock data with 3 brands
        Brand brand2 = new Brand(2L, "Adidas", true, Collections.emptyList());
        Brand brand3 = new Brand(3L, "Puma", true, Collections.emptyList());
        Page<Brand> brandPage = new PageImpl<>(List.of(brand, brand2, brand3));
        when(brandRepository.searchBrand(anyString(), any())).thenReturn(brandPage);

        // Act: Call the service method
        Page<BrandResponse> result = brandService.getAllBrand("", 0, 10, "id", "asc");

        // Assert: Verify the result and interactions
        assertEquals(3, result.getContent().size());
        verify(brandRepository).searchBrand("", PageRequest.of(0, 10, Sort.by("id").ascending()));
    }

    // TC18: getAllBrands (getAllBrand) - Equivalence Partitioning - Invalid partition (empty data)
    @Test
    public void testGetAllBrand_EmptyData_ReturnsEmptyList() {
        // Test case: TC18 - Kiểm tra truy xuất tất cả thương hiệu khi không có dữ liệu
        // Arrange: Set up mock data with empty list
        Page<Brand> brandPage = new PageImpl<>(Collections.emptyList());
        when(brandRepository.searchBrand(anyString(), any())).thenReturn(brandPage);

        // Act: Call the service method
        Page<BrandResponse> result = brandService.getAllBrand("", 0, 10, "id", "asc");

        // Assert: Verify the result and interactions
        assertTrue(result.getContent().isEmpty());
        verify(brandRepository).searchBrand("", PageRequest.of(0, 10, Sort.by("id").ascending()));
    }
}