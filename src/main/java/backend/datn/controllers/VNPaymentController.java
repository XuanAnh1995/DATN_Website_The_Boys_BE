package backend.datn.controllers;

import backend.datn.services.VNPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class VNPaymentController {

    @Autowired
    private VNPaymentService vnPayService;

    /**
     * Tạo URL thanh toán VNPay
     *
     * @param orderId ID đơn hàng (khóa chính Integer)
     * @param isPOS   True nếu là đơn hàng POS
     */
    @PostMapping("/create-payment-url/{orderId}")
    public ResponseEntity<?> createPaymentUrl(
            @PathVariable Integer orderId,
            @RequestParam(defaultValue = "false") boolean isPOS
    ) {
        try {
            String paymentUrl = vnPayService.generatePaymentUrl(orderId, isPOS);
//            String paymentUrl = vnPayService.generateQRCodeData(orderId, isPOS);
            return ResponseEntity.ok(paymentUrl);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(500).body("Lỗi mã hóa URL: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/handle-response")
    public ResponseEntity<?> handleVNPayResponse(@RequestParam Map<String, String> params) {
        String result = "Đã nhận phản hồi từ VNPay. Chi tiết: " + params.toString();
        return ResponseEntity.ok(result);
    }


    /**
     * Xử lý callback từ VNPay
     *
     * @param payload Dữ liệu trả về từ VNPay
     * @param isPOS   True nếu là đơn hàng POS
     */
    @RequestMapping("/vnpay-callback")
    public ResponseEntity<String> handleVNPayCallback(
            @RequestParam Map<String, String> payload,
            @RequestParam(defaultValue = "false") boolean isPOS
    ) {
        try {
            if (!payload.containsKey("vnp_TxnRef") || !payload.containsKey("vnp_ResponseCode")) {
                String htmlResponse = vnPayService.generateHtml("Dữ liệu không hợp lệ",
                        "Thiếu thông tin cần thiết trong callback.",
                        "Vui lòng kiểm tra và thử lại.");
                return new ResponseEntity<>(htmlResponse, HttpStatus.BAD_REQUEST);
            }

            String response = vnPayService.handleVnpayCallback(payload, isPOS);

            if ("Giao dịch thành công".equals(response)) {
                String htmlResponse = vnPayService.generateHtml("Thanh toán thành công",
                        "Cảm ơn bạn đã thanh toán.",
                        "Đơn hàng của bạn đã được thanh toán thành công.");
                return new ResponseEntity<>(htmlResponse, HttpStatus.OK);
            } else {
                String vnpResponseCode = payload.get("vnp_ResponseCode");
                String htmlResponse = vnPayService.generateHtml("Thanh toán thất bại",
                        "Giao dịch không thành công.",
                        "Mã lỗi: " + vnpResponseCode + "<br/>" +
                                "Xin lỗi, có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại sau.");
                return new ResponseEntity<>(htmlResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            String htmlResponse = vnPayService.generateHtml("Có lỗi xảy ra",
                    e.getMessage(),
                    "Vui lòng liên hệ với chúng tôi để biết thêm chi tiết.");
            return new ResponseEntity<>(htmlResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
