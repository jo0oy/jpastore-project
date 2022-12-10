package jpabook.jpastore.web.api.v1.order;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.domain.order.Pay;
import jpabook.jpastore.security.JwtTokenProvider;
import jpabook.jpastore.web.dto.order.OrderDto;
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
import java.util.List;

@Slf4j
@DisplayName("주문 생성/취소 API 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderCommandApiControllerTest {

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
    }

    @DisplayName("[성공][api] 새로운 주문 생성")
    @Test
    void givenOrderRegisterReq_whenPostNewOrder_thenReturnRegisteredOrderId() {
        //given
        var url = "http://localhost:" + port + "/api/v1/orders";

        var orderItemReq = List.of(
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(1L).quantity(2).build(),
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(4L).quantity(1).build(),
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(5L).quantity(3).build()
        );

        var registerReq = OrderDto.OrderRegisterReq.builder()
                .memberId(1L)
                .orderItems(orderItemReq)
                .city("서울시")
                .street("송파구")
                .zipcode("11111")
                .payInfo(Pay.CARD)
                .build();

        //when & then
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .bodyValue(registerReq)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.registeredOrderId").isEqualTo(18);
    }

    @DisplayName("[실패][api] 올바르지 않은 주문 데이터로 주문 생성 요청")
    @Test
    void givenInvalidOrderRegisterReq_whenPostNewOrder_thenReturnBadRequestError() {
        //given
        var url = "http://localhost:" + port + "/api/v1/orders";

        var orderItemReq = List.of(
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(1L).quantity(2).build(),
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(4L).quantity(1).build(),
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(5L).quantity(3).build()
        );

        var registerReq = OrderDto.OrderRegisterReq.builder()
                .memberId(1L)
                .orderItems(orderItemReq)
                .city("")
                .street("송파구")
                .zipcode("11111")
                .payInfo(Pay.CARD)
                .build();

        //when & then
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .bodyValue(registerReq)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("not_valid_arg");
    }

    @DisplayName("[실패][api] 주문 생성 요청 - 인증되지 않은 사용자")
    @Test
    void givenOrderRegisterReqWithNoneToken_whenPostNewOrder_thenReturnUnauthorizedError() {
        //given
        var url = "http://localhost:" + port + "/api/v1/orders";

        var orderItemReq = List.of(
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(1L).quantity(2).build(),
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(4L).quantity(1).build(),
                OrderDto.OrderItemRegisterReq.builder()
                        .itemId(5L).quantity(3).build()
        );

        var registerReq = OrderDto.OrderRegisterReq.builder()
                .memberId(1L)
                .orderItems(orderItemReq)
                .city("서울시")
                .street("송파구")
                .zipcode("11111")
                .payInfo(Pay.CARD)
                .build();

        //when & then
        webTestClient.post()
                .uri(url)
                .bodyValue(registerReq)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error.message").isEqualTo("Full authentication is required to access this resource");
    }

    @DisplayName("[성공] 주문 취소 - 주문자 본인")
    @Test
    void givenOrderIdAndUserToken_whenCancelOrder_thenWorksFine() {
        // given
        var orderId = 2L; // ordered by 'member1'
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

        // when & then
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message").isEqualTo("주문 취소 성공");
    }

    @DisplayName("[성공] 주문 취소 - 관리자")
    @Test
    void givenOrderIdAndAdminToken_whenCancelOrder_thenWorksFine() {
        // given
        var orderId = 6L; // ordered by 'member4'
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

        // when & then
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message").isEqualTo("주문 취소 성공");
    }

    @DisplayName("[실패] 주문 취소 실패- 권한 없는 사용자")
    @Test
    void givenOrderIdAndForbiddenUserToken_whenCancelOrder_thenReturnsForbiddenError() {
        // given
        var orderId = 4L; // ordered by 'member3'
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

        // when & then
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error.message").isEqualTo("해당 주문에 대한 접근 권한이 없습니다.");
    }

    @DisplayName("[실패] 주문 취소 실패- 존재하지 않는 주문")
    @Test
    void givenNoneExistOrderId_whenCancelOrder_thenReturnsBadRequestError() {
        // given
        var orderId = 100L;
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

        // when & then
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(userToken))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.error.message").isEqualTo("존재하지 않는 주문입니다. id = " + orderId);
    }

    @DisplayName("[실패] 주문 취소 실패 - 취소할 수 없는 주문 상태('배송중' 혹은 '배송 완료'")
    @Test
    void givenOrderIdThatCannotCancel_whenCancelOrder_thenReturnsServerError() {
        // given
        var orderId = 17L; // ordered by 'member3'
        var url = "http://localhost:" + port + "/api/v1/orders/" + orderId;

        // when & then
        webTestClient.post()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error.message").isEqualTo("이미 배송중 혹은 배송완료된 주문은 취소할 수 없습니다.");
    }
}
