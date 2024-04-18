package org.mockInvestment.stockTicker.repository;

import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StockTickerRepository extends JpaRepository<StockTicker, Long> {

    List<StockTicker> findTop1ByCodeOrderByDate(String code);

    List<StockTicker> findTop1ByCodeAndDateLessThanEqualOrderByDateDesc(String code, LocalDate date);

    @Query("SELECT DISTINCT st.code, st.name FROM StockTicker st WHERE st.name LIKE :keyword% OR st.code LIKE :keyword% " +
            "ORDER BY CASE WHEN st.name = :keyword OR st.code = :keyword THEN 0 ELSE 1 END")
    List<String[]> findAllByKeyword(String keyword);

}
