package org.mockInvestment.trade.domain;

import jakarta.persistence.*;
import org.mockInvestment.member.domain.Member;

@Entity
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "balance")
    private Member member;

    private Double balance;
}
