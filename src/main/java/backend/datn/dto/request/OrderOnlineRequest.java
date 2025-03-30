package backend.datn.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class OrderOnlineRequest {
    String voucherId;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    Integer paymentMethod;

    @NotNull(message = "Chi tiết đơn hàng không được để trống")
    List<OrderOnlineDetailRequest> orderOnlineDetails;

    @Pattern(regexp = "^\\d{10,15}$", message = "Số điện thoại không hợp lệ")
    String phone;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    String address;

    @DecimalMin(value = "0.0", message = "Phí vận chuyển không được âm")
    BigDecimal shipfee;
}
