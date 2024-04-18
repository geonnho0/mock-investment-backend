package org.mockInvestment.stockValue.repository;

import org.mockInvestment.stockValue.domain.StockValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockValueRepository extends JpaRepository<StockValue, Long> {

    List<StockValue> findAllByCodeAndDateIsLessThanEqual(String code, LocalDate date);

}
