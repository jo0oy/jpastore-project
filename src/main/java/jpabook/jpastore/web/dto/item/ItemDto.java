package jpabook.jpastore.web.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ItemDto {

    @ToString
    @Builder
    @Getter
    public static class AlbumItemRegisterReq {

        @NotNull(message = "{NotNull.itemName}")
        private String name;

        @Range(min = 1000, message = "{Range.price}")
        private int price;

        @Range(min = 100, max = 10000,
                message = "{Range.stockQuantity}")
        private int stockQuantity;

        @NotBlank
        private String artist;

        @NotBlank
        private String etc;

        @NotNull
        private Long categoryId;
    }

    @ToString
    @Builder
    @Getter
    public static class BookItemRegisterReq {

        @NotBlank(message = "{NotBlank.itemName}")
        private String name;

        @Range(min = 1000, message = "{Range.price}")
        private int price;

        @Range(min = 100, max = 10000,
                message = "{Range.stockQuantity}")
        private int stockQuantity;

        @NotBlank
        private String author;

        @NotBlank
        private String isbn;

        @NotNull
        private Long categoryId;
    }

    @ToString
    @Builder
    @Getter
    public static class DvdItemRegisterReq {

        @NotBlank(message = "{NotBlank.itemName}")
        private String name;

        @Range(min = 1000, message = "{Range.price}")
        private int price;

        @Range(min = 100, max = 10000,
                message = "{Range.stockQuantity}")
        private int stockQuantity;

        @NotBlank
        private String director;

        @NotBlank
        private String actor;

        @NotNull
        private Long categoryId;
    }

    @ToString
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
    public static class RegisterSuccessResponse {
        private Long registeredItemId;
    }

    @ToString
    @Builder
    @Getter
    public static class MainInfoResponse {
        private Long itemId;
        private String itemName;
        private Integer price;
        private Integer stockQuantity;
    }

    @ToString
    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DetailInfoResponse {
        private Long itemId;
        private String itemName;
        private Integer price;
        private Integer stockQuantity;
        private String author;
        private String isbn;
        private String artist;
        private String etc;
        private String actor;
        private String director;
    }

    @ToString
    @Builder
    @Getter
    public static class AlbumItemInfoResponse {
        private Long itemId;
        private String albumName;
        private Integer price;
        private int stockQuantity;
        private String artist;
        private String etc;
    }

    @ToString
    @Builder
    @Getter
    public static class BookItemInfoResponse {
        private Long itemId;
        private String bookName;
        private Integer price;
        private int stockQuantity;
        private String author;
        private String isbn;
    }

    @ToString
    @Builder
    @Getter
    public static class DvdItemInfoResponse {
        private Long itemId;
        private String dvdName;
        private Integer price;
        private int stockQuantity;
        private String actor;
        private String director;
    }

    @ToString
    @Getter
    public static class ListResponse<T> {
        private int totalCount;
        private List<T> items = new ArrayList<>();

        public ListResponse(List<T> items) {
            this.totalCount = items.size();
            this.items.addAll(items);
        }
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
