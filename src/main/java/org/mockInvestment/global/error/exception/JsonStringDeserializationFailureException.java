package org.mockInvestment.global.error.exception;

import org.mockInvestment.global.error.exception.general.BusinessException;

public class JsonStringDeserializationFailureException extends BusinessException {

    static final String MESSAGE = "역직렬화에 실패했습니다.";

    public JsonStringDeserializationFailureException() {
        super(MESSAGE);
    }
}
