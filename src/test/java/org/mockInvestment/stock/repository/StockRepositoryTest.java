package org.mockInvestment.stock.repository;

import org.junit.jupiter.api.Test;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void findByCode() {
        stockRepository.save(new Stock(1L, "CODE"));

        Stock findStock = stockRepository.findByCode("CODE")
                .orElseThrow(StockNotFoundException::new);

        assertThat(findStock.getCode()).isEqualTo("CODE");
    }

}