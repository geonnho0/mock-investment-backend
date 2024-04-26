package org.mockInvestment.memberOwnStock.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStockResponse;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStockValueResponse;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStocksResponse;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberOwnStockFindService {

    private final MemberRepository memberRepository;

    private final StockTickerRepository stockTickerRepository;

    private final StockPriceFindService stockPriceFindService;


    public MemberOwnStocksResponse findMyOwnStocksFilteredByCode(AuthInfo authInfo, String stockCode) {
        List<MemberOwnStock> ownStocks = findMemberOwnStocks(authInfo, stockCode);
        List<MemberOwnStockResponse> responses = new ArrayList<>();

        for (MemberOwnStock ownStock : ownStocks) {
            responses.add(MemberOwnStockResponse.of(ownStock, ownStock.getStockTicker()));
        }

        return new MemberOwnStocksResponse(responses);
    }

    private List<MemberOwnStock> findMemberOwnStocks(AuthInfo authInfo, String stockCode) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        return member.getOwnStocks().stream()
                .filter((ownStock -> {
                    if (stockCode.isEmpty())
                        return true;
                    return stockCode.equals(ownStock.getStockTicker());
                })).toList();
    }

    public MemberOwnStockValueResponse findMyOwnStockTotalValue(AuthInfo authInfo, LocalDate date) {
        List<MemberOwnStock> ownStocks = findMemberOwnStocks(authInfo, "");
        double basePrice = 0.0;
        double currentPrice = 0.0;
        for (MemberOwnStock ownStock : ownStocks) {
            basePrice += ownStock.getTotalValue();
            RecentStockPrice recent = stockPriceFindService.findRecentStockPriceAtDate(ownStock.getStockTicker(), date);
            currentPrice += recent.curr() * ownStock.getQuantity();
        }
        return new MemberOwnStockValueResponse(currentPrice, basePrice);
    }

}
