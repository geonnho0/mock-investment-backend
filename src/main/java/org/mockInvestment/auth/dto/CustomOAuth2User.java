package org.mockInvestment.auth.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final AuthInfo authInfo;


    public CustomOAuth2User(AuthInfo authInfo) {
        this.authInfo = authInfo;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return authInfo.role();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return authInfo.name();
    }
}
