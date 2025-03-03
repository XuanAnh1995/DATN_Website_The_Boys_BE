package backend.datn.helpers;

public class RandomHelper {
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (characters.length() * Math.random());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }
}
