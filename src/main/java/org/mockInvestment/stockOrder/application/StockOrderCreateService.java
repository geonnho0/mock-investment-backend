package org.mockInvestment.stockOrder.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockOrder.domain.StockOrderType;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
import org.mockInvestment.stockOrder.repository.PendingStockOrderCacheRepository;
import org.mockInvestment.stockOrder.repository.StockOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockOrderCreateService {

    private final MemberRepository memberRepository;

    private final StockOrderRepository stockOrderRepository;

    private final PendingStockOrderCacheRepository pendingStockOrderCacheRepository;


    public void createStockOrder(AuthInfo authInfo, String code, NewStockOrderRequest request) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        StockOrder stockOrder = createStockOrder(member, code, request);
        StockOrder saved = stockOrderRepository.save(stockOrder);

        PendingStockOrder pendingStockOrder = PendingStockOrder.from(saved, member);
        pendingStockOrderCacheRepository.save(pendingStockOrder);
    }

    private StockOrder createStockOrder(Member member, String code, NewStockOrderRequest request) {
        return StockOrder.builder()
                .member(member)
                .stockTicker(code)
                .bidPrice(request.bidPrice())
                .quantity(request.quantity())
                .stockOrderType(StockOrderType.parse(request.orderType()))
                .orderDate(request.orderDate())
                .build();
    }

}
