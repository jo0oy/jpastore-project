package jpabook.jpastore.domain.category;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<CategoryItem> categoryItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> child = new ArrayList<>();

    @Builder
    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    //==연관관계 메서드==//
    public void addCategoryItem(CategoryItem categoryItem) {
        this.categoryItems.add(categoryItem);
        categoryItem.setCategory(this);
    }

    private void setParent(Category parent) {
        this.parent = parent;
    }

    public void addChild(Category child) {
        this.child.add(child);
        child.setParent(this);
    }

    //==생성 메서드==//
    public static Category createCategory(String name, Category parent, Category... childList) {
        Category category = Category.builder()
                .name(name)
                .build();

        category.setParent(parent);
        if (parent != null) {
            parent.addChild(category);
        }

        for (Category c : childList) {
            category.addChild(c);
        }

        return category;
    }

}
