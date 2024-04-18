package org.mockInvestment.comment.dto.response;

import org.mockInvestment.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(Long id, String nickname, String content, int likeCount, boolean liked,
                              boolean updated, boolean blocked, List<ReplyResponse> replies, LocalDateTime createdAt) {

    public static CommentResponse of(Comment comment, boolean liked, List<ReplyResponse> replies) {
        return new CommentResponse(comment.getId(), comment.getMember().getNickname(), comment.getContent(), comment.getLikeCount(),
                liked, comment.isUpdated(), comment.isBlocked(), replies, comment.getCreatedAt());
    }

}
