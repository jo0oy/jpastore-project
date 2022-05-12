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

    //== 비즈니스 로직 메서드 ==//
    public void updateAlbum(String artist, String etc) {
        if (StringUtils.hasText(artist)) {
            this.artist = artist;
        }

        if (StringUtils.hasText(etc)) {
            this.etc = etc;
        }
    }
}
