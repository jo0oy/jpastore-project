package jpabook.jpastore.application.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Album;
import jpabook.jpastore.domain.item.Book;
import jpabook.jpastore.domain.item.Dvd;
import lombok.*;

public class ItemCommand {

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class AlbumItemRegisterReq {
        private String name;
        private int price;
        private int stockQuantity;
        private String artist;
        private String etc;
        private Long categoryId;

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

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class BookItemRegisterReq {
        private String name;
        private int price;
        private int stockQuantity;
        private String author;
        private String isbn;
        private Long categoryId;

        public Book toEntity() {
            return Book.builder()
                    .name(name)
                    .price(new Money(price))
                    .stockQuantity(stockQuantity)
                    .author(author)
                    .isbn(isbn)
                    .build();
        }
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class DvdItemRegisterReq {
        private String name;
        private int price;
        private int stockQuantity;
        private String director;
        private String actor;
        private Long categoryId;

        public Dvd toEntity() {
            return Dvd.builder()
                    .name(name)
                    .price(new Money(price))
                    .stockQuantity(stockQuantity)
                    .actor(actor)
                    .director(director)
                    .build();
        }
    }


    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class UpdateInfoReq {
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private String author;
        private String isbn;
        private String artist;
        private String etc;
        private String director;
        private String actor;
    }

    @ToString
    @Builder
    @Getter
    public static class SearchCondition {
        private String name;
        private Integer minPrice;
        private Integer maxPrice;
    }
}
