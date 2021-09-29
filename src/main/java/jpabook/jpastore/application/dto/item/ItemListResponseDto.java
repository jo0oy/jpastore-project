package jpabook.jpastore.application.dto.item;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ItemListResponseDto {
    private int totalCount;
    private List<ItemResponseDto> items = new ArrayList<>();

    public ItemListResponseDto(List<ItemResponseDto> items) {
        this.totalCount = items.size();
        this.items.addAll(items);
    }
}
