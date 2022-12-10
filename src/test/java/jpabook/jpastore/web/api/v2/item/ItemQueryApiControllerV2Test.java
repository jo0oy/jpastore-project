package jpabook.jpastore.web.api.v2.item;

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

@Slf4j
@DisplayName("상품 조회 API v2 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemQueryApiControllerV2Test {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.cleanUp();
    }

    @DisplayName("[성공][api] 단일 상품 조회 - DetailInfo -> DetailDTO 로 변환")
    @Test
    void givenItemId_whenGetRequestItemDetailInfo_thenReturnItemDetailInfo() {
        // given
        var itemId = 1L;
        var url = "http://localhost:" + port + "/api/v2/items/" + itemId;

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.itemName").isEqualTo("book1")
                .jsonPath("$.data.author").isEqualTo("kim1")
                .jsonPath("$.data.isbn").isEqualTo("11111")
                .jsonPath("$.data.director").doesNotExist()
                .jsonPath("$.data.artist").doesNotExist();
    }

    @DisplayName("[실패][api] 존재하지 않는 상품 조회")
    @Test
    void givenNoneExistItemId_whenGetRequestItemDetailInfo_thenReturnBadRequestError() {
        // given
        var itemId = 1000L;
        var url = "http://localhost:" + port + "/api/v2/items/" + itemId;

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("entity_not_found");
    }

    @DisplayName("[성공][api] 상품명으로 상품 리스트 조회")
    @Test
    void givenItemName_whenGetRequestSearchItemsByName_thenReturnSearchedItemInfoList() {
        // given
        var url = "/api/v2/items/list/search";
        var itemName = "book";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("name", itemName)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(3)
                .jsonPath("$.data.items[0].itemName").isEqualTo("book1");
    }

    @DisplayName("[성공][api] 대문자 상품명으로 상품 리스트 조회 - case-insensitive 한지 확인")
    @Test
    void givenUpperCaseItemName_whenGetRequestSearchItemsByName_thenReturnSearchedItemInfoList() {
        // given
        var url = "/api/v2/items/list/search";
        var itemName = "BOOK";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("name", itemName)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(3)
                .jsonPath("$.data.items[0].itemName").isEqualTo("book1");
    }

    @DisplayName("[성공][api] 상품명으로 상품 리스트 조회 - 조회 결과 0인 경우")
    @Test
    void givenNoneExistItemName_whenGetRequestSearchItemsByName_thenReturnEmptyList() {
        // given
        var url = "/api/v2/items/list/search";
        var itemName = "none";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("name", itemName)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(0)
                .jsonPath("$.data.items").isEmpty();
    }
}
