package backend.datn.dto.response;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Data
@Builder
public class OrderResponse implements Serializable {

    Integer id;

    EmployeeResponse employee;

    VoucherResponse voucher;

    CustomerResponse customer;

    String orderCode;

    Instant createDate;

    Integer totalAmount;

    BigDecimal totalBill;

    Integer paymentMethod;

    Integer statusOrder;

    Integer kindOfOrder;

    List<OrderDetailResponse> orderDetailResponses;
}