package org.mockInvestment.balance.repository;

import org.mockInvestment.balance.domain.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
