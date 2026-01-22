package com.team2.fabackend.api.goals.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalAnalysisResponse {
    private Long goalId;
    private String title;
    private Long targetAmount;
    private Long currentSpend;
    private Long remainingAmount;  // 남은 금액
    private String analysisMessage;
    private boolean isOver;        // 목표 초과 여부
}
