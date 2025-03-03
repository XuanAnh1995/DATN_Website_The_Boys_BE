package backend.datn.services;


import backend.datn.dto.request.VoucherCreateRequest;
import backend.datn.dto.request.VoucherUpdateRequest;
import backend.datn.dto.response.VoucherResponse;
import backend.datn.entities.Voucher;
import backend.datn.exceptions.ResourceNotFoundException;
import backend.datn.helpers.CodeGeneratorHelper;
import backend.datn.mapper.VoucherMapper;
import backend.datn.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class VoucherService {
    @Autowired
    VoucherRepository voucherRepository;

    public Page<VoucherResponse> getAllVoucher(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page , size, sort);
        Page<Voucher> voucherPage = voucherRepository.searchVouchers(search, pageable);
        return voucherPage.map(VoucherMapper::toVoucherRespone);
    }
    public VoucherResponse getVoucherById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại với ID: " + id));
        return VoucherMapper.toVoucherRespone(voucher);
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
        voucher.setStartDate(voucherRequest.getStartDate());
        voucher.setEndDate(voucherRequest.getEndDate());
        voucher.setStatus(voucherRequest.getStatus());

        voucher = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherRespone(voucher);
    }

    public VoucherResponse updateVoucher(VoucherUpdateRequest voucherUpdateRequest, Integer id) {
        Voucher voucher= voucherRepository.findById(id).orElseThrow(() -> new RuntimeException("Voucher không tồn tại với ID: " + id));
        voucher.setVoucherName(voucherUpdateRequest.getVoucherName());
        voucher.setDescription(voucherUpdateRequest.getDescription());
        voucher.setMinCondition(voucherUpdateRequest.getMinCondition());
        voucher.setMaxDiscount(voucherUpdateRequest.getMaxDiscount());
        voucher.setReducedPercent(voucherUpdateRequest.getReducedPercent());
        voucher.setStartDate(voucherUpdateRequest.getStartDate());
        voucher.setEndDate(voucherUpdateRequest.getEndDate());
        voucher.setStatus(voucherUpdateRequest.getStatus());
        Voucher newVoucher = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherRespone(newVoucher);
    }
    @Transactional
    public VoucherResponse toggleStatusVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher khong co id: " + id));

        voucher.setStatus(!voucher.getStatus());
        voucher = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherRespone(voucher);
    }
    @Transactional
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher khong co id " + id));

        voucherRepository.delete(voucher);
    }
    public static String generateVoucherCode() {
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "VOUCHER-" + uuidPart;
    }

    public Optional<Voucher> findById(Integer voucherId) {
        return voucherRepository.findById(voucherId);
    }
}
