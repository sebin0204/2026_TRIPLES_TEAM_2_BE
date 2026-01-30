package com.team2.fabackend.domain.goals;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long targetAmount; //목표금액

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String memo;

    private Double dailyAllowance; //일일 소비 허용치 (E)

    public void update(String title, Long targetAmount, LocalDate startDate, LocalDate endDate, String memo) {
        this.title = title;
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memo = memo;
        calculateDailyAllowance(); // 정보 변경 시 E 재계산
    }

    public void calculateDailyAllowance() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) throw new IllegalArgumentException("기한은 최소 1일 이상이여야 합니다.");
        this.dailyAllowance = (double)this.targetAmount/days;
    }
}