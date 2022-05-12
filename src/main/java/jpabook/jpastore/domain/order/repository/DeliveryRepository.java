package jpabook.jpastore.domain.order.repository;

import jpabook.jpastore.domain.order.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
