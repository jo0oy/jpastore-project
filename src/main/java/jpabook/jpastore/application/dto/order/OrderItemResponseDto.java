package jpabook.jpastore.application.dto.order;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.order.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponseDto {
    private String itemName;
    private Money orderPrice;
    private int quantity;


    public OrderItemResponseDto(OrderItem entity) {
        this.itemName = entity.getItem().getName();
        this.orderPrice = entity.getOrderPrice();
        this.quantity = entity.getQuantity();
    }
}
