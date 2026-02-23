package com.example.springmvc.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 배송 방식 클래스
 * - 셀렉트 박스 실습용 (30강~31강)
 * - code: 실제 저장되는 값 (FAST, NORMAL, SLOW)
 * - displayName: 화면에 표시되는 이름 (빠른 배송, 일반 배송, 느린 배송)
 * - @Data + @AllArgsConstructor: Lombok으로 생성자, getter/setter 자동 생성
 */
@Data
@AllArgsConstructor
public class DeliveryCode {

    private String code;
    private String displayName;
}
