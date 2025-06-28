package backend.datn.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final String PHONE_REGEX = "^(\\+[1-9]\\d{0,3}|[0])(?:[0-9]{1,3}[-]?){1,3}\\d{4,11}$";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isEmpty()) {
            return true; // Cho phép null hoặc rỗng nếu không bắt buộc
        }

        // Vô hiệu hóa thông báo mặc định từ @Phone
        context.disableDefaultConstraintViolation();
        // Xóa các ký tự không phải số và dấu - để kiểm tra độ dài
        String cleanPhone = phone.replaceAll("[^0-9]", "");

        // Kiểm tra các trường hợp lỗi
        if (cleanPhone.length() < 7) {
            context.buildConstraintViolationWithTemplate(
                    "Phone number is too short. It must be at least 7 digits long."
            ).addConstraintViolation();
            return false;
        }

        if (cleanPhone.length() > 15) {
            context.buildConstraintViolationWithTemplate(
                    "Phone number is too long. It must not exceed 15 digits."
            ).addConstraintViolation();
            return false;
        }

        if (phone.startsWith("0") && phone.length() > 1 && !Character.isDigit(phone.charAt(1))) {
            context.buildConstraintViolationWithTemplate(
                    "Phone number starting with 0 must be followed by a valid area or network code (e.g., 9, 24, 28, 01, 017)."
            ).addConstraintViolation();
            return false;
        }

        if (!phone.matches("^(\\+[1-9]\\d{0,3}|[0]).*")) {
            context.buildConstraintViolationWithTemplate(
                    "Phone number must start with 0 or a country code (e.g., +84, +44)."
            ).addConstraintViolation();
            return false;
        }

        if (!phone.matches(PHONE_REGEX)) {
            context.buildConstraintViolationWithTemplate(
                    "Invalid phone number format. It may include up to 3 '-' separators. Examples: 0901234567, 0123456789, 0171234567, 0241234567, 028-123-4567, +84901234567, +442071234567, or +44-207-123-4567."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

    @Override
    public void initialize(Phone constraintAnnotation) {
        // Không cần khởi tạo thêm gì, để trống
    }
}
