package org.mockInvestment.trade.domain;

import jakarta.persistence.*;
import org.mockInvestment.member.domain.Member;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
public class StockOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @CreatedDate
    private LocalDate orderTime;

}
