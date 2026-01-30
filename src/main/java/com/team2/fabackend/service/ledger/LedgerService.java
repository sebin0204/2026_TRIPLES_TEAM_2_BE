package com.team2.fabackend.service.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    // 가계부 내역 저장하기(C)
    public void saveLedger(LedgerRequest request) {
        Ledger ledger = Ledger.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .memo(request.getMemo())
                .type(request.getType())
                .date(request.getDate())
                .time(request.getTime())
                .goalId(request.getGoalId())
                .build();

        // DB에 저장
        ledgerRepository.save(ledger);
    }

    // 모든 소비 내역 가져오기(R)
    public List<Ledger> findAll() {
        return ledgerRepository.findAll(); //
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
        if (!ledgerRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 내역이 없습니다. id=" + id);
        }
        ledgerRepository.deleteById(id);
    }
}