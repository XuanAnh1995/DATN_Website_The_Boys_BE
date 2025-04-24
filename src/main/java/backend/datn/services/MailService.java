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
            System.err.println(" Lỗi gửi email đến " + to + ": " + e.getMessage());
        }
    }

    @Async
    public void sendVerificationMail(String username, String to, String verificationLink) {
        String subject = "Xác Minh Email Tài Khoản The boys";
        String htmlBody = """
            <p>Chào %s,</p>
            <p>Cảm ơn bạn đã đăng ký với The boys.</p>
            <p>Vui lòng nhấp vào liên kết dưới đây để xác minh địa chỉ email của bạn:</p>
            <p><a href="%s">Xác Minh Email</a></p>
            <p>Nếu bạn không tạo tài khoản này, vui lòng bỏ qua email này.</p>
            <p>Trân trọng,<br> The boys</p>
        """.formatted(username, verificationLink);

        sendHtmlMail(to, subject, htmlBody);
    }

    public void sendVerificationUpdateMail(String username, String to, String verificationLink) {
        String subject = "Xác Minh Thay Đổi Email";
        String htmlBody = """
            <p>Chào %s,</p>
            <p>Cảm ơn bạn đã sử dụng dịch vụ của The boys.</p>
            <p>Vui lòng nhấp vào liên kết dưới đây để xác minh địa chỉ email của bạn:</p>
            <p><a href="%s">Xác nhận thay đổi email của bạn</a></p>
            <p>Trân trọng,<br> The boys</p>
        """.formatted(username, verificationLink);

        sendHtmlMail(to, subject, htmlBody);
    }

    @Async
    public void sendTemporaryPasswordMail(String username, String to, String temporaryPassword, String confirmLink) {
        String subject = "Mật Khẩu Tạm Thời Của Bạn";
        String htmlBody = """
            <p>Chào %s,</p>
            <p>Mật khẩu tạm thời cho tài khoản của bạn là:</p>
            <p><strong>%s</strong></p>
            <p>Vui lòng nhấp vào liên kết dưới đây để xác nhận mật khẩu:</p>
            <p><a href="%s">Xác nhận mật khẩu</a></p>
            <p>Trân trọng,<br> The boys</p>
        """.formatted(username, temporaryPassword, confirmLink);

        sendHtmlMail(to, subject, htmlBody);
    }

    @Async
    public void sendNewPasswordMail(String username, String to, String newPassword) {
        String subject = "🔐 Mật khẩu mới của bạn";
        String htmlBody = """
            <p>Xin chào <strong>%s</strong>,</p>
            <p>Hệ thống đã tạo một mật khẩu mới cho tài khoản của bạn:</p>
            <p><strong style="color:red; font-size:18px;">%s</strong></p>
            <p>Vui lòng thay đổi mật khẩu ngay sau khi đăng nhập.</p>
            <p>Trân trọng,<br> The boys</p>
        """.formatted(username, newPassword);

        sendHtmlMail(to, subject, htmlBody);
    }
}
