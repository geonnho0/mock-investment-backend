package org.mockInvestment.simulation.domain;

import org.mockInvestment.member.domain.Member;

import java.time.LocalDate;

public record SimulationProceedInfo(LocalDate newDate, Member member) {
}
