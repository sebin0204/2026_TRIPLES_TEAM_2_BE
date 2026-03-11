package com.team2.fabackend.domain.category;

import com.team2.fabackend.domain.ledger.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(Long userId);
    List<Category> findAllByUserIdAndType(Long userId, TransactionType type);
}
