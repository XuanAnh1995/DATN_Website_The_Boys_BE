package backend.datn.mapper;

import backend.datn.dto.response.VoucherResponse;
import backend.datn.entities.Voucher;


public class VoucherMapper {
    public static VoucherResponse toVoucherRespone(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .voucherCode(voucher.getVoucherCode())
                .voucherName(voucher.getVoucherName())
                .description(voucher.getDescription())
                .minCondition(voucher.getMinCondition())
                .maxDiscount(voucher.getMaxDiscount())
                .reducedPercent(voucher.getReducedPercent())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .status(voucher.getStatus())
                .build();
    }
}
