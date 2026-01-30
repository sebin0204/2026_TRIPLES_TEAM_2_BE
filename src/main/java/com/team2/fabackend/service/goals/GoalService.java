package com.team2.fabackend.service.goals;

import com.team2.fabackend.api.goals.dto.CategoryStatResponse;
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
@Transactional(readOnly = true)
public class GoalService {
    private final GoalRepository goalRepository;
    private final LedgerRepository ledgerRepository; //가계부 내역 확인용

    //C : 목표 설정 및 저장
    @Transactional
    public Long createGoal(GoalRequest request) {
        Goal goal = Goal.builder()
                .title(request.getTitle())
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .memo(request.getMemo())
                .build();

        goal.calculateDailyAllowance();

        return goalRepository.save(goal).getId();
    }

    //R : 목표 조회(대시보드)
    public List<GoalResponse> findAllGoals() {
        return goalRepository.findAll().stream().map(goal -> {
            //현재까시 지출 누적 금액
            Long totalSpent = sumSpentAmount(goal.getId(), goal.getStartDate(), LocalDate.now());

            //누적 허용 지출 (E*경과일)
            long passedDays = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), goal.getEndDate());
            double cumulativeAllowance = goal.getDailyAllowance() * Math.max(0, passedDays);

            //미래 목표 달성까지의 상태값(안전, 주의, 위험)
            String status = determineStatus(totalSpent, cumulativeAllowance);

            List<CategoryStatResponse> categoryStats = ledgerRepository.findCategoryStatsBetweenDates(
                    goal.getStartDate(), LocalDate.now());

            return GoalResponse.builder()
                    .id(goal.getId())
                    .title(goal.getTitle())
                    .targetAmount(goal.getTargetAmount())
                    .currentSpend(totalSpent)
                    .status(status)
                    .categoryStats(categoryStats)
                    .build();
        }).collect(Collectors.toList());
    }

    //U
    @Transactional
    public void updateGoal(Long id, GoalRequest request) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("목표가 없습니다. id=" + id));
        goal.update(request.getTitle(), request.getTargetAmount(), request.getStartDate(), request.getEndDate(), request.getMemo());
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

        Long totalSpent = sumSpentAmount(goal.getId(), goal.getStartDate(), LocalDate.now().plusDays(1));
        System.out.println("DEBUG >>> GoalId: " + id + ", 조회된 총 지출액: " + totalSpent);

        Double E = goal.getDailyAllowance();
        long passedDays = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), LocalDate.now().plusDays(1));

        //(실제 지출 - (E*경과일))/E
        double diff = totalSpent - (E*passedDays);
        long changedDays = Math.round(Math.abs(diff/E));

        String type;
        String message;

        if (diff > 0) {
            type = "DELAYED"; // 초과 소비 -> 지연 (+)
            message = String.format("이 소비를 지속하면 목표 달성까지 약 %d일이 더 필요해요.", changedDays);
        } else {
            type = "SHORTENED"; // 절약 소비 -> 단축 (-)
            message = String.format("오늘의 소비로 목표 달성일을 약 %d일 단축시키고 있어요!", changedDays);
        }

        return GoalAnalysisResponse.builder()
                .goalId(goal.getId())
                .changedDays(changedDays)
                .type(type)
                .analysisMessage(message)
                .build();
    }

    private Long sumSpentAmount(Long goalId, LocalDate start, LocalDate end) {
        Long total = ledgerRepository.sumExpenseAmountBetween(goalId, start, end);
        return total != null ? total : 0L; // 데이터가 없을 경우 0 반환
    }

    private String determineStatus(Long spent, double allowance) {
        if (spent > allowance * 1.1) return "위험";
        if (spent > allowance) return "주의";
        return "안전";
    }
}
