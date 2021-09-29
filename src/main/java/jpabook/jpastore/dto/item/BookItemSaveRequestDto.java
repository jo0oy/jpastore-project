package jpabook.jpastore.dto.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Book;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class BookItemSaveRequestDto {
    private String name;
    private int price;
    private int stockQuantity;
    private String author;
    private String isbn;
    private Long categoryId;

    @Builder
    public BookItemSaveRequestDto(String name, int price, int stockQuantity, String author, String isbn, Long categoryId) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.author = author;
        this.isbn = isbn;
        this.categoryId = categoryId;
    }

    public Book toEntity() {
        return Book.builder()
                .name(name)
                .price(new Money(price))
                .stockQuantity(stockQuantity)
                .author(author)
                .isbn(isbn)
                .build();
    }
}
