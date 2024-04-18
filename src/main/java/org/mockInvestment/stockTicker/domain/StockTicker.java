package org.mockInvestment.stockTicker.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "kor_ticker")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockTicker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "종목코드")
    private String code;

    @Column(name = "종목명")
    private String name;

    @Column(name = "시장구분")
    private String market;

    @Column(name = "기준일")
    private LocalDate date;

    @Column(name = "시가총액")
    private Double marketCapitalization;

    @Column(name = "주당배당금")
    private Double dividend;

}
