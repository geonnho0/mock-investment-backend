package org.mockInvestment.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.memberOwnStock.domain.MemberOwnStock;
import org.mockInvestment.simulation.domain.MemberSimulationDate;
import org.mockInvestment.stockOrder.domain.PendingStockOrder;
import org.mockInvestment.stockOrder.domain.StockOrder;
import org.mockInvestment.stockTicker.domain.StockTickerLike;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String role;

    private String username;

    private String nickname;

    @OneToMany(mappedBy = "member")
    private List<StockOrder> stockOrders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberOwnStock> ownStocks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<StockTickerLike> stockTickerLikes = new ArrayList<>();

    @OneToOne
    private Balance balance;

    @OneToOne
    private MemberSimulationDate simulationDate;


    @Builder
    public Member(Long id, String name, String email, String role, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.username = username;
        nickname = username;
        balance = new Balance(this);
    }

    public void applyPendingStockOrder(PendingStockOrder stockOrder) {
        if (stockOrder.isBuy())
            buyStock(stockOrder);
        else
            sellStock(stockOrder);
    }

    public void buyStock(PendingStockOrder stockOrder) {
        balance.pay(stockOrder.totalBidPrice());
    }

    public void sellStock(PendingStockOrder stockOrder) {
        balance.receive(stockOrder.totalBidPrice());
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean equals(Member member) {
        return this.id == member.getId();
    }

    public void updateEMail(String email) {
        this.email = email;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void resetBalance() {
        balance.reset();
        simulationDate.reset();
    }

    public void addStockTickerLike(StockTickerLike stockTickerLike) {
        stockTickerLikes.add(stockTickerLike);
    }

    public void deleteStockTickerLike(StockTickerLike stockTickerLike) {
        stockTickerLikes.remove(stockTickerLike);
        stockTickerLike.delete();
    }

    public void startSimulation(MemberSimulationDate simulationDate) {
        this.simulationDate = simulationDate;
    }

}
