package org.mockInvestment.advice.exception;

import org.mockInvestment.advice.exception.general.NotFoundException;

public class StockNotFoundException extends NotFoundException {

    private static final String MESSAGE = "주식 정보를 찾을 수 없습니다.";


    public StockNotFoundException() {
        super(MESSAGE);
    }

}
