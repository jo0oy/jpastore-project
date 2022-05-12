package jpabook.jpastore.domain.category;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
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

        // 카테고리 부모 설정, 부모 카테고리에 this 자식으로 삽입
        if (Objects.nonNull(parent)) {
            parent.addChild(category);
        }

        // 자식 카테고리 리스트 연관관계 설정
        for (Category c : childList) {
            category.addChild(c);
        }

        return category;
    }

    //==비즈니스 로직==//
    public void update(Category parent, String name) {
        System.out.println("order update method 진입");
        parent.addChild(this);
        this.name = name;
    }
}
