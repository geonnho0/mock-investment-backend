package org.mockInvestment.comment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.domain.CommentLike;
import org.mockInvestment.comment.dto.response.CommentLikeToggleResponse;
import org.mockInvestment.comment.repository.CommentLikeRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class CommentLikeToggleServiceTest extends CommentServiceTest {

    @InjectMocks
    private CommentLikeToggleService commentLikeToggleService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private MemberRepository memberRepository;


    @Test
    @DisplayName("특정 댓글의 좋아요를 1 올린다.")
    void likeComment_up() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testComment));
        when(commentLikeRepository.findByCommentAndMember(any(Comment.class), any(Member.class)))
                .thenReturn(Optional.empty());

        CommentLikeToggleResponse response = commentLikeToggleService.toggleCommentLike(testAuthInfo, testComment.getId());

        assertAll(
                () -> assertThat(response.likeCount()).isEqualTo(testComment.getLikeCount() + 1),
                () -> assertThat(response.liked()).isEqualTo(true)
        );
    }

    @Test
    @DisplayName("특정 댓글의 좋아요를 1 내린다.")
    void likeComment_down() {
        CommentLike commentLike = CommentLike.builder()
                .comment(testComment)
                .member(testMember)
                .build();
        testComment.addCommentLike(commentLike);
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testComment));
        when(commentLikeRepository.findByCommentAndMember(any(Comment.class), any(Member.class)))
                .thenReturn(Optional.ofNullable(commentLike));

        CommentLikeToggleResponse response = commentLikeToggleService.toggleCommentLike(testAuthInfo, testComment.getId());

        assertAll(
                () -> assertThat(response.likeCount()).isEqualTo(testComment.getLikeCount() - 1),
                () -> assertThat(response.liked()).isEqualTo(false)
        );
    }

}