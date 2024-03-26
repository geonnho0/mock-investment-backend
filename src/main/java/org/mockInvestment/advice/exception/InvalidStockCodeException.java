package org.mockInvestment.advice.exception;

import org.mockInvestment.advice.exception.general.BadRequestException;

public class InvalidStockCodeException extends BadRequestException {

    private static final String MESSAGE = "주식 코드가 유효하지 않습니다.";

    public InvalidStockCodeException() {
        super(MESSAGE);
    }
}
