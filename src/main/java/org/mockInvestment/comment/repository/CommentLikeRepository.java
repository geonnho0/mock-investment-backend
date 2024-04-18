package org.mockInvestment.comment.repository;

import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.comment.domain.CommentLike;
import org.mockInvestment.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);

    boolean existsByCommentAndMember(Comment comment, Member member);

    void deleteAllByComment(Comment comment);

}
