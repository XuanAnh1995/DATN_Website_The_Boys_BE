package backend.datn.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderPOSCreateRequest {

    @NotNull
    @Min(1)
    private Integer orderId;

    @NotNull(message = "ID nhân viên không được để trống")
    @Min(1)
    private Integer employeeId;

    private Integer voucherId;

    @NotNull
    @Positive(message = "Tổng tiền phải lớn hơn 0")
    private Integer totalAmount;

    @NotNull(message = "ID khách hàng không được để trống")
    @Min(1)
    private Integer customerId;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Integer paymentMethod;

    @NotNull(message = "Loại đơn hàng không được để trống")
    private Boolean kindOfOrder = Boolean.TRUE;

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private Integer statusOrder = 1;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<OrderDetailCreateRequest> orderDetails;

}