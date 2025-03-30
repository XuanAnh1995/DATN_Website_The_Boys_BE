package backend.datn.services;

import backend.datn.config.VNPayConfig;
import backend.datn.entities.OrderOnline;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.repositories.OrderOnlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Tạo URL thanh toán VNPay dựa trên mã đơn hàng.
     */

    public String generatePaymentUrl(String maOrderOnline) throws UnsupportedEncodingException {
        // Lấy hóa đơn từ database
        OrderOnline order = orderOnlineRepository.findByOrderCode(maOrderOnline);
        if (order == null) {
            throw new EntityNotFoundException("Hóa đơn không tồn tại");
        }

        // Tổng tiền đơn hàng
        long totalAmount = order.getTotalBill().longValue();
        long amount = totalAmount * 100;

        // Tạo tham số VNPay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", ""); // Ngân hàng mặc định
        vnp_Params.put("vnp_TxnRef", maOrderOnline);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan hoa don " + maOrderOnline);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_OrderType", "other");

        // Lấy thời gian tạo hóa đơn và hạn thanh toán
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = order.getCreateDate().atZone(java.time.ZoneId.systemDefault()).format(formatter);
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

    public String handleVnpayCallback(Map<String, String> payload) throws Exception {
        String vnpTxnRef = payload.get("vnp_TxnRef"); // Mã hóa đơn
        String vnpResponseCode = payload.get("vnp_ResponseCode"); // Trạng thái giao dịch
        String vnpAmount = payload.get("vnp_Amount"); // Tổng tiền

        if (vnpTxnRef == null || vnpResponseCode == null) {
            throw new IllegalArgumentException("Thiếu thông tin 'vnp_TxnRef' hoặc 'vnp_ResponseCode'.");
        }

        // Lấy thông tin hóa đơn từ DB
        OrderOnline order = orderOnlineRepository.findByOrderCode(vnpTxnRef);
        if (order == null) {
            throw new EntityNotFoundException("Hóa đơn không tồn tại");
        }
        BigDecimal tongTienSanPham = order.getTotalAmount();

        // Chuyển đổi số tiền từ VNPay về VNĐ
        BigDecimal amountFromVNPay = new BigDecimal(vnpAmount).divide(BigDecimal.valueOf(100));

        // Kiểm tra số tiền có khớp không
        if (tongTienSanPham.compareTo(amountFromVNPay) != 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không khớp với hóa đơn.");
        }

        // Nếu giao dịch thành công
        if ("00".equals(vnpResponseCode)) {
            processSuccessfulTransaction(vnpTxnRef);
            order.setStatusOrder(2); // Đã xác nhận
            orderOnlineRepository.save(order);
            return "Giao dịch thành công";
        } else {
            order.setStatusOrder(-1);
            orderOnlineRepository.save(order);
            return "Giao dịch thất bại, mã lỗi: " + vnpResponseCode;
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
