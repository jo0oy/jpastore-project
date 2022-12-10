package jpabook.jpastore.application.order;

import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.Pay;
import jpabook.jpastore.domain.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문 서비스 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Slf4j
@Import(TestDBConfig.class)
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUp() {
        databaseCleanUp.cleanUp();
    }

    @Test
    @DisplayName("[성공][service] 새로운 주문 등록")
    void givenRegisterReq_whenOrder_thenReturnRegisteredOrderId() {
        //given
        var memberId = 2L;
        var city = "경기도";
        var street = "성남시";
        var zipcode = "33333";
        var payInfo = Pay.CARD;
        var orderItems = List.of(
                OrderCommand.OrderItemRegisterReq.builder()
                        .itemId(3L)
                        .quantity(2).build(),
                OrderCommand.OrderItemRegisterReq.builder()
                        .itemId(5L)
                        .quantity(3)
                        .build(),
                OrderCommand.OrderItemRegisterReq.builder()
                        .itemId(6L)
                        .quantity(1)
                        .build()
        );

        var command
                = OrderCommand.OrderRegisterReq.builder()
                .memberId(memberId)
                .orderItems(orderItems)
                .payInfo(payInfo)
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .build();

        //when
        var orderedId = orderService.order(command);

        //then
        var optionalOrder = orderRepository.findOrderWithMemberDelivery(orderedId);
        assertThat(optionalOrder).isPresent();

        var order = optionalOrder.get();
        assertThat(order.getMember().getId()).isEqualTo(memberId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getDelivery().getAddress().getCity()).isEqualTo(city);
    }

    @Test
    @DisplayName("[성공][service] 단일 주문 조회 by id")
    void givenOrderIdAndAuthMemberUsername_whenGetOrder_thenReturnOrderInfo() {
        //given
        var orderId = 2L;
        var authUsername = "member1";

        //when
        var order = orderService.getOrder(orderId, authUsername);

        //then
        assertThat(order.getMemberName()).isEqualTo(authUsername);
        assertThat(order.getOrderItems().size()).isEqualTo(3);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDER.getMessage());
    }

    @Test
    @DisplayName("[실패][service] 존재하지 않는 주문 조회")
    void givenNoneExistOrderId_whenGetOrder_thenThrowEntityNotFoundException() {
        //given
        var orderId = 100L;

        //then
        assertThatThrownBy(() -> orderService.getOrder(orderId, "member4"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("[성공][service] 단일 주문 페치 조인 조회 -> 총 3개 쿼리 통신")
    void givenOrderIdAndAuthMemberUsername_whenGetOrderFetch_thenReturnOrderInfo() {
        //given
        var orderId = 1L; // orderItems 2개
        var authUsername = "member2";

        //when
        var order = orderService.getOrderFetch(orderId, authUsername);

        //then
        assertThat(order.getMemberName()).isEqualTo(authUsername);
        assertThat(order.getOrderItems().size()).isEqualTo(2);
        assertThat(order.getOrderItems().get(0).getItemName()).isEqualTo("book1");
        assertThat(order.getTotalPrice().getValue()).isEqualTo(210000);
    }

    @Test
    @DisplayName("[성공][service] 단일 주문 간단 정보 조회")
    void givenOrderIdAndAuthMemberUsername_whenGetOrderSimpleInfo_thenReturnOrderSimpleInfo() {
        //given
        var orderId = 1L;
        var authUsername = "member2";

        //when
        var simpleInfo = orderService.getOrderSimpleInfo(orderId, authUsername);

        //then
        assertThat(simpleInfo.getMemberName()).isEqualTo(authUsername);
        assertThat(simpleInfo.getAddress().getCity()).isEqualTo("서울시");
    }

    @Test
    @DisplayName("[성공][service] 전체 주문 리스트 조회 - 최신순")
    void givenNothing_whenListOrder_thenReturnOrderList() {

        var list = orderService.listOrder();

        assertThat(list.size()).isEqualTo(17);
        assertThat(list.get(0).getOrderId()).isEqualTo(1L); // orderId=1
        assertThat(list.get(1).getMemberName()).isEqualTo("member1"); // orderId=2
        assertThat(list.get(2).getOrderItems().size()).isEqualTo(2); // orderId=3
    }

    @Test
    @DisplayName("[성공][service] 전체 주문 리스트 페이징 조회")
    void givenPageRequest_whenListOrder_thenReturnPagedOrderResult() {
        //given
        PageRequest pageRequest = PageRequest.of(2, 5, Sort.by("id").descending());

        //when
        var pagingList = orderService.listOrder(pageRequest);

        //then
        assertThat(pagingList.getTotalElements()).isEqualTo(17);
        assertThat(pagingList.getTotalPages()).isEqualTo(4);
        assertThat(pagingList.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("[성공][service] 전체 주문 리스트 컬렉션(OrderItems) 페치 조인 조회")
    void givenNothing_whenListOrderWithOrderItems_thenReturnOrderList() {

        var ordersWithOrderItems = orderService.listOrderFetchOrderItems();

        //then
        assertThat(ordersWithOrderItems.size()).isNotEqualTo(17);
        assertThat(ordersWithOrderItems.get(0).getOrderId()).isEqualTo(1L);
        assertThat(ordersWithOrderItems.get(0).getOrderItems().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("[성공][service] 전체 주문 리스트 컬렉션(OrderItems) 페치 조인 조회-distinct 키워드")
    void givenNothing_whenListOrderFetchOrderItemsDistinct_thenReturnOrderList() {

        var ordersWithOrderItems = orderService.listOrderFetchOrderItemsDistinct();

        //then
        assertThat(ordersWithOrderItems.size()).isEqualTo(17);
        assertThat(ordersWithOrderItems.get(0).getOrderId()).isEqualTo(1L);
        assertThat(ordersWithOrderItems.get(0).getOrderItems().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("[성공][service] 전체 주문 리스트 검색 페이징 조회 - 검색 조건, 페이징")
    void givenSearchConditionAndPageRequest_whenListOrder_thenReturnFilteredAndPagedOrderList() {
        // given
        var condition
                = OrderCommand.OrderSearchCondition
                .builder()
                .memberName("member")
                .status(OrderStatus.ORDER)
                .build();

        var pageRequest = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "id"));

        // when
        var orders = orderService.listOrder(condition, pageRequest);

        //then
        assertThat(orders.getTotalElements()).isEqualTo(11);
        assertThat(orders.getTotalPages()).isEqualTo(3);
        assertThat(orders.getPageable().getSort().isSorted()).isTrue();
        assertThat(orders.getContent().get(0).getOrderId()).isEqualTo(17);
    }

    @Test
    @DisplayName("[성공][service] 전체 주문 리스트 조회 - 쿼리로 DTO 바로 조회")
    void givenNothing_whenListOrderQueryInfos_thenReturnOrderMainInfoList() {
        // given
        // when
        var orders = orderService.listOrderQueryInfos();

        // then
        assertThat(orders.size()).isEqualTo(17);
        assertThat(orders.get(0).getOrderId()).isEqualTo(1L);
        assertThat(orders.get(0).getOrderItems().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("[성공][service] 주문 취소 - 주문자 본인")
    void givenOrderId_whenCancelOrder_thenWorksFine() {
        // given
        var orderId = 3L; // ordered by 'member1'
        var authUsername = "member1";

        // when
        orderService.cancelOrder(orderId, authUsername);

        // then
        var canceledOrder = orderRepository.findOrderWithDelivery(orderId);

        assertThat(canceledOrder).isPresent();
        assertThat(canceledOrder.get().getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(canceledOrder.get().getDelivery().getStatus()).isEqualTo(DeliveryStatus.NONE);
    }

    @Test
    @DisplayName("[성공][service] 주문 취소 - 관리자")
    void givenOrderIdAndAdminUsername_whenCancelOrder_thenWorksFine() {
        // given
        var orderId = 6L; // ordered by 'member4'
        var authUsername = "admin";

        // when
        orderService.cancelOrder(orderId, authUsername);

        // then
        var canceledOrder = orderRepository.findOrderWithDelivery(orderId);

        assertThat(canceledOrder).isPresent();
        assertThat(canceledOrder.get().getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(canceledOrder.get().getDelivery().getStatus()).isEqualTo(DeliveryStatus.NONE);
    }

    @Test
    @DisplayName("[실패][service] 존재하지 않는 주문 취소")
    void givenNonExistOrderId_whenCancelOrder_thenThrowEntityNotFoundException() {
        // given
        var orderId = 100L;

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, "member1"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 주문입니다. id = " + orderId);
    }

    @Test
    @DisplayName("[실패][service] 주문 취소 요청 - 접근 권한 없는 사용자")
    void givenOrderIdAndForbiddenUsername_whenCancelOrder_thenThrowAccessDeniedException() {
        // given
        var orderId = 10L; // ordered by 'member4'
        var authUsername = "member2";

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, authUsername))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("해당 주문에 대한 접근 권한이 없습니다.");
    }

    @Test
    @DisplayName("[실패][service] 주문 취소 불가한 주문에 대한 취소 요청 - 이미 배송중인 주문")
    void givenNotValidToCancelOrderId_whenCancelOrder_thenThrowIllegalStateException() {
        // given
        var orderId = 9L; // 배달 상태 - 배송중, ordered by 'member2'

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, "member2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 배송중 혹은 배송완료된 주문은 취소할 수 없습니다.");
    }
}
