package jpabook.jpastore.domain.membership;

import jpabook.jpastore.domain.Money;
import lombok.Getter;

@Getter
public enum Grade {
    SILVER(new Money(0), new Money(200000)),
    GOLD(new Money(200000), new Money(400000)),
    VIP(new Money(400000));

    private Money greaterEqual;
    private Money lessThan;

    Grade(Money greaterEqual) {
        this.greaterEqual = greaterEqual;
    }

    Grade(Money greaterEqual, Money lessThan) {
        this(greaterEqual);
        this.lessThan = lessThan;
    }
}
