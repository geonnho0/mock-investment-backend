package org.mockInvestment.comment.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.domain.CommentReport;
import org.mockInvestment.comment.dto.request.NewCommentReportRequest;
import org.mockInvestment.comment.exception.CommentNotFoundException;
import org.mockInvestment.comment.exception.DuplicatedCommentReportException;
import org.mockInvestment.comment.repository.CommentReportRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentReportCreateService {

    private final MemberRepository memberRepository;

    private final CommentRepository commentRepository;

    private final CommentReportRepository commentReportRepository;


    public void reportComment(Long commentId, NewCommentReportRequest request, AuthInfo authInfo) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        CommentReport report = CommentReport.builder()
                .comment(comment)
                .message(request.message())
                .member(member)
                .build();

        checkIfAlreadyReport(comment, member);
        comment.addReport(report);

        commentReportRepository.save(report);
    }

    private void checkIfAlreadyReport(Comment comment, Member member) {
        if (comment.hasReportByMember(member))
            throw new DuplicatedCommentReportException();
    }

}
