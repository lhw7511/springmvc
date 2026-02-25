package com.example.springmvc.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 * - 세션 적용 전: 단순 홈 화면 반환
 * - 세션 적용 후: 로그인 여부에 따라 홈 화면 분기 예정
 */
@Slf4j
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
