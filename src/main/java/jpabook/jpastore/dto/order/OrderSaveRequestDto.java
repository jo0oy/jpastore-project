package jpabook.jpastore.dto.order;

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

    @Builder
    public OrderSaveRequestDto(Long memberId, List<OrderItemRequestDto> orderItems) {
        this.memberId = memberId;
        this.orderItems.addAll(orderItems);
    }
}
