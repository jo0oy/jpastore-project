package jpabook.jpastore.application;

import jpabook.jpastore.dto.order.OrderItemRequestDto;
import jpabook.jpastore.dto.order.OrderSaveRequestDto;
import jpabook.jpastore.application.dto.order.OrderSimpleDto;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Book;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderRepository;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.queryRepo.OrderQueryRepository;
import jpabook.jpastore.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderQueryRepository orderQueryRepository;

    @Test
    public void 상품주문_order(){
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");

        int orderQuantity = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderQuantity);

        //then
        Order getOrder = orderRepository.findById(orderId).orElse(null);
        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(book.getPrice().multiply(orderQuantity));
        assertThat(book.getStockQuantity()).isEqualTo(8);
        assertThat(getOrder.getOrderItems().get(0).getItem() instanceof Book).isEqualTo(true);
    }

    @Test
    public void 상품주문_orderByDto() {
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
                .build();

        Long orderId = orderService.orderByDto(requestDto);

        //then
        Order getOrder = orderRepository.findById(orderId).orElse(null);
        assertThat(getOrder.getId()).isEqualTo(orderId);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(2);
        assertThat(getOrder.getTotalPrice()).isEqualTo(book1.getPrice().multiply(quantity1)
                .add(book2.getPrice().multiply(quantity2)));
    }

    @Test
    public void 상품주문_재고수량_초과() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");

        int orderQuantity = 11;

        //when
        Exception exception = assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), book.getId(), orderQuantity));

        //then
        assertThat(exception.getMessage()).isEqualTo("재고 수량이 부족합니다.");

    }

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
                .build();

        Long orderId = orderService.orderByDto(requestDto);
        OrderSimpleDto order = orderService.findOrderByIdWithMemberDelivery(orderId);
        //then
        assertThat(order.getOrderId()).isEqualTo(orderId);
        assertThat(order.getName()).isEqualTo(member.getName());
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("책1", new Money(10000), 10, "kim", "1234");

        int orderQuantity = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderQuantity);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findById(orderId).get();
        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(10);
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

    private Member createMember() {
        Member member = Member.builder()
                .name("kim")
                .address(new Address("서울시", "송파구", "00570"))
                .build();
        em.persist(member);
        return member;
    }
}
