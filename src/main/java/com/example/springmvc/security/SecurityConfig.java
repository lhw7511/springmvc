package com.example.springmvc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스
 *
 * [역할]
 * 어떤 URL에 누가 접근 가능한지, 로그인/로그아웃을 어떻게 처리할지 정의한다.
 *
 * [@EnableWebSecurity]
 * Spring Security의 웹 보안 기능을 활성화한다.
 * Spring Boot 3.x 에서는 자동 설정이 있지만, SecurityFilterChain 빈을 직접 등록하면
 * 자동 설정이 백오프(back-off)되고 이 설정이 적용된다.
 *
 * [SecurityFilterChain vs WebSecurityConfigurerAdapter]
 * Spring Security 5.7+ 부터 WebSecurityConfigurerAdapter는 deprecated.
 * 현재 권장 방식은 SecurityFilterChain 빈을 등록하는 것이다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * HTTP 보안 규칙 정의
     *
     * [URL 접근 제어 원칙]
     * - permitAll: 로그인 전에도 접근 가능한 공개 리소스
     * - authenticated: 로그인(인증)된 사용자만 접근 가능
     * - anyRequest().authenticated(): 위에서 명시되지 않은 나머지 모든 URL은 인증 필요
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 공개 URL - 인증 없이 접근 가능
                .requestMatchers("/", "/home", "/login", "/members/**").permitAll()
                .requestMatchers("/basic/**", "/validation/**").permitAll()
                // 인증 필요 URL
                .requestMatchers("/form/items/**", "/session-info").authenticated()
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            // 폼 로그인 설정
            // Spring Security가 POST /login 을 직접 처리한다 (컨트롤러 불필요)
            .formLogin(form -> form
                .loginPage("/login")                  // 커스텀 로그인 페이지 (GET)
                .loginProcessingUrl("/login")         // 로그인 폼 제출 URL (POST) - Security가 처리
                .usernameParameter("loginId")         // 폼의 username 필드명 (기본값: username)
                .passwordParameter("password")        // 폼의 password 필드명
                .defaultSuccessUrl("/home", true)     // 로그인 성공 후 이동 URL
                .failureUrl("/login?error=true")      // 로그인 실패 시 이동 URL
                .permitAll()
            )
            // 로그아웃 설정
            // Spring Security가 POST /logout 을 직접 처리한다 (컨트롤러 불필요)
            .logout(logout -> logout
                .logoutUrl("/logout")                 // 로그아웃 요청 URL (POST)
                .logoutSuccessUrl("/home")            // 로그아웃 성공 후 이동 URL
                .invalidateHttpSession(true)          // 세션 무효화
                .deleteCookies("JSESSIONID")          // 세션 쿠키 삭제
                .permitAll()
            );

        return http.build();
    }

    /**
     * 비밀번호 암호화 인코더 빈 등록
     *
     * [BCrypt 선택 이유]
     * - 단방향 해시: 원문 복원 불가
     * - Salt 자동 포함: 같은 비밀번호도 매번 다른 해시값 → Rainbow Table 공격 방지
     * - 비용 계수(cost factor): 연산 속도를 의도적으로 느리게 → Brute Force 공격 어려움
     *
     * [Spring Security 연동]
     * CustomUserDetailsService가 반환한 UserDetails.getPassword() (BCrypt 해시)와
     * 사용자 입력 비밀번호를 이 인코더가 matches()로 비교한다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
