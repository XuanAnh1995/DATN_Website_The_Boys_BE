package backend.datn.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse implements Serializable {

    private Integer id;
    private EmployeeResponse employee;
    private VoucherResponse voucher;
    private CustomerResponse customer;
    private String orderCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private LocalDateTime createDate;

    private Integer totalAmount;  // Tổng số lượng sản phẩm
    private BigDecimal totalBill; // Tổng tiền trước khi áp voucher
//    private BigDecimal finalTotal; // Tổng tiền sau khi áp voucher
//    private BigDecimal originalTotal; // Tổng tiền trước khi áp voucher


    private Integer paymentMethod;
    private Boolean kindOfOrder;
    private Integer statusOrder;

    @JsonIgnoreProperties("order")
    private List<OrderDetailResponse> orderDetails;
}
