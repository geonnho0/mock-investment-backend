package org.mockInvestment.comment.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.repository.CommentLikeRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockOrder.exception.AuthorizationException;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CommentDeleteServiceTest extends MockTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentDeleteService commentDeleteService;

    private Comment testCommentWithReply;

    private Comment testCommentWithoutReply;

    private Comment testCommentUnAuthorized;

    private Member member = Member.builder().id(2L).build();

    private Comment testReply;


    @BeforeEach
    void setUp() {
        testCommentWithReply = Comment.builder()
                .id(1L)
                .stockTicker(testStockTicker)
                .member(testMember)
                .build();
        testCommentWithoutReply = Comment.builder()
                .id(2L)
                .stockTicker(testStockTicker)
                .member(testMember)
                .build();
        testCommentUnAuthorized = Comment.builder()
                .id(3L)
                .member(member)
                .build();
        testReply = Comment.builder()
                .id(4L)
                .parent(testCommentWithReply)
                .member(testMember)
                .build();
        testCommentWithReply.addReply(testReply);
    }

    @Test
    @DisplayName("권한이 있는 댓글을 삭제한다.")
    void deleteComment() {
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testCommentWithoutReply));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        commentDeleteService.deleteComment(testAuthInfo, testCommentWithReply.getId());

        assertAll(
                () -> verify(commentLikeRepository).deleteAllByComment(testCommentWithoutReply),
                () -> verify(commentRepository).delete(testCommentWithoutReply)
        );
    }

    @Test
    @DisplayName("권한이 있는 대댓글을 삭제한다.")
    void deleteReply() {
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testReply));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        commentDeleteService.deleteComment(testAuthInfo, testReply.getId());

        assertAll(
                () -> verify(commentLikeRepository).deleteAllByComment(testReply),
                () -> verify(commentRepository).delete(testReply)
        );
    }

    @Test
    @DisplayName("권한이 없는 댓글을 삭제할 수 없다.")
    void deleteReply_exception_noAuth() {
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testCommentUnAuthorized));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        assertThatThrownBy(() -> commentDeleteService.deleteComment(testAuthInfo, testCommentUnAuthorized.getId()))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("부모 댓글을 삭제해도 대댓글은 남아있다.")
    void deleteComment_keepChildren() {
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testCommentWithReply));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        commentDeleteService.deleteComment(testAuthInfo, testCommentWithReply.getId());

        assertAll(
                () -> verify(commentLikeRepository).deleteAllByComment(testCommentWithReply),
                () -> assertThat(testCommentWithReply.isSoftRemoved()).isEqualTo(true),
                () -> assertThat(testCommentWithReply.canDelete()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("특정 대댓글 삭제 후, 삭제 예정으로 처리되고 대댓글이 없는 부모 댓글을 삭제한다.")
    void deletePrentAndReply() {
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testCommentWithReply));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        commentDeleteService.deleteComment(testAuthInfo, testCommentWithReply.getId());
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testReply));

        commentDeleteService.deleteComment(testAuthInfo, testReply.getId());

        assertAll(
                () -> verify(commentLikeRepository).deleteAllByComment(testReply),
                () -> verify(commentRepository).delete(testReply),
                () -> verify(commentRepository).delete(testCommentWithReply)
        );
    }

}