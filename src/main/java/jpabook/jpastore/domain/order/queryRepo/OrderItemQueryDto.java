package jpabook.jpastore.domain.order.queryRepo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpastore.domain.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderItemQueryDto {
    @JsonIgnore
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int quantity;

    public OrderItemQueryDto(Long orderId, String itemName, Money orderPrice, int quantity) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice.getValue();
        this.quantity = quantity;
    }
}
