package jpabook.jpastore.domain.membership;

import jpabook.jpastore.domain.Money;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByGrade(Grade grade);

    List<Membership> findByTotalSpendingBetween(Money start, Money end);

    @Modifying(clearAutomatically = true)
    @Query("update Membership m set m.grade = :changeGrade " +
            "where m.totalSpending >= :from and m.totalSpending < :to")
    int bulkUpdateGrade(@Param("from") Money from, @Param("to") Money to,
                        @Param("changeGrade") Grade changeGrade);

    @Modifying(clearAutomatically = true)
    @Query("update Membership m set m.grade = :changeGrade where m.totalSpending >= :from")
    int bulkUpdateGrade(@Param("from") Money from,
                        @Param("changeGrade") Grade changeGrade);

    @Modifying(clearAutomatically = true)
    @Query("update Membership m set m.totalSpending = 0")
    int bulkResetTotalSpending();
}
