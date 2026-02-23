# spring-gift-test

## 테스트 실행

3종류의 테스트를 제공합니다.

### 1. 인수테스트

H2 인메모리 DB를 사용하므로 별도 인프라가 필요 없습니다.

```bash
./gradlew test
```

JUnit 인수테스트만 실행:

```bash
./gradlew test --tests "gift.acceptance.*"
```

### 2. Cucumber 테스트

Docker로 PostgreSQL을 자동으로 띄운 뒤 Cucumber 시나리오를 실행합니다.
테스트 종료 시 컨테이너가 자동으로 정리됩니다.

**사전 조건**: Docker 실행 중

```bash
./gradlew cucumberTest
```

수동으로 Docker를 관리할 수도 있습니다:

```bash
./gradlew dockerUp    # PostgreSQL 컨테이너 시작
./gradlew dockerDown  # PostgreSQL 컨테이너 종료
```

### 테스트 결과 확인

```
build/reports/tests/test/index.html          # test
build/reports/tests/cucumberTest/index.html  # cucumberTest
```

## 테스트 구조

```
src/test/java/gift/
├── acceptance/     # JUnit 인수테스트 (RestAssured, H2)
│   ├── CategoryAcceptanceTest.java
│   ├── ProductAcceptanceTest.java
│   └── GiftAcceptanceTest.java
└── cucumber/       # Cucumber BDD 테스트 (RestAssured, PostgreSQL)
    ├── CucumberSpringConfiguration.java
    ├── CucumberHooks.java
    ├── ScenarioContext.java
    ├── CategoryStepDefinitions.java
    ├── ProductStepDefinitions.java
    └── GiftStepDefinitions.java
```

## Cucumber 시나리오

```
src/test/resources/features/
├── category.feature   # 카테고리 생성, 목록 조회 (3 scenarios)
├── product.feature    # 상품 생성, 목록 조회 (4 scenarios)
└── gift.feature       # 선물하기, 재고 관리 (8 scenarios)
```
