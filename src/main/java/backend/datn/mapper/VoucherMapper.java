package backend.datn.mapper;

import backend.datn.dto.response.VoucherResponse;
import backend.datn.entities.Voucher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VoucherMapper {
    public static VoucherResponse toVoucherResponse(Voucher voucher) {
        if (voucher == null) return null;
        return VoucherResponse.builder()
                .id(voucher.getId())
                .voucherCode(voucher.getVoucherCode())
                .voucherName(voucher.getVoucherName())
                .description(voucher.getDescription())
                .minCondition(voucher.getMinCondition())
                .maxDiscount(voucher.getMaxDiscount())
                .reducedPercent(BigDecimal.valueOf(voucher.getReducedPercent()))
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .status(voucher.getStatus())
                .build();
    }
}
