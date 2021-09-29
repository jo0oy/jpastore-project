package jpabook.jpastore.application.dto.category;

import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.domain.category.Category;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CategoryResponseDto {

    private Long categoryId;
    private String name;
    private CategoryDto parent;
    private List<CategoryDto> childList = new ArrayList<>();
    private ItemListResponseDto categoryItems;

    public CategoryResponseDto(Category category) {
        this.categoryId = category.getId();
        this.name = category.getName();
        if(category.getParent() != null) {
            this.parent = new CategoryDto(category.getParent());
        }

        if(category.getChild() != null){
            for (Category child : category.getChild()) {
                this.childList.add(new CategoryDto(child));
            }
        }

        if (category.getCategoryItems() != null) {
            this.categoryItems = new ItemListResponseDto(
                    category.getCategoryItems().stream()
                    .map(o -> o.getItem())
                    .map(ItemResponseDto::new)
                    .collect(Collectors.toList()));
        }
    }
}
