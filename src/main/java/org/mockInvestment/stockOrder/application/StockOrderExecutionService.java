package org.mockInvestment.stockOrder.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.memberOwnStock.repository.MemberOwnStockRepository;
import org.mockInvestment.simulation.domain.SimulationProceedInfo;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.mockInvestment.stockPrice.application.StockPriceFindService;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockOrderExecutionService {

    private final MemberOwnStockRepository memberOwnStockRepository;

    private final PendingStockOrderCacheRepository pendingStockOrderCacheRepository;

    private final StockPriceFindService stockPriceFindService;

    private final StockOrderRepository stockOrderRepository;

    private final StockTickerRepository stockTickerRepository;


    @EventListener
    public void checkAndApplyPendingStock(SimulationProceedInfo info) {
        List<PendingStockOrder> stockOrders = pendingStockOrderCacheRepository.findAllByMemberId(info.member().getId());

        for (PendingStockOrder order: stockOrders) {
            StockTicker stockTicker = stockTickerRepository.findByCode(order.code())
                    .orElseThrow(StockTickerNotFoundException::new);
            RecentStockPrice currentPrice = stockPriceFindService.findRecentStockPriceAtDate(stockTicker, info.newDate());
            checkAndApplyPendingStockOrder(info.member(), order, currentPrice, info.newDate());
        }
    }

    @Transactional
    protected void checkAndApplyPendingStockOrder(
            Member member,
            PendingStockOrder pendingStockOrder,
            RecentStockPrice currentPrice,
            LocalDate date
    ) {
        if (pendingStockOrder.cannotExecute(currentPrice)) {
            return;
        }

        applyPendingStockOrder(member, pendingStockOrder, date);
    }

    private void applyPendingStockOrder(Member member, PendingStockOrder pendingStockOrder, LocalDate date) {
        member.applyPendingStockOrder(pendingStockOrder);
        MemberOwnStock memberOwnStock = applyAndGetMemberOwnStock(member, pendingStockOrder, date);
        deleteMemberOwnStockAndPendingStockOrder(member, memberOwnStock, pendingStockOrder);
    }

    private MemberOwnStock applyAndGetMemberOwnStock(Member member, PendingStockOrder pendingStockOrder, LocalDate date) {
        MemberOwnStock memberOwnStock = findOrCreateMemberOwnStock(pendingStockOrder, member);
        StockOrder stockOrder = stockOrderRepository.findById(pendingStockOrder.id())
                .orElseThrow();
        memberOwnStock.apply(stockOrder, date);
        return memberOwnStock;
    }

    private MemberOwnStock findOrCreateMemberOwnStock(PendingStockOrder pendingStockOrder, Member member) {
        StockTicker stockTicker = stockTickerRepository.findByCode(pendingStockOrder.code())
                .orElseThrow(StockTickerNotFoundException::new);
        MemberOwnStock memberOwnStock = memberOwnStockRepository.findByMemberAndStockTicker(member, stockTicker)
                .orElseGet(() -> memberOwnStockRepository.save(createMemberOwnStock(pendingStockOrder, member)));
        member.addOwnStock(memberOwnStock);
        return memberOwnStock;
    }

    private MemberOwnStock createMemberOwnStock(PendingStockOrder stockOrder, Member member) {
        StockTicker stockTicker = stockTickerRepository.findByCode(stockOrder.code())
                .orElseThrow(StockTickerNotFoundException::new);
        return MemberOwnStock.builder()
                .member(member)
                .stockTicker(stockTicker)
                .build();
    }

    private void deleteMemberOwnStockAndPendingStockOrder(Member member, MemberOwnStock memberOwnStock, PendingStockOrder pendingStockOrder) {
        if (memberOwnStock.canDelete()) {
            member.deleteOwnStock(memberOwnStock);
            memberOwnStockRepository.delete(memberOwnStock);
        }
        pendingStockOrderCacheRepository.delete(pendingStockOrder);
    }

}
