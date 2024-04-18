package org.mockInvestment.comment.repository;

import org.mockInvestment.comment.domain.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
}
