package org.mockInvestment.stockTicker.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.domain.StockTickerLike;
import org.mockInvestment.stockTicker.repository.StockTickerLikeRepository;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockTickerLikeToggleServiceTest extends MockTest {

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StockTickerLikeRepository stockTickerLikeRepository;

    @InjectMocks
    private StockTickerLikeToggleService stockTickerLikeToggleService;

    private StockTicker stockTicker;


    @BeforeEach
    void setUp() {
        stockTicker = new StockTicker("CODE", "NAME");
    }

    @Test
    @DisplayName("특정 주식을 좋아요를 추가한다.")
    void addLike() {
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(stockTicker));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerLikeRepository.findByStockTickerAndMember(any(StockTicker.class), any(Member.class)))
                .thenReturn(Optional.empty());

        stockTickerLikeToggleService.toggleLike(stockTicker.getCode(), testAuthInfo);

        assertAll(
                () -> assertThat(testMember.getStockTickerLikes().size()).isEqualTo(1),
                () -> verify(stockTickerLikeRepository).save(any(StockTickerLike.class))
        );
    }

    @Test
    @DisplayName("특정 주식의 좋아요를 취소한다.")
    void deleteLike() {
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(stockTicker));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        StockTickerLike stockTickerLike = new StockTickerLike(stockTicker, testMember);
        testMember.addStockTickerLike(stockTickerLike);
        when(stockTickerLikeRepository.findByStockTickerAndMember(any(StockTicker.class), any(Member.class)))
                .thenReturn(Optional.of(stockTickerLike));

        stockTickerLikeToggleService.toggleLike(stockTicker.getCode(), testAuthInfo);

        assertAll(
                () -> assertThat(testMember.getStockTickerLikes().isEmpty()).isEqualTo(true),
                () -> verify(stockTickerLikeRepository).delete(stockTickerLike)
        );
    }

}