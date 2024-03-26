package org.mockInvestment.advice.exception.general;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(message);
    }
}