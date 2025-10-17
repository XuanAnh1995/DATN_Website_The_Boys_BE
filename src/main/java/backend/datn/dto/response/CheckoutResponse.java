package backend.datn.dto.response;

public class CheckoutResponse {

    private Long orderId;

    public CheckoutResponse(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

}
