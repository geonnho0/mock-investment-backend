package org.mockInvestment.stockTicker.exception;

import org.mockInvestment.global.error.exception.general.NotFoundException;

public class StockTickerNotFoundException extends NotFoundException {

    private static final String MESSAGE = "주식 티커를 찾을 수 없습니다.";


    public StockTickerNotFoundException() {
        super(MESSAGE);
    }

}
