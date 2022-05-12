package jpabook.jpastore.domain.order.repository;

import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderSearchCondition;
import jpabook.jpastore.domain.order.queryRepo.OrderQueryDto;
import jpabook.jpastore.domain.order.queryRepo.OrderSimpleQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {

    Order findOrderWithMemberDelivery(Long orderId);

    Order findOrderWithDelivery(Long orderId);

    Long findDeliveryId(Long orderId);

    List<Order> findAllWithMemberDelivery();

    Page<Order> findAllWithMemberDelivery(Pageable pageable);

    List<Order> findByCondition(OrderSearchCondition orderSearchCondition);

    Page<Order> findByCondition(OrderSearchCondition orderSearchCondition, Pageable pageable);

    List<OrderSimpleQueryDto> findAllOrderSimpleDto();

    List<OrderQueryDto> findAllOrderDto();

}
