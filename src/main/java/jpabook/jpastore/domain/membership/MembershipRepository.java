package jpabook.jpastore.domain.membership;

import jpabook.jpastore.domain.Money;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByGrade(Grade grade);

    List<Membership> findByTotalSpendingBetween(Money start, Money end);
}
