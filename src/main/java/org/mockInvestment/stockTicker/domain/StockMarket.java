package org.mockInvestment.stockTicker.domain;

import lombok.Getter;

@Getter
public enum StockMarket {

    NYSE("NYSE"), NASDAQ("NASDAQ"), NYSEAMERICAN("NYSEAMERICAN"),
    OTCMKTS("OTCMKTS"), BATS("BATS");

    private final String value;


    StockMarket(String value) {
        this.value = value;
    }

}
