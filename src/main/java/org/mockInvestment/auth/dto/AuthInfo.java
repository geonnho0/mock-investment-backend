package org.mockInvestment.auth.dto;

import lombok.Getter;
import org.mockInvestment.member.domain.Member;

@Getter
public class AuthInfo {

    private final Long id;

    private final String role;


    private final String username;

    public AuthInfo(Long id, String role, String username) {
        this.id = id;
        this.role = role;
        this.username = username;
    }

    public AuthInfo(Member member) {
        id = member.getId();
        role = member.getRole();
        username = member.getUsername();
    }

    public AuthInfo() {
        id = null;
        role = null;
        username = null;
    }

}
