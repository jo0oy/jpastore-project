package jpabook.jpastore.application.dto.category;


import com.fasterxml.jackson.annotation.JsonInclude;
import jpabook.jpastore.domain.category.Category;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class CategoryParentChildDto {

    private Long id;

    private String name;

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<CategoryParentChildDto> childList = new ArrayList<>();

    public CategoryParentChildDto(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();

        if (!entity.getChild().isEmpty()) {
            for (Category child : entity.getChild()) {
                this.childList.add(new CategoryParentChildDto(child));
            }
        }
    }
}
