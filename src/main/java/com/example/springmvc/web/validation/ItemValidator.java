package com.example.springmvc.web.validation;

import com.example.springmvc.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * [Validator 분리 1]
 * 검증 로직을 컨트롤러에서 분리하여 별도 클래스로 관리
 * - Validator 인터페이스 구현
 *   - supports(): 이 Validator가 어떤 타입을 지원하는지 반환
 *   - validate(): 실제 검증 로직
 */
@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        // 상품명 검증 - String 타입이라 typeMismatch는 없지만 일관성을 위해 동일하게 처리
        if (!errors.hasFieldErrors("itemName")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required");
        }

        // 가격 검증 - 바인딩 실패(typeMismatch)한 경우 스킵
        if (!errors.hasFieldErrors("price")) {
            if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
                errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
            }
        }

        // 수량 검증 - 바인딩 실패(typeMismatch)한 경우 스킵
        if (!errors.hasFieldErrors("quantity")) {
            if (item.getQuantity() == null || item.getQuantity() > 9999) {
                errors.rejectValue("quantity", "max", new Object[]{9999}, null);
            }
        }

        // 복합 규칙 검증 (글로벌 오류) - 둘 다 바인딩 성공했을 때만 체크
        if (!errors.hasFieldErrors("price") && !errors.hasFieldErrors("quantity")) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                int resultPrice = item.getPrice() * item.getQuantity();
                if (resultPrice < 10000) {
                    errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
                }
            }
        }
    }
}
