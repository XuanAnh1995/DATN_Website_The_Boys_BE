package backend.datn.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherUpdateRequest implements Serializable {
    @NotNull(message = "ID voucher không được để trống")
    Integer id ;

    @NotNull(message = "Tên voucher không được để trống")
    @Size(max = 250, message = "tên voucher không được vượt quá 250 ký tự")
    String voucherName;

    @Size(max = 255, message = "mô tả không được vượt quá 255 ký tự")
    String description;

    @NotNull(message = "Giá trị tối thiểu voucher không được để trống")
    BigDecimal minCondition;

    @NotNull(message = "Giá trị tối đa voucher không được để trống")
    BigDecimal maxDiscount;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    Double reducedPercent;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endDate;

    @NotNull(message = "Trạng thái không được để trống")
    Boolean status;

}