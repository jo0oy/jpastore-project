package jpabook.jpastore.application.dto.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.item.Book;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class BookItemResponseDto {

    private Long itemId;
    private String bookName;
    private Money price;
    private int stockQuantity;
    private String author;
    private String isbn;

    public BookItemResponseDto(Book entity) {
        this.itemId = entity.getId();
        this.bookName = entity.getName();
        this.price = entity.getPrice();
        this.stockQuantity = entity.getStockQuantity();
        this.author = entity.getAuthor();
        this.isbn = entity.getIsbn();
    }

}
