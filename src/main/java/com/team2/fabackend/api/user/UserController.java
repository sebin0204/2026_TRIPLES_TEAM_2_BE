package com.team2.fabackend.api.user;

import com.team2.fabackend.api.auth.dto.SignupRequest;
import com.team2.fabackend.api.user.dto.UserInfoResponse;
import com.team2.fabackend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 API")
public class UserController {
    private final UserService userService;

    /**
     * 회원 정보 조회
     */
    @GetMapping("/me")
    @Operation(summary = "회원 정보 조회", description = "AccessToken으로 사용자 조회")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
//      이어서 구현해야 함
    }
}
