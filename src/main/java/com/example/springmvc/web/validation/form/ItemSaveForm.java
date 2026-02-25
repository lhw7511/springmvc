package com.example.springmvc.web.validation.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 상품 등록 폼 전송 객체
 * - Item 도메인 객체와 분리하여 등록 시 필요한 필드와 검증 어노테이션만 보유
 * - id 없음 (등록 시 불필요)
 * - quantity 최대 9999 제한
 */
@Data
public class ItemSaveForm {

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(value = 9999)
    private Integer quantity;
}
