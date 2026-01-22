package com.team2.fabackend.service.goals;

import com.team2.fabackend.api.goals.dto.GoalAnalysisResponse;
import com.team2.fabackend.api.goals.dto.GoalRequest;
import com.team2.fabackend.api.goals.dto.GoalResponse;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.domain.goals.GoalRepository;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.ledger.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {
    private final GoalRepository goalRepository;
    private final LedgerRepository ledgerRepository; //가계부 내역 확인용

    //C
    public Long createGoal(GoalRequest request) {
        Goal goal = Goal.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return goalRepository.save(goal).getId();
    }

    //R
    public List<GoalResponse> findAllGoals() {
        List<Goal> goals = goalRepository.findAll();

        return goals.stream().map(goal -> {
            // 해당 목표 기간 동안 실제 지출한 금액 합산
            Long currentSpend = ledgerRepository.findByDateBetween(goal.getStartDate(), goal.getEndDate())
                    .stream()
                    .filter(ledger -> ledger.getType() == TransactionType.EXPENSE) // 지출만 계산
                    .mapToLong(Ledger::getAmount)
                    .sum();

            // progressRate = (실제 지출 / 사용자가 정한 목표액) * 100
            int rate = 0;
            if (goal.getTargetAmount() > 0) { // 0으로 나누기 방지
                rate = (int) ((double) currentSpend / goal.getTargetAmount() * 100);
            }

            return GoalResponse.builder()
                    .id(goal.getId())
                    .title(goal.getTitle())
                    //.termCategory(goal.getTermCategory())
                    .targetAmount(goal.getTargetAmount())
                    .currentConsumeAmount(currentSpend)
                    .progressRate(rate)
                    .build();
        }).collect(Collectors.toList());
    }

    //suggest
    public Long suggestTargetAmount() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);

        //최근 3개월간의 총 지출액
        List<Ledger> recentLedgers = ledgerRepository.findByDateBetween(startDate, endDate);

        long totalExpense = recentLedgers.stream()
                .filter(l -> l.getType() == TransactionType.EXPENSE)
                .mapToLong(Ledger::getAmount)
                .sum();

        long monthlyAverage = totalExpense / 3;

        // 평균보다 10% 적은 금액을 제안
        long suggestedAmount = (long) (monthlyAverage * 0.9);
        return (suggestedAmount / 10000) * 10000;
    }

    //U
    @Transactional
    public void updateGoal(Long id, GoalRequest request) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("목표가 없습니다. id=" + id));
        goal.update(request.getTitle(), request.getContent(), request.getTargetAmount());
    }

    //D
    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    //analyzeGoal
    public GoalAnalysisResponse analyzeGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다. id=" + id));

        // 현재까지 총 지출액
        Long currentSpend = ledgerRepository.findByDateBetween(goal.getStartDate(), goal.getEndDate())
                .stream()
                .filter(l -> l.getType() == TransactionType.EXPENSE)
                .mapToLong(Ledger::getAmount)
                .sum();

        // 남은 금액 및 상태 분석
        Long remaining = goal.getTargetAmount() - currentSpend;
        boolean isOver = remaining < 0;

        String message;
        if (isOver) {
            message = "목표 금액을 초과했어요! 소비를 멈춰주세요";
        } else if (remaining < goal.getTargetAmount() * 0.2) {
            message = "목표 금액에 거의 다 왔어요! 조금만 더 힘내보세요";
        } else {
            message = "잘하고 계시네요! 지금처럼만 유지하세요";
        }

        return GoalAnalysisResponse.builder()
                .goalId(goal.getId())
                .title(goal.getTitle())
                .targetAmount(goal.getTargetAmount())
                .currentSpend(currentSpend)
                .remainingAmount(Math.max(0, remaining))
                .analysisMessage(message)
                .isOver(isOver)
                .build();
    }
}
