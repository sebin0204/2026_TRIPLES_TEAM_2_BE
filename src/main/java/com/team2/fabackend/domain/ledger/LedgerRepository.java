package com.team2.fabackend.domain.ledger;

import com.team2.fabackend.api.goals.dto.CategoryStatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    // 기간 내 지출 합계 계산 (분석 로직의 핵심)
    @Query("SELECT SUM(l.amount) FROM Ledger l " +
            "WHERE l.goalId = :goalId " +
            "AND l.date BETWEEN :start AND :end " +
            "AND l.type = 'EXPENSE'")
    Long sumExpenseAmountBetween(@Param("goalId") Long goalId,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);

    // 카테고리별 지출 통계 조회
    @Query("SELECT new com.team2.fabackend.api.goals.dto.CategoryStatResponse(l.category, SUM(l.amount)) " +
            "FROM Ledger l " +
            "WHERE l.date BETWEEN :startDate AND :endDate " +
            "AND l.type = 'EXPENSE' " +
            "GROUP BY l.category")
    List<CategoryStatResponse> findCategoryStatsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 사용자별 내역 조회
    List<Ledger> findAllByUserId(Long userId);
    // 기간별 전체 내역 조회
    List<Ledger> findByDateBetween(LocalDate start, LocalDate end);
}