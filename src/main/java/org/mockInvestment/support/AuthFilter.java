package org.mockInvestment.support;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.auth.dto.CustomOAuth2User;
import org.mockInvestment.support.token.JwtTokenProvider;
import org.mockInvestment.support.token.TokenExtractor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    public AuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = TokenExtractor.extractAccessToken(request);
        if (token == null || isInvalidToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            filterChain.doFilter(request, response);
            return;
        }

        AuthInfo authInfo = jwtTokenProvider.getParsedClaims(token);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(authInfo);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isInvalidToken(String token) {
        return !jwtTokenProvider.isValid(token);
    }

}
