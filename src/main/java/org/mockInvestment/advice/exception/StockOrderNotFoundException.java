package org.mockInvestment.advice.exception;

import org.mockInvestment.advice.exception.general.NotFoundException;

public class StockOrderNotFoundException extends NotFoundException {

    private static final String MESSAGE = "주식 거래 내역을 찾을 수 없습니다.";

    public StockOrderNotFoundException() {
        super(MESSAGE);
    }
}
