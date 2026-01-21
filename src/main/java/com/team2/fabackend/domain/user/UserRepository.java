package com.team2.fabackend.domain.user;

import com.team2.fabackend.global.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserIdAndSocialType(String userId, SocialType socialType);
    boolean existsByUserIdAndSocialType(String userId, SocialType socialType);
    boolean existsByPhoneNumber(String phoneNumber);
}
