package jpabook.jpastore.application.dto.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Album;
import lombok.Getter;

@Getter
public class AlbumItemResponseDto {

    private Long itemId;
    private String albumName;
    private Money price;
    private int stockQuantity;
    private String artist;
    private String etc;

    public AlbumItemResponseDto(Album entity) {
        this.itemId = entity.getId();
        this.albumName = entity.getName();
        this.price = entity.getPrice();
        this.stockQuantity = entity.getStockQuantity();
        this.artist = entity.getArtist();
        this.etc = entity.getEtc();
    }
}
