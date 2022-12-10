package jpabook.jpastore.domain.order.repository;

import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryCustom {

    Optional<Order> findOrderWithMemberDelivery(Long orderId);

    Optional<Order> findOrderWithMember(Long orderId);

    Optional<Order> findOrderWithDelivery(Long orderId);

    Long findDeliveryId(Long orderId);

    List<Order> findAllWithMemberDelivery();

    Page<Order> findAllWithMemberDelivery(Pageable pageable);

    List<Order> findAllWithOrderItems();

    List<Order> findAllWithOrderItemsDistinct();

    List<Order> findByCondition(OrderSearchCondition orderSearchCondition);

    Page<Order> findByCondition(OrderSearchCondition orderSearchCondition, Pageable pageable);

    List<OrderQueryInfo.SimpleInfo> findAllOrderSimpleInfo();

    List<OrderQueryInfo.MainInfo> findAllOrderInfo();

}
