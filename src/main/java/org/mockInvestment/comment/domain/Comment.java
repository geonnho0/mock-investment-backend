package org.mockInvestment.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockOrder.exception.AuthorizationException;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<CommentLike> commentLikes = new ArrayList<>();

    @ColumnDefault("0")
    private int likeCount;

    @OneToMany(mappedBy = "comment")
    private List<CommentReport> reports = new ArrayList<>();

    @ColumnDefault("false")
    private boolean softRemoved;

    @ColumnDefault("false")
    private boolean updated;

    @ColumnDefault("false")
    private boolean blocked;

    @ManyToOne(fetch = FetchType.LAZY)
    private StockTicker stockTicker;

    @CreatedDate
    private LocalDateTime createdAt;


    @Builder
    public Comment(Member member, String content, StockTicker stockTicker, Comment parent) {
        this.member = member;
        this.content = content;
        this.stockTicker = stockTicker;
        this.parent = parent;
    }

    public void addCommentLike(CommentLike commentLike) {
        commentLikes.add(commentLike);
    }

    public void deleteCommentLike(CommentLike commentLike) {
        commentLikes.remove(commentLike);
        commentLike.delete();
    }

    public void addReport(CommentReport report) {
        reports.add(report);
        if (!blocked && reports.size() >= 5)
            blocked = true;
    }

    public boolean hasReportByMember(Member member) {
        for (CommentReport report: reports)
            if (report.isOwner(member))
                return true;
        return false;
    }

    public void validateAuthorization(Member member) {
        if (!this.member.equals(member))
            throw new AuthorizationException();
    }

    public void updateContent(String content) {
        this.content = content;
        updated = true;
    }

    public void addChildren(Comment reply) {
        children.add(reply);
    }

    public void deleteChild(Comment reply) {
        children.remove(reply);
        reply.delete();
    }

    public void delete() {
        parent = null;
    }

    public boolean isParent() {
        return Objects.isNull(parent);
    }

    public boolean isReply() {
        return !isParent();
    }

    public boolean hasNoReply() {
        return children.isEmpty();
    }

    public void willBeDeleted() {
        softRemoved = true;
    }

    public boolean canDelete() {
        return hasNoReply() && softRemoved;
    }

    public String getContent() {
        return blocked || softRemoved ? null : content;
    }

}
