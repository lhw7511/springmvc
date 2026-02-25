package com.example.springmvc.web.validation;

import com.example.springmvc.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [Bean Validation - HttpMessageConverter (@RequestBody)]
 *
 * @ModelAttribute vs @RequestBody 검증 차이
 *
 * @ModelAttribute (폼 전송 - 필드 단위 바인딩)
 *   - 특정 필드 타입 오류가 나도 나머지 필드는 정상 바인딩됨
 *   - BindingResult에 오류 담기고 컨트롤러 정상 호출됨
 *
 * @RequestBody (JSON - 객체 단위 바인딩)
 *   - HttpMessageConverter가 JSON → 객체 변환 실패 시 컨트롤러 자체가 호출 안 됨
 *   - 타입 오류 시 400 에러 바로 반환 (BindingResult 의미 없음)
 *   - 타입은 맞지만 Bean Validation 실패 시에는 BindingResult 사용 가능
 */
@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    @PostMapping("/add")
    public Object addItem(@Validated @RequestBody ItemSaveForm form, BindingResult bindingResult) {

        log.info("API 컨트롤러 호출");

        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors={}", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");
        return form;
    }
}
