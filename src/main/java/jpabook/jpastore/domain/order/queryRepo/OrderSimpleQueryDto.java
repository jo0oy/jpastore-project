package jpabook.jpastore.domain.order.queryRepo;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.order.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDateTime; //주문시간
    private OrderStatus orderStatus;
    private Address address;

    @Builder
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDateTime,
                               OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
