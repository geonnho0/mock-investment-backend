package org.mockInvestment.stockPrice.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockPrice.domain.StockPriceCandle;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;
import org.mockInvestment.stockPrice.dto.StockPriceResponse;
import org.mockInvestment.stockPrice.dto.StockPricesResponse;
import org.mockInvestment.stockPrice.repository.StockPriceCandleRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.domain.StockTickerLike;
import org.mockInvestment.stockTicker.repository.StockTickerLikeRepository;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockPriceFindService {

    private final StockTickerRepository stockTickerRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final MemberRepository memberRepository;

    private final StockTickerLikeRepository stockTickerLikeRepository;


    public StockPricesResponse findStockPricesAtDate(List<String> stockCodes, LocalDate date) {
        List<StockPriceResponse> responses = new ArrayList<>();
        for (String code : stockCodes)
            responses.add(findStockPriceAtDate(code, date));
        return new StockPricesResponse(responses);
    }

    public StockPriceResponse findStockPriceAtDate(String stockCode, LocalDate date) {
        RecentStockPrice recentStockPrice = findRecentStockPriceAtDate(stockCode, date);
        return StockPriceResponse.of(stockCode, recentStockPrice);
    }

    public StockPricesResponse findLikedStockPricesAtDate(LocalDate date, AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        List<String> stockCodes = stockTickerLikeRepository.findAllByMember(member).stream()
                .map(StockTickerLike::getStockTicker)
                .toList();

        return findStockPricesAtDate(stockCodes, date);
    }

    public RecentStockPrice findRecentStockPriceAtDate(String stockCode, LocalDate date) {
        StockTicker stockTicker = stockTickerRepository.findTop1ByCodeAndDateLessThanEqualOrderByDateDesc(stockCode, date).get(0);
        List<StockPriceCandle> stockPriceCandles = stockPriceCandleRepository.findTop2ByStockTickerAndDateLessThanEqualOrderByDateDesc(stockCode, date);
        StockPriceCandle current = stockPriceCandles.get(0);
        double curr = current.getClose();
        double base = stockPriceCandles.get(1).getClose();
        double high = current.getHigh();
        double low = current.getLow();
        return new RecentStockPrice(stockCode, stockTicker.getName(), curr, base, high, low);
    }

}
