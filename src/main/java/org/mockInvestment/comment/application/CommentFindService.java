package org.mockInvestment.comment.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.dto.response.CommentResponse;
import org.mockInvestment.comment.dto.response.CommentsResponse;
import org.mockInvestment.comment.dto.response.ReplyResponse;
import org.mockInvestment.comment.repository.CommentLikeRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFindService {

    private final CommentRepository commentRepository;

    private final StockTickerRepository stockTickerRepository;

    private final CommentLikeRepository commentLikeRepository;

    private final MemberRepository memberRepository;


    public CommentsResponse findCommentsByCode(AuthInfo authInfo, String stockCode) {
        StockTicker stockTicker = stockTickerRepository.findByCode(stockCode)
                .orElseThrow(StockTickerNotFoundException::new);
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        List<CommentResponse> comments = commentRepository.findAllByStockTicker(stockTicker).stream()
                .map(comment -> convertToCommentResponse(comment, member))
                .filter(response -> !Objects.isNull(response))
                .toList();
        return new CommentsResponse(comments);
    }

    private CommentResponse convertToCommentResponse(Comment comment, Member member) {
        if (comment.isReply())
            return null;

        boolean liked = commentLikeRepository.existsByCommentAndMember(comment, member);
        return CommentResponse.of(comment, liked, convertToReplyResponses(comment, member));
    }

    private List<ReplyResponse> convertToReplyResponses(Comment parent, Member member) {
        return parent.getChildren().stream()
                .map(reply -> {
                    boolean liked = commentLikeRepository.existsByCommentAndMember(reply, member);
                    return ReplyResponse.of(reply, liked);
                })
                .toList();
    }

}
