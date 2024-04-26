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
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
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


    public StockPricesResponse findStockPricesByCodeAtDate(List<String> stockCodes, LocalDate date) {
        List<StockTicker> stockTickers = stockCodes.stream()
                .map(code -> stockTickerRepository.findByCode(code)
                        .orElseThrow(StockTickerNotFoundException::new))
                .toList();
        return findStockPricesAtDate(stockTickers, date);
    }

    public StockPricesResponse findAllLikedStockPricesAtDate(LocalDate date, AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        List<StockTicker> stockTickers = stockTickerLikeRepository.findAllByMember(member).stream()
                .map(StockTickerLike::getStockTicker)
                .toList();

        return findStockPricesAtDate(stockTickers, date);
    }

    public RecentStockPrice findRecentStockPriceAtDate(StockTicker stockTicker, LocalDate date) {
        List<StockPriceCandle> stockPriceCandles = stockPriceCandleRepository
                .findTop2ByStockTickerAndDateLessThanEqualOrderByDateDesc(stockTicker, date);
        return createRecentStockPrice(stockTicker, stockPriceCandles);
    }

    public StockPriceResponse findStockPriceAtDate(StockTicker stockTicker, LocalDate date) {
        RecentStockPrice recentStockPrice = findRecentStockPriceAtDate(stockTicker, date);
        return StockPriceResponse.of(stockTicker, recentStockPrice);
    }

    private StockPricesResponse findStockPricesAtDate(List<StockTicker> stockTickers, LocalDate date) {
        List<StockPriceResponse> responses = new ArrayList<>();
        for (StockTicker stockTicker : stockTickers)
            responses.add(findStockPriceAtDate(stockTicker, date));
        return new StockPricesResponse(responses);
    }

    private RecentStockPrice createRecentStockPrice(StockTicker stockTicker, List<StockPriceCandle> stockPriceCandles) {
        StockPriceCandle current = stockPriceCandles.get(0);
        double curr = current.getClose();
        double base = stockPriceCandles.get(1).getClose();
        double high = current.getHigh();
        double low = current.getLow();
        return new RecentStockPrice(stockTicker.getCode(), stockTicker.getName(), curr, base, high, low);
    }

}
