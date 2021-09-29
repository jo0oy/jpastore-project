package jpabook.jpastore.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
public class OrderItemAddDto {
    private Long orderId;
    private Long itemId;
    private int quantity;

    public OrderItemAddDto(Long orderId, Long itemId, int quantity) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
