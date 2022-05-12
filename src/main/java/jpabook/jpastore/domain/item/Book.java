package jpabook.jpastore.domain.item;

import jpabook.jpastore.domain.Money;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "Book")
@Entity
public class Book extends Item {

    private String author;
    private String isbn;

    @Builder
    public Book(String name, Money price, int stockQuantity, String author, String isbn) {
        super(name, price, stockQuantity);
        this.author = author;
        this.isbn = isbn;
    }

    //== 비즈니스 로직 메서드 ==//
    public void updateBook(String author, String isbn) {
        if (StringUtils.hasText(author)) {
            this.author = author;
        }

        if (StringUtils.hasText(isbn)) {
            this.isbn = isbn;
        }
    }
}
