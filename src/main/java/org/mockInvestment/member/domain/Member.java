package org.mockInvestment.member.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.stock.domain.MemberOwnStock;
import org.mockInvestment.stockOrder.domain.StockOrder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String role;

    private String username;

    @OneToMany(mappedBy = "member")
    private List<StockOrder> stockOrders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberOwnStock> ownStocks = new ArrayList<>();

    @OneToOne
    private Balance balance;


    @Builder
    public Member(Long id, String name, String email, String role, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.username = username;
        balance = new Balance(this);
    }

    public void bidStock(StockOrder stockOrder) {
        if (stockOrder.isBuy())
            balance.purchase(stockOrder.totalBidPrice());
        stockOrders.add(stockOrder);
    }

    public void cancelBidStock(StockOrder stockOrder) {
        stockOrder.checkCancelAuthority(id);
        balance.cancelPayment(stockOrder.totalBidPrice());
        stockOrders.remove(stockOrder);
    }

}
