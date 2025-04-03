package backend.datn.dto.response;

public class CheckoutResponse {

    private Integer orderId;

    public CheckoutResponse(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

}
