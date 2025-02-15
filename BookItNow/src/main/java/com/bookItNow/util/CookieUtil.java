package com.bookItNow.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            Cookie cookie = new Cookie(name, encodedValue);
            cookie.setMaxAge(maxAge);
            cookie.setHttpOnly(true); // For security purposes
            cookie.setSecure(true);
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding cookie value", e);
        }
    }

}
