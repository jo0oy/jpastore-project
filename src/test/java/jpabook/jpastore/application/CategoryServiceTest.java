package jpabook.jpastore.application;

import jpabook.jpastore.application.category.CategoryServiceImpl;
import jpabook.jpastore.application.dto.category.CategoryListResponseDto;
import jpabook.jpastore.application.dto.category.CategoryParentChildDto;
import jpabook.jpastore.application.dto.category.CategoryResponseDto;
import jpabook.jpastore.application.dto.category.CategorySingleResponseDto;
import jpabook.jpastore.application.dto.item.ItemListResponseDto;
import jpabook.jpastore.application.dto.item.ItemResponseDto;
import jpabook.jpastore.application.item.ItemServiceImpl;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.dto.category.CategoryUpdateReqDto;
import jpabook.jpastore.dto.item.AlbumItemSaveRequestDto;
import jpabook.jpastore.dto.item.BookItemSaveRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private EntityManager em;

    @Transactional
    @Test
    public void getCategory_findById() {
        //given
        Category category = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();
        Long secondChildId = savedCategory.getChild().get(1).getId();

        BookItemSaveRequestDto requestDto1 = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("author1")
                .isbn("1111")
                .stockQuantity(100)
                .price(20000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto2 = BookItemSaveRequestDto.builder()
                .name("book2")
                .author("author2")
                .isbn("2222")
                .stockQuantity(100)
                .price(17000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto3 = BookItemSaveRequestDto.builder()
                .name("book3")
                .author("author3")
                .isbn("3333")
                .stockQuantity(150)
                .price(25000)
                .categoryId(secondChildId)
                .build();

        itemService.saveBookItem(requestDto1);
        itemService.saveBookItem(requestDto2);
        itemService.saveBookItem(requestDto3);

        //when
        em.flush();
        em.clear();

        //when
        CategorySingleResponseDto findCategoryDto = categoryService.getCategoryV2(firstChildId);

        System.out.println(findCategoryDto);

        //then
    }

    @Transactional
    @Test
    public void getCategory_findById_fetchJoin() {
        //given
        Category category = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();
        Long secondChildId = savedCategory.getChild().get(1).getId();

        BookItemSaveRequestDto requestDto1 = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("author1")
                .isbn("1111")
                .stockQuantity(100)
                .price(20000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto2 = BookItemSaveRequestDto.builder()
                .name("book2")
                .author("author2")
                .isbn("2222")
                .stockQuantity(100)
                .price(17000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto3 = BookItemSaveRequestDto.builder()
                .name("book3")
                .author("author3")
                .isbn("3333")
                .stockQuantity(150)
                .price(25000)
                .categoryId(secondChildId)
                .build();

        itemService.saveBookItem(requestDto1);
        itemService.saveBookItem(requestDto2);
        itemService.saveBookItem(requestDto3);

        //when
        em.flush();
        em.clear();

        //when
        CategorySingleResponseDto findCategoryDto = categoryService.getCategoryV3(firstChildId);

        System.out.println(findCategoryDto);

        //then
    }

    @Transactional
    @Test
    public void getItemsByCategoryId_simple_test() {
        //given
        Category category = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();
        Long secondChildId = savedCategory.getChild().get(1).getId();

        BookItemSaveRequestDto requestDto1 = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("author1")
                .isbn("1111")
                .stockQuantity(100)
                .price(20000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto2 = BookItemSaveRequestDto.builder()
                .name("book2")
                .author("author2")
                .isbn("2222")
                .stockQuantity(100)
                .price(17000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto3 = BookItemSaveRequestDto.builder()
                .name("book3")
                .author("author3")
                .isbn("3333")
                .stockQuantity(150)
                .price(25000)
                .categoryId(secondChildId)
                .build();

        itemService.saveBookItem(requestDto1);
        itemService.saveBookItem(requestDto2);
        itemService.saveBookItem(requestDto3);

        //when
        em.flush();
        em.clear();

        //when
        ItemListResponseDto<ItemResponseDto> items = categoryService.itemListByCategoryId(firstChildId);

        //then
        assertThat(items.getTotalCount()).isEqualTo(2);
        assertThat(items.getItems().get(0).getName()).isEqualTo("book1");
    }

    @Transactional
    @Test
    public void categoryWithItems_test() {
        //given
        Category category = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();
        Long secondChildId = savedCategory.getChild().get(1).getId();

        BookItemSaveRequestDto requestDto1 = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("author1")
                .isbn("1111")
                .stockQuantity(100)
                .price(20000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto2 = BookItemSaveRequestDto.builder()
                .name("book2")
                .author("author2")
                .isbn("2222")
                .stockQuantity(100)
                .price(17000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto3 = BookItemSaveRequestDto.builder()
                .name("book3")
                .author("author3")
                .isbn("3333")
                .stockQuantity(150)
                .price(25000)
                .categoryId(secondChildId)
                .build();

        AlbumItemSaveRequestDto requestDto4 = AlbumItemSaveRequestDto.builder()
                .name("album1")
                .artist("artist1")
                .etc("album1 etc")
                .price(20000)
                .stockQuantity(150)
                .categoryId(firstChildId)
                .build();

        itemService.saveBookItem(requestDto1);
        itemService.saveBookItem(requestDto2);
        itemService.saveBookItem(requestDto3);
        itemService.saveAlbumItem(requestDto4);

        //when
        em.flush();
        em.clear();

        //when
        CategoryResponseDto result = categoryService.categoryWithItems(firstChildId);

        //then
        System.out.println(result);
    }

    @Transactional
    @Test
    public void categoryList_test() {
        //given
        Category category1 = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory1 = categoryRepository.save(category1);
        Long firstChildId = savedCategory1.getChild().get(0).getId();
        Long secondChildId = savedCategory1.getChild().get(1).getId();

        Category category2 = Category.createCategory("외국도서", null,
                Category.builder().name("소설").build()
                , Category.builder().name("시/수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory2 = categoryRepository.save(category2);
        Long firstChildId2 = savedCategory2.getChild().get(0).getId();
        Long secondChildId2 = savedCategory2.getChild().get(1).getId();
        Long thirdChildId2 = savedCategory2.getChild().get(2).getId();

        Category category3 = Category.createCategory("장편소설", savedCategory2.getChild().get(0),
                Category.builder().name("북미").build());

        Category category4 = Category.createCategory("단편소설", savedCategory2.getChild().get(0));

        Category savedCategory3 = categoryRepository.save(category3);
        Category savedCategory4 = categoryRepository.save(category4);


        //when
        em.flush();
        em.clear();

        //when
        CategoryListResponseDto<CategoryParentChildDto> result = categoryService.categoryList();

        System.out.println(result);

        //then
        assertThat(result.getTotalCount()).isEqualTo(10);
        assertThat(result.getCategories().get(0).getChildList().size()).isEqualTo(2);
    }

    @Transactional
    @Test
    public void list_fetchJoin() {
        //given
        Category category = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();
        Long secondChildId = savedCategory.getChild().get(1).getId();

        BookItemSaveRequestDto requestDto1 = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("author1")
                .isbn("1111")
                .stockQuantity(100)
                .price(20000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto2 = BookItemSaveRequestDto.builder()
                .name("book2")
                .author("author2")
                .isbn("2222")
                .stockQuantity(100)
                .price(17000)
                .categoryId(firstChildId)
                .build();

        BookItemSaveRequestDto requestDto3 = BookItemSaveRequestDto.builder()
                .name("book3")
                .author("author3")
                .isbn("3333")
                .stockQuantity(150)
                .price(25000)
                .categoryId(secondChildId)
                .build();

        itemService.saveBookItem(requestDto1);
        itemService.saveBookItem(requestDto2);
        itemService.saveBookItem(requestDto3);

        //when
        em.flush();
        em.clear();

        //when
        List<CategoryResponseDto> list = categoryService.list();

        assertThat(list.size()).isEqualTo(3);

        for (CategoryResponseDto dto : list) {
            System.out.println(dto);
        }

        //then
    }

    @Transactional
    @Test
    public void category_update_test() {
        //given
        Category category1 = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory1 = categoryRepository.save(category1);
        Long firstChildId = savedCategory1.getChild().get(0).getId();
        Long secondChildId = savedCategory1.getChild().get(1).getId();

        Category category2 = Category.createCategory("외국도서", null,
                Category.builder().name("소설").build()
                , Category.builder().name("시/수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory2 = categoryRepository.save(category2);
        Long firstChildId2 = savedCategory2.getChild().get(0).getId();
        Long secondChildId2 = savedCategory2.getChild().get(1).getId();
        Long thirdChildId2 = savedCategory2.getChild().get(2).getId();

        Category category3 = Category.createCategory("장편소설", savedCategory2.getChild().get(0),
                Category.builder().name("북미").build());

        Category category4 = Category.createCategory("단편소설", savedCategory2.getChild().get(0));

        Category savedCategory3 = categoryRepository.save(category3);
        Category savedCategory4 = categoryRepository.save(category4);


        //when
        em.flush();
        em.clear();

        categoryService.update(10L,
                CategoryUpdateReqDto.builder()
                        .parentId(3L)
                        .name("시간관리")
                        .build());

        em.flush();
        em.clear();

        Category changedCategory = categoryRepository.findById(10L).get();

        //then
        assertThat(changedCategory).isNotNull();
        assertThat(changedCategory.getName()).isEqualTo("시간관리");
        assertThat(changedCategory.getParent().getId()).isEqualTo(3L);
    }




}