package jpabook.jpastore.dto.order;

import jpabook.jpastore.domain.order.Pay;
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
    private Pay payInfo;

    @Builder
    public OrderSimpleRequestDto(Long memberId, Long itemId, int quantity, Pay payInfo) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.payInfo = payInfo;
    }
}
