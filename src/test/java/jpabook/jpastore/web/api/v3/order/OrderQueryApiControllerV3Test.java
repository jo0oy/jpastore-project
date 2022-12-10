package jpabook.jpastore.web.api.v3.order;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.security.JwtTokenProvider;
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
@DisplayName("주문 조회 API v3 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderQueryApiControllerV3Test {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private String userToken;

    private String adminToken;

    @BeforeEach
    void setUp() {
        // 'member1' 이 생성한 주문 id: 2, 3, 14
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

    @DisplayName("[성공][api] 전체 주문 리스트 조회(컬렉션 페치조인: 'distinct' 키워드 적용) - 관리자 계정")
    @Test
    void givenAdminToken_whenGetRequestReadOrderList_thenReturnOrderInfoList() {
        //given
        var url = "http://localhost:" + port + "/api/v3/orders/distinct";

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(17)
                .jsonPath("$.data.list[0].orderId").isEqualTo(1);
    }

    @DisplayName("[실패][api] 전체 주문 리스트 페이징, 정렬 조회 - 일반회원 계정")
    @Test
    void givenUserToken_whenGetRequestReadOrderList_thenReturnForbiddenError() {
        //given
        var url = "http://localhost:" + port + "/api/v3/orders/distinct";

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("AccessDeniedException");
    }

    @DisplayName("[성공][api] 전체 주문 리스트 페이징 조회(페이징, 정렬) - 관리자 계정")
    @Test
    void givenPageRequestAndAdminToken_whenGetRequestReadOrderPageResult_thenReturnOrderPagingList() {
        //given
        var url = "/api/v3/orders";
        var page = 1;
        var size = 5;
        var sort = "order.memberId,ASC";

        //when & then
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("sort", sort)
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(17)
                .jsonPath("$.data.totalPages").isEqualTo(4)
                .jsonPath("$.data.content[0].memberName").isEqualTo("member1")
                .jsonPath("$.data.content[0].orderItems").exists();
    }

    @DisplayName("[실패][api] 전체 주문 리스트 페이징 조회(페이징, 정렬) - 인증되지 않은 사용자")
    @Test
    void givenNothingUnAuthenticatedUser_whenGetRequestReadOrderPageResult_thenReturnUnauthorizedError() {
        //given
        var url = "http://localhost:" + port + "/api/v3/orders";

        //when & then
        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error.message").isEqualTo("Full authentication is required to access this resource");
    }

    @DisplayName("[성공][api] 전체 주문 리스트 페이징 조회 - 음수의 페이지 번호 입력시 페이지 번호 0으로 반환")
    @Test
    void givenNegativePageNumAndAdminToken_whenGetRequestReadOrderPageResult_thenReturnOrderPagingFirstPage() {
        //given
        var url = "/api/v3/orders";
        var page = -2;
        var size = 5;
        var sort = "order.memberId,ASC";

        //when & then
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("sort", sort)
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(17)
                .jsonPath("$.data.totalPages").isEqualTo(4)
                .jsonPath("$.data.pageable.pageNumber").isEqualTo(0)
                .jsonPath("$.data.content[0].memberName").isEqualTo("member1")
                .jsonPath("$.data.content[0].orderItems").exists();
    }

    @DisplayName("[실패][api] 전체 주문 리스트 페이징 조회 - 올바르지 않은 정렬 기준 입력")
    @Test
    void givenInvalidSortProperty_whenGetRequestReadOrderPageResult_thenReturnBadRequestError() {
        //given
        var url = "/api/v3/orders";
        var page = 0;
        var size = 5;
        var sort = "order.itemName,ASC";

        //when & then
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .queryParam("sort", sort)
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("not_valid_params")
                .jsonPath("$.error.message").isEqualTo("올바르지 않은 정렬 기준 속성입니다. property=order.itemName");
    }
}