package backend.datn.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendHtmlMail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    @Async
    public void sendNewPasswordMail(String username, String to, String newPassword) {
        String subject = "🔐 Mật khẩu mới của bạn";
        String htmlBody = "<p>Xin chào <strong>" + username + "</strong>,</p>"
                + "<p>Hệ thống đã tạo một mật khẩu mới cho tài khoản của bạn:</p>"
                + "<p><strong style=\"color:red; font-size:18px;\">" + newPassword + "</strong></p>"
                + "<p>Vui lòng sử dụng mật khẩu này để đăng nhập và thay đổi mật khẩu ngay lập tức.</p>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng liên hệ với bộ phận hỗ trợ.</p>"
                + "<p>Trân trọng,</p>";

        sendHtmlMail(to, subject, htmlBody);
    }
}
