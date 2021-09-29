package jpabook.jpastore.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrderSimpleRequestDto {

    private Long memberId;
    private Long itemId;
    private int quantity;

    @Builder
    public OrderSimpleRequestDto(Long memberId, Long itemId, int quantity) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
