package org.mockInvestment.stockValue.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "kor_value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "종목코드")
    private String code;

    @Column(name = "기준일")
    private LocalDate date;

    @Column(name = "지표")
    private String indicator;

    @Column(name = "값")
    private Double value;

}
