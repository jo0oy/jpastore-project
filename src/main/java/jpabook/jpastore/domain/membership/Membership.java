package jpabook.jpastore.domain.membership;

import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.Money;
import lombok.*;

import javax.persistence.*;

@ToString(of = {"id", "grade", "totalSpending"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Membership extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "membership_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_spending"))
    private Money totalSpending;

    @Builder
    public Membership(Grade grade, Money totalSpending) {
        this.grade = grade;
        this.totalSpending = totalSpending;
    }

    // 비즈니스 로직
    public void addTotalSpending(Money spending) {
        this.totalSpending = this.totalSpending.add(spending);
    }

    public void minusTotalSpending(Money totalAmount) {
        this.totalSpending = this.totalSpending.minus(totalAmount);
    }

    public void resetMembership() {
        this.grade = Grade.SILVER;
        this.totalSpending = new Money(0);
    }

    public void updateMembership() {
        int value = totalSpending.getValue();

        if (value >= 0 && value < 200000) {
            this.grade = Grade.SILVER;
        } else if (value >= 200000 && value < 400000) {
            this.grade = Grade.GOLD;
        } else {
            this.grade = Grade.VIP;
        }

        this.totalSpending = new Money(0);
    }


}
