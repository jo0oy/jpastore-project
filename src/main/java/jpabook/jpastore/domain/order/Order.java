package jpabook.jpastore.domain.order;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // ORDER, CANCEL

    @Builder
    public Order(Member member, Delivery delivery, LocalDateTime orderDateTime, OrderStatus status) {
        this.member = member;

        this.delivery = delivery;
        delivery.setOrder(this);

        this.orderDateTime = orderDateTime;
        this.status = status;
    }

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .member(member)
                .delivery(delivery)
                .orderDateTime(LocalDateTime.now())
                .status(OrderStatus.ORDER)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMPLETE){
            throw new IllegalStateException("이미 배송완료된 주문은 취소할 수 없습니다.");
        }

        this.status = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public Money getTotalPrice() {
        return new Money(orderItems.stream()
                .mapToInt(o -> o.getTotalPrice().getValue()).sum());
    }
}
