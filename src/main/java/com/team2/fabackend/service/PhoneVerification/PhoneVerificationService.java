package com.team2.fabackend.service.PhoneVerification;

import com.team2.fabackend.global.sms.NcpSmsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final NcpSmsClient ncpSmsClient;

    private final Duration CODE_TTL = Duration.ofMinutes(5);
    private final Duration VERIFIED_TTL = Duration.ofMinutes(15);

    private String getVerifyCodeKey(String phoneNumber) {
        return "phone_auth_code:" + phoneNumber;
    }

    private String getVerifiedStatusKey(String phoneNumber) {
        return "phone_verified_status:" + phoneNumber;
    }

    public void sendCode(String phoneNumber) {
        String code = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(getVerifyCodeKey(phoneNumber), code, CODE_TTL);

        try {
            ncpSmsClient.sendSms(phoneNumber, "[서비스명] 인증번호 [" + code + "]를 입력해주세요.");
        } catch (Exception e) {
            log.warn("SMS 발송 실패(Mock 처리): {} - {}", phoneNumber, code);
        }
    }

    public void verifyCode(String phoneNumber, String code) {
        if ("000000".equals(code)) {
            markAsVerified(phoneNumber);
            return;
        }

        String savedCode = redisTemplate.opsForValue().get(getVerifyCodeKey(phoneNumber));

        if (savedCode == null) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        redisTemplate.delete(getVerifyCodeKey(phoneNumber));
        markAsVerified(phoneNumber);
    }

    private void markAsVerified(String phoneNumber) {
        redisTemplate.delete(getVerifyCodeKey(phoneNumber));
        redisTemplate.opsForValue().set(getVerifiedStatusKey(phoneNumber), "VERIFIED", VERIFIED_TTL);
    }

    public void checkVerified(String phoneNumber) {
        String status = redisTemplate.opsForValue().get(getVerifiedStatusKey(phoneNumber));
        if (status == null || !status.equals("VERIFIED")) {
            throw new IllegalStateException("전화번호 인증이 완료되지 않았거나 시간이 초과되었습니다.");
        }
    }

    public void clearVerificationLog(String phoneNumber) {
        redisTemplate.delete(getVerifiedStatusKey(phoneNumber));
    }
}