package org.mockInvestment.global.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.auth.dto.CustomOAuth2User;
import org.mockInvestment.global.auth.token.JwtTokenProvider;
import org.mockInvestment.global.auth.token.TokenExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

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
        Optional<AuthInfo> authInfo = parseToAuthInfo(token);
        authInfo.ifPresent(this::authentication);
        filterChain.doFilter(request, response);
    }

    private Optional<AuthInfo> parseToAuthInfo(String token) {
        if (token == null || isInValidToken(token))
            return Optional.empty();
        return Optional.ofNullable(jwtTokenProvider.getParsedClaims(token));
    }

    private void authentication(AuthInfo authInfo) {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(authInfo);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isInValidToken(String token) {
        return jwtTokenProvider.isValid(token);
    }

}
