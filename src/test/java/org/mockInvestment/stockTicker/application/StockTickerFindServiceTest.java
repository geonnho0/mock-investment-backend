package org.mockInvestment.stockTicker.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.dto.StockTickerResponse;
import org.mockInvestment.stockTicker.dto.StockTickersResponse;
import org.mockInvestment.stockTicker.repository.StockTickerLikeRepository;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class StockTickerFindServiceTest extends MockTest {

    @Mock
    private StockTickerLikeRepository stockTickerLikeRepository;

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private StockTickerFindService stockTickerFindService;

    private StockTicker stockTicker;


    @BeforeEach
    void setUp() {
        stockTicker = new StockTicker("CODE", "NAME");
    }

    @Test
    @DisplayName("코드로 특정 주식의 정보를 반환한다.")
    void findStockTickerByCode() {
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(stockTicker));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerLikeRepository.existsByStockTickerAndMember(any(StockTicker.class), any(Member.class)))
                .thenReturn(true);

        StockTickerResponse response = stockTickerFindService.findStockTickerByCode(stockTicker.getCode(), testAuthInfo);

        assertAll(
                () -> assertThat(response.code()).isEqualTo(stockTicker.getCode()),
                () -> assertThat(response.name()).isEqualTo(stockTicker.getName()),
                () -> assertThat(response.isLiked()).isEqualTo(true)
        );
    }

    @Test
    @DisplayName("키워드로 주식들의 정보를 반환한다.")
    void findStockTickersByKeyword() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerRepository.findAllByKeyword(anyString()))
                .thenReturn(createStockTickers());
        when(stockTickerLikeRepository.existsByStockTickerAndMember(any(StockTicker.class), any(Member.class)))
                .thenReturn(true);

        StockTickersResponse response = stockTickerFindService.findStockTickersByKeyword("keyword", testAuthInfo);

        assertAll(
                () -> assertThat(response.stockTickers().size()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("키워드가 없으면, 빈 정보를 반환한다.")
    void findStockTickersByKeyword_empty() {
        StockTickersResponse response = stockTickerFindService.findStockTickersByKeyword("", testAuthInfo);

        assertThat(response.stockTickers().isEmpty()).isEqualTo(true);
    }

    private List<StockTicker> createStockTickers() {
        List<StockTicker> stockTickers = new ArrayList<>();
        stockTickers.add(new StockTicker("NAME", "NAM"));
        stockTickers.add(new StockTicker("NAMED", "NA"));
        return stockTickers;
    }

}