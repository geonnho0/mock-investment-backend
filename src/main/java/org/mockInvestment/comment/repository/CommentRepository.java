package org.mockInvestment.comment.repository;

import org.mockInvestment.comment.domain.Comment;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByStockTicker(String stockTicker);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE comments SET like_count = like_count + 1 WHERE id = :commentId", nativeQuery = true)
    void increaseLikeCount(Long commentId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE comments SET like_count = like_count - 1 WHERE id = :commentId", nativeQuery = true)
    void decreaseLikeCount(Long commentId);

}
