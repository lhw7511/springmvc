package com.example.springmvc.domain.item;

/**
 * 상품 종류 Enum
 * - 라디오 버튼 실습용 (28강~29강)
 * - description: 화면에 표시할 한글 이름
 * - getDescription(): 타임리프에서 ${type.description} 으로 접근
 */
public enum ItemType {

    BOOK("도서"), FOOD("음식"), ETC("기타");

    private final String description;

    ItemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
