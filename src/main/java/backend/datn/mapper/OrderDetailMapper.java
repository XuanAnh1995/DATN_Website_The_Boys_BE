package backend.datn.mapper;

import backend.datn.dto.response.OrderDetailResponse;
import backend.datn.entities.OrderDetail;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailMapper {
    public static OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail) {
        if (orderDetail == null) return null;
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .order(orderDetail.getOrder() != null ? OrderMapper.toOrderResponse(orderDetail.getOrder()) : null)
                .productDetail(ProductDetailMapper.toProductDetailResponse(orderDetail.getProductDetail()))
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .build();
    }
}
