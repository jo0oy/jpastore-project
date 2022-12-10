package jpabook.jpastore.domain.order;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.config.TestQuerydslConfig;
import jpabook.jpastore.domain.order.repository.OrderQueryInfo;
import jpabook.jpastore.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import({TestQuerydslConfig.class, TestDBConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.cleanUp();
    }

    @Test
    @DisplayName("[성공][repo] querydsl 단일 주문 정상 조회 테스트")
    void findOrderWithMemberDelivery_querydsl() {
        //given
        var orderId = 2L;

        //when
        var findOrder = orderRepository.findOrderWithMemberDelivery(orderId);

        //then
        assertThat(findOrder).isPresent();
        assertThat(findOrder.get().getMember().getId()).isEqualTo(1L);
        assertThat(findOrder.get().getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.get().getDelivery().getStatus()).isEqualTo(DeliveryStatus.READY);
    }

    @Test
    @DisplayName("[성공][repo] querydsl 전체 주문 페이징 정상 조회 테스트")
    void findAllWithMemberDelivery_paging_querydsl() {
        //given
        var offset = 1;
        var size = 5;
        var dir = Sort.Direction.DESC;
        var property = "id";

        var pageRequest = PageRequest.of(offset, size, dir, property);

        //when
        var result = orderRepository.findAllWithMemberDelivery(pageRequest);

        //then
        assertThat(result.getTotalElements()).isEqualTo(17);
        assertThat(result.getTotalPages()).isEqualTo(4);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent().get(0).getId()).isEqualTo(12L); // 2번째 페이지의 첫번째 주문
    }

    @Test
    @DisplayName("[성공][repo] querydsl 동적쿼리를 통한 컬렉션(OrderItems) 페치 조인('distinct' 키워드 없음) 정상 조회 테스트")
    void findAllWithOrderItems_no_distinct_querydsl () {
        //given

        //when
        var orders = orderRepository.findAllWithOrderItems();

        //then
        assertThat(orders.size()).isEqualTo(31); // 1 : N 이므로 row 갯수 orderItems 총 갯수만큼 증가
        assertThat(orders.get(0).getOrderItems().size()).isEqualTo(2);
        assertThat(orders.get(0).getOrderItems().get(0).getItem().getName()).isEqualTo("book1");
    }

    @Test
    @DisplayName("[성공][repo] querydsl 동적쿼리를 통한 컬렉션(OrderItems) 페치 조인('distinct' 키워드 있음) 정상 조회 테스트")
    void findAllWithOrderItemsDistinct_querydsl () {
        //given

        //when
        var orders = orderRepository.findAllWithOrderItemsDistinct();

        //then
        assertThat(orders.size()).isEqualTo(17);
        assertThat(orders.get(3).getOrderItems().size()).isEqualTo(2);
        assertThat(orders.get(3).getOrderItems().get(1).getItem().getName()).isEqualTo("album3");
    }

    @Test
    @DisplayName("[성공][repo] querydsl 동적퀴리를 통한 전체 주문 검색 조회 테스트")
    void findByOrderSearch_querydsl() {
        //given
        var orderSearchCondition = OrderSearchCondition.builder()
                .memberName("member1")
                .status(OrderStatus.ORDER)
                .build();

        //when
        var result = orderRepository.findByCondition(orderSearchCondition);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMember().getId()).isEqualTo(1L);
        assertThat(result.get(0).getMember().getUsername()).isEqualTo("member1");
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    @DisplayName("[성공][repo] querydsl 전체 주문 simple dto(OrderQueryInfo.SimpleInfo)로 정상 조회 테스트")
    void findAllOrderSimpleInfo_querydsl() {

        //when
        var result = orderRepository.findAllOrderSimpleInfo();

        //then
        assertThat(result.size()).isEqualTo(17);
        assertThat(result.get(0)).isInstanceOf(OrderQueryInfo.SimpleInfo.class);
    }

    @Test
    @DisplayName("[성공][repo] querydsl 전체 주문 상세 dto(OrderQueryInfo.MainInfo) 정상 조회 테스트")
    void findAllOrderInfo_querydsl() {
        //when
        var result = orderRepository.findAllOrderInfo();

        //then
        assertThat(result.size()).isEqualTo(17);
        assertThat(result.get(0)).isInstanceOf(OrderQueryInfo.MainInfo.class);
        assertThat(result.get(0).getOrderItems().get(0)).isInstanceOf(OrderQueryInfo.OrderItemInfo.class);
        assertThat(result.get(0).getOrderId()).isEqualTo(1L);
    }
}
