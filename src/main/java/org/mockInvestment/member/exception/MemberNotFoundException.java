package org.mockInvestment.member.exception;

import org.mockInvestment.global.error.exception.general.NotFoundException;

public class MemberNotFoundException extends NotFoundException {

    private static final String MESSAGE = "멤버 정보를 찾을 수 없습니다.";

    public MemberNotFoundException() {
        super(MESSAGE);
    }
}
