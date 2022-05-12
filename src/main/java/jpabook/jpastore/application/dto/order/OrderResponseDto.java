package jpabook.jpastore.application.dto.order;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.order.Order;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDto {

    private Long id;
    private LocalDateTime orderDateTime;
    private String orderStatus;
    private String deliveryStatus;
    private String memberName;
    private Money totalPrice;
    private List<OrderItemResponseDto> orderItems;

    public OrderResponseDto(Order entity) {
        this.id = entity.getId();
        this.orderDateTime = entity.getCreatedDate();
        this.orderStatus = entity.getStatus().getMessage();
        this.deliveryStatus = entity.getDelivery().getStatus().getMessage();
        this.memberName = entity.getMember().getName();
        this.totalPrice = entity.getTotalPrice();
        orderItems = entity.getOrderItems().stream()
                .map(OrderItemResponseDto::new).collect(Collectors.toList());
    }
}
