package jpabook.jpastore.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrderItemRequestDto {

    private Long itemId;
    private int quantity;

    @Builder
    public OrderItemRequestDto(Long itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
