package org.mockInvestment.comment.dto.response;

import org.mockInvestment.comment.domain.Comment;

import java.time.LocalDateTime;

public record ReplyResponse(Long id, String content, String nickname, int likeCount,
                            boolean liked, boolean updated, boolean blocked, LocalDateTime createdAt) {

    public static ReplyResponse of(Comment reply, boolean liked) {
        return new ReplyResponse(reply.getId(), reply.getContent(), reply.getMember().getNickname(), reply.getLikeCount(),
                liked, reply.isUpdated(), reply.isBlocked(), reply.getCreatedAt());
    }

}
