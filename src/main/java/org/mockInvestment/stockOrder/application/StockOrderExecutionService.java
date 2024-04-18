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


    @EventListener
    public void checkAndApplyPendingStock(SimulationProceedInfo info) {
        List<PendingStockOrder> stockOrders = pendingStockOrderCacheRepository.findAllByMemberId(info.member().getId());

        for (PendingStockOrder order: stockOrders) {
            RecentStockPrice currentPrice = stockPriceFindService.findRecentStockPriceAtDate(order.code(), info.newDate());
            checkAndApplyPendingStockOrder(info.member(), order, currentPrice, info.newDate());
        }
    }

    @Transactional
    protected void checkAndApplyPendingStockOrder(Member member,
                                                  PendingStockOrder pendingStockOrder,
                                                  RecentStockPrice currentPrice,
                                                  LocalDate date) {
        if (pendingStockOrder.cannotExecute(currentPrice)) {
            return;
        }

        member.applyPendingStockOrder(pendingStockOrder);

        MemberOwnStock memberOwnStock = findOrCreateMemberOwnStock(pendingStockOrder, member);
        StockOrder stockOrder = stockOrderRepository.findById(pendingStockOrder.id())
                .orElseThrow();

        memberOwnStock.apply(stockOrder, date);

        deleteMemberOwnStockAndPendingStockOrder(memberOwnStock, pendingStockOrder);
    }

    private MemberOwnStock findOrCreateMemberOwnStock(PendingStockOrder pendingStockOrder, Member member) {
        return memberOwnStockRepository.findByMemberAndStockTicker(member, pendingStockOrder.code())
                .orElseGet(() -> memberOwnStockRepository.save(createMemberOwnStock(pendingStockOrder, member)));
    }

    private MemberOwnStock createMemberOwnStock(PendingStockOrder stockOrder, Member member) {
        return MemberOwnStock.builder()
                .member(member)
                .stockTicker(stockOrder.code())
                .build();
    }

    private void deleteMemberOwnStockAndPendingStockOrder(MemberOwnStock memberOwnStock, PendingStockOrder pendingStockOrder) {
        if (memberOwnStock.canDelete()) {
            memberOwnStockRepository.delete(memberOwnStock);
        }
        pendingStockOrderCacheRepository.delete(pendingStockOrder);
    }

}
