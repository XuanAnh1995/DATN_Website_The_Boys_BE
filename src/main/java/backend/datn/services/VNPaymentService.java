package backend.datn.services;

import backend.datn.config.VNPayConfig;
import backend.datn.entities.Order;
import backend.datn.entities.OrderOnline;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.repositories.OrderPOSRepository;
import backend.datn.repositories.OrderRepository;
import backend.datn.repositories.OrderOnlineRepository;
import backend.datn.utils.VNPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class VNPaymentService {

    @Autowired
    private OrderOnlineService orderOnlineService;

    @Autowired
    private OrderOnlineRepository orderOnlineRepository;

    @Autowired
    private OrderPOSRepository orderPOSRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.secretKey}")
    private String vnp_SecretKey;

    private final RestTemplate restTemplate = new RestTemplate();

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
        vnp_Params.put("vnp_BankCode", ""); // Ngân hàng mặc định
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
        String vnp_SecureHash;
        try {
            vnp_SecureHash = VNPayUtil.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán HMAC-SHA512 không được hỗ trợ: " + e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Khóa bí mật không hợp lệ: " + e.getMessage(), e);
        }
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        // Trả về URL thanh toán
        return VNPayConfig.vnp_PayUrl + "?" + query.toString();
    }


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

    // Phương thức mới để lấy URL mã QR từ VNPay
    public String generateVNPayQRUrl(Integer orderId, boolean isPOS) throws UnsupportedEncodingException {
        long totalAmount;

        if (isPOS) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn POS không tồn tại với ID: " + orderId));
            totalAmount = order.getTotalBill().longValue() * 100;
        } else {
            OrderOnline order = orderOnlineRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Hóa đơn Online không tồn tại với ID: " + orderId));
            totalAmount = order.getTotalBill().longValue() * 100;
        }

        String vnp_TxnRef = orderId + "_" + System.currentTimeMillis();
        String vnp_IpAddr = "127.0.0.1";
        String vnp_Version = "2.1.0";
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(totalAmount));
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Kiểm tra vnp_SecretKey
        if (vnp_SecretKey == null || vnp_SecretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Khóa bí mật VNPay (vnp_SecretKey) không được để trống");
        }

        String secureHash;
        try {
            secureHash = VNPayUtil.hmacSHA512(vnp_SecretKey, VNPayUtil.getQueryString(vnp_Params));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán HMAC-SHA512 không được hỗ trợ: " + e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Khóa bí mật không hợp lệ: " + e.getMessage(), e);
        }
        vnp_Params.put("vnp_SecureHash", secureHash);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(vnp_Params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://sandbox.vnpayment.vn/vnpaygw-sit/v2/VNPayQR/CreateQRTransaction",
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && "00".equals(responseBody.get("code"))) {
            return (String) responseBody.get("data");
        } else {
            throw new RuntimeException("Không thể tạo mã QR VNPay: " + responseBody.get("message"));
        }
    }

    public String generateQRPayPayload(Order order) throws Exception {
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String vnp_HashSecret = VNPayConfig.secretKey;
        String vnp_Url = "https://sandbox.vnpayment.vn/qrpay/create";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(order.getTotalBill().longValue() * 100));
        vnp_Params.put("vnp_TxnRef", String.valueOf(order.getId()));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan hoa don " + order.getId());
        vnp_Params.put("vnp_CreateDate", VNPayConfig.getCurrentDateTime());
        vnp_Params.put("vnp_ExpireDate", VNPayConfig.getCurrentDateTimePlusMinutes(15));
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

        // Tạo Secure Hash
        String hashData = VNPayConfig.getHashData(vnp_Params);
        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnp_HashSecret, hashData);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        // Gọi API
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(vnp_Url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new JSONObject(vnp_Params).toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());

        if ("00".equals(jsonResponse.getString("code"))) {
            return jsonResponse.getString("data"); // Trả về QR Payload
        } else {
            throw new Exception("Failed to create QR Pay: " + jsonResponse.getString("message"));
        }
    }
}
