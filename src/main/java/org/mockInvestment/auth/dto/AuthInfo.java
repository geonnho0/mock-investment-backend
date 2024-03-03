package org.mockInvestment.auth.dto;

import org.mockInvestment.member.domain.Member;

public record AuthInfo(Long id, String role, String name, String username) {

    public AuthInfo(Member member) {
        this(member.getId(), member.getRole(), member.getName(), member.getUsername());
    }
}
