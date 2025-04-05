package backend.datn.services;

import backend.datn.config.VNPayConfig;
import backend.datn.entities.Order;
import backend.datn.entities.OrderOnline;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.repositories.OrderRepository;
import backend.datn.repositories.OrderOnlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VNPaymentService {

    @Autowired
    private OrderOnlineService orderOnlineService;

    @Autowired
    private OrderOnlineRepository orderOnlineRepository;

    @Autowired
    private OrderRepository orderRepository;

//    private final RestTemplate restTemplate;
//
//    public VNPaymentService() {
//        this.restTemplate = new RestTemplate();
//    }

    /**
     * Tạo URL thanh toán VNPay dựa trên mã đơn hàng.
     *
     * @param orderId ID đơn hàng (khóa chính Integer cho cả Order và OrderOnline)
     * @param isPOS   True nếu là đơn hàng POS, False nếu là đơn hàng Online
     */

    public String generatePaymentUrl(Integer orderId, boolean isPOS) throws UnsupportedEncodingException {

        long totalAmount;

        if (isPOS) {
            // Tìm đơn hàng POS bằng orderId
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn POS không tồn tại với ID: " + orderId));
            totalAmount = order.getTotalBill().longValue() * 100; // Chuyển sang đơn vị VNPay (VND * 100)
        } else {
            // Tìm đơn hàng Online bằng orderId
            OrderOnline order = orderOnlineRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn Online không tồn tại với ID: " + orderId));
            totalAmount = order.getTotalBill().longValue() * 100;
        }

        // Chuyển orderId thành String cho vnp_TxnRef
        String orderIdStr = String.valueOf(orderId);
        String vnp_ReturnUrl = VNPayConfig.vnp_ReturnUrl + (isPOS ? "?isPOS=true" : "?isPOS=false");

        // Tạo tham số VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(totalAmount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB"); // Ngân hàng mặc định
        vnp_Params.put("vnp_TxnRef", orderIdStr);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan hoa don " + orderIdStr);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_OrderType", "other");

        // Lấy thời gian tạo hóa đơn và hạn thanh toán
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = LocalDateTime.now().format(formatter);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(15);
        String vnp_ExpireDate = expireTime.format(formatter);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Tạo chuỗi hash và URL thanh toán
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        // Tạo Secure Hash
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        // Trả về URL thanh toán
        return VNPayConfig.vnp_PayUrl + "?" + query.toString();
    }

    /**
     * Tạo dữ liệu QR động để thanh toán VNPay.
     *
     * @param orderId ID đơn hàng (khóa chính Integer cho cả Order và OrderOnline)
     * @param isPOS   True nếu là đơn hàng POS, False nếu là đơn hàng Online
     * @return Chuỗi dữ liệu QR (theo chuẩn VietQR)
     */
//    public String generateQRCodeData(Integer orderId, boolean isPOS) throws UnsupportedEncodingException {
//        long totalAmount;
//
//        if (isPOS) {
//            Order order = orderRepository.findById(orderId)
//                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn POS không tồn tại với ID: " + orderId));
//            totalAmount = order.getTotalBill().longValue() * 100;
//        } else {
//            OrderOnline order = orderOnlineRepository.findById(orderId)
//                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn Online không tồn tại với ID: " + orderId));
//            totalAmount = order.getTotalBill().longValue() * 100;
//        }
//
//        String orderIdStr = String.valueOf(orderId);
//        String vnp_ReturnUrl = VNPayConfig.vnp_ReturnUrl + (isPOS ? "?isPOS=true" : "?isPOS=false");
//
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_Version", "2.1.0");
//        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
//        vnp_Params.put("vnp_Amount", String.valueOf(totalAmount));
//        vnp_Params.put("vnp_CurrCode", "VND");
//        vnp_Params.put("vnp_TxnRef", orderIdStr);
//        vnp_Params.put("vnp_OrderInfo", "Thanh toan hoa don " + orderIdStr);
//        vnp_Params.put("vnp_Locale", "vn");
//        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
//        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//        String vnp_CreateDate = LocalDateTime.now().format(formatter);
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//
//        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(15);
//        String vnp_ExpireDate = expireTime.format(formatter);
//        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
//
//        StringBuilder hashData = new StringBuilder();
//        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//
//        for (String fieldName : fieldNames) {
//            String fieldValue = vnp_Params.get(fieldName);
//            if (fieldValue != null && !fieldValue.isEmpty()) {
//                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
//                if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
//                    hashData.append('&');
//                }
//            }
//        }
//
//        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
//        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
//
//        // Gửi yêu cầu đến API tạo mã QR của VNPay
//        String qrApiUrl = "https://sandbox.vnpayment.vn/qrpay/create";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(vnp_Params, headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(qrApiUrl, HttpMethod.POST, request, Map.class);
//        Map<String, Object> responseBody = response.getBody();
//
//        if (responseBody != null && responseBody.containsKey("qrData")) {
//            return (String) responseBody.get("qrData"); // Trả về chuỗi dữ liệu QR
//        } else {
//            throw new RuntimeException("Không thể tạo mã QR: " + responseBody.get("message"));
//        }
//    }

    /**
     * Xử lý callback từ VNPay
     *
     * @param payload Dữ liệu trả về từ VNPay
     * @param isPOS   True nếu là đơn hàng POS
     */
    public String handleVnpayCallback(
            Map<String, String> payload,
            boolean isPOS
    ) throws Exception {
        String vnpTxnRef = payload.get("vnp_TxnRef"); // Mã hóa đơn
        String vnpResponseCode = payload.get("vnp_ResponseCode"); // Trạng thái giao dịch
        String vnpAmount = payload.get("vnp_Amount"); // Tổng tiền

        if (vnpTxnRef == null || vnpResponseCode == null) {
            throw new IllegalArgumentException("Thiếu thông tin 'vnp_TxnRef' hoặc 'vnp_ResponseCode'.");
        }

        // Chuyển vnp_TxnRef thành Integer để tìm kiếm
        Integer orderId = Integer.valueOf(vnpTxnRef);

        if (isPOS) {
            // Xử lý đơn hàng POS
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn POS không tồn tại với ID: " + orderId));
            BigDecimal totalBill = order.getTotalBill();
            BigDecimal amountFromVNPay = new BigDecimal(vnpAmount).divide(BigDecimal.valueOf(100));

            if (totalBill.compareTo(amountFromVNPay) != 0) {
                throw new IllegalArgumentException("Số tiền thanh toán không khớp với hóa đơn POS.");
            }

            if ("00".equals(vnpResponseCode)) {
                order.setStatusOrder(5); // Hoàn thành
                orderRepository.save(order);
                return "Giao dịch thành công";
            } else {
                order.setStatusOrder(-1); // Thất bại
                orderRepository.save(order);
                return "Giao dịch thất bại, mã lỗi: " + vnpResponseCode;
            }
        } else {
            // Xử lý đơn hàng Online
            OrderOnline order = orderOnlineRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn Online không tồn tại với ID: " + orderId));
            BigDecimal totalBill = order.getTotalBill();
            BigDecimal amountFromVNPay = new BigDecimal(vnpAmount).divide(BigDecimal.valueOf(100));

            if (totalBill.compareTo(amountFromVNPay) != 0) {
                throw new IllegalArgumentException("Số tiền thanh toán không khớp với hóa đơn Online.");
            }

            if ("00".equals(vnpResponseCode)) {
                processSuccessfulTransaction(orderId.toString());
                order.setStatusOrder(5); // Hoàn thành
                orderOnlineRepository.save(order);
                return "Giao dịch thành công";
            } else {
                order.setStatusOrder(-1); // Thất bại
                orderOnlineRepository.save(order);
                return "Giao dịch thất bại, mã lỗi: " + vnpResponseCode;
            }
        }
    }


    public String generateHtml(String title, String message, String content) {
        return "<!DOCTYPE html>" +
                "<html lang=\"vi\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>" + title + "</title>" +
                "<link href=\"https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css\" rel=\"stylesheet\">" +
                "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">" +
                "<style>" +
                "  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');" +
                "  body { font-family: 'Inter', sans-serif; }" +
                "  .bg-orange { background-color: #FF7F00; }" +
                "  .text-orange { color: #FF7F00; }" +
                "  .border-orange { border-color: #FF7F00; }" +
                "  .btn-hover:hover { background-color: #FF6200; }" +
                "</style>" +
                "</head>" +
                "<body class=\"bg-gray-50\">" +
                "<div class=\"min-h-screen flex items-center justify-center p-6\">" +
                "  <div class=\"max-w-2xl w-full bg-white rounded-lg shadow-lg overflow-hidden\">" +
                "    <div class=\"bg-orange p-6 flex items-center justify-center\">" +
                "      <i class=\"fas fa-exclamation-circle text-white text-5xl\"></i>" +
                "    </div>" +
                "    <div class=\"p-8 text-center\">" +
                "      <h1 class=\"text-4xl font-bold text-orange mb-4\">" + title + "</h1>" +
                "      <h2 class=\"text-2xl font-semibold text-gray-800 mb-6\">" + message + "</h2>" +
                "      <div class=\"my-8 text-lg text-gray-600\">" +
                "        <p>" + content + "</p>" +
                "      </div>" +
                "      <div class=\"mt-10\">" +
                "        <a href=\"http://localhost:5173/\" class=\"bg-orange text-white px-10 py-4 rounded-lg font-medium inline-block btn-hover transition duration-300\">" +
                "          <i class=\"fas fa-home mr-2\"></i> Trở lại trang chủ" +
                "        </a>" +
                "      </div>" +
                "    </div>" +
                "    <div class=\"bg-gray-50 py-4 text-center text-gray-500 text-sm border-t border-gray-100\">" +
                "      <p>© 2025 - Thông báo hệ thống</p>" +
                "    </div>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private void processSuccessfulTransaction(String maOrderOnline) {
        orderOnlineService.updateOrderStatusAfterPayment(maOrderOnline);
    }
}
