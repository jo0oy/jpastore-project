package jpabook.jpastore.domain.order.queryRepo;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.order.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class OrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDateTime;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems = new ArrayList<>();

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDateTime,
                         OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
