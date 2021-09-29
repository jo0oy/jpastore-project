package jpabook.jpastore.application.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Album;
import jpabook.jpastore.domain.item.Book;
import jpabook.jpastore.domain.item.Dvd;
import jpabook.jpastore.domain.item.Item;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemInfoResponseDto<T extends Item> {
    private Long itemId;
    private String itemName;
    private Money price;
    private int stockQuantity;
    private String author;
    private String isbn;
    private String artist;
    private String etc;
    private String actor;
    private String director;

    public ItemInfoResponseDto(T entity) {
        this.itemId = entity.getId();
        this.itemName = entity.getName();
        this.price = entity.getPrice();
        this.stockQuantity = entity.getStockQuantity();

        if (entity instanceof Book) {
            this.author = ((Book) entity).getAuthor();
            this.isbn = ((Book) entity).getIsbn();
        } else if (entity instanceof Album) {
            this.artist = ((Album) entity).getArtist();
            this.etc = ((Album) entity).getEtc();
        } else if (entity instanceof Dvd) {
            this.actor = ((Dvd) entity).getActor();
            this.director = ((Dvd) entity).getDirector();
        }
    }
}
