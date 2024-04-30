package org.mockInvestment.comment.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.dto.request.NewCommentRequest;
import org.mockInvestment.comment.exception.CommentNotFoundException;
import org.mockInvestment.comment.exception.ReplyDepthException;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentCreateService {

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;

    private final StockTickerRepository stockTickerRepository;


    public void addComment(AuthInfo authInfo, String stockCode, NewCommentRequest request) {
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        StockTicker stockTicker = stockTickerRepository.findByCode(stockCode)
                .orElseThrow(StockTickerNotFoundException::new);
        Comment comment = Comment.builder()
                .member(member)
                .stockTicker(stockTicker)
                .content(request.content())
                .build();

        commentRepository.save(comment);
    }

    public void addReply(AuthInfo authInfo, Long commentId, NewCommentRequest request) {
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!parent.isParent())
            throw new ReplyDepthException();

        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);

        Comment reply = Comment.builder()
                .content(request.content())
                .member(member)
                .stockTicker(parent.getStockTicker())
                .parent(parent)
                .build();
        parent.addReply(reply);

        commentRepository.save(reply);
    }

}
