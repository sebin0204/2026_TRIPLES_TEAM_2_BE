package com.team2.fabackend.api.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.service.ledger.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
@Tag(name = "Ledger", description = "가계부 API")
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/add") //C
    @Operation(summary = "가계부 내역 저장", description = "금액, 카테고리 등 저장")
    public ResponseEntity<Void> addLedger(@RequestBody LedgerRequest request) {
        ledgerService.saveLedger(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list") //R
    @Operation(summary = "가계부 전체 내역 조회", description = "저장된 모든 가계부 내역 불러오기")
    public ResponseEntity<List<Ledger>> getAllLedgers() {
        List<Ledger> ledgers = ledgerService.findAll(); //
        return ResponseEntity.ok(ledgers); //
    }

    @PatchMapping("/{id}") //U
    @Operation(summary = "가계부 내역 수정", description = "특정 ID 가계부 내역 수정")
    public ResponseEntity<Void> updateLedger(@PathVariable Long id, @RequestBody LedgerRequest request) {
        ledgerService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}") //D
    @Operation(summary = "가계부 내역 삭제", description = "특정 ID 가계부 내역 삭제")
    public ResponseEntity<Void> deleteLedger(@PathVariable Long id) {
        ledgerService.delete(id);
        return ResponseEntity.ok().build();
    }
}