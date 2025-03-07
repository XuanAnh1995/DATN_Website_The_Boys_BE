package backend.datn.services;

import backend.datn.dto.request.VoucherCreateRequest;
import backend.datn.dto.request.VoucherUpdateRequest;
import backend.datn.dto.response.VoucherResponse;
import backend.datn.entities.Voucher;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.mapper.VoucherMapper;
import backend.datn.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoucherService {

    @Autowired
    VoucherRepository voucherRepository;


    public Page<VoucherResponse> getAllVoucher(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        String formattedSearch = (search == null || search.isEmpty()) ? null : "%" + search.toLowerCase() + "%";
        Page<Voucher> voucherPage = voucherRepository.searchVouchers(formattedSearch, pageable);

        return voucherPage.map(VoucherMapper::toVoucherResponse);
    }


    public VoucherResponse createVoucher(VoucherCreateRequest voucherRequest) {
        String voucherCode;
        do {
            voucherCode = generateVoucherCode();
        } while (voucherRepository.existsByVoucherCode(voucherCode));

        Voucher voucher = new Voucher();
        voucher.setVoucherName(voucherRequest.getVoucherName());
        voucher.setVoucherCode(voucherCode);
        voucher.setDescription(voucherRequest.getDescription());
        voucher.setMinCondition(voucherRequest.getMinCondition());
        voucher.setMaxDiscount(voucherRequest.getMaxDiscount());
        voucher.setReducedPercent(voucherRequest.getReducedPercent());

        // 🛠 Chuyển đổi `startDate` & `endDate`
        voucher.setStartDate(parseInstant(voucherRequest.getStartDate()));
        voucher.setEndDate(parseInstant(voucherRequest.getEndDate()));

        voucher.setStatus(voucherRequest.getStatus());

        voucher = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherResponse(voucher);
    }

    // ✅ Cập nhật voucher
    public VoucherResponse updateVoucher(int id, VoucherUpdateRequest voucherUpdateRequest) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại với ID: " + id));

        voucher.setVoucherName(voucherUpdateRequest.getVoucherName());
        voucher.setDescription(voucherUpdateRequest.getDescription());
        voucher.setMinCondition(voucherUpdateRequest.getMinCondition());
        voucher.setMaxDiscount(voucherUpdateRequest.getMaxDiscount());
        voucher.setReducedPercent(voucherUpdateRequest.getReducedPercent());

        // 🛠 Chuyển đổi `startDate` & `endDate`
        voucher.setStartDate(parseInstant(voucherUpdateRequest.getStartDate()));
        voucher.setEndDate(parseInstant(voucherUpdateRequest.getEndDate()));

        Voucher updatedVoucher = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherResponse(updatedVoucher);
    }

    // ✅ Toggle trạng thái voucher
    @Transactional
    public VoucherResponse toggleStatusVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher không có ID: " + id));

        voucher.setStatus(!voucher.getStatus());
        voucher = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherResponse(voucher);
    }

    // ✅ Xóa voucher
    @Transactional
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher không có ID: " + id));

        voucherRepository.delete(voucher);
    }

    // ✅ Tạo mã voucher ngẫu nhiên
    public static String generateVoucherCode() {
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "VOUCHER-" + uuidPart;
    }

    // ✅ Lấy voucher theo ID
    public Optional<Voucher> findById(Integer voucherId) {
        return voucherRepository.findById(voucherId);
    }

    // ✅ Hàm hỗ trợ: Chuyển đổi `String`, `Long` -> `Instant`
    private Instant parseInstant(Object date) {
        try {
            if (date instanceof String) {
                return Instant.parse((String) date); // ✅ Chuỗi ISO 8601
            } else if (date instanceof Long) {
                return Instant.ofEpochMilli((Long) date); // ✅ Timestamp (millisecond)
            } else if (date instanceof Instant) {
                return (Instant) date; // ✅ Nếu đã là Instant thì giữ nguyên
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi: Ngày tháng không đúng định dạng (ISO 8601 hoặc timestamp).");
        }
        return null;
    }
}
