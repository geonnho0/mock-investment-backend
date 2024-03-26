package org.mockInvestment.advice.exception;

import org.mockInvestment.advice.exception.general.NotFoundException;

public class PendingStockOrderNotFoundException extends NotFoundException {

    private static final String MESSAGE = "대기중인 구매 요청을 찾을 수 없습니다.";

    public PendingStockOrderNotFoundException() {
        super(MESSAGE);
    }
}
