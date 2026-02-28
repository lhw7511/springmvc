package com.example.springmvc;

import com.example.springmvc.domain.member.Member;
import com.example.springmvc.domain.member.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 서버 시작 시 테스트용 기본 계정 자동 생성
 *
 * Spring Security 적용 후 비밀번호를 BCrypt로 암호화하여 저장한다.
 * Spring Security의 DaoAuthenticationProvider가 로그인 시
 * BCryptPasswordEncoder.matches(입력값, 저장된해시) 로 비교하므로
 * 저장 시에도 반드시 동일한 인코더로 암호화해야 한다.
 */
@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Member member = new Member();
        member.setLoginId("test");
        // 평문 "test!" → BCrypt 해시값으로 변환하여 저장
        member.setPassword(passwordEncoder.encode("test!"));
        member.setName("테스터");
        memberRepository.save(member);
    }
}
