package org.mockInvestment.balance.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mockInvestment.balance.exception.PaymentFailureException;
import org.mockInvestment.member.domain.Member;

@Entity
@Table(name = "balances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {

    public static final Double DEFAULT_BALANCE = 1000000.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "balance")
    private Member member;

    private Double balance;


    public Balance(Member member) {
        this.member = member;
        reset();
    }

    public void pay(Double price) {
        if (balance - price < 0)
            throw new PaymentFailureException();

        balance -= price;
    }

    public void receive(Double price) {
        balance += price;
    }

    public double getBalance() {
        return balance;
    }

    public void reset() {
        balance = DEFAULT_BALANCE;
    }

}
