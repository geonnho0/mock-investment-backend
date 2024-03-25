package org.mockInvestment.balance.service;

import org.mockInvestment.advice.exception.MemberNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BalanceService {

    private final MemberRepository memberRepository;


    public BalanceService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public CurrentBalanceResponse findBalance(AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        return new CurrentBalanceResponse(member.getBalance().getBalance());
    }

}
