package org.mockInvestment.auth.dto;

import lombok.Getter;
import org.mockInvestment.member.domain.Member;

@Getter
public class AuthInfo {

    private final Long id;

    private final String role;

    private final String name;

    private final String username;

    public AuthInfo(Long id, String role, String name, String username) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.username = username;
    }

    public AuthInfo(Member member) {
        id = member.getId();
        role = member.getRole();
        name = member.getName();
        username = member.getUsername();
    }

    public AuthInfo() {
        id = null;
        role = null;
        name = null;
        username = null;
    }

}
