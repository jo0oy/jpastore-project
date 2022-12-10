package jpabook.jpastore.domain.item;

import jpabook.jpastore.common.exception.NotEnoughStockException;
import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.review.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Table(name = "items")
@Entity
public abstract class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Money price;

    @Column(nullable = false)
    private int stockQuantity;

    @OneToMany(mappedBy = "item",
            fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    private boolean isDeleted;

    private LocalDateTime deletedAt;

    public Item(String name,
                Money price,
                int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.isDeleted = false;
    }

    //== 연관관계 메서드 ==//
    public void addReview(Review review) {
        this.reviews.add(review);
    }

    //==비즈니스 로직==//
    /**
     * 재고 수량 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 재고 수량 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("재고 수량이 부족합니다.");
        }
        this.stockQuantity = restStock;
    }

    /**
     * 상품 정보 수정
     */
    public void updateItem(String name, Integer price, Integer stockQuantity) {
        if (StringUtils.hasText(name)) {
            this.name = name;
        }

        if (Objects.nonNull(price)) {
            this.price = new Money(price);
        }

        if (Objects.nonNull(stockQuantity)) {
            this.stockQuantity = stockQuantity;
        }
    }

    /**
     * 상품 삭제
     */
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
