package org.mockInvestment.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NewCommentReportRequest(@NotBlank(message = "신고 내용은 1자 이상 255자 이하 여야 합니다.") String message) {
}
