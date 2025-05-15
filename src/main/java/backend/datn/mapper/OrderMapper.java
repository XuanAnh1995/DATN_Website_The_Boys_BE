package backend.datn.mapper;

import backend.datn.dto.response.OrderDetailResponse;
import backend.datn.dto.response.OrderResponse;
import backend.datn.entities.Order;
import backend.datn.entities.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public static OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;
        // Sử dụng Builder để tạo OrderResponse
        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId() != null ? order.getId() : 0) // Sửa vì id trong Order đã là Integer
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
                .kindOfOrder(order.getKindOfOrder() != null ? order.getKindOfOrder() : false);

        // Ánh xạ orderDetails (thêm vào chuỗi Builder trước khi build)
        if (order.getOrderDetails() != null) {
            builder.orderDetails(order.getOrderDetails().stream()
                    .map(OrderMapper::toOrderDetailResponse)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    // Phương thức ánh xạ OrderDetail sang OrderDetailResponse, không ánh xạ trường order
    private static OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail) {
        if (orderDetail == null) return null;

        return OrderDetailResponse.builder()
                .id(orderDetail.getId() != null ? orderDetail.getId() : 0) // Sửa vì id trong OrderDetail đã là Integer
                .productDetail(orderDetail.getProductDetail() != null ? ProductDetailMapper.toProductDetailResponse(orderDetail.getProductDetail()) : null)
                .quantity(orderDetail.getQuantity() != null ? orderDetail.getQuantity() : 0)
                .build();
    }


}
