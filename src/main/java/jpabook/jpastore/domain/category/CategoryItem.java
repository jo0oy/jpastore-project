package jpabook.jpastore.domain.category;

import jpabook.jpastore.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category_items")
@Entity
public class CategoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    //==연관관계 메서드==//
    public void setCategory(Category category) {
        this.category = category;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    //==생성 메서드==//
    public static CategoryItem createCategoryItem(Item item, Category category) {
        CategoryItem ci = new CategoryItem();
        ci.setItem(item);
        category.addCategoryItem(ci);

        return ci;
    }
}
