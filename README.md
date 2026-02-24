# spring-gift-test

## 테스트 실행

2종류의 테스트를 제공합니다.

| 태스크 | DB | 앱 실행 방식 | 용도 |
|---|---|---|---|
| `./gradlew test` | H2 (인메모리) | `@SpringBootTest` | 단위 + 인수테스트 |
| `./gradlew cucumberTest` | PostgreSQL (Docker) | Docker 컨테이너 | Cucumber E2E 테스트 |

### 1. 인수테스트

H2 인메모리 DB를 사용하므로 별도 인프라가 필요 없습니다.

```bash
./gradlew test
```

JUnit 인수테스트만 실행:

```bash
./gradlew test --tests "gift.acceptance.*"
```

### 2. Cucumber 테스트 (Docker E2E)

Spring Boot 앱과 PostgreSQL 모두 Docker 컨테이너에서 실행합니다.

**사전 조건**: Docker 실행 중

```bash
./gradlew dockerBuild                        # Docker 이미지 빌드
./gradlew dockerUp                           # App + PostgreSQL 컨테이너 시작
curl http://localhost:28080/api/categories   # 애플리케이션 응답 확인
./gradlew cucumberTest                       # Docker 환경에서 테스트
./gradlew dockerDown                         # 컨테이너 정리
```

### 테스트 결과 확인

```
build/reports/tests/test/index.html          # test
build/reports/tests/cucumberTest/index.html  # cucumberTest
```

## 아키텍처

```
테스트 (Host) → HTTP → localhost:28080 (Docker App)
테스트 (Host) → JDBC → localhost:5432  (Docker DB)
App (Container) → JDBC → postgres:5432 (Docker DB)
```

- API 테스트는 Docker 컨테이너의 앱에 HTTP 요청
- 테스트 데이터 생성/초기화는 같은 DB에 JDBC로 직접 접근
- 프로덕션 코드 변경 없이 테스트 코드만으로 구성

## 테스트 구조

```
src/test/java/gift/
├── acceptance/     # JUnit 인수테스트 (RestAssured, H2)
│   ├── CategoryAcceptanceTest.java
│   ├── ProductAcceptanceTest.java
│   └── GiftAcceptanceTest.java
└── e2e/            # Cucumber E2E 테스트 (RestAssured + Repository, Docker)
    ├── E2eSpringConfiguration.java
    ├── E2eHooks.java
    ├── E2eCategoryStepDefinitions.java
    ├── E2eProductStepDefinitions.java
    └── E2eGiftStepDefinitions.java
```

## Cucumber 시나리오

```
src/test/resources/features/
├── category.feature   # 카테고리 생성, 목록 조회 (3 scenarios)
├── product.feature    # 상품 생성, 목록 조회 (4 scenarios)
└── gift.feature       # 선물하기, 재고 관리 (8 scenarios)
```
