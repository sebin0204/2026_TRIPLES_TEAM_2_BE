package com.team2.fabackend.service.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.category.Category;
import com.team2.fabackend.domain.category.CategoryRepository;
import com.team2.fabackend.domain.category.SubCategory;
import com.team2.fabackend.domain.category.SubCategoryRepository;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.domain.goals.GoalRepository;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.ledger.TransactionType;
import com.team2.fabackend.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final GoalRepository goalRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    // 가계부 내역 저장하기(C)
    public void saveLedger(Long userId, LedgerRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(request.getSubCategoryId()).orElse(null);
        }
        // 1. 내역 저장
        Ledger ledger = Ledger.builder()
                .userId(userId)
                .amount(request.getAmount())
                .category(category)
                .subCategory(subCategory)
                .memo(request.getMemo())
                .type(request.getType())
                .date(request.getDate())
                .time(request.getTime())
                .build();

        ledgerRepository.save(ledger);

        // 2. 지출(EXPENSE)인 경우 모든 활성 목표에 자동 반영
        if (request.getType() == TransactionType.EXPENSE || request.getType() == TransactionType.INCOME) {
            updateRelatedGoals(userId, category.getName(), request.getAmount(), request.getType());
        }
    }

    // 목표 업데이트 로직 분리
    private void updateRelatedGoals(Long userId, String categoryName, Long amount, TransactionType type) {
        List<Goal> activeGoals = goalRepository.findAllByUserId(userId);

        for (Goal goal : activeGoals) {
            if("전체".equals(goal.getCategory()) || goal.getCategory().equals(categoryName)) {

                if(type == TransactionType.EXPENSE) {
                    goal.addCurrentAmount(amount);
                } else if (type == TransactionType.INCOME) {
                    goal.subtractCurrentAmount(amount);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Long getTotalBalance(Long userId) {
        Long balance = ledgerRepository.getBalanceByUserId(userId);
        return (balance != null) ? balance : 0L; // 내역이 없으면 0원 반환
    }

    // 특정 유저의 내역만 가져오기
    @Transactional
    public List<Ledger> findAllByUserId(Long userId) {
        categoryService.checkAndInitCategories(userId);
        return ledgerRepository.findAllByUserId(userId);
    }

    // 수정하기(U)
    @Transactional
    public void update(Long id, LedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));

        // 1. 기존 금액을 목표에서 롤백 (원복)
        updateRelatedGoals(ledger.getUserId(), ledger.getCategory().getName(), -ledger.getAmount(), ledger.getType());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        SubCategory subCategory = (request.getSubCategoryId() != null) ?
                subCategoryRepository.findById(request.getSubCategoryId()).orElse(null) : null;

        // 2. 내역 업데이트
        ledger.update(request.getAmount(), category, subCategory, request.getMemo(),
                request.getType(), request.getDate(), request.getTime());

        // 3. 새 금액을 목표에 반영
        updateRelatedGoals(ledger.getUserId(), category.getName(), request.getAmount(), request.getType());
    }

    // 지우기(D)
    @Transactional
    public void delete(Long id) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));
        ledgerRepository.delete(ledger);
    }
}