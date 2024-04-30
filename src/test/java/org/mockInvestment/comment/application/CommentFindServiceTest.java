package org.mockInvestment.comment.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.dto.response.CommentsResponse;
import org.mockInvestment.comment.repository.CommentLikeRepository;
import org.mockInvestment.comment.repository.CommentRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class CommentFindServiceTest extends MockTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private StockTickerRepository stockTickerRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentFindService commentFindService;

    private final int COMMENT_COUNT = 10;

    private List<Comment> comments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (long i = 0; i < COMMENT_COUNT; i++) {
            Comment comment = Comment.builder()
                    .id(i)
                    .content("comment" + i)
                    .member(testMember)
                    .stockTicker(testStockTicker)
                    .build();
            comments.add(comment);
        }
    }

    @Test
    @DisplayName("유효한 code를 통해 특정 주식의 댓글들을 불러온다.")
    void findAllCommentsByCode() {
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStockTicker));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(commentRepository.findAllByStockTicker(any(StockTicker.class)))
                .thenReturn(comments);
        when(commentLikeRepository.existsByCommentAndMember(any(Comment.class), any(Member.class)))
                .thenReturn(false);

        CommentsResponse response = commentFindService.findAllCommentsByCode(testAuthInfo, testStockTicker.getCode());

        assertAll(
                () -> assertThat(response.comments().size()).isEqualTo(COMMENT_COUNT)
        );
    }

    @Test
    @DisplayName("삭제 처리될 댓글의 내용은 비어있다.")
    void findAllCommentsByPostId_softRemoved() {
        comments = new ArrayList<>();
        comments.add(Comment.builder()
                .id(1L)
                .content("Content")
                .member(testMember)
                .stockTicker(testStockTicker)
                .build());
        comments.get(0).willBeDeleted();
        when(stockTickerRepository.findByCode(anyString()))
                .thenReturn(Optional.ofNullable(testStockTicker));
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));
        when(commentRepository.findAllByStockTicker(any(StockTicker.class)))
                .thenReturn(comments);

        CommentsResponse response = commentFindService.findAllCommentsByCode(testAuthInfo, testStockTicker.getCode());

        assertThat(response.comments().get(0).content()).isEqualTo(null);
    }

//    @Test
//    @DisplayName("대댓글만 불러올 수 없다.")
//    void findAllCommentsByPostId_cannot_only_reply() {
//        Comment comment = Comment.builder()
//                .id(1L)
//                .content("Parent")
//                .author("author1")
//                .post(post)
//                .build();
//        comments = new ArrayList<>();
//        comments.add(Comment.builder()
//                .id(2L)
//                .content("Child")
//                .author("Author2")
//                .post(post)
//                .parent(comment)
//                .build());
//        when(commentRepository.findAllByPostId(anyLong()))
//                .thenReturn(comments);
//
//        CommentsResponse response = commentFindService.findAllCommentsByPostId(2L, authInfo);
//
//        assertThat(response.getComments().size()).isEqualTo(0);
//    }
//
//    @Test
//    @DisplayName("댓글과 대댓글들을 모두 불러온다.")
//    void findAllCommentsByPostId_withReplies() {
//        comments = new ArrayList<>();
//        Comment comment = Comment.builder()
//                .id(1L)
//                .content("Parent")
//                .author("author1")
//                .post(post)
//                .build();
//        comments.add(comment);
//        Comment reply = Comment.builder()
//                .id(2L)
//                .content("Child")
//                .author("Author2")
//                .post(post)
//                .parent(comment)
//                .build();
//        comments.add(reply);
//        comment.addReply(reply);
//        when(commentRepository.findAllByPostId(anyLong()))
//                .thenReturn(comments);
//
//        CommentsResponse response = commentFindService.findAllCommentsByPostId(2L, authInfo);
//
//        assertThat(response.getComments().get(0).getReplies().size()).isEqualTo(1);
//    }

}