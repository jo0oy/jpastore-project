package jpabook.jpastore.domain.order.queryRepo;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class OrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDateTime;
    private OrderStatus orderStatus;
    private DeliveryStatus deliveryStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems = new ArrayList<>();

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDateTime,
                         OrderStatus orderStatus, DeliveryStatus deliveryStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.deliveryStatus = deliveryStatus;
        this.address = address;
    }
}
