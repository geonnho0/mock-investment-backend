package org.mockInvestment.comment.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.dto.request.NewCommentRequest;
import org.mockInvestment.comment.exception.ReplyDepthException;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentCreateServiceTest extends MockTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentCreateService commentCreateService;

    private Comment testComment;

    private Comment testReply;


    @BeforeEach
    void setUp() {
        testComment = Comment.builder()
                .stockTicker(testStockTicker)
                .member(testMember)
                .build();
        testReply = Comment.builder()
                .parent(testComment)
                .build();
    }

    @Test
    @DisplayName("특정 게시글의 댓글을 작성한다.")
    void addComment() {
        NewCommentRequest request = new NewCommentRequest("Comment content");
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStockTicker));

        commentCreateService.addComment(testAuthInfo, testStockTicker.getCode(), request);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("특정 댓글의 대댓글을 작성한다.")
    void addReply() {
        NewCommentRequest request = new NewCommentRequest("Reply content");
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testComment));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        commentCreateService.addReply(testAuthInfo, 1L, request);

        assertAll(
                () -> assertThat(testComment.getReplies().size()).isEqualTo(1),
                () -> verify(commentRepository).save(any(Comment.class))
        );
    }

    @Test
    @DisplayName("대댓글의 대댓글을 작성할 수 없다.")
    void addReply_exception_depth() {
        NewCommentRequest request = new NewCommentRequest("Reply content");
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testReply));

        assertThatThrownBy(() -> commentCreateService.addReply(testAuthInfo, 1L, request))
                .isInstanceOf(ReplyDepthException.class);
    }

}