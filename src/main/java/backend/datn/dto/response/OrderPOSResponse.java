package backend.datn.dto.response;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data

public class OrderPOSResponse {

    Integer id;
    VoucherResponse voucher;
    CustomerResponse customer;
    String orderCode;
    LocalDateTime createDate;
    Integer totalAmount;
    BigDecimal totalBill;
    Integer paymentMethod;
    Integer statusOrder;
    Boolean kindOfOrder;

}
