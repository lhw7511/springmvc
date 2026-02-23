package com.example.springmvc.web.item;

import com.example.springmvc.domain.item.DeliveryCode;
import com.example.springmvc.domain.item.Item;
import com.example.springmvc.domain.item.ItemRepository;
import com.example.springmvc.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 타임리프 스프링 통합 실습 컨트롤러
 * - 김영한 스프링 MVC 2편 - 22강~31강
 * - 다루는 내용: th:object, th:field, 체크박스, 라디오버튼, 셀렉트박스
 * - URL: /form/items/...
 */
@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    /**
     * [강의 25강] @ModelAttribute - 공통 모델 데이터
     * 등록 지역 데이터를 모든 요청에 공통으로 모델에 담아줌
     * LinkedHashMap → 순서 보장 (SEOUL → BUSAN → JEJU 순서 유지)
     * ⚠️ 실무에서는 static으로 미리 만들어두는 게 성능상 좋음 (매 요청마다 생성되므로)
     */
    @ModelAttribute("regions")
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;
    }

    /**
     * [강의 28강] 라디오 버튼 공통 데이터
     * ItemType enum의 모든 값을 배열로 반환
     * 타임리프에서 th:each로 반복하여 라디오 버튼 렌더링
     */
    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        return ItemType.values(); // BOOK, FOOD, ETC
    }

    /**
     * [강의 30강] 셀렉트 박스 공통 데이터
     * 배송 방식 목록을 모든 요청에 공통으로 모델에 담아줌
     * DeliveryCode: code(실제 값) + displayName(화면 표시용)
     */
    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }

    /**
     * [강의 22강] 상품 목록
     * 전체 상품 조회 후 모델에 담아 뷰로 전달
     */
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/items";
    }

    /**
     * [강의 23강] 상품 상세
     * PathVariable로 itemId를 받아 단건 조회
     */
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/item";
    }

    /**
     * [강의 24강] 상품 등록 폼 - GET
     * 빈 Item 객체를 모델에 담아 전달
     * → 타임리프에서 th:object="${item}" 사용을 위해 빈 객체라도 필요
     */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "form/addForm";
    }

    /**
     * [강의 24강~30강] 상품 등록 - POST
     * @ModelAttribute로 폼 데이터를 Item 객체에 자동 바인딩
     * - open: 단일 체크박스 (체크 안 하면 null → 히든 필드로 false 처리)
     * - regions: 멀티 체크박스 (선택된 값들이 List로 바인딩)
     * - itemType: 라디오 버튼 (enum 값으로 바인딩)
     * - deliveryCode: 셀렉트 박스 (code 값으로 바인딩)
     * RedirectAttributes로 리다이렉트 시 파라미터 전달 (중복 저장 방지 - PRG 패턴)
     */
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        log.info("item.open={}", item.getOpen());       // 단일 체크박스 확인
        log.info("item.regions={}", item.getRegions()); // 멀티 체크박스 확인
        log.info("item.itemType={}", item.getItemType()); // 라디오 버튼 확인

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true); // 저장 완료 여부 전달
        return "redirect:/form/items/{itemId}"; // PRG 패턴으로 중복 저장 방지
    }

    /**
     * [강의 24강] 상품 수정 폼 - GET
     * 기존 상품 데이터를 조회해서 폼에 채워줌
     * → th:field 가 기존 값을 자동으로 채워줌 (체크박스, 라디오버튼, 셀렉트박스 포함)
     */
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/editForm";
    }

    /**
     * [강의 24강] 상품 수정 - POST
     * 수정된 폼 데이터를 받아 업데이트 후 상세 페이지로 리다이렉트
     */
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/form/items/{itemId}";
    }
}
