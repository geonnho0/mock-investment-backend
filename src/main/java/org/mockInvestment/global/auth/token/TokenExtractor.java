package org.mockInvestment.global.auth.token;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


public class TokenExtractor {

    private static final String COOKIE_NAME = "Authorization";

    public static String extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return extract(cookies);
    }

    private static String extract(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals(COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
