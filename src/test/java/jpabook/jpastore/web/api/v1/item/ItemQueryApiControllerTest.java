package jpabook.jpastore.web.api.v1.item;

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

@DisplayName("상품 조회 API 테스트")
@Slf4j
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemQueryApiControllerTest {

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

    @DisplayName("[성공][api] 단일 상품 조회")
    @Test
    void givenItemId_whenGetRequestItemInfo_thenReturnItemInfo() {
        // given
        var itemId = 1L;
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.bookName").isEqualTo("book1")
                .jsonPath("$.data.author").isEqualTo("kim1")
                .jsonPath("$.data.isbn").isEqualTo("11111");
    }

    @DisplayName("[실패][api] 존재하지 않는 상품 조회")
    @Test
    void givenNotExistItemId_whenGetRequestItemInfo_thenReturnBadRequestError() {
        // given
        var itemId = 100L;
        var url = "http://localhost:" + port + "/api/v1/items/" + itemId;

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

    @DisplayName("[성공][api] 상품명으로 검색 결과 상품 리스트 조회")
    @Test
    void givenItemName_whenGetRequestItemListSearch_thenReturnItemInfoList() {
        // given
        var itemName = "book";
        var url = "/api/v1/items/list/search";

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

    @DisplayName("[성공][api] 전체 상품 리스트 조회")
    @Test
    void givenNothing_whenGetRequestItemList_thenReturnItemInfoList() {
        // given
        var url = "http://localhost:" + port + "/api/v1/items/list";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(9)
                .jsonPath("$.data.items[0].itemName").isEqualTo("book1");
    }

    @DisplayName("[성공][api] 전체 상품 리스트 조회 - 페이징, 정렬")
    @Test
    void givenPageRequest_whenGetRequestItemPagingList_thenReturnItemInfoPagingList() {
        // given
        var url = "/api/v1/items";
        var page = 0;
        var size = 5;
        var sort = "name";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("sort", sort)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(9)
                .jsonPath("$.data.totalPages").isEqualTo(2)
                .jsonPath("$.data.content[0].itemName").isEqualTo("album1");
    }

    @DisplayName("[성공][api] 전체 상품 리스트 검색 조회 - 페이징, 정렬, 검색")
    @Test
    void givenSearchConditionAndPageRequest_whenGetRequestItemPagingList_thenReturnItemInfoPagingList() {
        // given
        var url = "/api/v1/items";
        var minPrice = 15000;
        var maxPrice = 25000;
        var page = 0;
        var size = 5;
        var sort = "name";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("minPrice", minPrice)
                                .queryParam("maxPrice", maxPrice)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("sort", sort)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(6)
                .jsonPath("$.data.totalPages").isEqualTo(2)
                .jsonPath("$.data.content[0].itemName").isEqualTo("album1");
    }
}
