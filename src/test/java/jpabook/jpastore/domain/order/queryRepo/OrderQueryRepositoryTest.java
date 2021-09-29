package jpabook.jpastore.domain.order.queryRepo;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderItem;
import jpabook.jpastore.domain.order.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "classpath:data/data-h2-test.sql")
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(OrderQueryRepository.class)
class OrderQueryRepositoryTest {

    @Autowired
    private OrderQueryRepository orderQueryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    @Test
    @DisplayName("1. batch_fetch_size 정상 동작 확인")
    public void find_all_orders() {
        //given

        //when
        List<Order> orders = orderQueryRepository.findAllWithMemberDelivery();

        List<OrderDto> orderDtoList = orders.stream().map(OrderDto::new).collect(Collectors.toList());

        //then
        assertThat(orderDtoList.size()).isEqualTo(2);
        assertThat(orderDtoList.get(0).getMemberName()).isEqualTo("member2");
    }

    @Transactional
    @Test
    @DisplayName("2. @OneToOne 연관관계 주인 Lazy Loading, batch_fetch_size 정상 동작 확인")
    public void find_all_orders_not_fetch() {
        //given

        //when
        List<Order> orders = orderRepository.findAll();


        System.out.println("findAll() method finished....");
        System.out.println("delvery 접근 시도.....");
        DeliveryStatus status = orders.get(0).getDelivery().getStatus();
        System.out.println("delivery status is " + status);

        List<OrderDto> orderDtoList = orders.stream().map(OrderDto::new).collect(Collectors.toList());

        //then
        assertThat(orderDtoList.size()).isEqualTo(2);
        assertThat(orderDtoList.get(0).getMemberName()).isEqualTo("member2");
    }



    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
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

    static class OrderDto {
        private Long orderId;
        private String memberName;
        private Address address;
        private List<OrderItemDto> orderItems = new ArrayList<>();
        private String deliveryStatus;

        public OrderDto(Order entity) {
            this.orderId = entity.getId();
            this.memberName = entity.getMember().getName();
            this.address = entity.getDelivery().getAddress();
            this.deliveryStatus = entity.getDelivery().getStatus().getMessage();
            this.orderItems.addAll(entity.getOrderItems().stream()
                    .map(OrderItemDto::new).collect(Collectors.toList()));
        }

        public Long getOrderId() {
            return orderId;
        }

        public String getMemberName() {
            return memberName;
        }

        public Address getAddress() {
            return address;
        }

        public List<OrderItemDto> getOrderItems() {
            return orderItems;
        }

        public String getDeliveryStatus() {
            return deliveryStatus;
        }
    }

}