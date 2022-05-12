package jpabook.jpastore.application.dto.item;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class ItemListResponseDto<T> {
    private int totalCount;
    private List<T> items = new ArrayList<>();

    public ItemListResponseDto(List<T> items) {
        this.totalCount = items.size();
        this.items.addAll(items);
    }
}
