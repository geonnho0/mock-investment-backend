package org.mockInvestment.comment.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.dto.request.CommentUpdateRequest;
import org.mockInvestment.comment.exception.CommentNotFoundException;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentUpdateService {

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;


    public void updateComment(Long commentId, CommentUpdateRequest request, AuthInfo authInfo) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        comment.validateAuthorization(member);

        comment.updateContent(request.content());
    }

}