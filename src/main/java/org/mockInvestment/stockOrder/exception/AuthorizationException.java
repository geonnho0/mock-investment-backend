package org.mockInvestment.stockOrder.exception;

import org.mockInvestment.global.error.exception.general.ForbiddenException;

public class AuthorizationException extends ForbiddenException {

    private static final String MESSAGE = "권한이 없습니다.";

    public AuthorizationException() {
        super(MESSAGE);
    }
}