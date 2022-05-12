package jpabook.jpastore.application;

import jpabook.jpastore.application.dto.order.OrderSimpleDto;
import jpabook.jpastore.application.order.OrderService2;
import jpabook.jpastore.application.order.OrderServiceImpl;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Book;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.Pay;
import jpabook.jpastore.domain.order.repository.OrderRepository;
import jpabook.jpastore.dto.order.OrderItemRequestDto;
import jpabook.jpastore.dto.order.OrderSaveRequestDto;
import jpabook.jpastore.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderService2 orderService2;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    @Test
    public void 상품주문_order(){
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");
        Pay pay = Pay.NAVER_PAY;

        int orderQuantity = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderQuantity, pay);

        //then
        Order getOrder = orderRepository.findById(orderId).orElse(null);
        assertThat(getOrder).isNotNull();
        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice().getValue()).isEqualTo(book.getPrice().multiply(orderQuantity).getValue());
        assertThat(book.getStockQuantity()).isEqualTo(8);
        assertThat(getOrder.getOrderItems().get(0).getItem() instanceof Book).isEqualTo(true);
    }

    @Transactional
    @Test
    public void 상품주문_orderByDto() {
        //given
        Member member = createMember();
        Book book1 = createBook("Book1", new Money(12000), 100, "이주연", "1234");
        Book book2 = createBook("Book2", new Money(15000), 50, "이유진", "5678");

        int quantity1 = 5;
        int quantity2 = 7;

        //when
        OrderSaveRequestDto requestDto = OrderSaveRequestDto.builder()
                .memberId(member.getId())
                .orderItems(Arrays.asList(OrderItemRequestDto.builder()
                        .itemId(book1.getId())
                        .quantity(quantity1).build(),
                        OrderItemRequestDto.builder()
                        .itemId(book2.getId())
                        .quantity(quantity2)
                        .build()))
                .payInfo(Pay.NAVER_PAY)
                .build();

        Long orderId = orderService.order(requestDto);

        //then
        Order getOrder = orderRepository.findById(orderId).orElse(null);
        assertThat(getOrder).isNotNull();
        assertThat(getOrder.getId()).isEqualTo(orderId);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(2);
        assertThat(getOrder.getTotalPrice().getValue()).isEqualTo(book1.getPrice().multiply(quantity1)
                .add(book2.getPrice().multiply(quantity2)).getValue());
    }

    @Transactional
    @Test
    public void 상품주문_재고수량_초과() {
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");
        Pay pay = Pay.CARD;

        int orderQuantity = 11;

        //when
        Exception exception = assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), book.getId(), orderQuantity, pay));

        //then
        assertThat(exception.getMessage()).isEqualTo("재고 수량이 부족합니다.");

    }

    @Transactional
    @Test
    public void 단일상품_조회() {
        //given
        Member member = createMember();
        Book book1 = createBook("Book1", new Money(12000), 100, "이주연", "1234");
        Book book2 = createBook("Book2", new Money(15000), 50, "김영한", "5678");

        int quantity1 = 5;
        int quantity2 = 7;

        //when
        OrderSaveRequestDto requestDto = OrderSaveRequestDto.builder()
                .memberId(member.getId())
                .orderItems(Arrays.asList(OrderItemRequestDto.builder()
                                .itemId(book1.getId())
                                .quantity(quantity1).build(),
                        OrderItemRequestDto.builder()
                                .itemId(book2.getId())
                                .quantity(quantity2)
                                .build()))
                .payInfo(Pay.CARD)
                .build();

        Long orderId = orderService.order(requestDto);
        OrderSimpleDto order = orderService.getOrderWithMemberDelivery(orderId);
        //then
        assertThat(order.getOrderId()).isEqualTo(orderId);
        assertThat(order.getName()).isEqualTo(member.getName());
    }

    @Transactional
    @Test
    public void 배달상태_변경() {
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");
        Pay pay = Pay.CARD;

        OrderSaveRequestDto requestDto = OrderSaveRequestDto.builder()
                .memberId(member.getId())
                .orderItems(Collections.singletonList(OrderItemRequestDto.builder()
                        .itemId(book.getId())
                        .quantity(2).build())
                )
                .payInfo(pay)
                .build();

        Long orderId = orderService2.order(requestDto);

        // when
        orderService2.changeDeliveryStatus(orderId, DeliveryStatus.DELIVERING);

        Order changedOrder = orderRepository.findOrderWithMemberDelivery(orderId);

        assertThat(changedOrder).isNotNull();
        assertThat(changedOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.DELIVERING);
    }

    @Transactional
    @Test
    public void 주문상태_변경_테스트() {
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");
        Pay pay = Pay.BANK_TRANS;

        OrderSaveRequestDto requestDto = OrderSaveRequestDto.builder()
                .memberId(member.getId())
                .orderItems(Collections.singletonList(OrderItemRequestDto.builder()
                        .itemId(book.getId())
                        .quantity(2).build())
                )
                .payInfo(pay)
                .build();

        Long orderId = orderService2.order(requestDto);

        //when
        orderService2.changeOrderStatus(orderId, OrderStatus.ORDER);

        Optional<Order> findOrder = orderRepository.findById(orderId);

        //then
        assertThat(findOrder).isNotNull();
        assertThat(findOrder.get().getStatus()).isEqualTo(OrderStatus.ORDER);
    }

    @Transactional
    @Test
    public void 주문취소_테스트() {
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");
        Pay pay = Pay.CARD;

        OrderSaveRequestDto requestDto = OrderSaveRequestDto.builder()
                .memberId(member.getId())
                .orderItems(Collections.singletonList(OrderItemRequestDto.builder()
                        .itemId(book.getId())
                        .quantity(2).build())
                )
                .payInfo(pay)
                .build();

        Long orderId = orderService2.order(requestDto);

        //when
        orderService2.cancelOrder(orderId);
        Order canceledOrder = orderRepository.findOrderWithMemberDelivery(orderId);

        //then
        assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(canceledOrder.getDelivery().getStatus()).isEqualTo(DeliveryStatus.NONE);
        assertThat(canceledOrder.getOrderItems().get(0).getItem().getStockQuantity()).isEqualTo(10);
    }

    private Book createBook(String name, Money price, int stockQuantity, String author, String isbn) {
        Book book = Book.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .author(author)
                .isbn(isbn)
                .build();

        em.persist(book);
        return book;
    }

    Member createMember() {
        Member member = Member.builder()
                .name("kim")
                .address(new Address("서울시", "송파구", "00570"))
                .phoneNumber("010-1111-1111")
                .membership(Membership.builder()
                        .grade(Grade.SILVER)
                        .totalSpending(new Money(150000))
                        .build())
                .build();
        em.persist(member);
        return member;
    }
}
