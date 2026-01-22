package com.team2.fabackend.domain.goals;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    // private String termCategory;
    private Long targetAmount;
    private LocalDate startDate;
    private LocalDate endDate;

    public void update(String title, String content, Long targetAmount) {
        this.title = title;
        this.content = content;
        this.targetAmount = targetAmount;
    }
}