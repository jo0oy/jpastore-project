package jpabook.jpastore.application.dto.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Item;
import lombok.Getter;

@Getter
public class ItemResponseDto {

    private Long id;
    private String name;
    private Money price;
    private int stockQuantity;

    public ItemResponseDto(Item entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
        this.stockQuantity = entity.getStockQuantity();
    }
}
