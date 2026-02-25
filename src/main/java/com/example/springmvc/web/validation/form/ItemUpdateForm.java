package com.example.springmvc.web.validation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 상품 수정 폼 전송 객체
 * - Item 도메인 객체와 분리하여 수정 시 필요한 필드와 검증 어노테이션만 보유
 * - id 필수 (수정 대상 식별)
 * - quantity 제한 없음 (수정 시에는 9999 초과 가능)
 */
@Data
public class ItemUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    // 수정 시 수량은 자유롭게
    private Integer quantity;
}
