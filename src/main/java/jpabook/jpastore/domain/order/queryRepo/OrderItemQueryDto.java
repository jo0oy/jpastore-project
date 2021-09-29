package jpabook.jpastore.domain.order.queryRepo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public class OrderItemQueryDto {
    @JsonIgnore
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int quantity;

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int quantity) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }
}
