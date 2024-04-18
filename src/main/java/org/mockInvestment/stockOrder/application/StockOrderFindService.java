package org.mockInvestment.stockOrder.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.dto.*;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockOrderFindService {

    private final MemberRepository memberRepository;

    private final StockTickerRepository stockTickerRepository;

    private final StockOrderRepository stockOrderRepository;


    public StockOrderHistoriesResponse findAllStockOrderHistories(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        List<StockOrder> stockOrders = stockOrderRepository.findAllByMember(member);

        return createStockOrderHistoriesResponse(stockOrders);
    }

    public StockOrderHistoriesResponse findMyStockOrderHistoriesByCode(AuthInfo authInfo, String stockCode) {
        if (stockCode.isEmpty())
            return findAllStockOrderHistories(authInfo.getId());

        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        StockTicker stockTicker = stockTickerRepository.findTop1ByCodeOrderByDate(stockCode).get(0);
        List<StockOrder> stockOrders = stockOrderRepository.findAllByMemberAndStockTicker(member, stockTicker.getCode());

        return createStockOrderHistoriesResponse(stockOrders);
    }

    private StockOrderHistoriesResponse createStockOrderHistoriesResponse(List<StockOrder> stockOrders) {
        List<StockOrderHistoryResponse> histories = new ArrayList<>();
        for (StockOrder stockOrder : stockOrders) {
            StockTicker stockTicker = stockTickerRepository.findTop1ByCodeOrderByDate(stockOrder.getStockTicker()).get(0);
            histories.add(StockOrderHistoryResponse.of(stockOrder, stockTicker));
        }

        return new StockOrderHistoriesResponse(histories);
    }

}
