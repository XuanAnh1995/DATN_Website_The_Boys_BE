package backend.datn.mapper;

import backend.datn.dto.response.OrderDetailResponse;
import backend.datn.entities.OrderDetail;

public class OrderDetailMapper {
    public static OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .productDetail(ProductDetailMapper.toProductDetailResponse(orderDetail.getProductDetail()))
                .quantity(orderDetail.getQuantity())
                .build();
    }
}
