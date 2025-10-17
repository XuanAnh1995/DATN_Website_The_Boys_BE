package backend.datn.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data

public class OrderPOSResponse {

    Long id;
    VoucherResponse voucher;
    CustomerResponse customer;
    EmployeeResponse employee;
    String orderCode;
    LocalDateTime createDate;
    BigDecimal originalTotal;
    BigDecimal totalAmount;
    BigDecimal totalBill;
    Integer paymentMethod;
    Integer statusOrder;
    Boolean kindOfOrder;

    @JsonIgnoreProperties("order")
    private List<OrderDetailResponse> orderDetails;
}
