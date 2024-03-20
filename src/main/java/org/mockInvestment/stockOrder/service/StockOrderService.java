package org.mockInvestment.stockOrder.service;

import org.mockInvestment.advice.exception.MemberNotFoundException;
import org.mockInvestment.advice.exception.PendingStockOrderNotFoundException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.advice.exception.StockOrderNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.dto.StockPurchaseCancelRequest;
import org.mockInvestment.stockOrder.dto.StockPurchaseRequest;
import org.mockInvestment.stockOrder.dto.StockCurrentPrice;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockOrderService {

    private final MemberRepository memberRepository;

    private final StockRepository stockRepository;

    private final StockOrderRepository stockOrderRepository;

    private final PendingStockOrderCacheRepository pendingStockOrderCacheRepository;


    public StockOrderService(MemberRepository memberRepository,
                             StockRepository stockRepository,
                             StockOrderRepository stockOrderRepository,
                             PendingStockOrderCacheRepository pendingStockOrderCacheRepository) {
        this.memberRepository = memberRepository;
        this.stockRepository = stockRepository;
        this.stockOrderRepository = stockOrderRepository;
        this.pendingStockOrderCacheRepository = pendingStockOrderCacheRepository;
    }

    @Transactional
    public void requestStockPurchase(AuthInfo authInfo, String code, StockPurchaseRequest request) {
        Member member = memberRepository.findById(authInfo.getId())
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
        pendingStockOrderCacheRepository.save(PendingStockOrder.of(saved));
    }

    @Transactional
    public void cancelStockPurchase(AuthInfo authInfo, StockPurchaseCancelRequest request) {
        StockOrder stockOrder = stockOrderRepository.findById(request.orderId())
                .orElseThrow(StockOrderNotFoundException::new);
        stockOrder.checkCancelAuthority(authInfo.getId());

        stockOrderRepository.delete(stockOrder);
        cancelPendingStockOrder(stockOrder.getStock().getId(), stockOrder.getId());
    }

    @EventListener
    public void executePendingStockOrders(StockCurrentPrice stockCurrentPrice) {
        List<PendingStockOrder> pendingStockOrders = pendingStockOrderCacheRepository.findAllByStockId(stockCurrentPrice.stockId());
        pendingStockOrders.forEach(pendingStockOrder -> {
            if (pendingStockOrder.canConclude(stockCurrentPrice.curr())) {
                executePendingStockOrder(pendingStockOrder);
            }
        });
    }

    private void executePendingStockOrder(PendingStockOrder pendingStockOrder) {
        StockOrder stockOrder = stockOrderRepository.findById(pendingStockOrder.orderId())
                .orElseThrow(StockOrderNotFoundException::new);
        stockOrder.execute();
        pendingStockOrderCacheRepository.remove(pendingStockOrder);
    }

    private void cancelPendingStockOrder(long stockId, long stockOrderId) {
        PendingStockOrder pendingStockOrder = pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(stockId, stockOrderId)
                .orElseThrow(PendingStockOrderNotFoundException::new);
        pendingStockOrderCacheRepository.remove(pendingStockOrder);
    }

}
