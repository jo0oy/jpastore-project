package jpabook.jpastore.dto.order;

import jpabook.jpastore.domain.order.Pay;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class OrderSaveRequestDto {

    private Long memberId;
    private List<OrderItemRequestDto> orderItems = new ArrayList<>();
    private Pay payInfo;

    @Builder
    public OrderSaveRequestDto(Long memberId, List<OrderItemRequestDto> orderItems, Pay payInfo) {
        this.memberId = memberId;
        this.orderItems.addAll(orderItems);
        this.payInfo = payInfo;
    }
}
