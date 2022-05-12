package jpabook.jpastore.application.dto.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Dvd;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class DvdItemResponseDto {
    private Long itemId;
    private String dvdName;
    private Money price;
    private int stockQuantity;
    private String actor;
    private String director;

    public DvdItemResponseDto(Dvd entity) {
        this.itemId = entity.getId();
        this.dvdName = entity.getName();
        this.price = entity.getPrice();
        this.stockQuantity = entity.getStockQuantity();
        this.actor = entity.getActor();
        this.director = entity.getDirector();
    }
}
