package org.mockInvestment.stockTicker.repository;

import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockTickerRepository extends JpaRepository<StockTicker, Long> {

    @Query("SELECT st FROM StockTicker st WHERE st.name LIKE :keyword% OR st.code LIKE :keyword% " +
            "ORDER BY CASE WHEN st.name = :keyword OR st.code = :keyword THEN 0 ELSE 1 END")
    List<StockTicker> findAllByKeyword(String keyword);

    Optional<StockTicker> findByCode(String code);
}
