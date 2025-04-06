package backend.datn.mapper;

import backend.datn.dto.response.OrderPOSResponse;
import backend.datn.entities.OrderPOS;

public class OrderPOSMapper {

    public static OrderPOSResponse toOrderPOSResponse(OrderPOS orderPOS) {
        if (orderPOS == null) {
            return null;
        }

        return OrderPOSResponse.builder()
                .id(orderPOS.getId())
                .voucher(orderPOS.getVoucher() != null ? VoucherMapper.toVoucherResponse(orderPOS.getVoucher()) : null)
                .customer(orderPOS.getCustomer() != null ? CustomerMapper.toCustomerResponse(orderPOS.getCustomer()) : null)
                .orderCode(orderPOS.getOrderCode() != null ? orderPOS.getOrderCode() : "")
                .createDate(orderPOS.getCreateDate())
                .totalAmount(orderPOS.getTotalAmount())
                .totalBill(orderPOS.getTotalBill())
                .paymentMethod(orderPOS.getPaymentMethod() != null ? orderPOS.getPaymentMethod() : 0)
                .statusOrder(orderPOS.getStatusOrder() != null ? orderPOS.getStatusOrder() : 0)
                .kindOfOrder(orderPOS.getKindOfOrder())
                .build();
    }
}
