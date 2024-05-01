package org.mockInvestment.comment.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.comment.domain.CommentReport;
import org.mockInvestment.comment.dto.request.NewCommentReportRequest;
import org.mockInvestment.comment.exception.DuplicatedCommentReportException;
import org.mockInvestment.comment.repository.CommentReportRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentReportCreateServiceTest extends CommentServiceTest {

    @InjectMocks
    private CommentReportCreateService commentReportCreateService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentReportRepository commentReportRepository;

    @Mock
    private MemberRepository memberRepository;


    @Test
    @DisplayName("유효한 id를 통해 특정 댓글을 신고한다.")
    void reportComment() {
        NewCommentReportRequest request = new NewCommentReportRequest("report message");
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testComment));

        commentReportCreateService.reportComment(testComment.getId(), request, testAuthInfo);

        assertAll(
                () -> assertThat(testComment.getReports().size()).isEqualTo(1),
                () -> verify(commentReportRepository).save(any(CommentReport.class))
        );
    }

    @Test
    @DisplayName("동일한 댓글을 한 번 이상 신고하면 예외를 발생한다.")
    void reportComment_exception_duplicated() {
        NewCommentReportRequest request = new NewCommentReportRequest("report message");
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testComment));

        commentReportCreateService.reportComment(testComment.getId(), request, testAuthInfo);

        assertThatThrownBy(() -> commentReportCreateService.reportComment(1L, request, testAuthInfo))
                .isInstanceOf(DuplicatedCommentReportException.class);
    }

}