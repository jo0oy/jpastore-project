package jpabook.jpastore.domain.order.queryRepo;

import jpabook.jpastore.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public Order findOrderWithMemberDelivery(Long orderId) {
        return em.createQuery("select o from Order o" +
                " join fetch o.member" +
                " join fetch o.delivery" +
                " where o.id = :orderId", Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o" +
                " join fetch o.member" +
                " join fetch o.delivery", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o" +
                " join fetch o.member" +
                " join fetch o.delivery", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Order> findAllWithItems() {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i" , Order.class)
                .getResultList();
    }

    public List<Order> findAllWithItemsDistinct() {
        return em.createQuery("select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
                .getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new" +
                " jpabook.jpastore.domain.order.queryRepo.OrderSimpleQueryDto(o.id, m.name, o.createdDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }

    public List<OrderQueryDto> findOrderQueryDto() {
        List<OrderQueryDto> result = findOrdersDto();

        result.forEach(o -> o.setOrderItems(findOrderItemsDto(o.getOrderId())));

        return result;
    }

    public List<OrderQueryDto> findOrderQueryDto_optimazation() {
        List<OrderQueryDto> result = findOrdersDto();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(getOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<Long> getOrderIds(List<OrderQueryDto> result) {
        return result.stream().map(OrderQueryDto::getOrderId).collect(Collectors.toList());
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems
                = em.createQuery("select " +
                "new jpabook.jpastore.domain.order.queryRepo.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.quantity)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }

    private List<OrderQueryDto> findOrdersDto() {
        return em.createQuery(
                "select" +
                        " new jpabook.jpastore.domain.order.queryRepo.OrderQueryDto(o.id, m.name, o.createdDate, o.status, d.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" , OrderQueryDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItemsDto(Long orderId) {
        return em.createQuery(
                "select" +
                        " new jpabook.jpastore.domain.order.queryRepo.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.quantity)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
