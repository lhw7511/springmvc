package com.example.springmvc.domain.login;

import com.example.springmvc.domain.member.Member;
import com.example.springmvc.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 로그인 서비스
 * - loginId, password 일치 여부 확인
 * - 일치하면 Member 반환, 불일치하면 null 반환
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
