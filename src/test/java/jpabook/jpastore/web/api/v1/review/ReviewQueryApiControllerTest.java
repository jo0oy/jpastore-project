package jpabook.jpastore.web.api.v1.review;

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
@DisplayName("리뷰 조회 API 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewQueryApiControllerTest {

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

    @DisplayName("[성공][api] 단일 리뷰 조회 by reviewId")
    @Test
    void givenReviewId_whenGetRequestReviewInfo_thenReturnReviewInfo() {
        // given
        var reviewId = 1L;
        var url = "http://localhost:" + port + "/api/v1/reviews/" + reviewId;

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.memberInfo.memberId").isEqualTo(1L)
                .jsonPath("$.data.itemInfo.itemId").isEqualTo(1L)
                .jsonPath("$.data.reviewBody").isEqualTo("리뷰1 입니다.");
    }

    @DisplayName("[실패][api] 존재하지 않는 리뷰 조회 by reviewId")
    @Test
    void givenNotExistReviewId_whenGetRequestReviewInfo_thenReturnBadRequestError() {
        // given
        var reviewId = 100L;
        var url = "http://localhost:" + port + "/api/v1/reviews/" + reviewId;

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

    @DisplayName("[성공][api] 전체 리뷰 리스트 조회 - 검색조건 없음")
    @Test
    void givenNothing_whenGetRequestReviewList_thenReturnReviewInfoList() {
        // given
        var url = "http://localhost:" + port + "/api/v1/reviews/list";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(19);
    }

    @DisplayName("[성공][api] 검색 조건에 따른 리뷰 리스트 조회")
    @Test
    void givenSearchCondition_whenGetRequestReviewList_thenReturnReviewInfoList() {
        // given
        var url = "/api/v1/reviews/list";
        var username = "member1";
        var itemId = 1L;

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("username", username)
                                .queryParam("itemId", itemId)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(1)
                .jsonPath("$.data.list[0].reviewId").isEqualTo(1L);
    }

    @DisplayName("[성공][api] 전체 리뷰 리스트 페이징, 정렬 조회 - 검색조건 없음")
    @Test
    void givenPageable_whenGetRequestReviewPagingList_thenReturnReviewInfoPagingList() {
        // given
        var url = "http://localhost:" + port + "/api/v1/reviews";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(19)
                .jsonPath("$.data.totalPages").isEqualTo(2);
    }

    @DisplayName("[성공][api] 검색 조건에 따른 리뷰 리스트 페이징, 정렬 조회")
    @Test
    void givenSearchParamAndPageable_whenGetRequestReviewList_thenReturnReviewInfoList() {
        // given
        var url = "/api/v1/reviews";
        var username = "member1";
        var itemId = 1L;

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("username", username)
                                .queryParam("itemId", itemId)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(1)
                .jsonPath("$.data.totalPages").isEqualTo(1);
    }
}
