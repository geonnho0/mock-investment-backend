package org.mockInvestment.stock.repository;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stock.domain.MemberOwnStock;
import org.mockInvestment.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberOwnStockRepository extends JpaRepository<MemberOwnStock, Long> {

    Optional<MemberOwnStock> findByMemberAndStock(Member member, Stock stock);

}
