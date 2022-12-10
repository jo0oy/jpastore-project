package jpabook.jpastore.domain.order.queryRepo;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.config.TestQuerydslConfig;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderItem;
import jpabook.jpastore.domain.order.repository.OrderRepository;
import jpabook.jpastore.domain.order.repository.queryRepo.OrderQueryRepository;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import({TestQuerydslConfig.class, TestDBConfig.class, OrderQueryRepository.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class OrderQueryRepositoryTest {

    @Autowired
    private OrderQueryRepository orderQueryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUp() {
        databaseCleanUp.cleanUp();
    }

    @Test
    @DisplayName("[성공][repo] 글로벌 배치 설정(default_batch_fetch_size) 정상 동작 확인")
    public void whenFindAllOrdersAndMappingToDto_thenWorksFineByInQuery() {
        //given

        //when
        var orders = orderQueryRepository.findAllWithMemberDelivery();

        var orderDtoList = orders.stream().map(OrderDto::new).collect(Collectors.toList());

        //then
        assertThat(orderDtoList.size()).isEqualTo(17);
        assertThat(orderDtoList.get(0).getMemberName()).isEqualTo("member2");
    }

    @Test
    @DisplayName("[성공][repo] @OneToOne 연관관계 Delivery, batch_fetch_size 정상 동작 확인")
    public void find_all_orders_not_fetch() {
        //given

        //when
        var order = orderRepository.findById(1L);

        //then
        assertThat(order).isPresent();
        assertThat(order.get().getDelivery().getStatus()).isEqualTo(DeliveryStatus.READY);
    }



    static class OrderItemDto {
        private Long orderItemId;
        private Long itemId;
        private String itemName;
        private int orderPrice;
        private int quantity;

        public OrderItemDto(OrderItem entity) {
            this.orderItemId = entity.getId();
            this.itemId = entity.getItem().getId();
            this.itemName = entity.getItem().getName();
            this.orderPrice = entity.getOrderPrice().getValue();
            this.quantity = entity.getQuantity();
        }
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String memberName;
        private Address address;
        private List<OrderItemDto> orderItems = new ArrayList<>();
        private String deliveryStatus;

        public OrderDto(Order entity) {
            this.orderId = entity.getId();
            this.memberName = entity.getMember().getUsername();
            this.address = entity.getDelivery().getAddress();
            this.deliveryStatus = entity.getDelivery().getStatus().getMessage();
            this.orderItems.addAll(entity.getOrderItems().stream()
                    .map(OrderItemDto::new).collect(Collectors.toList()));
        }
    }
}
