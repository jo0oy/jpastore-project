package jpabook.jpastore.application;

import jpabook.jpastore.application.dto.item.BookItemResponseDto;
import jpabook.jpastore.application.dto.item.ItemDetailResponseDto;
import jpabook.jpastore.application.item.ItemServiceImpl;
import jpabook.jpastore.domain.category.Category;
import jpabook.jpastore.domain.category.CategoryItem;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.domain.item.Item;
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
class ItemServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    @Test
    public void 상품_디테일_조회_테스트_v1() {
        //given
        Category category = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();

        BookItemSaveRequestDto requestDto = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("author1")
                .isbn("1111")
                .stockQuantity(100)
                .price(20000)
                .categoryId(firstChildId)
                .build();

        Long savedItemId = itemService.saveBookItem(requestDto);

        //when
        em.flush();
        em.clear();

        //when
        Object v1Result = itemService.itemDetail_V1(savedItemId);
//        System.out.println(itemService.getParticularItemById_V2(item4));

        //then
        assertThat(v1Result).isInstanceOf(BookItemResponseDto.class);
    }

    @Transactional
    @Test
    public void 상품_디테일_조회_테스트_v2() {
        //given
        Category category = Category.createCategory("앨범", null,
                Category.builder().name("국내앨범").build(), Category.builder().name("해외앨범").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();

        AlbumItemSaveRequestDto requestDto = AlbumItemSaveRequestDto.builder()
                .name("album1")
                .artist("artist1")
                .etc("album1 etc")
                .price(20000)
                .stockQuantity(150)
                .categoryId(firstChildId)
                .build();

        Long savedItemId = itemService.saveAlbumItem(requestDto);

        //when
        em.flush();
        em.clear();

        //when
        ItemDetailResponseDto<Item> item = itemService.itemDetail_V2(savedItemId);
        System.out.println(item);

        //then
        assertThat(item.getEtc()).isEqualTo("album1 etc");
        assertThat(item.getArtist()).isEqualTo("artist1");
    }

    @Transactional
    @Test
    public void 카테고리_카테고리상품_연관관계_성공_테스트() {
        //given
        Category category = Category.createCategory("국내도서", null,
                Category.builder().name("수필").build(), Category.builder().name("자기계발").build());

        Category savedCategory = categoryRepository.save(category);
        Long firstChildId = savedCategory.getChild().get(0).getId();

        BookItemSaveRequestDto requestDto = BookItemSaveRequestDto.builder()
                .name("book1")
                .author("author1")
                .isbn("1111")
                .stockQuantity(100)
                .price(20000)
                .categoryId(firstChildId)
                .build();

        //when
        itemService.saveBookItem(requestDto);

        em.flush();
        em.clear();

        Category changedCategory = categoryRepository.findById(firstChildId).orElse(null);

        //then
        assertThat(changedCategory).isNotNull();
        assertThat(changedCategory.getCategoryItems().size()).isEqualTo(1);
        assertThat(changedCategory.getCategoryItems().get(0).getItem().getName()).isEqualTo("book1");
        assertThat(changedCategory.getCategoryItems().get(0).getCategory().getName()).isEqualTo("수필");
    }

    @Transactional
    @Test
    public void 카테고리아이템_페치조인_테스트() {
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
        List<CategoryItem> categoryItems
                = em.createQuery("select ci from CategoryItem ci"
                + " join fetch ci.category c"
                + " join fetch ci.item i"
                + " where c.id = :categoryId", CategoryItem.class)
                .setParameter("categoryId", firstChildId)
                .getResultList();


        //then
        assertThat(categoryItems.size()).isEqualTo(2);
        assertThat(categoryItems.get(0).getCategory().getParent().getId()).isEqualTo(1L);
        assertThat(categoryItems.get(0).getCategory().getName()).isEqualTo("수필");
        assertThat(categoryItems.get(0).getItem().getName()).isEqualTo("book1");
        assertThat(categoryItems.get(1).getItem().getName()).isEqualTo("book2");
    }

    @Transactional
    @Test
    public void 카테고리_컬렉션_카테고리아이템_테스트() {
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

        Category findCategory
                = em.createQuery("select c from Category c"
                + " join fetch c.parent p"
                + " where c.id = :categoryId", Category.class)
                .setParameter("categoryId", firstChildId)
                .getSingleResult();

//        List<CategoryItem> categoryItems = findCategory.getCategoryItems();

        //then
        assertThat(findCategory.getName()).isEqualTo("수필");
//        assertThat(findCategory.getParent().getName()).isEqualTo("국내도서");
        assertThat(findCategory.getCategoryItems().size()).isEqualTo(2);
        assertThat(findCategory.getCategoryItems().get(0).getItem().getName()).isEqualTo("book1");
        assertThat(findCategory.getCategoryItems().get(1).getItem().getName()).isEqualTo("book2");
    }



}