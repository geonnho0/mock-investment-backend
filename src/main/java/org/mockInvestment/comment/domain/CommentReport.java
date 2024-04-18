package org.mockInvestment.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mockInvestment.member.domain.Member;

@Entity
@Getter
@Table(name = "comment_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String message;


    @Builder
    public CommentReport(Comment comment, Member member, String message) {
        this.comment = comment;
        this.member = member;
        this.message = message;
    }

    public boolean isOwner(Member member) {
        return this.member.equals(member);
    }
}

