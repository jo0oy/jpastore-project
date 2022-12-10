package jpabook.jpastore.application.category;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import jpabook.jpastore.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryInfo {

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class MainInfo {
        private Long categoryId;
        private String name;
        private SimpleInfo parent;
        private List<SimpleInfo> childList;

        public MainInfo(Category entity) {
            this.categoryId = entity.getId();
            this.name = entity.getName();
            this.childList = new ArrayList<>();

            if (validateList(entity.getChild())) {
                this.childList.addAll(
                        entity.getChild()
                                .stream()
                                .filter(c -> !c.isDeleted())
                                .map(SimpleInfo::new).collect(Collectors.toList())
                );
            }

            this.parent = (entity.getParent() == null) ? null : new SimpleInfo(entity.getParent());
        }
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class SimpleInfo {
        private Long categoryId;
        private String name;

        public SimpleInfo(Category entity) {
            this.categoryId = entity.getId();
            this.name = entity.getName();
        }
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class CategoryItemInfo {
        private Long itemId;
        private String itemName;
        private Money price;
        private int stockQuantity;

        public CategoryItemInfo(Item entity) {
            this.itemId = entity.getId();
            this.itemName = entity.getName();
            this.price = entity.getPrice();
            this.stockQuantity = entity.getStockQuantity();
        }
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class DetailWithItemsInfo {
        private Long categoryId;
        private String name;
        private SimpleInfo parent;
        private List<SimpleInfo> childList;
        private List<CategoryItemInfo> categoryItems;

        public DetailWithItemsInfo(Category entity) {
            this.categoryId = entity.getId();
            this.name = entity.getName();
            this.childList = new ArrayList<>();


            this.categoryItems = new ArrayList<>();
            if (validateList(entity.getCategoryItems())) {
                var items = entity.getCategoryItems().stream()
                        .map(CategoryItem::getItem)
                        .map(CategoryItemInfo::new)
                        .collect(Collectors.toList());
                this.categoryItems.addAll(items);
            }

            if(entity.getChild() != null){
                for (Category child : entity.getChild()) {
                    if (!child.isDeleted()) {
                        this.childList.add(new SimpleInfo(child));
                    }
                }
            }

            if(entity.getParent() != null) {
                this.parent = new SimpleInfo(entity.getParent());
            }
        }
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class CategoryItemListInfo {
        private Long categoryId;
        private String name;
        private int totalItemsCount;
        private List<CategoryItemInfo> categoryItems;

        public CategoryItemListInfo(Category entity) {
            this.categoryId = entity.getId();
            this.name = entity.getName();

            this.categoryItems = new ArrayList<>();
            if (validateList(entity.getCategoryItems())) {
                var items = entity.getCategoryItems().stream()
                        .map(CategoryItem::getItem)
                        .map(CategoryItemInfo::new)
                        .collect(Collectors.toList());
                this.categoryItems.addAll(items);
            }

            // 카테고리 아이템 리스트 매핑 후 최종 cnt
            this.totalItemsCount = categoryItems.size();
        }
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class ParentChildInfo {
        private Long categoryId;
        private String name;
        private List<ParentChildInfo> childList;

        public ParentChildInfo(Category entity) {
            this.categoryId = entity.getId();
            this.name = entity.getName();
            this.childList = new ArrayList<>();

            if (validateList(entity.getChild())) {
                for (Category child : entity.getChild()) {
                    if (!child.isDeleted()) { // 삭제되지 않은 카테고리인 경우
                        this.childList.add(new ParentChildInfo(child));
                    }
                }
            }
        }
    }

    // 리스트 검증 (null 체크 && empty list 체크)
    private static boolean validateList(List<?> list) {
        return (list != null) && (!list.isEmpty());
    }
}
