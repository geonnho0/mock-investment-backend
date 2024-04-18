package org.mockInvestment.stockTicker.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.domain.StockTickerLike;
import org.mockInvestment.stockTicker.dto.StockTickerLikeResponse;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerLikeRepository;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockTickerLikeToggleService {

    private final StockTickerRepository stockTickerRepository;

    private final MemberRepository memberRepository;

    private final StockTickerLikeRepository stockTickerLikeRepository;


    public StockTickerLikeResponse toggleLike(String stockCode, AuthInfo authInfo) {
        StockTicker stockTicker = stockTickerRepository.findTop1ByCodeOrderByDate(stockCode).get(0);

        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        Optional<StockTickerLike> stockTickerLike = stockTickerLikeRepository.findByStockTickerAndMember(stockTicker.getCode(), member);

        if (stockTickerLike.isEmpty()) {
            addLike(stockTicker, member);
            return new StockTickerLikeResponse(true);
        }

        deleteLike(member, stockTickerLike.get());
        return new StockTickerLikeResponse(false);
    }

    private void addLike(StockTicker stockTicker, Member member) {
        StockTickerLike stockTickerLike = StockTickerLike.builder()
                .stockTicker(stockTicker.getCode())
                .member(member)
                .build();

        member.addStockTickerLike(stockTickerLike);
        stockTickerLikeRepository.save(stockTickerLike);
    }

    private void deleteLike(Member member, StockTickerLike stockTickerLike) {
        member.deleteStockTickerLike(stockTickerLike);
        stockTickerLikeRepository.delete(stockTickerLike);
    }

}
