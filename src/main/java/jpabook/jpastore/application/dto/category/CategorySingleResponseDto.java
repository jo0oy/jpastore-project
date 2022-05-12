package jpabook.jpastore.application.dto.category;

import jpabook.jpastore.domain.category.Category;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
public class CategorySingleResponseDto {
    private Long categoryId;
    private String name;
    private CategoryDto parent;
    private List<CategoryDto> childList = new ArrayList<>();

    public CategorySingleResponseDto(Category entity) {
        this.categoryId = entity.getId();
        this.name = entity.getName();

        if (!entity.getChild().isEmpty() && entity.getChild() != null) {
            this.childList.addAll(entity.getChild().stream()
                    .map(CategoryDto::new).collect(Collectors.toList()));
        }

        this.parent = new CategoryDto(entity.getParent());
    }
}
