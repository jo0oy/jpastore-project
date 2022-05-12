package jpabook.jpastore.domain.order.repository;

import jpabook.jpastore.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("select o from Order o join fetch o.delivery join fetch o.member")
    List<Order> findOrders();

    @Query("select o from Order o join fetch o.delivery join fetch o.member m " +
            "where m.id = :memberId order by o.id desc")
    List<Order> findOrdersByMemberId(Long memberId);
}
