package org.mockInvestment.stockOrder.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.mockInvestment.stockOrder.exception.AuthorizationException;
import org.mockInvestment.stockOrder.exception.InvalidStockOrderException;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "stock_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private LocalDate orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private StockTicker stockTicker;

    private Double bidPrice;

    private Long quantity;

    @Enumerated(EnumType.STRING)
    private StockOrderType stockOrderType;

    @ColumnDefault("false")
    private boolean executed;

    private LocalDate executedDate;


    @Builder
    public StockOrder(Long id, Member member, StockTicker stockTicker, Double bidPrice,
                      Long quantity, StockOrderType stockOrderType, LocalDate orderDate) {
        this.id = id;
        this.member = member;
        this.stockTicker = stockTicker;
        this.bidPrice = bidPrice;
        this.quantity = quantity;
        this.stockOrderType = stockOrderType;
        this.orderDate = orderDate;
    }

    public Double totalBidPrice() {
        return quantity * bidPrice;
    }

    public boolean isBuy() {
        return stockOrderType == StockOrderType.BUY;
    }

    public void checkQuantity(long quantity) {
        if (quantity - this.quantity < 0)
            throw new InvalidStockOrderException();
    }

    public void execute(LocalDate executedDate) {
        executed = true;
        this.executedDate = executedDate;
    }

}
