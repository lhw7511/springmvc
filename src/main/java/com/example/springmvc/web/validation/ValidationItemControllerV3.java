package com.example.springmvc.web.validation;

import com.example.springmvc.domain.item.Item;
import com.example.springmvc.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * [40강] 검증 V3 - BindingResult 2
 *
 * V2(BindingResult 1)의 문제점:
 * - 오류 발생 시 사용자가 입력한 값이 사라짐 (rejectedValue 없음)
 * - 예: 가격에 "abc" 입력 → 오류 폼으로 돌아올 때 입력값이 비어있음
 *
 * V3(BindingResult 2)에서 해결:
 * 1. FieldError 생성자에 rejectedValue(거절된 값) 추가
 *    → FieldError(objectName, field, rejectedValue, bindingFailure, codes, arguments, defaultMessage)
 *    → 오류 발생해도 사용자 입력값 유지
 *
 * 2. rejectValue(), reject() 축약 메서드 사용
 *    → bindingResult.rejectValue("field", "errorCode", "defaultMessage")
 *    → bindingResult.reject("errorCode", "defaultMessage")
 *    → 내부적으로 MessageCodesResolver가 오류 코드를 생성해줌
 */
@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    /**
     * [40강] BindingResult 2 - rejectedValue로 입력값 유지
     *
     * FieldError 생성자 (full):
     * - objectName: 오류가 발생한 객체 이름 ("item")
     * - field: 오류 필드명
     * - rejectedValue: 사용자가 입력한 거절된 값 (이 값이 폼에 다시 표시됨)
     * - bindingFailure: 바인딩 실패 여부 (타입 오류면 true, 검증 오류면 false)
     * - codes: 메시지 코드 배열 (null이면 defaultMessage 사용)
     * - arguments: 메시지 인수 (null 가능)
     * - defaultMessage: 기본 오류 메시지
     */
    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // 상품명 검증
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }

        // 가격 검증 - rejectedValue에 item.getPrice() 담아서 오류 시 입력값 유지
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000,
                    1000000}, null);
        }

        // 수량 검증
        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999},
                    null);
        }

        // 복합 규칙 검증 (글로벌 오류)
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000,
                        resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item,
                       BindingResult bindingResult) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),
                    false, null, null, "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(),
                    false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(),
                    false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", null, null,
                        "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }
}
