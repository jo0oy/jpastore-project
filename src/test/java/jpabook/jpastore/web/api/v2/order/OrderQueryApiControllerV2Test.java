package jpabook.jpastore.web.api.v2.order;

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
@DisplayName("주문 조회 API v2 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderQueryApiControllerV2Test {

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

    @DisplayName("[성공][api] 단일 주문 간단 정보 조회 - 본인이 생성한 주문 건 조회")
    @Test
    void givenOrderId_whenGetRequestSimpleOrderInfo_thenReturnSimpleOrderInfo() {
        //given
        var orderId = 2L;
        var url = "http://localhost:" + port + "/api/v2/simple-orders/" + orderId;

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.memberName").isEqualTo("member1")
                .jsonPath("$.message").isEqualTo("단일 주문 조회 성공");
    }

    @DisplayName("[실패][api] 존재하지 않는 주문 간단 정보 조회")
    @Test
    void givenNotExistOrderId_whenGetRequestSimpleOrderInfo_thenReturnBadRequestError() {
        //given
        var orderId = 1000L;
        var url = "http://localhost:" + port + "/api/v2/simple-orders/" + orderId;

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("entity_not_found");
    }

    @DisplayName("[실패][api] 조회 권한이 없는 주문 간단 정보 조회")
    @Test
    void givenOrderIdWithForbiddenUserToken_whenGetRequestSimpleOrderInfo_thenReturnForbiddenError() {
        //given
        var orderId = 1L;
        var url = "http://localhost:" + port + "/api/v2/simple-orders/" + orderId;

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

    @DisplayName("[성공][api] 단일 주문 상세 정보 조회 - 본인이 생성한 주문 건 조회")
    @Test
    void givenOrderId_whenGetRequestOrderDetailInfo_thenReturnOrderDetailInfo() {
        //given
        var orderId = 2L;
        var url = "http://localhost:" + port + "/api/v2/orders/" + orderId;

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.memberName").isEqualTo("member1")
                .jsonPath("$.data.orderStatus").isEqualTo("주문 완료")
                .jsonPath("$.data.orderItems").exists();
    }

    @DisplayName("[실패][api] 조회 권한이 없는 주문 상세 정보 조회")
    @Test
    void givenOrderIdWithForbiddenUserToken_whenGetRequestOrderDetailInfo_thenReturnForbiddenError() {
        //given
        var orderId = 1L;
        var url = "http://localhost:" + port + "/api/v2/orders/" + orderId;

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

    @DisplayName("[성공][api] 전체 주문 리스트 간단 정보 조회(페이징, 정렬) - 관리자 계정")
    @Test
    void givenAdminToken_whenGetRequestOrderSimplePagingList_thenReturnOrderSimplePagingList() {
        //given
        var url = "/api/v2/simple-orders";
        var size = 5;
        var sort = "order.memberId,ASC";

        //when & then
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
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
                .jsonPath("$.data.content[0].memberName").isEqualTo("member1");
    }

    @DisplayName("[실패][api] 전체 주문 리스트 간단 정보 조회 - 일반회원 계정")
    @Test
    void givenUserToken_whenGetRequestOrderSimplePagingList_thenReturnForbiddenError() {
        //given
        var url = "http://localhost:" + port + "/api/v2/simple-orders";

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

    @DisplayName("[성공][api] 전체 주문 리스트 조회(컬렉션 페치조인: 'distinct' 사용안함) - 관리자 계정")
    @Test
    void givenAdminToken_whenGetRequestOrderList_thenReturnOrderListNoDistinct() {
        //given
        var url = "http://localhost:" + port + "/api/v2/orders";

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(31);
    }
}
