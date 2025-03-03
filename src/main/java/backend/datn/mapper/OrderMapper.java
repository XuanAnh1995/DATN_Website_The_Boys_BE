package backend.datn.mapper;

import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.Order;

public class OrderMapper {
    public static OrderResponse toOrderRespone(Order order) {
        return OrderResponse.builder()
                .id(order.getId() != null ? Math.toIntExact(order.getId()) : 0)
                .employee(EmployeeMapper.toEmployeeResponse(order.getEmployee()))
                .voucher(VoucherMapper.toVoucherRespone(order.getVoucher()))
                .customer(CustomerMapper.toCustomerResponse(order.getCustomer()))
                .orderCode(order.getOrderCode() != null ? order.getOrderCode() : "")
                .createDate(order.getCreateDate() != null ? order.getCreateDate() : null)
                .totalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : 0)
                .totalBill(order.getTotalBill() != null ? order.getTotalBill() : null)
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : 0)
                .statusOrder(order.getStatusOrder() != null ? order.getStatusOrder() :0)
                .kindOfOrder(order.getKindOfOrder() ? 1 : 0)
                .build();
    }
}
