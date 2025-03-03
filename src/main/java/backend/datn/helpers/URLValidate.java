package backend.datn.helpers;

public class URLValidate {
    public static boolean isValidURL(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String urlRegex = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
        return url.matches(urlRegex);
    }
}
