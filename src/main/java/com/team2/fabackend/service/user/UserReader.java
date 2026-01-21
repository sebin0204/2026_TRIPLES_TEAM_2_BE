package com.team2.fabackend.service.user;

import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import com.team2.fabackend.global.enums.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReader {
    private final UserRepository userRepository;

    public User findByUserIdAndSocialType(String userId, SocialType socialType) {
        return userRepository.findByUserIdAndSocialType(userId, socialType)
                .orElseThrow(() ->
                        new UsernameNotFoundException("존재하지 않는 사용자")
                );
    }

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserIdAndSocialType(userId, SocialType.LOCAL);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
