package backend.datn.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderCreateRequest{

    @NotNull
    private Integer employeeId;

    private Integer voucherId;

    @NotNull
    private Integer customerId;

    //remove this
    @NotEmpty
    private String orderCode;

    //remove this
    @NotNull
    @PositiveOrZero
    private Integer totalAmount;

    //remove this
    @NotNull
    @Positive
    private BigDecimal totalBill;

    @NotNull
    private Integer paymentMethod;

    @NotNull
    private Boolean kindOfOrder;

    @NotNull
    private Integer statusOrder;

    // Thêm danh sách chi tiết sản phẩm trong đơn hàng
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<OrderDetailCreateRequest> orderDetails;
}