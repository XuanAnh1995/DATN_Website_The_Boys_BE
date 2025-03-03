package backend.datn.helpers;

public class PhoneValidate {
    public static boolean isValidPhoneNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        String phoneRegex = "^\\+?[0-9]{10,13}$";
        return str.matches(phoneRegex);
    }
}
