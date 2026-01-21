package com.team2.fabackend.service.user;

import com.team2.fabackend.api.user.dto.UserInfoResponse;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.service.PhoneVerification.PhoneVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserReader userReader;
    private final UserWriter userWriter;
    private final PhoneVerificationService phoneVerificationService;

    public UserInfoResponse getUser(Long id) {
        User user = userReader.findById(id);

        return new UserInfoResponse(
                user.getId(),
                user.getUserId(),
                user.getSocialType(),
                user.getPhoneNumber(),
                user.getName(),
                user.getNickName(),
                user.getBirth()
        );
    }
}
