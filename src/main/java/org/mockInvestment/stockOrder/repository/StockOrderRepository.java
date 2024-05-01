package org.mockInvestment.stockOrder.repository;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {

    List<StockOrder> findAllByMember(Member member);

    List<StockOrder> findAllByMemberAndStockTicker(Member member, StockTicker stockTicker);

}
