package com.example.springmvc.basic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 타임리프 템플릿 조각 & 레이아웃 실습 컨트롤러
 * - 김영한 스프링 MVC 2편 - 19강~21강
 * - URL: /template/...
 */
@Controller
@RequestMapping("/template")
public class TemplateController {

    /**
     * [강의 19강] 템플릿 조각
     * th:fragment 로 조각을 정의하고
     * th:insert / th:replace 로 다른 페이지에 삽입
     * - th:insert: 현재 태그 안에 조각을 삽입
     * - th:replace: 현재 태그 자체를 조각으로 교체
     */
    @GetMapping("/fragment")
    public String template() {
        return "template/fragment/fragmentMain";
    }

    /**
     * [강의 20강] 템플릿 레이아웃1
     * head 태그를 공통 레이아웃으로 만들어 재사용
     * - 공통 CSS/JS는 base.html에 정의
     * - 각 페이지에서 필요한 title, link만 넘겨서 조합
     */
    @GetMapping("/layout")
    public String layout() {
        return "template/layout/layoutMain";
    }

    /**
     * [강의 21강] 템플릿 레이아웃2 (레이아웃 상속)
     * HTML 전체를 레이아웃으로 만들어 상속받는 방식
     * - layoutFile.html이 전체 틀(레이아웃)
     * - layoutExtendMain.html이 title, content 영역만 오버라이드
     * - 마치 자바 상속처럼 레이아웃을 확장하는 개념
     */
    @GetMapping("/layoutExtend")
    public String layoutExtend() {
        return "template/layoutExtend/layoutExtendMain";
    }
}
