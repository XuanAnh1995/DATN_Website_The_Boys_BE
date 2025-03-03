package backend.datn.helpers;


import java.security.SecureRandom;

public class CodeGeneratorHelper {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Tạo mã duy nhất bằng cách mã hóa timestamp + random
     * @param prefix Tiền tố (ví dụ: PRD, USR, BRD)
     * @return Mã duy nhất (ví dụ: PRD8Q3I4GZ7)
     */
    public static String generateCode(String prefix) {
        long timestamp = System.currentTimeMillis(); // Lấy timestamp hiện tại
        String encodedTimestamp = encodeBase36(timestamp); // Mã hóa Base36
        String randomPart = getRandomString(2); // Thêm 2 ký tự random
        return prefix.toUpperCase() + encodedTimestamp + randomPart;
    }

    /**
     * Mã hóa số nguyên sang hệ cơ số 36 (Base36)
     * @param value Giá trị cần mã hóa
     * @return Chuỗi mã hóa
     */
    private static String encodeBase36(long value) {
        return Long.toString(value, 36).toUpperCase();
    }

    /**
     * Sinh chuỗi ngẫu nhiên từ A-Z và 0-9
     * @param length Độ dài chuỗi random
     * @return Chuỗi ngẫu nhiên
     */
    private static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}