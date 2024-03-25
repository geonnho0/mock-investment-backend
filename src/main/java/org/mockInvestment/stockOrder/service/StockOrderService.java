package org.mockInvestment.stockOrder.service;

import org.mockInvestment.advice.exception.MemberNotFoundException;
import org.mockInvestment.advice.exception.PendingStockOrderNotFoundException;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.advice.exception.StockOrderNotFoundException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stock.domain.MemberOwnStock;
import org.mockInvestment.stock.repository.MemberOwnStockRepository;
import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.UpdateStockCurrentPriceEvent;
import org.mockInvestment.stock.repository.StockRepository;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrderType;
import org.mockInvestment.stockOrder.dto.*;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockOrderService {

    private final MemberRepository memberRepository;

    private final StockRepository stockRepository;

    private final StockOrderRepository stockOrderRepository;

    private final PendingStockOrderCacheRepository pendingStockOrderCacheRepository;

    private final MemberOwnStockRepository memberOwnStockRepository;


    public StockOrderService(MemberRepository memberRepository,
                             StockRepository stockRepository,
                             StockOrderRepository stockOrderRepository,
                             PendingStockOrderCacheRepository pendingStockOrderCacheRepository,
                             MemberOwnStockRepository memberOwnStockRepository) {
        this.memberRepository = memberRepository;
        this.stockRepository = stockRepository;
        this.stockOrderRepository = stockOrderRepository;
        this.pendingStockOrderCacheRepository = pendingStockOrderCacheRepository;
        this.memberOwnStockRepository = memberOwnStockRepository;
    }

    @Transactional
    public void createStockOrder(AuthInfo authInfo, String code, NewStockOrderRequest request) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        Stock stock = stockRepository.findByCode(code)
                .orElseThrow(StockNotFoundException::new);
        StockOrder stockOrder = StockOrder.builder()
                .member(member)
                .stock(stock)
                .bidPrice(request.bidPrice())
                .volume(request.volume())
                .stockOrderType(StockOrderType.parse(request.orderType()))
                .build();
        member.bidStock(stockOrder);

        StockOrder saved = stockOrderRepository.save(stockOrder);
        pendingStockOrderCacheRepository.save(PendingStockOrder.of(saved));
    }

    @Transactional
    public void cancelStockOrder(AuthInfo authInfo, StockOrderCancelRequest request) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        StockOrder stockOrder = stockOrderRepository.findById(request.orderId())
                .orElseThrow(StockOrderNotFoundException::new);
        stockOrder.checkCancelAuthority(authInfo.getId());
        member.cancelBidStock(stockOrder);

        stockOrderRepository.delete(stockOrder);
        cancelPendingStockOrder(stockOrder.getStock().getId(), stockOrder.getId());
    }

    @EventListener
    @Transactional
    public void executePendingStockOrders(UpdateStockCurrentPriceEvent updateStockCurrentPriceEvent) {
        List<PendingStockOrder> pendingStockOrders = pendingStockOrderCacheRepository.findAllByStockId(updateStockCurrentPriceEvent.stockId());
        pendingStockOrders.forEach(pendingStockOrder -> {
            if (pendingStockOrder.canConclude(updateStockCurrentPriceEvent.curr())) {
                executePendingStockOrder(pendingStockOrder);
            }
        });
    }

    private void executePendingStockOrder(PendingStockOrder pendingStockOrder) {
        StockOrder stockOrder = stockOrderRepository.findById(pendingStockOrder.orderId())
                .orElseThrow(StockOrderNotFoundException::new);
        stockOrder.execute();
        MemberOwnStock memberOwnStock = memberOwnStockRepository.findByMemberAndStock(stockOrder.getMember(), stockOrder.getStock())
                        .orElseThrow();
        memberOwnStock.apply(stockOrder.getBidPrice(), stockOrder.getVolume(), stockOrder.isBuy());
        pendingStockOrderCacheRepository.remove(pendingStockOrder);
    }

    private void cancelPendingStockOrder(long stockId, long stockOrderId) {
        PendingStockOrder pendingStockOrder = pendingStockOrderCacheRepository.findByStockIdAndStockOrderId(stockId, stockOrderId)
                .orElseThrow(PendingStockOrderNotFoundException::new);
        pendingStockOrderCacheRepository.remove(pendingStockOrder);
    }

    public StockOrderHistoriesResponse findStockOrderHistories(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        List<StockOrder> stockOrders = stockOrderRepository.findAllByMember(member);

        return createStockOrderHistoriesResponse(stockOrders);
    }

    public StockOrderHistoriesResponse findMyStockOrderHistoriesByCode(AuthInfo authInfo, String stockCode) {
        if (stockCode.isEmpty())
            return findStockOrderHistories(authInfo.getId());

        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        Stock stock = stockRepository.findByCode(stockCode)
                .orElseThrow(StockNotFoundException::new);
        List<StockOrder> stockOrders = stockOrderRepository.findAllByMemberAndStock(member, stock);

        return createStockOrderHistoriesResponse(stockOrders);
    }

    private StockOrderHistoriesResponse createStockOrderHistoriesResponse(List<StockOrder> stockOrders) {
        List<StockOrderHistoryResponse> histories = new ArrayList<>();
        for (StockOrder stockOrder : stockOrders)
            histories.add(StockOrderHistoryResponse.of(stockOrder));

        return new StockOrderHistoriesResponse(histories);
    }

}
