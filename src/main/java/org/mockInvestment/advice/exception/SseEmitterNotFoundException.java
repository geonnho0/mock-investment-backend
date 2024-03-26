package org.mockInvestment.advice.exception;

import org.mockInvestment.advice.exception.general.NotFoundException;

public class SseEmitterNotFoundException extends NotFoundException {

    private static final String MESSAGE = "SSE 객체를 찾을 수 없습니다.";

    public SseEmitterNotFoundException() {
        super(MESSAGE);
    }
}
