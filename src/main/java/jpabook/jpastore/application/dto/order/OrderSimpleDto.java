package jpabook.jpastore.application.dto.order;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class OrderSimpleDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderedDate;
    private OrderStatus orderStatus;
    private DeliveryStatus deliveryStatus;
    private Address address;

    @Builder
    public OrderSimpleDto(Long orderId, String name, LocalDateTime orderedDate,
                          OrderStatus orderStatus, DeliveryStatus deliveryStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderedDate = orderedDate;
        this.orderStatus = orderStatus;
        this.deliveryStatus = deliveryStatus;
        this.address = address;
    }

    public OrderSimpleDto(Order entity) {
        this.orderId = entity.getId();
        this.name = entity.getMember().getName();
        this.orderedDate = entity.getCreatedDate();
        this.orderStatus = entity.getStatus();
        this.address = entity.getDelivery().getAddress();
    }
}
