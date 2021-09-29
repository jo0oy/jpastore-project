package jpabook.jpastore.dto.category;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CategorySaveRequestDto {

    private String name;
    private Long parentId;

    @Builder
    public CategorySaveRequestDto(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }
}
