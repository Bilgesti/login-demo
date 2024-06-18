package com.example.login.util;

public class MaskingUtils {

    public static String anonymize(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        String username = parts[0];
        if (username.length() <= 1) {
            return email;
        }
        return username.charAt(0) + "**********" + username.charAt(username.length() - 1) + "@" + parts[1];
    }
}
