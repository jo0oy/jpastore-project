package jpabook.jpastore.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;

@ToString(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Money{

    private int value;

    public Money(int value) {
        this.value = value;
    }

    public Money add(Money money) {
        return new Money(this.value + money.value);
    }

    public Money minus(Money money) {
        return new Money(this.value - money.value);
    }

    public Money multiply(int multiplier) {
        return new Money(this.value * multiplier);
    }

}
