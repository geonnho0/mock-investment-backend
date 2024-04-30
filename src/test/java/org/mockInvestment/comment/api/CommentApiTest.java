package org.mockInvestment.comment.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.dto.request.CommentUpdateRequest;
import org.mockInvestment.comment.dto.request.NewCommentRequest;
import org.mockInvestment.comment.dto.response.CommentLikeToggleResponse;
import org.mockInvestment.comment.dto.response.CommentResponse;
import org.mockInvestment.comment.dto.response.CommentsResponse;
import org.mockInvestment.comment.dto.response.ReplyResponse;
import org.mockInvestment.comment.exception.ReplyDepthException;
import org.mockInvestment.stockOrder.exception.AuthorizationException;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.util.ApiTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class CommentApiTest extends ApiTest {

    private List<CommentResponse> responses = new ArrayList<>();

    private StockTicker stockTicker;


    @BeforeEach
    void setUp() {
        stockTicker = new StockTicker("CODE", "NAME");
        for (long i = 1; i <= 7; i++) {
            responses.add(new CommentResponse(i, "user-" + (i % 5), "content" + i,
                    (int) i, i % 3 == 0, i % 5 == 0,
                    false, new ArrayList<>(), LocalDateTime.now()));
        }
        for (long i = 8; i <= 12; i++) {
            responses.get(2).replies()
                    .add(new ReplyResponse(i, "content" + i, "user-" + (i % 5), (int) i,
                            i % 3 == 0, i % 5 == 0, false, LocalDateTime.now()));
        }
    }

    @Test
    @DisplayName("특정 주식의 댓글들을 요청한다.")
    void findAllCommentsByCode() {
        when(commentFindService.findAllCommentsByCode(any(AuthInfo.class), anyString()))
                .thenReturn(new CommentsResponse(responses));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .when().get("/stocks/CODE/comments")
                .then().log().all()
                .assertThat()
                .apply(document("comments/find/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("특정 게시글의 댓글을 작성한다.")
    void addComment() {
        NewCommentRequest request = new NewCommentRequest("new comment");

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .body(request)
                .when().post("/stocks/CODE/comments")
                .then().log().all()
                .assertThat()
                .apply(document("comments/create/success"))
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("특정 댓글의 좋아요를 1 올리거나 내린다.")
    void likeComment() {
        when(commentLikeToggleService.toggleCommentLike(any(AuthInfo.class), anyLong()))
                .thenReturn(new CommentLikeToggleResponse(5, false));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .when().put("/comments/3/like")
                .then().log().all()
                .assertThat()
                .apply(document("comments/like/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("권한이 있는 댓글을 수정한다.")
    void updateComment() {
        CommentUpdateRequest request = new CommentUpdateRequest("Updated comment");

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .body(request)
                .when().put("/comments/3")
                .then().log().all()
                .assertThat()
                .apply(document("comments/update/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("수정하는 댓글의 내용이 없으면 400을 반환한다.")
    void updateComment_exception_noContent() {
        CommentUpdateRequest request = new CommentUpdateRequest("");

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .body(request)
                .when().put("/comments/3")
                .then().log().all()
                .assertThat()
                .apply(document("comments/update/fail/noContent"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("권한이 없는 댓글을 수정하면 403을 반환한다.")
    void updateComment_exception_noAuth() {
        CommentUpdateRequest request = new CommentUpdateRequest("content");

        doThrow(new AuthorizationException())
                .when(commentUpdateService)
                .updateComment(anyLong(), any(CommentUpdateRequest.class), any(AuthInfo.class));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .body(request)
                .when().put("/comments/3")
                .then().log().all()
                .assertThat()
                .apply(document("comments/update/fail/noAuth"))
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("권한이 있는 댓글을 삭제한다.")
    void deleteComment() {
        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .when().delete("/comments/3")
                .then().log().all()
                .assertThat()
                .apply(document("comments/delete/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("권한이 없는 댓글을 삭제하면 404를 반환한다.")
    void deleteComment_exception_noAuth() {
        doThrow(new AuthorizationException())
                .when(commentDeleteService)
                .deleteComment(any(AuthInfo.class), anyLong());

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .when().delete("/comments/3")
                .then().log().all()
                .assertThat()
                .apply(document("comments/delete/fail/noAuth"))
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("특정 댓글의 대댓글을 작성한다.")
    void addReply() {
        NewCommentRequest request = new NewCommentRequest("Reply content");

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .body(request)
                .when().post("/comments/3/reply")
                .then().log().all()
                .assertThat()
                .apply(document("reply/create/success"))
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("특정 대댓글의 대댓글을 작성하려고 하면, 400 에러를 반환한다.")
    void addReply_exception_depth() {
        NewCommentRequest request = new NewCommentRequest("content");

        doThrow(new ReplyDepthException())
                .when(commentCreateService)
                .addReply(any(AuthInfo.class), anyLong(), any(NewCommentRequest.class));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer aws-cognito-access-token")
                .body(request)
                .when().post("/comments/3/reply")
                .then().log().all()
                .assertThat()
                .apply(document("reply/create/fail/depth"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

}