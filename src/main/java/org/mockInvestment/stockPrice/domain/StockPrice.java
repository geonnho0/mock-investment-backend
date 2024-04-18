package org.mockInvestment.stockPrice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPrice {

    @Column(name = "시가")
    private Double open;

    @Column(name = "고가")
    private Double high;

    @Column(name = "저가")
    private Double low;

    @Column(name = "종가")
    private Double close;

}
