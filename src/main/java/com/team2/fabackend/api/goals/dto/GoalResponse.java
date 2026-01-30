package com.team2.fabackend.api.goals.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GoalResponse {
    private Long id;
    private String title;
    private Long targetAmount; //목표 금액
    private Long currentSpend; //누적 지출액
    private String status;
    public int progressRate; //진행률(목표달성률?)
    private List<CategoryStatResponse> categoryStats;
}
