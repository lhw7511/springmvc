package com.example.springmvc.basic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

/**
 * 타임리프 유틸리티 객체와 날짜 실습 컨트롤러
 * - 김영한 스프링 MVC 2편 - 9강
 * - #temporals 유틸리티 객체로 Java 8 날짜/시간 타입(LocalDateTime 등) 처리
 * - URL: /basic/date
 */
@Controller
@RequestMapping("/basic")
public class UtilityObjectController {

    /**
     * [강의 9강] 유틸리티 객체와 날짜
     * LocalDateTime.now() 를 모델에 담아 타임리프에 전달
     * 타임리프에서 #temporals 객체로 날짜 포맷팅 및 각 필드 접근 가능
     * ex) #temporals.format(localDateTime, 'yyyy-MM-dd HH:mm:ss')
     *     #temporals.year(localDateTime), #temporals.month(localDateTime) 등
     */
    @GetMapping("/date")
    public String date(Model model) {
        model.addAttribute("localDateTime", LocalDateTime.now());
        return "basic/date";
    }
}
