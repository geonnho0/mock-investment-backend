package org.mockInvestment.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(@NotBlank String content) {
}
