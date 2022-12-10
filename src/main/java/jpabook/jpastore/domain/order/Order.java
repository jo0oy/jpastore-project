package jpabook.jpastore.domain.order;

import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.Role;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@ToString
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // PAYMENT_WAITING, ORDER, CANCEL

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Pay payInfo;

    @Builder
    public Order(Member member, Delivery delivery, OrderStatus status, Pay payInfo) {
        this.member = member;

        setDelivery(delivery);

        this.status = status;
        this.payInfo = payInfo;
    }

    //==연관관계 메서드==//
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    private void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery,
                                    List<OrderItem> orderItems, Pay payInfo, OrderStatus status) {
        Order order = Order.builder()
                .member(member)
                .delivery(delivery)
                .payInfo(payInfo)
                .status(status)
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
        if(delivery.getStatus() == DeliveryStatus.DELIVERING
                || delivery.getStatus() == DeliveryStatus.COMPLETE){
            throw new IllegalStateException("이미 배송중 혹은 배송완료된 주문은 취소할 수 없습니다.");
        }

        this.status = OrderStatus.CANCEL;

        delivery.cancel(); // 배달 상태 (NONE)으로 변경

        for (OrderItem orderItem : orderItems) {
            orderItem.cancel(); // 취소된 재고 추가
        }
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    // 조회/ 변경/ 삭제 권한 확인
    public boolean hasAuthority(Member member) {
        return this.getMember().equals(member) || member.getRole() == Role.ADMIN;
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
