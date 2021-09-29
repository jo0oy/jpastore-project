package jpabook.jpastore.domain.item;

import jpabook.jpastore.domain.Money;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "Dvd")
@Entity
public class Dvd extends Item {

    private String director;
    private String actor;

    @Builder
    public Dvd(String name, Money price, int stockQuantity, String director, String actor) {
        super(name, price, stockQuantity);
        this.director = director;
        this.actor = actor;
    }
}
