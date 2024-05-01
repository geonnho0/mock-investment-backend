package org.mockInvestment.comment.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.comment.application.*;
import org.mockInvestment.comment.dto.request.CommentUpdateRequest;
import org.mockInvestment.comment.dto.request.NewCommentReportRequest;
import org.mockInvestment.comment.dto.request.NewCommentRequest;
import org.mockInvestment.comment.dto.response.CommentLikeToggleResponse;
import org.mockInvestment.comment.dto.response.CommentsResponse;
import org.mockInvestment.global.auth.Login;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentApi {

    private final CommentFindService commentFindService;

    private final CommentCreateService commentCreateService;

    private final CommentReportCreateService commentReportCreateService;

    private final CommentUpdateService commentUpdateService;

    private final CommentDeleteService commentDeleteService;

    private final CommentLikeToggleService commentLikeToggleService;


    @GetMapping("/stocks/{code}/comments")
    public ResponseEntity<CommentsResponse> findAllCommentsByCode(@PathVariable("code") String stockCode,
                                                                  @Login AuthInfo authInfo) {
        CommentsResponse response = commentFindService.findAllCommentsByCode(authInfo, stockCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stocks/{code}/comments")
    public ResponseEntity<Void> addComment(@Login AuthInfo authInfo,
                                           @PathVariable("code") String stockCode,
                                           @RequestBody NewCommentRequest request) {
        commentCreateService.addComment(authInfo, stockCode, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/comments/{id}/report")
    public ResponseEntity<Void> reportComment(@PathVariable("id") Long commentId,
                                              @Valid @RequestBody NewCommentReportRequest request,
                                              @Login AuthInfo authInfo) {
        commentReportCreateService.reportComment(commentId, request, authInfo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/comments/{id}/reply")
    public ResponseEntity<Void> addReply(@PathVariable("id") Long commentId,
                                         @RequestBody NewCommentRequest request,
                                         @Login AuthInfo authInfo) {
        commentCreateService.addReply(authInfo, commentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Void> updateComment(@PathVariable("id") Long commentId,
                                              @Valid @RequestBody CommentUpdateRequest request,
                                              @Login AuthInfo authInfo) {
        commentUpdateService.updateComment(commentId, request, authInfo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId,
                                              @Login AuthInfo authInfo) {
        commentDeleteService.deleteComment(authInfo, commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comments/{id}/like")
    public ResponseEntity<CommentLikeToggleResponse> toggleCommentLike(@PathVariable("id") Long commentId,
                                                                       @Login AuthInfo authInfo) {
        CommentLikeToggleResponse response = commentLikeToggleService.toggleCommentLike(authInfo, commentId);
        return ResponseEntity.ok(response);
    }

}
