package org.mockInvestment.simulation.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.memberOwnStock.repository.MemberOwnStockRepository;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationStartService {

    private final MemberRepository memberRepository;

    private final StockOrderRepository stockOrderRepository;

    private final MemberOwnStockRepository memberOwnStockRepository;

    private final PendingStockOrderCacheRepository pendingStockOrderCacheRepository;


    public void restartSimulation(AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        member.resetBalance();

        List<StockOrder> stockOrders = member.getStockOrders();
        deleteStockOrders(stockOrders);
        List<MemberOwnStock> ownStocks = member.getOwnStocks();
        deleteMemberOwnStocks(ownStocks);
    }

    private void deleteStockOrders(List<StockOrder> stockOrders) {
        stockOrderRepository.deleteAll(stockOrders);
        pendingStockOrderCacheRepository.deleteAll();
    }

    private void deleteMemberOwnStocks(List<MemberOwnStock> memberOwnStocks) {
        memberOwnStockRepository.deleteAll(memberOwnStocks);
    }

}
