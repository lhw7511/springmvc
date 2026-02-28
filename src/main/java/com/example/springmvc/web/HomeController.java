package com.example.springmvc.web;

import com.example.springmvc.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러 - Spring Security @AuthenticationPrincipal 방식
 *
 * [변경 이유]
 * Spring Security 적용 후에는 인증된 사용자 정보가 SecurityContext에 저장된다.
 * 기존 @SessionAttribute 방식 대신 @AuthenticationPrincipal 로 현재 인증 주체를 주입받는다.
 *
 * [@AuthenticationPrincipal 동작 원리]
 * SecurityContext → Authentication → Principal(CustomUserDetails) 를 꺼내
 * 메서드 파라미터에 자동으로 주입해준다.
 * 비로그인 상태이면 null이 주입된다 (anonymous authentication은 CustomUserDetails가 아님).
 */
@Slf4j
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        // 비로그인 상태 (null) 이면 일반 홈
        if (userDetails == null) {
            return "home";
        }

        // 로그인 상태이면 회원 정보를 모델에 담아 로그인 홈으로
        model.addAttribute("member", userDetails.getMember());
        return "loginHome";
    }
}
