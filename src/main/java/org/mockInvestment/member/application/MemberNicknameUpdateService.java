package org.mockInvestment.member.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.dto.NicknameUpdateRequest;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberNicknameUpdateService {

    private final MemberRepository memberRepository;


    public void updateNickname(AuthInfo authInfo, NicknameUpdateRequest request) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        member.updateNickname(request.nickname());
    }

}
