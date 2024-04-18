package org.mockInvestment.comment.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.domain.CommentLike;
import org.mockInvestment.comment.dto.response.CommentLikeToggleResponse;
import org.mockInvestment.comment.exception.CommentNotFoundException;
import org.mockInvestment.comment.repository.CommentLikeRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentLikeToggleService {

    private final CommentRepository commentRepository;

    private final CommentLikeRepository commentLikeRepository;

    private final MemberRepository memberRepository;


    public CommentLikeToggleResponse toggleCommentLike(AuthInfo authInfo, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        Optional<CommentLike> commentLike = commentLikeRepository.findByCommentAndMember(comment, member);

        if (commentLike.isEmpty()) {
            addCommentLike(comment, member);
            return createCommentLikeToggleResponse(comment, true);
        }

        deleteCommentLike(comment, commentLike.get());
        return createCommentLikeToggleResponse(comment, false);
    }

    private void addCommentLike(Comment comment, Member member) {
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();

        comment.addCommentLike(commentLike);
        commentLikeRepository.save(commentLike);
        commentRepository.increaseLikeCount(comment.getId());
    }

    private void deleteCommentLike(Comment comment, CommentLike commentLike) {
        comment.deleteCommentLike(commentLike);
        commentRepository.decreaseLikeCount(comment.getId());
    }

    private CommentLikeToggleResponse createCommentLikeToggleResponse(Comment comment, boolean liked) {
        int likeCount = comment.getLikeCount() + (liked ? 1 : -1);
        return new CommentLikeToggleResponse(likeCount, liked);
    }

}

