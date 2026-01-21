package com.team2.fabackend.api.user.dto;

import com.team2.fabackend.global.enums.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "유저 정보")
public class UserInfoResponse {
    @Schema(description = "유저 값")
    private Long id;

    @Schema(description = "유저 아이디")
    private String userId;

    @Schema(description = "유저 소셜 타입")
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Schema(description = "유저 전화번호")
    private String phoneNumber;

    @Schema(description = "유저 실명")
    private String name;

    @Schema(description = "유저 별명")
    private String nickName;

    @Schema(description = "유저 생년월일")
    private LocalDate birth;
}
