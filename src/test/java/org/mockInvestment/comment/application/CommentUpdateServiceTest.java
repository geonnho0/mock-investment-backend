package org.mockInvestment.comment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.comment.dto.request.CommentUpdateRequest;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockOrder.exception.AuthorizationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class CommentUpdateServiceTest extends CommentServiceTest {

    @InjectMocks
    private CommentUpdateService commentUpdateService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;


    @Test
    @DisplayName("권한이 있는 댓글을 수정한다.")
    void updateComment() {
        CommentUpdateRequest request = new CommentUpdateRequest("new comment content");
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testComment));

        commentUpdateService.updateComment(testComment.getId(), request, testAuthInfo);

        assertAll(
                () -> assertThat(testComment.isUpdated()).isEqualTo(true),
                () -> assertThat(testComment.getContent()).isEqualTo(request.content())
        );
    }

    @Test
    @DisplayName("권한이 없는 댓글을 수정할 수 없다.")
    void updateComment_exception_noAuth() {
        CommentUpdateRequest request = new CommentUpdateRequest("new comment content");
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(new Member(5L, "email", "USER", "username")));
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testComment));

        assertThatThrownBy(() -> commentUpdateService.updateComment(testComment.getId(), request, testAuthInfo))
                .isInstanceOf(AuthorizationException.class);
    }

}