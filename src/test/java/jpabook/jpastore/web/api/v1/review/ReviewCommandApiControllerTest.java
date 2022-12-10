package jpabook.jpastore.web.api.v1.review;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.security.JwtTokenProvider;
import jpabook.jpastore.web.dto.review.ReviewDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

@Slf4j
@DisplayName("리뷰 등록/수정/삭제 API 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewCommandApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userToken = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("member1", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))).getAccessToken();

        adminToken = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))).getAccessToken();
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.cleanUp();
        userToken = null;
        adminToken = null;
    }

    @DisplayName("[성공][api] 리뷰 등록 - 인증된 사용자")
    @Test
    void givenValidRegisterReq_whenPostNewReviewReq_thenReturnSavedReviewId() {
        // given
        var req = ReviewDto.RegisterReviewReq.builder()
                .itemId(4L)
                .memberId(1L)
                .reviewBody("리뷰 20 입니다.")
                .build();

        var url = "http://localhost:" + port + "/api/v1/reviews";

        // when
        webTestClient.post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.registeredReviewId").isEqualTo(20L);
    }

    @DisplayName("[실패][api] 리뷰 등록 - 미인증 사용자")
    @Test
    void givenValidRegisterReqWithUnauthenticatedUser_whenPostNewReviewReq_thenReturnUnauthorizedError() {
        // given
        var req = ReviewDto.RegisterReviewReq.builder()
                .itemId(4L)
                .memberId(1L)
                .reviewBody("리뷰 20 입니다.")
                .build();

        var url = "http://localhost:" + port + "/api/v1/reviews";

        // when
        webTestClient.post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error.code").isEqualTo("error-002");
    }

    @DisplayName("[실패][api] 리뷰 등록 - 요청 바디 검증 에러")
    @Test
    void givenInvalidRegisterReq_whenPostNewReviewReq_thenReturnBadRequestError() {
        // given
        var req = ReviewDto.RegisterReviewReq.builder()
                .itemId(4L)
                .memberId(1L)
                .reviewBody("리뷰")
                .build();

        var url = "http://localhost:" + port + "/api/v1/reviews";

        // when
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("not_valid_arg");
    }

    @DisplayName("[실패][api] 리뷰 수정 - 권한 없는 사용자")
    @Test
    void givenValidUpdateReqWithUnauthenticatedUser_whenPutUpdateReq_thenReturnForbiddenError() {
        // given
        var reviewId = 2L;
        var req = ReviewDto.UpdateReviewReq.builder()
                .reviewBody("수정한 리뷰 내용 입니다.")
                .build();

        var url = "http://localhost:" + port + "/api/v1/reviews/" + reviewId;

        // when
        webTestClient.put()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error.message").isEqualTo("리뷰에 대한 수정 권한이 없습니다.");
    }

    @DisplayName("[성공][api] 리뷰 수정")
    @Test
    void givenValidUpdateReq_whenPutUpdateReq_thenUpdateReview() {
        // given
        var reviewId = 1L;
        var req = ReviewDto.UpdateReviewReq.builder()
                .reviewBody("수정한 리뷰 내용 입니다.")
                .build();

        var url = "http://localhost:" + port + "/api/v1/reviews/" + reviewId;

        // when
        webTestClient.put()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message").isEqualTo("리뷰 수정 성공");
    }

    @DisplayName("[실패][api] 리뷰 삭제 - 권한 없는 사용자")
    @Test
    void givenReviewIdWithUnauthenticatedUser_whenDeleteReq_thenReturnForbiddenError() {
        // given
        var reviewId = 2L;
        var url = "http://localhost:" + port + "/api/v1/reviews/" + reviewId;

        // when
        webTestClient.delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error.message").isEqualTo("리뷰에 대한 삭제 권한이 없습니다.");
    }

    @DisplayName("[실패][api] 리뷰 삭제 - 존재하지 않는 리뷰")
    @Test
    void givenNotExistReviewId_whenDeleteReq_thenReturnBadRequestError() {
        // given
        var reviewId = 100L;
        var url = "http://localhost:" + port + "/api/v1/reviews/" + reviewId;

        // when
        webTestClient.delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("entity_not_found");
    }

    @DisplayName("[성공][api] 리뷰 삭제 - 관리자 계정")
    @Test
    void givenReviewIdWithAdminUser_whenDeleteReq_thenDeleteReview() {
        // given
        var reviewId = 2L;
        var url = "http://localhost:" + port + "/api/v1/reviews/" + reviewId;

        // when
        webTestClient.delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message").isEqualTo("리뷰 삭제 성공");
    }
}
