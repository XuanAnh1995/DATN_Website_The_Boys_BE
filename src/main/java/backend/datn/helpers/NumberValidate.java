package backend.datn.helpers;

public class NumberValidate {
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }

    public static boolean isPositive(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return Double.parseDouble(str) > 0;
    }

    public static boolean isNegative(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return Double.parseDouble(str) < 0;
    }

    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }
}
