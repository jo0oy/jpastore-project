package jpabook.jpastore.application.dto.category;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CategoryListResponseDto<T> {
    private Long totalCount;
    private List<T> categories = new ArrayList<>();

    public CategoryListResponseDto(List<T> categories) {
        this.totalCount = (long) categories.size();
        this.categories.addAll(categories);
    }

    public CategoryListResponseDto(Long totalCount, List<T> categories) {
        this(categories);
        this.totalCount = totalCount;
    }
}
