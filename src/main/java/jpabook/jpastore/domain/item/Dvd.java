package jpabook.jpastore.domain.item;

import jpabook.jpastore.domain.Money;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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

    //== 비즈니스 로직 메서드 ==//
    public void updateDvd(String director, String actor) {
        if (StringUtils.hasText(director)) {
            this.director = director;
        }

        if (StringUtils.hasText(actor)) {
            this.actor = actor;
        }
    }
}
