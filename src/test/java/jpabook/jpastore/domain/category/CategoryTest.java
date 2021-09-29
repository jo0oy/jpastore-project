package jpabook.jpastore.domain.category;

import org.junit.jupiter.api.Test;

public class CategoryTest {

    @Test
    void entity_test() {
        Category parent = Category.createCategory("부모카테고리", null);
        System.out.println(parent);

        Category category = Category.createCategory("카테고리1", parent,
                Category.builder().name("자식카테고리1").build());
        System.out.println(category);

        category.addChild(Category.createCategory("자식카테고리2", category));
        System.out.println(category);
        category.addChild(Category.createCategory("자식카테고리3", category));
        System.out.println(category);
    }
}
