package jpabook.jpastore.domain.order;

import lombok.*;

@NoArgsConstructor
@Getter
@Builder
public class OrderSearchCondition {

    private Long memberId;
    private String memberName;
    private Long orderId;
    private OrderStatus status;
    private DeliveryStatus deliveryStatus;

    @Builder
    public OrderSearchCondition(Long memberId,
                                String memberName,
                                Long orderId,
                                OrderStatus status,
                                DeliveryStatus deliveryStatus) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.orderId = orderId;
        this.status = status;
        this.deliveryStatus = deliveryStatus;
    }
}
