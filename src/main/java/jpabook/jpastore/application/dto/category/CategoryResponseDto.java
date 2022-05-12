package jpabook.jpastore.application.dto.category;

import jpabook.jpastore.application.dto.item.ItemDetailResponseDto;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import jpabook.jpastore.domain.item.Book;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
public class CategoryResponseDto {

    private Long categoryId;
    private String name;
    private CategoryDto parent;
    private List<CategoryDto> childList = new ArrayList<>();
    private ItemListResponseDto<?> categoryItems;

    public CategoryResponseDto(Category category) {
        this.categoryId = category.getId();
        this.name = category.getName();

        if(category.getChild() != null){
            for (Category child : category.getChild()) {
                this.childList.add(new CategoryDto(child));
            }
        }

        if (category.getCategoryItems() != null) {
            this.categoryItems = new ItemListResponseDto<>(
                    category.getCategoryItems().stream()
                    .map(CategoryItem::getItem)
                    .map(ItemResponseDto::new)
                    .collect(Collectors.toList()));
        }

        if(category.getParent() != null) {
            this.parent = new CategoryDto(category.getParent());
        }
    }
}
