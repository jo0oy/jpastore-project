package jpabook.jpastore.web.api.v1.category;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

@DisplayName("카테고리 조회 API 테스트")
@Slf4j
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryQueryApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUp() {
        databaseCleanUp.cleanUp();
    }

    @DisplayName("[성공][api] 단일 카테고리 간단 조회(상품 정보 포함X) by categoryId")
    @Test
    void givenCategoryId_whenGetRequestSimpleCategoryInfo_thenReturnSimpleCategoryInfo() {
        // given
        var categoryId = 1L;
        var url = "http://localhost:" + port + "/api/v1/simple-categories/" + categoryId;

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("도서");
    }

    @DisplayName("[성공][api] 단일 카테고리 상세 조회(상품 정보 포함) by categoryId")
    @Test
    void givenCategoryId_whenGetRequestCategoryInfo_thenReturnCategoryInfo() {
        // given
        var categoryId = 6L; // name: '국내소설', category-items-size: 2
        var url = "http://localhost:" + port + "/api/v1/categories/" + categoryId;

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("국내소설")
                .jsonPath("$.data.categoryItems").isNotEmpty();
    }

    @DisplayName("[성공][api] 전체 카테고리 리스트 조회")
    @Test
    void givenNothing_whenGetRequestCategoryList_thenReturnCategoryInfoList() {
        // given
        var url = "http://localhost:" + port + "/api/v1/categories/list";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data[0].name").isEqualTo("도서")
                .jsonPath("$.data[0].parent").isEmpty()
                .jsonPath("$.data[0].childList").isNotEmpty();
    }

    @DisplayName("[성공][api] 전체 카테고리 계층 리스트 조회")
    @Test
    void givenNothing_whenGetRequestCategoryHierarchicalList_thenReturnCategoryParentChildInfoList() {
        // given
        var url = "http://localhost:" + port + "/api/v1/categories/hierarchical-list";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data[0].name").isEqualTo("도서")
                .jsonPath("$.data[0].childList").isNotEmpty();
    }

    @DisplayName("[성공][api] 전체 카테고리 계층 리스트 조회")
    @Test
    void givenCategoryId_whenGetRequestCategoryWithItemList_thenReturnCategoryWithItemList() {
        // given
        var categoryId = 6L;
        var url = "http://localhost:" + port + "/api/v1/categories/" + categoryId + "/items";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("국내소설")
                .jsonPath("$.data.categoryItems").isNotEmpty()
                .jsonPath("$.data.categoryItems[0].itemName").isEqualTo("book1");
    }
}
