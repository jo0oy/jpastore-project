package jpabook.jpastore.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

//    List<Order> findAllWithOption(OrderSearch orderSearch);
}
