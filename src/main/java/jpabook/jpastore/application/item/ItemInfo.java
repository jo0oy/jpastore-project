package jpabook.jpastore.application.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Album;
import jpabook.jpastore.domain.item.Book;
import jpabook.jpastore.domain.item.Dvd;
import jpabook.jpastore.domain.item.Item;
import lombok.*;

public class ItemInfo {

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long itemId;
        private String itemName;
        private Money price;
        private Integer stockQuantity;

        public MainInfo(Item entity) {
            this.itemId = entity.getId();
            this.itemName = entity.getName();
            this.price = entity.getPrice();
            this.stockQuantity = entity.getStockQuantity();
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class DetailInfo<T extends Item> {
        private Long itemId;
        private String itemName;
        private Money price;
        private Integer stockQuantity;
        private String author;
        private String isbn;
        private String artist;
        private String etc;
        private String actor;
        private String director;

        public DetailInfo(T entity) {
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

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class AlbumItemInfo {
        private Long itemId;
        private String albumName;
        private Money price;
        private int stockQuantity;
        private String artist;
        private String etc;

        public AlbumItemInfo(Album entity) {
            this.itemId = entity.getId();
            this.albumName = entity.getName();
            this.price = entity.getPrice();
            this.stockQuantity = entity.getStockQuantity();
            this.artist = entity.getArtist();
            this.etc = entity.getEtc();
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class BookItemInfo {
        private Long itemId;
        private String bookName;
        private Money price;
        private int stockQuantity;
        private String author;
        private String isbn;

        public BookItemInfo(Book entity) {
            this.itemId = entity.getId();
            this.bookName = entity.getName();
            this.price = entity.getPrice();
            this.stockQuantity = entity.getStockQuantity();
            this.author = entity.getAuthor();
            this.isbn = entity.getIsbn();
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class DvdItemInfo {
        private Long itemId;
        private String dvdName;
        private Money price;
        private int stockQuantity;
        private String actor;
        private String director;

        public DvdItemInfo(Dvd entity) {
            this.itemId = entity.getId();
            this.dvdName = entity.getName();
            this.price = entity.getPrice();
            this.stockQuantity = entity.getStockQuantity();
            this.actor = entity.getActor();
            this.director = entity.getDirector();
        }
    }
}
