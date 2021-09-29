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
@DiscriminatorValue(value = "Album")
@Entity
public class Album extends Item {

    private String artist;
    private String etc;

    @Builder
    public Album(String name, Money price, int stockQuantity, String artist, String etc) {
        super(name, price, stockQuantity);
        this.artist = artist;
        this.etc = etc;
    }
}
