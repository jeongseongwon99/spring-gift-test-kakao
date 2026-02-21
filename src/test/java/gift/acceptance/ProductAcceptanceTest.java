package gift.acceptance;

import gift.model.Category;
import gift.model.CategoryRepository;
import gift.model.OptionRepository;
import gift.model.Product;
import gift.model.ProductRepository;
import gift.model.WishRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    WishRepository wishRepository;

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        wishRepository.deleteAllInBatch();
        optionRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    void 상품_생성_성공() {
        // given
        var category = categoryRepository.save(new Category("음료"));
        var request = Map.of(
                "name", "아메리카노",
                "price", 4500,
                "imageUrl", "http://example.com/image.jpg",
                "categoryId", category.getId()
        );

        // when
        var response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/products")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getLong("id")).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo("아메리카노");
        assertThat(response.jsonPath().getInt("price")).isEqualTo(4500);
    }

    @Test
    void 존재하지_않는_카테고리로_상품_생성시_실패한다() {
        // given
        var request = Map.of(
                "name", "아메리카노",
                "price", 4500,
                "imageUrl", "http://example.com/image.jpg",
                "categoryId", 999999
        );

        // when
        var response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/products")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    void 상품_목록_조회_성공() {
        // given
        var category = categoryRepository.save(new Category("음료"));
        productRepository.save(new Product("아메리카노", 4500, "http://example.com/image.jpg", category));
        productRepository.save(new Product("카페라떼", 5000, "http://example.com/latte.jpg", category));

        // when
        var response = RestAssured.given().log().all()
                .when()
                .get("/api/products")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("name"))
                .containsExactlyInAnyOrder("아메리카노", "카페라떼");
    }

    @Test
    void 상품_없을때_빈_목록을_반환한다() {
        // when
        RestAssured.given().log().all()
                .when()
                .get("/api/products")
                .then().log().all()
                .statusCode(200)
                .body("$", hasSize(0));
    }
}
