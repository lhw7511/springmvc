package com.example.springmvc.basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 타임리프 기본 기능 실습 컨트롤러
 * - 김영한 스프링 MVC 2편 - 타임리프 기초 파트
 * - URL: /basic/...
 */
@Controller
@RequestMapping("/basic")
public class BasicController {

    /**
     * [강의 5강] 텍스트 - text, utext
     * th:text 사용 → HTML 엔티티로 이스케이프 처리됨
     * ex) <b> → &lt;b&gt; 로 출력 (태그가 문자로 보임)
     */
    @GetMapping("/text-basic")
    public String textBasic(Model model) {
        model.addAttribute("data", "hello <b>spring</b>");
        return "basic/text-basic";
    }

    /**
     * [강의 6강] 텍스트 - text, utext (Unescape)
     * th:utext 사용 → HTML 태그가 실제로 렌더링됨
     * ex) <b>spring</b> → spring이 굵게 출력
     * ⚠️ XSS 공격 위험 있으므로 신뢰된 데이터에만 사용할 것!
     */
    @GetMapping("/text-unescaped")
    public String textUnescaped(Model model) {
        model.addAttribute("data", "hello <b>spring</b>");
        return "basic/text-unescaped";
    }

    /**
     * [강의 7강] 변수 - SpringEL 표현식
     * 타임리프에서 변수를 사용할 때는 ${...} 문법 사용
     * - 객체 단건, List, Map 세 가지 형태로 모델에 전달
     * - 템플릿에서 user.username / users[0].username / userMap['userA'].username 등으로 접근
     */
    @GetMapping("/variable")
    public String variable(Model model) {
        User userA = new User("userA", 10);
        User userB = new User("userB", 20);

        // List 형태로 전달
        List<User> list = new ArrayList<>();
        list.add(userA);
        list.add(userB);

        // Map 형태로 전달
        Map<String, User> map = new HashMap<>();
        map.put("userA", userA);
        map.put("userB", userB);

        model.addAttribute("user", userA);
        model.addAttribute("users", list);
        model.addAttribute("userMap", map);
        return "basic/variable";
    }

    /**
     * [강의 8강] 기본 객체들
     * 타임리프가 기본으로 제공하는 객체들
     * - ${#request}, ${#response}, ${#session}, ${#servletContext}
     * - ${#locale}
     * 세션에 데이터를 담아서 타임리프에서 꺼내는 실습
     */
    @GetMapping("/basic-objects")
    public String basicObjects(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        // 세션에 데이터 저장 → 타임리프에서 ${#session.sessionData} 로 접근
        session.setAttribute("sessionData", "Hello Session");
        model.addAttribute("request", request);
        return "basic/basic-objects";
    }

    /**
     * [강의 10강] URL 링크
     * 타임리프에서 URL 표현 시 @{...} 문법 사용
     * - 기본 URL: @{/hello}
     * - 쿼리 파라미터: @{/hello(param1=${param1}, param2=${param2})}
     * - 경로 변수: @{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}
     */
    @GetMapping("/link")
    public String link(Model model) {
        model.addAttribute("param1", "data1");
        model.addAttribute("param2", "data2");
        return "basic/link";
    }

    /**
     * [강의 11강] 리터럴
     * 타임리프에서 리터럴(고정된 값)을 다루는 방법
     * - 문자 리터럴: 항상 작은 따옴표로 감싸야 함 (공백 포함 시 필수!)
     * - 리터럴 대체: |hello ${data}| 문법으로 편리하게 문자열 조합 가능
     */
    @GetMapping("/literal")
    public String literal(Model model) {
        model.addAttribute("data", "Spring!");
        return "basic/literal";
    }

    /**
     * [강의 12강] 연산
     * 타임리프에서 사용 가능한 연산자들
     * - 산술 연산: +, -, *, /, %
     * - 비교 연산: >, <, >=, <= (HTML에서는 gt, lt, ge, le 권장)
     * - 조건식(삼항): (조건) ? 참 : 거짓
     * - Elvis 연산자: 값 ?: 기본값 (null이면 기본값 출력)
     * - No-Operation(_): null이면 타임리프가 개입하지 않고 HTML 태그 내용 그대로 출력
     */
    @GetMapping("/operation")
    public String operation(Model model) {
        model.addAttribute("nullData", null);   // Elvis/No-Operation 테스트용 null 데이터
        model.addAttribute("data", "Spring!");  // 정상 데이터
        return "basic/operation";
    }

    /**
     * [강의 13강] 속성 값 설정
     * th:속성명 으로 HTML 속성을 동적으로 설정
     * - th:attrappend / th:attrprepend: 기존 속성값에 추가
     * - th:classappend: class 속성에 값 추가 (공백 처리 자동)
     * - th:checked: true/false로 체크박스 상태 제어 (HTML checked="false"는 무시되지만 타임리프는 정상 처리)
     */
    @GetMapping("/attribute")
    public String attribute() {
        return "basic/attribute";
    }

    /**
     * [강의 14강] 반복
     * th:each 로 컬렉션 반복 처리
     * - 기본: th:each="user : ${users}"
     * - 반복 상태 변수: th:each="user, userStat : ${users}"
     *   → userStat.index, count, size, first, last, even, odd, current 사용 가능
     */
    @GetMapping("/each")
    public String each(Model model) {
        addUsers(model);
        return "basic/each";
    }

    /**
     * [강의 15강] 조건부 평가
     * 조건에 따라 HTML 요소를 렌더링하거나 제거
     * - th:if: 조건이 true일 때만 렌더링
     * - th:unless: 조건이 false일 때만 렌더링 (if의 반대)
     * - th:switch / th:case: 자바의 switch문과 동일, th:case="*"는 default
     */
    @GetMapping("/condition")
    public String condition(Model model) {
        addUsers(model);
        return "basic/condition";
    }

    /**
     * [강의 16강] 주석
     * 타임리프 주석 3가지 종류
     * 1. 표준 HTML 주석: 브라우저에서 주석으로 보임, 타임리프는 처리하지 않음
     * 2. 타임리프 파서 주석: 렌더링 시 완전히 제거됨 (소스에도 안 보임)
     * 3. 프로토타입 주석: HTML로 직접 열면 주석, 타임리프 렌더링 시에는 정상 출력
     */
    @GetMapping("/comments")
    public String comments(Model model) {
        model.addAttribute("data", "Spring!");
        return "basic/comments";
    }

    /**
     * [강의 17강] 블록
     * <th:block> 은 타임리프 자체 태그로, 렌더링 시 사라짐
     * - th:each 등을 여러 태그에 한번에 적용하고 싶을 때 사용
     * - 실제 HTML에는 존재하지 않는 가상의 블록 역할
     */
    @GetMapping("/block")
    public String block(Model model) {
        addUsers(model);
        return "basic/block";
    }

    /**
     * [강의 18강] 자바스크립트 인라인
     * <script th:inline="javascript"> 선언 시 자바스크립트에서 타임리프 변수 사용 가능
     * - 문자열 자동 따옴표 처리, 객체 자동 JSON 변환
     * - 내추럴 템플릿: 주석으로 감싸면 HTML 직접 열 때도 기본값 표시 가능
     * - th:each로 자바스크립트 반복문도 처리 가능
     */
    @GetMapping("/javascript")
    public String javascript(Model model) {
        model.addAttribute("user", new User("UserA", 10)); // 단건 객체 → JSON 변환 확인용
        addUsers(model); // 리스트 → 인라인 each 확인용
        return "basic/javascript";
    }

    /**
     * 여러 컨트롤러에서 공통으로 사용하는 테스트용 유저 목록을 모델에 추가
     * UserA(10), UserB(20), UserC(30) 3명을 담아 전달
     */
    private void addUsers(Model model) {
        List<User> list = new ArrayList<>();
        list.add(new User("UserA", 10));
        list.add(new User("UserB", 20));
        list.add(new User("UserC", 30));
        model.addAttribute("users", list);
    }

    /**
     * 실습용 내부 정적 클래스
     * - @Data: getter, setter, toString, equals, hashCode 자동 생성 (Lombok)
     * - @AllArgsConstructor: 모든 필드를 받는 생성자 자동 생성 (Lombok)
     */
    @Data
    @AllArgsConstructor
    static class User {
        private String username;
        private int age;
    }

}
