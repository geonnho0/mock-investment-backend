package org.mockInvestment.memberOwnStock.repository;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberOwnStockRepository extends JpaRepository<MemberOwnStock, Long> {

    Optional<MemberOwnStock> findByMemberAndStockTicker(Member member, String stockTicker);

}
