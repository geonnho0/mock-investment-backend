package org.mockInvestment.simulation.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.simulation.domain.MemberSimulationDate;
import org.mockInvestment.simulation.domain.SimulationProceedInfo;
import org.mockInvestment.simulation.dto.SimulationDateResponse;
import org.mockInvestment.simulation.dto.request.ProceedSimulationRequest;
import org.mockInvestment.simulation.repository.MemberSimulationDateRepository;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationProceedService {

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final MemberRepository memberRepository;

    private final MemberSimulationDateRepository memberSimulationDateRepository;

    private final ApplicationEventPublisher applicationEventPublisher;


    public SimulationDateResponse proceedNextDate(ProceedSimulationRequest request, AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        MemberSimulationDate simulationDate = memberSimulationDateRepository.findByMember(member)
                .orElseThrow();

        List<LocalDate> dates = stockPriceCandleRepository.findCandidateDates(simulationDate.getSimulationDate(),
                PageRequest.of(0, request.length() + 1));

        for (int i = 0; i <= request.length(); i++) {
            applicationEventPublisher.publishEvent(new SimulationProceedInfo(dates.get(i), member));
        }

        LocalDate nextDate = dates.get(request.length());
        simulationDate.updateDate(nextDate);
        return new SimulationDateResponse(nextDate);
    }

}
