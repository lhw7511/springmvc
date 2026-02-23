package com.example.springmvc.web.validation;

import com.example.springmvc.domain.item.Item;
import com.example.springmvc.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [38강] 검증1 - V1 직접 검증 처리
 * 검증 오류를 Map에 담아서 뷰에 전달하는 가장 기본적인 방식
 * - errors Map에 오류 내용을 담아서 모델에 추가
 * - 뷰에서 errors 맵을 확인해서 오류 메시지 출력
 * ⚠️ 문제점: 타입 오류 시 스프링이 컨트롤러 진입 전에 튕겨버림 → V2에서 해결
 */
@Slf4j
@Controller
@RequestMapping("/validation/v1/items")
@RequiredArgsConstructor
public class ValidationItemControllerV1 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v1/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v1/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v1/addForm";
    }

    /**
     * [38강] 검증 V1 - 직접 Map으로 검증
     * 검증 오류를 errors Map에 담아서 처리
     * 오류 있으면 다시 폼으로, 없으면 저장 후 리다이렉트
     *
     * 검증 항목:
     * - itemName: 공백이면 오류
     * - price: null 이거나 1000~1000000 범위 벗어나면 오류
     * - quantity: null 이거나 9999 초과하면 오류
     * - 복합 규칙: price * quantity >= 10000 이어야 함
     */
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 결과를 담는 Map (key: 필드명, value: 오류메시지)
        Map<String, String> errors = new HashMap<>();

        // 상품명 검증: 공백이면 오류
        if (!StringUtils.hasText(item.getItemName())) {
            errors.put("itemName", "상품 이름은 필수입니다.");
        }

        // 가격 검증: null 이거나 범위 벗어나면 오류
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
        }

        // 수량 검증: null 이거나 9999 초과하면 오류
        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
        }

        // 복합 규칙 검증: 가격 * 수량 합계가 10000원 이상이어야 함
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // globalError: 특정 필드가 아닌 전체 오류
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }

        // 검증 실패 시 다시 입력 폼으로
        if (!errors.isEmpty()) {
            log.info("errors = {}", errors);
            model.addAttribute("errors", errors);
            return "validation/v1/addForm"; // 다시 폼으로 돌아감
        }

        // 검증 성공 시 저장
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v1/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v1/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item, Model model) {

        // 검증 오류 결과를 담는 Map
        Map<String, String> errors = new HashMap<>();

        if (!StringUtils.hasText(item.getItemName())) {
            errors.put("itemName", "상품 이름은 필수입니다.");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
        }
        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
        }
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                errors.put("globalError", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }

        if (!errors.isEmpty()) {
            log.info("errors = {}", errors);
            model.addAttribute("errors", errors);
            return "validation/v1/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v1/items/{itemId}";
    }
}
