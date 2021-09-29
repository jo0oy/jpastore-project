package jpabook.jpastore.application.dto.category;

import jpabook.jpastore.domain.category.Category;
import lombok.Getter;

@Getter
public class CategoryDto {

    private Long categoryId;
    private String name;

    public CategoryDto(Category category) {
        this.categoryId = category.getId();
        this.name = category.getName();
    }

}
