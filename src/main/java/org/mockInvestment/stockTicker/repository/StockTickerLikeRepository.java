package org.mockInvestment.stockTicker.repository;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockTicker.domain.StockTickerLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockTickerLikeRepository extends JpaRepository<StockTickerLike, Long> {

    Optional<StockTickerLike> findByStockTickerAndMember(String stockTicker, Member member);

    boolean existsByStockTickerAndMember(String stockTicker, Member member);

    List<StockTickerLike> findAllByMember(Member member);

}
