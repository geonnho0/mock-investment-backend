package org.mockInvestment.simulation.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.simulation.domain.MemberSimulationDate;
import org.mockInvestment.simulation.dto.SimulationDateResponse;
import org.mockInvestment.simulation.repository.MemberSimulationDateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationDateFindService {

    private final MemberSimulationDateRepository memberSimulationDateRepository;

    private final MemberRepository memberRepository;


    public SimulationDateResponse findCurrentDate(AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        MemberSimulationDate date = memberSimulationDateRepository.findByMember(member)
                .orElseThrow();
        return new SimulationDateResponse(date.getSimulationDate());
    }

}
