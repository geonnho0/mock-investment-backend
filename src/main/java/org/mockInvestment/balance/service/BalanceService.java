package org.mockInvestment.balance.service;

import org.mockInvestment.advice.exception.MemberNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.balance.repository.BalanceRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    private final MemberRepository memberRepository;


    public BalanceService(BalanceRepository balanceRepository, MemberRepository memberRepository) {
        this.balanceRepository = balanceRepository;
        this.memberRepository = memberRepository;
    }


    public CurrentBalanceResponse findBalance(AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
//        Balance balance = balanceRepository.findByMember(member)
//                .orElseThrow();
        return new CurrentBalanceResponse(member.getBalance().getBalance());
    }

}
