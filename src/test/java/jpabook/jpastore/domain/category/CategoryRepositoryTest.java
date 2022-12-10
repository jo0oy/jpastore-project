package jpabook.jpastore.domain.category;

import jpabook.jpastore.config.TestQuerydslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestQuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리_생성_테스트")
    public void create_category_test() {
        //given
        var parent = Category.createCategory("도서", null);
        categoryRepository.save(parent);

        var category = Category.createCategory("국내도서", parent);
        categoryRepository.save(category);

        //when
        var categoryList = categoryRepository.findAll();

        //then
        assertThat(categoryList.size()).isEqualTo(2);
        assertThat(categoryList.get(0).getName()).isEqualTo("도서");
        assertThat(categoryList.get(1).getParent().getId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("카테고리_자식_생성")
    @Transactional
    @Rollback(value = false)
    public void add_child_test() {
        //given
        Category parent = Category.createCategory("도서", null);
        categoryRepository.save(parent);

        Long createdId = categoryRepository.save(Category.createCategory("국내도서", parent)).getId();
        Category category = categoryRepository.findById(createdId).get();

        for (int i = 1; i <= 5; i++) {
            Category child = Category.createCategory("국내하위도서" + i, category);
            category.addChild(child);
        }

        for(int i = 0; i < category.getChild().size(); i++){
            System.out.println("카테고리 이름: " + category.getChild().get(i).getName());
        }

        //when
        List<Category> categoryList = categoryRepository.findAll();

        //then
        assertThat(categoryList.size()).isEqualTo(7);
        assertThat(categoryList.get(2).getName()).isEqualTo("국내하위도서1");
    }

    @AfterEach
    void cleanUp() {
        categoryRepository.deleteAll();;
    }
}
