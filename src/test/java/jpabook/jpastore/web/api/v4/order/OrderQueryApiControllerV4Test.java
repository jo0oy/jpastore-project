package jpabook.jpastore.web.api.v4.order;

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
@DisplayName("주문 조회 API v4 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderQueryApiControllerV4Test {

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

    @DisplayName("[성공][api] 전체 주문 리스트 간단 정보 조회(DTO 로 바로 조회) - 관리자 계정")
    @Test
    void givenAdminToken_whenGetRequestReadOrderSimpleList_thenReturnOrderSimpleInfoList() {
        //given
        var url = "http://localhost:" + port + "/api/v4/simple-orders";

        //when & then
        webTestClient.get()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(17)
                .jsonPath("$.data.list[0].orderId").isEqualTo(1)
                .jsonPath("$.data.list[0].orderItems").doesNotExist();
    }

    @DisplayName("[실패][api] 전체 주문 리스트 간단 정보 조회(DTO 로 바로 조회) - 일반회원 계정(접근 불가)")
    @Test
    void givenUserToken_whenGetRequestReadOrderSimpleList_thenReturnForbiddenError() {
        //given
        var url = "http://localhost:" + port + "/api/v4/simple-orders";

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

    @DisplayName("[성공][api] 전체 주문 리스트 검색 조회 - 관리자 계정")
    @Test
    void givenSearchConditionAndAdminToken_whenGetRequestSearchOrderPageList_thenReturnSearchedOrderResult() {
        //given
        var url = "/api/v4/orders";
        var memberName = "member1";
        var orderStatus = "order";

        //when & then
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("memberName", memberName)
                                .queryParam("status", orderStatus)
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(1)
                .jsonPath("$.data.totalPages").isEqualTo(1)
                .jsonPath("$.data.content[0].orderId").isEqualTo(2);
    }

    @DisplayName("[성공][api] 전체 주문 리스트 검색 조회(페이징, 정렬) - 관리자 계정")
    @Test
    void givenSearchConditionAndPageRequestAndAdminToken_whenGetRequestSearchOrderPageList_thenReturnSearchedPagedOrderResult() {
        //given
        var url = "/api/v4/orders";
        var memberName = "member1";
        var orderStatus = "payment_waiting"; // 검색 조건에 의하면 총 검색 건수 2개 : 주문 아이디 1, 14
        var size = 5;
        var page = 1;
        var sort = "id,DESC";

        //when & then
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("memberName", memberName)
                                .queryParam("status", orderStatus)
                                .queryParam("size", size)
                                .queryParam("page", page)
                                .queryParam("sort", sort)
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(2)
                .jsonPath("$.data.totalPages").isEqualTo(1)
                .jsonPath("$.data.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.data.content[0].orderId").isEqualTo(14);
    }

    @DisplayName("[실패][api] 올바르지 않은 주문 상태 값으로 전체 주문 리스트 검색 조회 - 관리자 계정")
    @Test
    void givenInvalidSearchConditionAndAdminToken_whenGetRequestSearchOrderPageList_thenReturnBadRequestError() {
        //given
        var url = "/api/v4/orders";
        var memberName = "member1";
        var orderStatus = "done";

        //when & then
        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("memberName", memberName)
                                .queryParam("status", orderStatus)
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("illegal_argument");
    }
}
