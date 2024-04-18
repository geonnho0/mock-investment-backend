package org.mockInvestment.comment.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.exception.CommentNotFoundException;
import org.mockInvestment.comment.repository.CommentLikeRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentDeleteService {

    private final CommentRepository commentRepository;

    private final CommentLikeRepository commentLikeRepository;

    private final MemberRepository memberRepository;


    public void deleteComment(AuthInfo authInfo, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        comment.validateAuthorization(member);

        commentLikeRepository.deleteAllByComment(comment);
        deleteCommentOrReply(comment);
    }

    private void deleteCommentOrReply(Comment comment) {
        if (comment.isParent()) {
            deleteParent(comment);
            return;
        }
        deleteChild(comment);
    }

    private void deleteParent(Comment parent) {
        if (parent.hasNoReply()) {
            commentRepository.delete(parent);
            return;
        }
        parent.willBeDeleted();
    }

    private void deleteChild(Comment reply) {
        Comment parent = reply.getParent();
        parent.deleteChild(reply);
        commentRepository.delete(reply);

        if (parent.canDelete())
            commentRepository.delete(parent);
    }

}

