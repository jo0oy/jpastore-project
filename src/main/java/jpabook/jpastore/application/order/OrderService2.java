package jpabook.jpastore.application.order;

import jpabook.jpastore.application.dto.order.OrderResponseDto;
import jpabook.jpastore.application.dto.order.OrderSimpleDto;
import jpabook.jpastore.domain.order.DeliveryStatus;
import jpabook.jpastore.domain.order.OrderStatus;
import jpabook.jpastore.domain.order.Pay;
import jpabook.jpastore.dto.order.OrderSaveRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService2 {

    Long order(Long memberId, Long itemId, int quantity, Pay payInfo);

    Long order(OrderSaveRequestDto requestDto);

    OrderResponseDto getOrder(Long orderId);

    OrderSimpleDto getOrderFetchSimpleDto(Long orderId);

    OrderResponseDto getOrderFetch(Long orderId);

    List<OrderResponseDto> listOrder();

    Page<OrderResponseDto> listOrder(Pageable pageable);

    void changeDeliveryStatus(Long orderId, DeliveryStatus status);

    void changeOrderStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);
}
