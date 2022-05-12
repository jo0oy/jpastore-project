package jpabook.jpastore.dto.item;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ItemUpdateRequestDto {

    private String name;
    private Integer price;
    private Integer stockQuantity;
    private String author;
    private String isbn;
    private String artist;
    private String etc;
    private String director;
    private String actor;

    @Builder
    public ItemUpdateRequestDto(String name, Integer price, Integer stockQuantity,
                                String author, String isbn, String artist, String etc,
                                String director, String actor) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.author = author;
        this.isbn = isbn;
        this.artist = artist;
        this.etc = etc;
        this.director = director;
        this.actor = actor;
    }
}
