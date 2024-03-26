package org.mockInvestment.stockOrder.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.mockInvestment.advice.exception.AuthorizationException;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stock.domain.MemberOwnStock;
import org.mockInvestment.stock.domain.Stock;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StockOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberOwnStock memberOwnStock;

    @CreatedDate
    private LocalDate orderDate;

    @ManyToOne
    private Stock stock;

    private Double bidPrice;

    private Long volume;

    @ColumnDefault("false")
    private boolean executed;

    @Enumerated(EnumType.STRING)
    private StockOrderType stockOrderType;


    @Builder
    public StockOrder(Long id, Member member, Stock stock, Double bidPrice, Long volume, StockOrderType stockOrderType) {
        this.id = id;
        this.member = member;
        this.stock = stock;
        this.bidPrice = bidPrice;
        this.volume = volume;
        this.stockOrderType = stockOrderType;
    }

    public Double totalBidPrice() {
        return volume * bidPrice;
    }

    public void execute() {
        executed = true;
    }

    public void checkCancelAuthority(long memberId) {
        if (member.getId() != memberId)
            throw new AuthorizationException();
    }

    public boolean isBuy() {
        return stockOrderType == StockOrderType.BUY;
    }

}
