package com.example.springmvc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

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
     * FindByIndexNameSessionRepository
     *
     * Spring Session이 Redis에 세션을 저장할 때 사용하는 저장소 인터페이스.
     * "사용자 이름으로 세션을 찾는" 기능을 제공한다.
     *
     * 예) test 계정으로 로그인한 세션이 몇 개인지 Redis에서 조회 가능
     * → 이 기능이 있어야 중복 로그인 감지가 가능함
     *
     * Spring Boot가 spring-session-data-redis 의존성을 보고 자동으로 빈을 생성해준다.
     * 여기서는 생성자 주입으로 받아서 sessionRegistry()에서 사용한다.
     */
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public SecurityConfig(FindByIndexNameSessionRepository<? extends Session> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * SpringSessionBackedSessionRegistry
     *
     * Spring Security의 동시 세션 제어에서 "현재 로그인된 세션 목록"을 관리하는 역할.
     *
     * 기본 구현체인 SessionRegistryImpl은 JVM 메모리에 세션 목록을 저장한다.
     * → Redis를 세션 저장소로 쓰면 Security는 메모리, 실제 세션은 Redis → 따로 놀아서 중복 감지 안 됨
     *
     * SpringSessionBackedSessionRegistry는 Redis에서 직접 세션 목록을 조회하기 때문에
     * Redis 세션 저장소와 Security의 동시 세션 제어가 정상적으로 연동된다.
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }

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
                .deleteCookies("RSESSIONID")          // 세션 쿠키 삭제
                .permitAll()
            )
            // 동시 세션 제어
            // 동일 계정으로 중복 로그인이 들어왔을 때 어떻게 처리할지 정의한다
            .sessionManagement(session -> session
                // 한 계정당 허용할 최대 동시 세션 수
                // 1로 설정 → A 기기에서 로그인 중인 상태에서 B 기기로 로그인하면 A 세션이 만료됨
                .maximumSessions(1)

                // 위에서 만든 Redis 기반 레지스트리를 사용하도록 지정
                // 이걸 설정 안 하면 기본 InMemory 레지스트리를 사용해서 중복 감지가 안 됨
                .sessionRegistry(sessionRegistry())

                // maxSessionsPreventsLogin(true)  → 이미 로그인 중이면 새 로그인 차단 (2번 방식)
                // maxSessionsPreventsLogin(false) → 새 로그인 허용, 기존 세션 만료 (1번 방식, 기본값)
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