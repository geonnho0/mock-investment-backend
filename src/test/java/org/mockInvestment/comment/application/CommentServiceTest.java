package org.mockInvestment.comment.application;

import org.junit.jupiter.api.BeforeEach;
import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.util.MockTest;

public class CommentServiceTest extends MockTest {

    protected Comment testComment;

    protected Comment testReply;


    @BeforeEach
    void setUp() {
        testComment = Comment.builder()
                .id(1L)
                .stockTicker(testStockTicker)
                .member(testMember)
                .build();
        testReply = Comment.builder()
                .id(2L)
                .parent(testComment)
                .build();
    }

}
