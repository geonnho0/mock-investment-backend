package org.mockInvestment.advice.exception;

import org.mockInvestment.advice.exception.general.BusinessException;

public class SseEmitterEventSendException extends BusinessException {

    private static final String MESSAGE = "SseEmitter 이벤트 전송이 실패했습니다.";

    public SseEmitterEventSendException() {
        super(MESSAGE);
    }
}
