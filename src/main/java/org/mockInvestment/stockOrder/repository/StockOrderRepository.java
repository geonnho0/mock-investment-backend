package org.mockInvestment.stockOrder.repository;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {

    List<StockOrder> findAllByMember(Member member);

    List<StockOrder> findAllByMemberAndStock(Member member, Stock stock);

}
