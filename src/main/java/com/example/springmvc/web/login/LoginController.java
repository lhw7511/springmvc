package com.example.springmvc.web.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인 컨트롤러 - Spring Security 방식
 *
 * [변경 이유]
 * Spring Security의 UsernamePasswordAuthenticationFilter가 POST /login 을 직접 처리한다.
 * 따라서 로그인 폼 제출(POST)과 로그아웃(POST /logout)은 이 컨트롤러에서 제거하고,
 * GET /login (로그인 페이지 표시)만 담당한다.
 *
 * [Spring Security 로그인 흐름]
 * 1. 사용자가 보호된 URL 접근 → Security가 /login 으로 리다이렉트
 * 2. GET /login → 이 컨트롤러가 loginForm 뷰 반환
 * 3. 사용자가 폼 제출(POST /login) → UsernamePasswordAuthenticationFilter 처리
 * 4. 성공: /home 이동 / 실패: /login?error=true 리다이렉트
 */
@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm(
            @RequestParam(value = "error", required = false) String error,
            Model model) {

        // Spring Security 로그인 실패 시 ?error=true 파라미터가 붙어서 리다이렉트된다.
        // 이를 감지하여 뷰에 오류 플래그를 전달한다.
        if (error != null) {
            model.addAttribute("loginError", true);
        }

        // loginForm 뷰는 th:object 없이 error 플래그만으로 오류 메시지 표시
        model.addAttribute("loginForm", new LoginForm());
        return "login/loginForm";
    }
}
