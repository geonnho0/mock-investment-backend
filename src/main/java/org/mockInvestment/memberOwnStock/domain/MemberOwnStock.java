package org.mockInvestment.memberOwnStock.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockTicker.domain.StockTicker;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "member_own_stocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberOwnStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private StockTicker stockTicker;

    private Long quantity;

    private Double averageCost;

    @Builder
    public MemberOwnStock(Long id, Member member, StockTicker stockTicker) {
        this.id = id;
        this.member = member;
        this.stockTicker = stockTicker;
        averageCost = 0.0;
        quantity = 0L;
    }

    public void apply(StockOrder stockOrder, LocalDate date) {
        if (stockOrder.isBuy())
            buy(stockOrder);
        else
            sell(stockOrder);
        stockOrder.execute(date);
    }

    private void buy(StockOrder stockOrder) {
        double total = totalValue() + stockOrder.totalBidPrice();
        this.quantity += stockOrder.getQuantity();
        updateAverageCost(total);
    }

    private void sell(StockOrder stockOrder) {
        double total = totalValue() - stockOrder.totalBidPrice();
        stockOrder.checkQuantity(quantity);
        this.quantity -= stockOrder.getQuantity();
        updateAverageCost(total);
    }

    private double totalValue() {
        return averageCost * quantity;
    }

    private void updateAverageCost(double total) {
        averageCost = total / (double) quantity;
    }

    public boolean canDelete() {
        return quantity == 0;
    }

    public double getTotalValue() {
        return totalValue();
    }

}
