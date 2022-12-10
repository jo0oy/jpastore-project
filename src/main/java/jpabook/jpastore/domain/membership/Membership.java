package jpabook.jpastore.domain.membership;

import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.Money;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString(of = {"id", "grade", "totalSpending"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "memberships")
@Entity
public class Membership extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_spending"))
    private Money totalSpending;

    private boolean isDeleted;

    private LocalDateTime deletedAt;

    @Builder
    public Membership(Grade grade, Money totalSpending) {
        this.grade = grade;
        this.totalSpending = totalSpending;
        this.isDeleted = false;
    }

    // 초기 멤버십 생성 메서드
    public static Membership createMembership() {
        return Membership.builder()
                .grade(Grade.SILVER)
                .totalSpending(new Money(0))
                .build();
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

    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
