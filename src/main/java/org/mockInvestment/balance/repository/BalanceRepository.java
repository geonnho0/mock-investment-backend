package org.mockInvestment.balance.repository;

import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByMember(Member member);

}
