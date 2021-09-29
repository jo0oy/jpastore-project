package jpabook.jpastore.domain.item;

import jpabook.jpastore.domain.Money;
import jpabook.jpastore.exception.NotEnoughStockException;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Entity
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Money price;

    private int stockQuantity;

    public Item(String name, Money price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
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

}
