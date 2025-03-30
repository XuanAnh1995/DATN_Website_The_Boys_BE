package backend.datn.mapper;

import backend.datn.dto.response.OrderDetailResponse;
import backend.datn.dto.response.OrderOnlineDetailResponse;
import backend.datn.entities.OrderDetail;
import backend.datn.entities.OrderOnlineDetail;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailOnlineMapper {
    public static OrderOnlineDetailResponse toOrderDetailResponse(OrderOnlineDetail orderDetail) {
        if (orderDetail == null) return null;
        return OrderOnlineDetailResponse.builder()
                .id(orderDetail.getId())
                .order(OrderOnlineMapper.toOrderOnlineResponse(orderDetail.getOrder()))
                .productDetail(ProductDetailMapper.toProductDetailResponse(orderDetail.getProductDetail()))
                .quantity(orderDetail.getQuantity())
                .build();
    }
}
