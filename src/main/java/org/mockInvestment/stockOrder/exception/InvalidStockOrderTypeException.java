package org.mockInvestment.stockOrder.exception;

import org.mockInvestment.global.error.exception.general.BadRequestException;

public class InvalidStockOrderTypeException extends BadRequestException {

    private static final String MESSAGE = "주식 구매 요청이 유효하지 않습니다.";

    public InvalidStockOrderTypeException() {
        super(MESSAGE);
    }
}


