package org.mockInvestment.memberOwnStock.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStockValueResponse;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStocksResponse;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class MemberOwnStockFindServiceTest extends MockTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private StockPriceFindService stockPriceFindService;

    @InjectMocks
    private MemberOwnStockFindService memberOwnStockFindService;

    private StockTicker another;


    @BeforeEach
    void setUp() {
        another = new StockTicker("CODE2", "NAME2");
        testMember.addOwnStock(new MemberOwnStock(1L, testMember, testStockTicker));
        testMember.addOwnStock(new MemberOwnStock(2L, testMember, another));
    }

    @Test
    @DisplayName("코드로 본인이 소유한 주식 정보를 반환한다.")
    void findMyOwnStocksFilteredByCode() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(another));

        MemberOwnStocksResponse response = memberOwnStockFindService
                .findMyOwnStocksFilteredByCode(testAuthInfo, "CODE2");

        assertAll(
                () -> assertThat(response.ownStocks().size()).isEqualTo(1),
                () -> assertThat(response.ownStocks().get(0).id()).isEqualTo(2L)
        );
    }

    @Test
    @DisplayName("코드가 없으면 본인이 소유한 주식 정보를 모두 반환한다.")
    void findMyOwnStocksFilteredByCode_emptyCode() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.empty());

        MemberOwnStocksResponse response = memberOwnStockFindService
                .findMyOwnStocksFilteredByCode(testAuthInfo, "");

        assertThat(response.ownStocks().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("본인이 소유한 주식들의 총 가격을 반환한다.")
    void findMyOwnStockTotalValue() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.empty());
        when(stockPriceFindService.findRecentStockPriceAtDate(any(StockTicker.class), any(LocalDate.class)))
                .thenReturn(new RecentStockPrice("CODE", "NAME", 1.0, 0.5, 1.5, 0.5));

        MemberOwnStockValueResponse response = memberOwnStockFindService
                .findMyOwnStockTotalValue(testAuthInfo, LocalDate.now());

        assertThat(response.base()).isEqualTo(0.0);
        assertThat(response.curr()).isEqualTo(0.0);
    }

}