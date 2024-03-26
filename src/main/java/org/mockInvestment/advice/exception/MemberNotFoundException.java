package org.mockInvestment.advice.exception;

import org.mockInvestment.advice.exception.general.NotFoundException;

public class MemberNotFoundException extends NotFoundException {

    private static final String MESSAGE = "멤버 정보를 찾을 수 없습니다.";

    public MemberNotFoundException() {
        super(MESSAGE);
    }
}
