package org.mockInvestment.balance.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.mockInvestment.advice.exception.PaymentFailureException;
import org.mockInvestment.member.domain.Member;

@Entity
@NoArgsConstructor
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "balance")
    private Member member;

    @ColumnDefault("1000000")
    private Double balance;


    public Balance(Member member) {
        this.member = member;
    }

    public void purchase(Double price) {
        if (balance - price < 0) {
            throw new PaymentFailureException();
        }
        balance -= price;
    }
}
