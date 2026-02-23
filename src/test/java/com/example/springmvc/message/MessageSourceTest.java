package com.example.springmvc.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.context.MessageSource.*;

/**
 * [34강~35강] MessageSource 직접 사용 테스트
 * 스프링이 자동으로 등록해주는 MessageSource 빈을 직접 주입받아서 사용
 * - messages.properties    → Locale.KOREAN or Locale.getDefault()
 * - messages_en.properties → Locale.ENGLISH
 */
@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource ms;

    /**
     * [35강] 기본 메시지 조회
     * Locale.KOREAN: messages.properties 에서 item.itemName 조회
     * → "상품명" 반환
     */
    @Test
    void helloMessage() {
        // getMessage(코드, 파라미터배열, 기본값, 로케일)
        String result = ms.getMessage("item.itemName", null, null, Locale.KOREAN);
        assertThat(result).isEqualTo("상품명");
    }

    /**
     * [35강] 메시지 없을 때 기본값 반환
     * 존재하지 않는 코드 조회 시 defaultMessage 반환
     * defaultMessage가 null이면 NoSuchMessageException 발생
     */
    @Test
    void notFoundMessageCodeDefaultMessage() {
        // 존재하지 않는 코드, 기본값 "기본 메시지" 설정
        String result = ms.getMessage("no_code", null, "기본 메시지", Locale.KOREAN);
        assertThat(result).isEqualTo("기본 메시지");
    }

    /**
     * [35강] 메시지 없을 때 예외 발생
     * defaultMessage가 null이면 NoSuchMessageException 발생
     */
    @Test
    void notFoundMessageCode() {
        // defaultMessage를 null로 설정하면 예외 발생
        assertThatThrownBy(() -> ms.getMessage("no_code", null, null, Locale.KOREAN))
                .isInstanceOf(org.springframework.context.NoSuchMessageException.class);
    }

    /**
     * [35강] 국제화 - 영어 메시지 조회
     * Locale.ENGLISH → messages_en.properties 에서 조회
     * → "Item Name" 반환
     */
    @Test
    void enLang() {
        // Locale.ENGLISH: messages_en.properties 파일 사용
        assertThat(ms.getMessage("item.itemName", null, Locale.ENGLISH))
                .isEqualTo("Item Name");
    }
}
