package jpabook.jpastore.dto.category;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryUpdateReqDto {
    private Long parentId;
    private String name;

    @Builder
    public CategoryUpdateReqDto(Long parentId, String name) {
        this.parentId = parentId;
        this.name = name;
    }
}
