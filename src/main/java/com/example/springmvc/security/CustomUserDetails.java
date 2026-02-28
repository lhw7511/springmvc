package com.example.springmvc.security;

import com.example.springmvc.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security의 UserDetails 구현체 - 어댑터 패턴
 *
 * [역할]
 * Spring Security는 인증 처리 시 UserDetails 인터페이스로만 사용자 정보를 다룬다.
 * 우리의 도메인 Member 객체를 UserDetails로 감싸주는 어댑터 역할을 한다.
 *
 * [왜 이 방식을 사용하는가]
 * Member 도메인 객체에 UserDetails를 직접 구현하면 도메인 계층이 Spring Security에
 * 의존하게 된다 (계층 오염). 별도 어댑터 클래스를 두면 도메인은 순수하게 유지되고,
 * Security 관련 코드는 security 패키지에 격리된다.
 */
public class CustomUserDetails implements UserDetails {

    // 실제 도메인 회원 객체를 감싸고 있음
    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    /**
     * 도메인 Member 객체를 반환한다.
     * 컨트롤러에서 @AuthenticationPrincipal CustomUserDetails userDetails 로 주입받은 후
     * userDetails.getMember() 로 실제 회원 정보에 접근할 때 사용한다.
     */
    public Member getMember() {
        return member;
    }

    /**
     * 사용자의 권한(Role) 목록 반환
     * Spring Security는 권한 앞에 "ROLE_" 접두사를 붙이는 관례를 따른다.
     * hasRole("USER") → 내부적으로 "ROLE_USER" 와 비교함
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * 인증에 사용할 비밀번호 반환
     * BCrypt 해시값이 저장된 member.password 를 반환한다.
     * Spring Security의 AuthenticationProvider가 이 값과 입력값을 BCryptPasswordEncoder로 비교한다.
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 인증에 사용할 사용자명(식별자) 반환
     * 우리 시스템에서는 loginId 가 고유 식별자 역할을 한다.
     */
    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    // 계정 만료, 잠금, 자격증명 만료, 활성화 여부
    // 현재는 모두 true(정상)로 고정 - 필요 시 Member 필드로 제어 가능
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
