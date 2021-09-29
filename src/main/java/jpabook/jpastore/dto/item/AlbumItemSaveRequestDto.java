package jpabook.jpastore.dto.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Album;
import jpabook.jpastore.domain.item.Book;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class AlbumItemSaveRequestDto {
    private String name;
    private int price;
    private int stockQuantity;
    private String artist;
    private String etc;
    private Long categoryId;

    @Builder
    public AlbumItemSaveRequestDto(String name, int price, int stockQuantity, String artist, String etc, Long categoryId) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.artist = artist;
        this.etc = etc;
        this.categoryId = categoryId;
    }

    public Album toEntity() {
        return Album.builder()
                .name(name)
                .price(new Money(price))
                .stockQuantity(stockQuantity)
                .artist(artist)
                .etc(etc)
                .build();
    }
}
