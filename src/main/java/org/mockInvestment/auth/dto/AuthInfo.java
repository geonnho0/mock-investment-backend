package org.mockInvestment.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthInfo {

    private final Long id;
    private final String role;
    private final String name;
    private final String username;

    @Builder
    public AuthInfo(Long id, String role, String name, String username) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.username = username;
    }

}
