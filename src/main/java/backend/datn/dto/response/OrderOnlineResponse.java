package backend.datn.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
public class OrderOnlineResponse  {
    Long id;
    VoucherResponse voucher;
    CustomerResponse customer;
    String orderCode;
    LocalDateTime createDate;
    BigDecimal totalAmount;
    BigDecimal totalBill;
    Integer paymentMethod;
    Integer statusOrder;
    Boolean kindOfOrder;
    String phone;
    String address;
    BigDecimal shipfee;
    BigDecimal discount;

    @JsonIgnoreProperties("order")
    private List<OrderDetailResponse> orderDetails;
}