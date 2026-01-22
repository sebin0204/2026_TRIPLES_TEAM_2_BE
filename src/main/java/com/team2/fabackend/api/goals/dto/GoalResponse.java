package com.team2.fabackend.api.goals.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalResponse {
    private Long id;
    private String title;
    private String termCategory;
    private Long targetAmount; //목표 금액
    private Long currentConsumeAmount; //현재 소비량
    public int progressRate; //진행률(목표달성률?)
}
