package jpabook.jpastore.web.dto.category;

import jpabook.jpastore.domain.Money;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class CategoryDto {

    @ToString
    @Builder
    @Getter
    public static class RegisterReq {

        @NotBlank(message = "{NotBlank.categoryName}")
        private String categoryName;

        private Long parentId;
    }

    @ToString
    @Builder
    @Getter
    public static class UpdateInfoReq {
        private String categoryName;
        private Long parentId;
    }

    @ToString
    @Builder
    @Getter
    public static class RegisterSuccessResponse {
        private Long registeredCategoryId;
    }

    @ToString
    @Builder
    @Getter
    public static class SimpleInfoResponse {
        private Long categoryId;
        private String name;
    }

    @ToString
    @Builder
    @Getter
    public static class MainInfoResponse {
        private Long categoryId;
        private String name;
        private SimpleInfoResponse parent;
        private List<SimpleInfoResponse> childList;
    }

    @ToString
    @Builder
    @Getter
    public static class CategoryItemInfoResponse {
        private Long itemId;
        private String itemName;
        private Money price;
        private Integer stockQuantity;
    }

    @ToString
    @Builder
    @Getter
    public static class DetailWithItemsInfoResponse {
        private Long categoryId;
        private String name;
        private SimpleInfoResponse parent;
        private List<SimpleInfoResponse> childList;
        private List<CategoryItemInfoResponse> categoryItems;
    }

    @ToString
    @Builder
    @Getter
    public static class ParentChildInfoResponse {
        private Long categoryId;
        private String name;
        private List<ParentChildInfoResponse> childList;
    }

    @ToString
    @Builder
    @Getter
    public static class CategoryItemListInfoResponse {
        private Long categoryId;
        private String name;
        private int totalItemsCount;
        private List<CategoryItemInfoResponse> categoryItems;
    }
}
