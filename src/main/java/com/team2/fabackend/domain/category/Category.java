package com.team2.fabackend.domain.category;

import com.team2.fabackend.domain.ledger.TransactionType;
import com.team2.fabackend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<SubCategory> subCategoryList = new ArrayList<>();
}
