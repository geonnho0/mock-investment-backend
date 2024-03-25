package org.mockInvestment.stock.service;

import org.mockInvestment.advice.exception.MemberNotFoundException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stock.dto.MemberOwnStockResponse;
import org.mockInvestment.stock.dto.MemberOwnStocksResponse;
import org.mockInvestment.stock.domain.MemberOwnStock;
import org.mockInvestment.stock.domain.RecentStockInfo;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceCandle;
import org.mockInvestment.stock.dto.*;
import org.mockInvestment.stock.repository.RecentStockInfoCacheRepository;
import org.mockInvestment.stock.repository.StockPriceCandleRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockInfoService {

    private final MemberRepository memberRepository;

    private final StockRepository stockRepository;

    private final StockPriceCandleRepository stockPriceCandleRepository;

    private final RecentStockInfoCacheRepository recentStockInfoCacheRepository;


    public StockInfoService(MemberRepository memberRepository,
                            StockRepository stockRepository,
                            StockPriceCandleRepository stockPriceCandleRepository,
                            RecentStockInfoCacheRepository recentStockInfoCacheRepository) {
        this.memberRepository = memberRepository;
        this.stockRepository = stockRepository;
        this.stockPriceCandleRepository = stockPriceCandleRepository;
        this.recentStockInfoCacheRepository = recentStockInfoCacheRepository;
    }

    public StockInfoResponse findStockInfo(String stockCode) {
        RecentStockInfo stockInfo = recentStockInfoCacheRepository.findByStockCode(stockCode)
                .orElseGet(() -> {
                    RecentStockInfo findStockInfo = findRecentStockInfo(stockCode);
                    recentStockInfoCacheRepository.saveByCode(stockCode, findStockInfo);
                    return findStockInfo;
                });
        double base = stockInfo.base();
        double currentPrice = stockInfo.curr();
        return new StockInfoResponse(stockInfo.name(), stockInfo.symbol(), base, currentPrice);
    }

    private RecentStockInfo findRecentStockInfo(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockPriceCandle> stockPriceCandles = stockPriceCandleRepository.findTop2ByStockOrderByDateDesc(stock);
        StockPriceCandle recentCandle = stockPriceCandles.get(0);
        return new RecentStockInfo(stockPriceCandles.get(1).getClose(), stock, recentCandle);
    }

    public MemberOwnStocksResponse findMyOwnStocks(AuthInfo authInfo, String stockCode) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        List<MemberOwnStock> ownStocks = member.getOwnStocks().stream()
                .filter((ownStock -> {
                    if (stockCode.isEmpty())
                        return true;
                    return stockCode.equals(ownStock.getStock().getCode());
                })).toList();
        List<MemberOwnStockResponse> responses = new ArrayList<>();
        for (MemberOwnStock ownStock : ownStocks)
            responses.add(MemberOwnStockResponse.of(ownStock));
        return new MemberOwnStocksResponse(responses);
    }

}
