package jpabook.jpastore.dto.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Dvd;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class DvdItemSaveRequestDto {
    private String name;
    private int price;
    private int stockQuantity;
    private String director;
    private String actor;
    private Long categoryId;

    @Builder
    public DvdItemSaveRequestDto(String name, int price, int stockQuantity, String director, String actor, Long categoryId) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.director = director;
        this.actor = actor;
        this.categoryId = categoryId;
    }

    public Dvd toEntity() {
        return Dvd.builder()
                .name(name)
                .price(new Money(price))
                .stockQuantity(stockQuantity)
                .actor(actor)
                .director(director)
                .build();
    }
}
