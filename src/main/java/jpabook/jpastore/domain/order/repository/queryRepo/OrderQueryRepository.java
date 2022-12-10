package jpabook.jpastore.domain.order.repository.queryRepo;

import jpabook.jpastore.domain.order.Order;
import jpabook.jpastore.domain.order.repository.OrderQueryInfo;
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

    public List<OrderQueryInfo.SimpleInfo> findOrderSimpleQueryInfos() {
        return em.createQuery("select new" +
                " jpabook.jpastore.domain.order.repository.OrderQueryInfo.SimpleInfo(o.id, m.username, o.createdDate, o.status, d.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryInfo.SimpleInfo.class)
                .getResultList();
    }

    public List<OrderQueryInfo.MainInfo> findOrderMainQueryInfos() {
        var result = getOrderInfos();

        result.forEach(o -> o.setOrderItems(getOrderItemInfos(o.getOrderId())));

        return result;
    }

    public List<OrderQueryInfo.MainInfo> findOrderQueryDto_optimazation() {
        var result = getOrderInfos();

        var orderItemMap = getOrderItemInfoMap(getOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<Long> getOrderIds(List<OrderQueryInfo.MainInfo> result) {
        return result.stream().map(OrderQueryInfo.MainInfo::getOrderId).collect(Collectors.toList());
    }

    private Map<Long, List<OrderQueryInfo.OrderItemInfo>> getOrderItemInfoMap(List<Long> orderIds) {
        List<OrderQueryInfo.OrderItemInfo> orderItems
                = em.createQuery("select " +
                "new jpabook.jpastore.domain.order.repository.OrderQueryInfo.OrderItemInfo(oi.order.id, i.name, oi.orderPrice, oi.quantity)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderQueryInfo.OrderItemInfo.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream().collect(Collectors.groupingBy(OrderQueryInfo.OrderItemInfo::getOrderId));
    }

    private List<OrderQueryInfo.MainInfo> getOrderInfos() {
        return em.createQuery(
                "select" +
                        " new jpabook.jpastore.domain.order.repository.OrderQueryInfo.MainInfo(o.id, m.username, o.createdDate, o.status, d.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" , OrderQueryInfo.MainInfo.class)
                .getResultList();
    }

    private List<OrderQueryInfo.OrderItemInfo> getOrderItemInfos(Long orderId) {
        return em.createQuery(
                "select" +
                        " new jpabook.jpastore.domain.order.repository.OrderQueryInfo.OrderItemInfo(oi.order.id, i.name, oi.orderPrice, oi.quantity)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderQueryInfo.OrderItemInfo.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
