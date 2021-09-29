package jpabook.jpastore.application.dto.member;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import lombok.Getter;

@Getter
public class MembershipDto {
    private Long membershipId;
    private Grade grade;
    private Money totalSpending;

    public MembershipDto(Membership entity) {
        this.membershipId = entity.getId();
        this.grade = entity.getGrade();
        this.totalSpending = entity.getTotalSpending();
    }
}
