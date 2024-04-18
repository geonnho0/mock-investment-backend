package org.mockInvestment.simulation.repository;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.simulation.domain.MemberSimulationDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberSimulationDateRepository extends JpaRepository<MemberSimulationDate, Long> {

    Optional<MemberSimulationDate> findByMember(Member member);

    boolean existsByMember(Member member);

}
