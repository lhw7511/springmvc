package com.example.springmvc.security;

import com.example.springmvc.domain.member.Member;
import com.example.springmvc.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security의 UserDetailsService 구현체
 *
 * [역할]
 * Spring Security의 UsernamePasswordAuthenticationFilter가 로그인 폼 데이터를
 * 받으면, 내부적으로 이 서비스의 loadUserByUsername() 을 호출하여 사용자 정보를 조회한다.
 *
 * [동작 흐름]
 * 1. POST /login 요청 → UsernamePasswordAuthenticationFilter 가로챔
 * 2. request에서 username(loginId), password 추출
 * 3. loadUserByUsername(loginId) 호출 → DB(여기선 메모리)에서 회원 조회
 * 4. 반환된 UserDetails의 password를 BCryptPasswordEncoder로 입력값과 비교
 * 5. 일치하면 SecurityContext에 Authentication 저장 → 로그인 성공
 * 6. 불일치하면 AuthenticationException → 로그인 실패
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * loginId로 회원을 조회하여 CustomUserDetails 로 감싸 반환한다.
     *
     * @param username SecurityConfig에서 usernameParameter("loginId")로 설정한 폼 필드값
     * @throws UsernameNotFoundException 해당 loginId의 회원이 없을 때
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다: " + username));

        return new CustomUserDetails(member);
    }
}
