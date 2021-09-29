package jpabook.jpastore.domain.order;

import lombok.*;

@NoArgsConstructor
@Getter
@Builder
public class OrderSearch {

    private int userId;
    private int orderId;
    private OrderStatus status;

    public OrderSearch(int userId, int orderId, OrderStatus status) {
        this.userId = userId;
        this.orderId = orderId;
        this.status = status;
    }
}
