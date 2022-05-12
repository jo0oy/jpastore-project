package jpabook.jpastore.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderSearchCondition;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.queryRepo.OrderItemQueryDto;
import jpabook.jpastore.domain.order.queryRepo.OrderQueryDto;
import jpabook.jpastore.domain.order.queryRepo.OrderSimpleQueryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = "classpath:data/data-h2-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(OrderRepositoryTest.TestConfig.class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("querydsl 단일 주문 정상 조회 테스트")
    public void findOrderWithMemberDelivery_querydsl_정상조회_테스트() {
        //given
        Long orderId = 2L;

        //when
        Order findOrder = orderRepository.findOrderWithMemberDelivery(orderId);

        //then
        assertThat(findOrder).isNotNull();
        assertThat(findOrder.getMember().getId()).isEqualTo(1L);
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.READY);
    }

    @Test
    @DisplayName("querydsl 전체 주문 페이징 정상조회 테스트")
    public void findOrderWithMemberDelivery_페이징_정상조회_테스트() {
        //given
        int offset = 1;
        int size = 5;

        PageRequest pageRequest = PageRequest.of(offset, size);

        //when
        Page<Order> result = orderRepository.findAllWithMemberDelivery(pageRequest);

        //then
        assertThat(result.getTotalElements()).isEqualTo(17L);
        assertThat(result.getTotalPages()).isEqualTo(4);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent().get(0).getId()).isEqualTo(12L);
    }

    @Test
    @DisplayName("querydsl 동적퀴리를 통한 전체 주문 정상 조회 테스트")
    public void findByOrderSearch_정상조회_테스트() {
        //given
        OrderSearchCondition orderSearchCondition = OrderSearchCondition.builder()
                .memberName("member1")
                .status(OrderStatus.ORDER)
                .build();

        //when
        List<Order> result = orderRepository.findByCondition(orderSearchCondition);

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMember().getId()).isEqualTo(1L);
        assertThat(result.get(0).getMember().getName()).isEqualTo("member1");
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Test
    @DisplayName("querydsl simple dto 전체 주문 정상조회 테스트")
    public void findAllOrderSimpleDto_정상조회_테스트() {

        //when
        List<OrderSimpleQueryDto> result = orderRepository.findAllOrderSimpleDto();

        //then
        assertThat(result.size()).isEqualTo(17);
        assertThat(result.get(0).getOrderId()).isEqualTo(17L);
        assertThat(result.get(0).getName()).isEqualTo("member3");
    }

    @Test
    @DisplayName("querydsl 전체 주문 상세 dto 정상 조회 테스트")
    public void findAllOrderDto_정상조회_테스트() {
        //when
        List<OrderQueryDto> result = orderRepository.findAllOrderDto();

        //then
        assertThat(result.size()).isEqualTo(17);
        assertThat(result.get(0)).isInstanceOf(OrderQueryDto.class);
        assertThat(result.get(0).getOrderItems().get(0)).isInstanceOf(OrderItemQueryDto.class);
        assertThat(result.get(0).getOrderId()).isEqualTo(17L);
    }


    @TestConfiguration
    static class TestConfig {

        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }


}