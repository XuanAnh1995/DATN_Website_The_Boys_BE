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

    @NotNull(message = "ID nhân viên không được để trống")
    private Integer employeeId;

    private Integer voucherId;

    @NotNull(message = "ID khách hàng không được để trống")
    private Integer customerId;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private Integer paymentMethod;

    @NotNull(message = "Loại đơn hàng không được để trống")
    @Builder.Default
    private Boolean kindOfOrder = true;

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    @Builder.Default
    private Integer statusOrder = 1;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<OrderDetailCreateRequest> orderDetails;

}