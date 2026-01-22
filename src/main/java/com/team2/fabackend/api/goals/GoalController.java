package com.team2.fabackend.api.goals;

import com.team2.fabackend.api.goals.dto.GoalRequest;
import com.team2.fabackend.api.goals.dto.GoalResponse;
import com.team2.fabackend.service.goals.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @GetMapping("/suggestion")
    @Operation(summary = "목표 지출액 제안", description = "최근 3개월 지출 데이터를 기반으로 적정 목표액을 추천")
    public ResponseEntity<Map<String, Long>> getSuggestion() {
        Long suggestedAmount = goalService.suggestTargetAmount();

        Map<String, Long> response = new HashMap<>();
        response.put("suggestedAmount", suggestedAmount);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody GoalRequest request) {
        Long goalId = goalService.createGoal(request);
        return ResponseEntity.ok(goalId);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getGoalList() {
        List<GoalResponse> data = goalService.findAllGoals();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }



    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody GoalRequest request) {
        goalService.updateGoal(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok().build();
    }
}
