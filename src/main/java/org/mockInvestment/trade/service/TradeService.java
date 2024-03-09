package org.mockInvestment.trade.service;

import org.mockInvestment.advice.exception.MemberNotFoundException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.advice.exception.StockOrderNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.repository.StockPriceRedisRepository;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.trade.domain.StockOrder;
import org.mockInvestment.trade.dto.StockBuyRequest;
import org.mockInvestment.trade.repository.StockOrderRedisRepository;
import org.mockInvestment.trade.repository.StockOrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class TradeService {

    final MemberRepository memberRepository;

    final StockRepository stockRepository;

    final StockOrderRepository stockOrderRepository;

    final StockPriceRedisRepository stockPriceRedisRepository;

    final StockOrderRedisRepository stockOrderRedisRepository;


    public TradeService(MemberRepository memberRepository, StockRepository stockRepository, StockOrderRepository stockOrderRepository, StockPriceRedisRepository stockPriceRedisRepository, StockOrderRedisRepository stockOrderRedisRepository) {
        this.memberRepository = memberRepository;
        this.stockRepository = stockRepository;
        this.stockOrderRepository = stockOrderRepository;
        this.stockPriceRedisRepository = stockPriceRedisRepository;
        this.stockOrderRedisRepository = stockOrderRedisRepository;
    }

    @Transactional
    public void buyStock(AuthInfo authInfo, String code, StockBuyRequest request) {
        Member member = memberRepository.findById(authInfo.id())
                .orElseThrow(MemberNotFoundException::new);
        Stock stock = stockRepository.findByCode(code)
                .orElseThrow(StockNotFoundException::new);
        StockOrder stockOrder = StockOrder.builder()
                .member(member)
                .stock(stock)
                .bidPrice(request.bidPrice())
                .volume(request.volume())
                .build();
        member.bidStock(stockOrder);
        StockOrder saved = stockOrderRepository.save(stockOrder);
        stockOrderRedisRepository.put(String.valueOf(stock.getId()), String.valueOf(saved.getId()));
    }

    @EventListener
    @Transactional
    public void concludeStockOrders(String message) {
        String[] messages = message.split(":");
        String stockId = messages[0];
        double currentPrice = Double.parseDouble(messages[1]);
        Set<String> stockOrderIds = stockOrderRedisRepository.get(stockId);
        Set<String> concludedIds = new HashSet<>();
        for (String stockOrderId : stockOrderIds) {
            StockOrder stockOrder = stockOrderRepository.findById(Long.parseLong(stockOrderId))
                    .orElseThrow(StockOrderNotFoundException::new);

            if ((Math.abs(stockOrder.getBidPrice() - currentPrice) / currentPrice) < 0.06) {
                stockOrder.conclude();
                concludedIds.add(stockOrderId);
            }
        }

        for (String concludeId : concludedIds) {
            stockOrderRedisRepository.remove(stockId, concludeId);
        }
    }
}
