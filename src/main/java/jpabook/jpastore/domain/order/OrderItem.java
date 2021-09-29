package jpabook.jpastore.domain.order;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Item;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "order_price"))
    private Money orderPrice; // 주문 가격

    private int quantity; // 주문 수량

    @Builder
    public OrderItem(Item item, Order order, Money orderPrice, int quantity) {
        this.item = item;
        this.order = order;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }

    //==연관관계 메서드==//
    public void setOrder(Order order) {
        this.order = order;
    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, Money orderPrice, int quantity) {
        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .orderPrice(orderPrice)
                .quantity(quantity)
                .build();

        item.removeStock(quantity);
        return orderItem;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소 -> 취소한 수량 재고에 추가
     */
    public void cancel() {
        getItem().addStock(quantity);
    }

    //==조회 로직==//
    /**
     * 주문상품 전체 가격 조회
     */
    public Money getTotalPrice() {
        return orderPrice.multiply(quantity);
    }
}
