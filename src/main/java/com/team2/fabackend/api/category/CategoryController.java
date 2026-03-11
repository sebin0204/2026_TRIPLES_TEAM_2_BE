package com.team2.fabackend.api.category;

import com.team2.fabackend.domain.category.Category;
import com.team2.fabackend.domain.ledger.TransactionType;
import com.team2.fabackend.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(@RequestParam Long userId, @RequestParam TransactionType type) {
        return ResponseEntity.ok(categoryService.getCategories(userId, type));
    }

    @PostMapping("/init")
    public ResponseEntity<String> init(@RequestParam Long userId) {
        categoryService.initDefaultCategories(userId);
        return ResponseEntity.ok("기본 카테고리 생성 완료!");
    }
}
