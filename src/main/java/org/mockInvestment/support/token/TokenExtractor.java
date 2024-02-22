package org.mockInvestment.support.token;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


public class TokenExtractor {

    private static final String BEARER = "Bearer";

    public static String extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return extract(cookies);
    }

    private static String extract(Cookie[] cookies) {
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("Authorization")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
