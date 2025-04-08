package backend.datn.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public class VNPayUtil {

    /**
     * Tạo query string từ Map các tham số, sắp xếp theo thứ tự key
     * @param params Map chứa các tham số
     * @return Chuỗi query string (ví dụ: "key1=value1&key2=value2")
     * @throws UnsupportedEncodingException Nếu có lỗi mã hóa
     */
    public static String getQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        // Sắp xếp các key theo thứ tự bảng chữ cái
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder queryString = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            }
        }

        return queryString.toString();
    }

    /**
     * Tính toán mã băm SecureHash bằng thuật toán HMAC-SHA512
     * @param secretKey Khóa bí mật (Secret Key) của VNPay
     * @param data Dữ liệu cần băm (thường là query string)
     * @return Chuỗi SecureHash (hex string)
     * @throws NoSuchAlgorithmException Nếu thuật toán HMAC-SHA512 không được hỗ trợ
     * @throws InvalidKeyException Nếu khóa bí mật không hợp lệ
     */
    public static String hmacSHA512(String secretKey, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Chuyển byte array thành hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
