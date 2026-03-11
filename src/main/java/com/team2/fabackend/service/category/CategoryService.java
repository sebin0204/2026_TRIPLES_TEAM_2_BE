package com.team2.fabackend.service.category;

import com.team2.fabackend.domain.category.Category;
import com.team2.fabackend.domain.category.CategoryRepository;
import com.team2.fabackend.domain.category.SubCategory;
import com.team2.fabackend.domain.category.SubCategoryRepository;
import com.team2.fabackend.domain.ledger.TransactionType;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void checkAndInitCategories(Long userId) {
        List<Category> userCategories = categoryRepository.findAllByUserId(userId);

        if (userCategories.isEmpty()) {
            initDefaultCategories(userId);
        }
    }

    @Transactional
    public void initDefaultCategories(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // --- 지출(EXPENSE) 카테고리 설정 ---
        createCategoryWithSubs(user, "식비", TransactionType.EXPENSE, List.of("식재료", "외식", "배달", "카페"));
        createCategoryWithSubs(user, "교통", TransactionType.EXPENSE, List.of("버스/지하철", "택시"));
        createCategoryWithSubs(user, "여가", TransactionType.EXPENSE, List.of("영화/공연", "운동", "여행"));
        createCategoryWithSubs(user, "고정", TransactionType.EXPENSE, List.of("월세", "공과금", "구독료", "통신비"));

        // --- 수입(INCOME) 카테고리 설정 ---
        createCategoryWithSubs(user, "월급", TransactionType.INCOME, List.of("본업", "상여금"));
        createCategoryWithSubs(user, "부수입", TransactionType.INCOME, List.of("알바", "당근마켓", "앱테크"));
        createCategoryWithSubs(user, "금융", TransactionType.INCOME, List.of("이자", "배당금", "환급금"));
        createCategoryWithSubs(user, "용돈", TransactionType.INCOME, List.of("정기용돈", "기타"));
    }

    @Transactional
    public Long addCustomCategory(Long userId, String name, TransactionType type) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        Category category = Category.builder()
                .name(name)
                .type(type)
                .user(user)
                .build();
        return categoryRepository.save(category).getId();
    }

    @Transactional
    public Long addCustomSubCategory(Long categoryId, String name) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));
        SubCategory subCategory = SubCategory.builder()
                .name(name)
                .category(category)
                .build();
        return subCategoryRepository.save(subCategory).getId();
    }

    private void createCategoryWithSubs(User user, String catName, TransactionType type, List<String> subNames) {
        Category category = categoryRepository.save(Category.builder()
                .name(catName)
                .type(type)
                .user(user)
                .build());

        for (String subName : subNames) {
            subCategoryRepository.save(SubCategory.builder()
                    .name(subName)
                    .category(category)
                    .build());
        }
    }

    public List<Category> getCategories(Long userId, TransactionType type) {
        return categoryRepository.findAllByUserIdAndType(userId, type);
    }
}