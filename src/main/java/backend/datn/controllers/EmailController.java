package backend.datn.controllers;

import backend.datn.dto.request.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send-voucher-email")
    public ResponseEntity<String> sendVoucherEmail(@RequestBody EmailRequest request) {
        try {
            // Validate request
            if (request.getToEmails() == null || request.getToEmails().isEmpty()) {
                return ResponseEntity.badRequest().body("Danh sách email nhận không được để trống");
            }
            if (request.getFromEmail() == null || request.getFromEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("Email gửi không được để trống");
            }

            // Tạo MimeMessage thay vì SimpleMailMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(request.getFromEmail());
            helper.setTo(request.getToEmails().toArray(new String[0]));
            helper.setSubject("Voucher Mới Từ Hệ Thống The Boy");

            // Định dạng ngày
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date startDate = Date.from(request.getStartDate());
            Date endDate = Date.from(request.getEndDate());
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            // Định dạng tiền tệ VND với dấu chấm
            NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            String minConditionStr = currencyFormat.format(request.getMinCondition()) + " VND";
            String maxDiscountStr = currencyFormat.format(request.getMaxDiscount()) + " VND";

            // Nội dung HTML cho email
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; color: #333; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; }" +
                    ".header { text-align: center; }" +
                    ".header img { max-width: 150px; }" +
                    ".content { margin-top: 20px; }" +
                    ".voucher-info { background-color: #f9f9f9; padding: 15px; border-radius: 5px; }" +
                    ".footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<img src='https://your-logo-url.com/logo.png' alt='The Boy Logo'>" + // Thay bằng URL logo thực tế
                    "<h2>Voucher Mới Từ Hệ Thống The Boy</h2>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<p>Chào bạn,</p>" +
                    "<p>Bạn vừa nhận được một voucher đặc biệt từ hệ thống cửa hàng <strong>The Boy</strong>. Dưới đây là thông tin chi tiết:</p>" +
                    "<div class='voucher-info'>" +
                    "<p><strong>Mã voucher:</strong> " + request.getVoucherCode() + "</p>" +
                    "<p><strong>Tên voucher:</strong> " + request.getVoucherName() + "</p>" +
                    "<p><strong>Điều kiện tối thiểu:</strong> " + minConditionStr + "</p>" +
                    "<p><strong>Giảm tối đa:</strong> " + maxDiscountStr + "</p>" +
                    "<p><strong>Phần trăm giảm:</strong> " + request.getReducedPercent() + "%</p>" +
                    "<p><strong>Ngày bắt đầu:</strong> " + startDateStr + "</p>" +
                    "<p><strong>Ngày kết thúc:</strong> " + endDateStr + "</p>" +
                    "</div>" +
                    "<p>Hãy sử dụng ngay để nhận ưu đãi hấp dẫn này!</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Trân trọng,<br>Hệ thống The Boy</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true); // true để bật chế độ HTML
            mailSender.send(message);

            return ResponseEntity.ok("Email sent successfully to " + request.getToEmails().size() + " customers");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }
}