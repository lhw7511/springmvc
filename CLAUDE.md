# CLAUDE.md

이 파일은 이 저장소에서 작업할 때 Claude Code(claude.ai/code)에게 제공되는 가이드입니다.

## 빌드 & 실행

```bash
# 애플리케이션 실행 (http://localhost:8080)
./gradlew bootRun

# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 클린 빌드
./gradlew clean build
```

**기술 스택:** Spring Boot 3.5.11, Java 17, Gradle, Thymeleaf, Bean Validation, Lombok

## 테스트 계정

`TestDataInit.java`가 `@PostConstruct`를 통해 애플리케이션 시작 시 테스트 회원을 생성한다:
- 로그인 ID: `test`
- 비밀번호: `test!`

## 아키텍처

**인메모리 레포지토리** 기반의 계층형 MVC 구조 (DB 없음 — 재시작 시 데이터 초기화).

```
web/          → 컨트롤러, Form DTO (LoginForm, ItemSaveForm, ItemUpdateForm)
domain/       → 도메인 엔티티 (Item, Member), 레포지토리, 서비스
resources/
  templates/  → Thymeleaf HTML 템플릿
  *.properties → messages, errors (검증 메시지)
```

**핵심 관심사 분리:**
- 도메인 객체(`Item`, `Member`)에는 검증 애노테이션 없음
- Form DTO(`ItemSaveForm`, `ItemUpdateForm`)가 검증을 담당하며, 작업별로 규칙이 다름 (저장 vs. 수정)
- `ItemValidator`가 복합 필드 규칙 처리 (가격 × 수량 ≥ 10,000)

## 검증 시스템

`errors.properties`를 통한 다단계 오류 메시지 해석 (우선순위 높음 → 낮음):
1. `required.item.itemName`
2. `required.itemName`
3. `required.java.lang.String`
4. `required`

사용된 Bean Validation 애노테이션: `@NotBlank`, `@NotNull`, `@Range`, `@Max`

`domain/item/`의 커스텀 검증 그룹(`SaveCheck`, `UpdateCheck`)으로 작업별 다른 규칙 적용 가능.

## 세션 관리

- 로그인 시 `Member`를 `HttpSession`에 `LoginController.LOGIN_MEMBER` 키로 저장
- 세션 타임아웃: 60초 (`application.properties`에서 설정)
- 컨트롤러에서 `@SessionAttribute`로 세션 회원 조회
- `HomeController`는 세션 상태에 따라 `home.html` 또는 `loginHome.html` 렌더링

## 주요 엔드포인트

| 경로 | 설명 |
|------|------|
| `/` | 홈 (세션 상태 반영) |
| `/login`, `/logout` | 인증 |
| `/form/items` | 상품 CRUD (PRG 패턴 적용) |
| `/validation/*` | 검증 예제 (V1–V4 + API) |
| `/basic/*` | Thymeleaf 학습 페이지 |
| `/session-info` | 세션 상태 디버그 (REST) |

## 학습 모듈

이 프로젝트는 학습용이다. `basic/`과 `validation/` 패키지에는 버전별 컨트롤러 변형(`V1`–`V4`)이 있으며, 단계적으로 다양한 접근법을 보여준다. 수정 시 이 점진적인 구조를 유지해야 한다.
