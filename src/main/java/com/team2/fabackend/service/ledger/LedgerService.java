package com.team2.fabackend.service.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.domain.goals.GoalRepository;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.ledger.TransactionType;
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

    // 가계부 내역 저장하기(C)
    public void saveLedger(Long userId, LedgerRequest request) {
        // 1. 내역 저장
        Ledger ledger = Ledger.builder()
                .userId(userId)
                .amount(request.getAmount())
                .category(request.getCategory())
                .memo(request.getMemo())
                .type(request.getType())
                .date(request.getDate())
                .time(request.getTime())
                .build();

        ledgerRepository.save(ledger);

        // 2. 지출(EXPENSE)인 경우 모든 활성 목표에 자동 반영
        if (request.getType() == TransactionType.EXPENSE || request.getType() == TransactionType.INCOME) {
            updateRelatedGoals(userId, request);
        }
    }

    // 목표 업데이트 로직 분리
    private void updateRelatedGoals(Long userId, LedgerRequest request) {
        List<Goal> activeGoals = goalRepository.findAllByUserId(userId);

        for (Goal goal : activeGoals) {
            if("전체".equals(goal.getCategory()) || goal.getCategory().equals(request.getCategory())) {

                if(request.getType() == TransactionType.EXPENSE) {
                    goal.addCurrentAmount(request.getAmount());
                } else if (request.getType() == TransactionType.INCOME) {
                    goal.subtractCurrentAmount(request.getAmount());
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
    @Transactional(readOnly = true)
    public List<Ledger> findAllByUserId(Long userId) {
        return ledgerRepository.findAllByUserId(userId);
    }

    // 수정하기(U)
    @Transactional
    public void update(Long id, LedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));
        ledger.update(request.getAmount(),
                request.getCategory(),
                request.getMemo(),
                request.getType(),
                request.getDate(),
                request.getTime());
    }

    // 지우기(D)
    @Transactional
    public void delete(Long id) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));
        ledgerRepository.delete(ledger);
    }
}