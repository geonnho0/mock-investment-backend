package org.mockInvestment.comment.exception;

import org.mockInvestment.global.error.exception.general.BadRequestException;

public class DuplicatedCommentReportException extends BadRequestException {

    private static final String MESSAGE = "이미 신고한 댓글입니다.";

    public DuplicatedCommentReportException() {
        super(MESSAGE);
    }
}
