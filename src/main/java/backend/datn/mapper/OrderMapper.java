package backend.datn.mapper;

import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public static OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;
        return OrderResponse.builder()
                .id(order.getId() != null ? Math.toIntExact(order.getId()) : 0)
                .employee(order.getEmployee() != null ? EmployeeMapper.toEmployeeResponse(order.getEmployee()) : null)
                .voucher(order.getVoucher() != null ? VoucherMapper.toVoucherResponse(order.getVoucher()) : null)
                .customer(order.getCustomer() != null ? CustomerMapper.toCustomerResponse(order.getCustomer()) : null)
                .orderCode(order.getOrderCode() != null ? order.getOrderCode() : "")
                .createDate(order.getCreateDate())
                .totalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : 0)
                .totalBill(order.getTotalBill())
                .originalTotal(order.getOriginalTotal())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : 0)
                .statusOrder(order.getStatusOrder() != null ? order.getStatusOrder() : 0)
                .kindOfOrder(order.getKindOfOrder() != null ? order.getKindOfOrder() : false)
                .build();
    }
}
