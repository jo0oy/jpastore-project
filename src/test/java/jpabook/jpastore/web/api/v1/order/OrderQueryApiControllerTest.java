package jpabook.jpastore.web.api.v1.order;

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
@DisplayName("주문 조회 API 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderQueryApiControllerTest {

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

    @DisplayName("[성공][api] 단일 주문 조회 by orderId - 주문자 본인")
    @Test
    void givenOrderId_whenGetRequestReadOrder_thenReturnOrderInfo() {
        //given
        var orderId = 2L;
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.orderId").isEqualTo(orderId)
                .jsonPath("$.data.memberName").isEqualTo("member1");
    }

    @DisplayName("[성공][api] 단일 주문 조회 by orderId - 관리자 계정")
    @Test
    void givenOrderIdWithAdminToken_whenGetRequestReadOrder_thenReturnOrderInfo() {
        //given
        var orderId = 1L;
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.orderId").isEqualTo(orderId)
                .jsonPath("$.data.memberName").isEqualTo("member2");
    }

    @DisplayName("[실패][api] 존재하지 않는 주문 조회 by orderId")
    @Test
    void givenNotExistOrderId_whenGetRequestReadOrder_thenReturnBadRequestError() {
        //given
        var orderId = 100L;
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

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

    @DisplayName("[실패][api] 접근 권한이 없는 사용자가 주문 조회")
    @Test
    void givenOrderIdWithNoAuthorityToReadUserToken_whenGetRequestReadOrder_thenReturnForbiddenError() {
        //given
        var orderId = 1L;
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

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

    @DisplayName("[성공][api] 전체 주문 리스트 조회 - 관리자 계정")
    @Test
    void givenAdminToken_whenGetRequestReadOrderList_thenReturnOrderInfoList() {
        //given
        var url = "http://localhost:" + port + "/api/v1/orders";

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

    @DisplayName("[실패][api] 전체 주문 리스트 조회 - 일반 회원 계정")
    @Test
    void givenUserToken_whenGetRequestReadOrderList_thenReturnForbiddenError() {
        //given
        var url = "http://localhost:" + port + "/api/v1/orders";

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
}
