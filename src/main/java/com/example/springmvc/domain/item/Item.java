package com.example.springmvc.domain.item;

import lombok.Data;

import java.util.List;

/**
 * 상품 도메인 클래스
 * - 타임리프 스프링 통합 실습용 (22강~31강)
 * - @Data: getter, setter, toString, equals, hashCode 자동 생성 (Lombok)
 *
 * 필드 설명:
 * - open: 판매 여부 → 단일 체크박스 (Boolean, 체크 안 하면 null)
 * - regions: 등록 지역 → 멀티 체크박스 (List, 여러 개 선택 가능)
 * - itemType: 상품 종류 → 라디오 버튼 (enum)
 * - deliveryCode: 배송 방식 → 셀렉트 박스 (String code 값)
 */
@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    private Boolean open;           // 판매 여부
    private List<String> regions;   // 등록 지역
    private ItemType itemType;      // 상품 종류
    private String deliveryCode;    // 배송 방식

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
