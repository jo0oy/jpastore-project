package jpabook.jpastore.application.item;

import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.config.AopConfig;
import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.domain.category.CategoryRepository;
import jpabook.jpastore.domain.item.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("상품 서비스 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import({AopConfig.class, TestDBConfig.class})
@SpringBootTest
class ItemServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUp() {
        databaseCleanUp.cleanUp();
    }

    @DisplayName("[성공][service] 책 상품 등록")
    @Test
    public void givenBookRegisterReq_whenSaveBookItem_thenReturnsSavedItemId() {
        //given
        var request = ItemCommand.BookItemRegisterReq.builder()
                .name("newBook")
                .author("risingStar")
                .isbn("12345")
                .stockQuantity(100)
                .price(15900)
                .categoryId(4L)
                .build();

        //when
        var savedItemId = itemService.saveBookItem(request);

        //then
        assertThat(savedItemId).isNotNull();
        assertThat(savedItemId).isGreaterThan(9L);
    }

    @DisplayName("[실패][service] 책 상품 등록 - 존재하지 않는 카테고리")
    @Test
    public void givenRegisterReqWithCategoryIdNotExist_whenSaveBookItem_thenThrowsEntityNotExistException() {
        //given
        var categoryId = 100L;
        var request = ItemCommand.BookItemRegisterReq.builder()
                .name("newBook")
                .author("risingStar")
                .isbn("12345")
                .stockQuantity(100)
                .price(15900)
                .categoryId(categoryId)
                .build();

        //when & then
        assertThatThrownBy(() -> itemService.saveBookItem(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 카테고리 입니다. id = " + categoryId);
    }

    @DisplayName("[성공][service] 앨범 상품 등록")
    @Test
    public void givenAlbumRegisterReq_whenSaveBookItem_thenReturnsSavedItemId() {
        //given
        var request = ItemCommand.AlbumItemRegisterReq.builder()
                .name("newAlbum")
                .artist("Adele")
                .stockQuantity(1000)
                .price(23000)
                .categoryId(9L)
                .build();

        //when
        var savedItemId = itemService.saveAlbumItem(request);

        //then
        assertThat(savedItemId).isNotNull();
        assertThat(savedItemId).isGreaterThan(9L);
    }

    @DisplayName("[성공][service] DVD 상품 등록")
    @Test
    public void givenDvdRegisterReq_whenSaveBookItem_thenReturnsSavedItemId() {
        //given
        var request = ItemCommand.DvdItemRegisterReq.builder()
                .name("newDVD")
                .actor("newActor")
                .director("Tim")
                .stockQuantity(500)
                .price(30000)
                .categoryId(11L)
                .build();

        //when
        var savedItemId = itemService.saveDvdItem(request);

        //then
        assertThat(savedItemId).isNotNull();
        assertThat(savedItemId).isGreaterThan(9L);
    }

    @DisplayName("[성공][service] 상품 정보 수정")
    @Test
    public void givenItemIdAndUpdateReq_whenUpdateItemInfo_thenWorksFine() {
        //given
        var itemId = 1L;
        var updateName = "updateBookName";
        var request = ItemCommand.UpdateInfoReq
                .builder()
                .name(updateName)
                .build();

        //when
        itemService.updateItemInfo(itemId, request);

        //then
        var item = itemRepository.findItemById(itemId);
        assertThat(item).isPresent();
        assertThat(item.get().getName()).isEqualTo(updateName);
    }

    @DisplayName("[실패][service] 상품 정보 수정 - 존재하지 않는 상품")
    @Test
    public void givenItemIdThatNotExistAndUpdateReq_whenUpdateItemInfo_thenThrowsEntityNotExistException() {
        //given
        var itemId = 100L;
        var updateName = "updateBookName";
        var request = ItemCommand.UpdateInfoReq
                .builder()
                .name(updateName)
                .isbn("909090")
                .build();

        //when & then
        assertThatThrownBy(() -> itemService.updateItemInfo(itemId, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("[성공][service] 상품 삭제")
    @Test
    public void givenItemId_whenDeleteItem_thenWorksFine() {
        //given
        var itemId = 2L;

        //when
        itemService.delete(itemId);

        //then
        var item = itemRepository.findById(itemId);
        var categoryItems = categoryRepository.findCategoryItemsByItem_Id(itemId);

        assertThat(item).isPresent();
        assertThat(item.get().isDeleted()).isTrue();
        assertThat(categoryItems.size()).isEqualTo(0);
    }

    @DisplayName("[실패][service] 상품 삭제 - 존재하지 않는 상품")
    @Test
    public void givenItemIdThatNotExist_whenDeleteItem_thenThrowsEntityNotExistException() {
        //given
        var itemId = 100L;

        //when & then
        assertThatThrownBy(() -> itemService.delete(itemId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("[성공][service] 상품 간단 정보 단건 조회")
    @Test
    public void givenItemId_whenGetItem_thenReturnsItemMainInfo() {
        //given
        var itemId = 1L;

        //when
        var itemInfo = itemService.getItem(itemId);

        //then
        assertThat(itemInfo.getItemId()).isEqualTo(itemId);
        assertThat(itemInfo.getItemName()).isEqualTo("book1");
    }

    @DisplayName("[성공][service] 상품 상세 정보 단건 조회 - v1")
    @Test
    public void givenAlbumItemId_whenItemDetail_V1_thenReturnsObject() {
        //given
        var itemId = 4L;

        //when
        var itemInfo = itemService.itemDetail_V1(itemId);

        //then
        assertThat(itemInfo).isInstanceOf(ItemInfo.AlbumItemInfo.class);
        var result = (ItemInfo.AlbumItemInfo) itemInfo;
        assertThat(result.getAlbumName()).isEqualTo("album1");
        assertThat(result.getItemId()).isEqualTo(itemId);
        assertThat(result.getPrice().getValue()).isEqualTo(21000);
    }

    @DisplayName("[성공][service] 상품 상세 정보 단건 조회 - v2")
    @Test
    public void givenAlbumItemId_whenItemDetail_V2_thenReturnsItemInfoDetailInfo() {
        //given
        var itemId = 4L;

        //when
        var itemInfo = itemService.itemDetail_V2(itemId);

        //then
        assertThat(itemInfo).isInstanceOf(ItemInfo.DetailInfo.class);
        assertThat(itemInfo.getItemName()).isEqualTo("album1");
        assertThat(itemInfo.getItemId()).isEqualTo(itemId);
        assertThat(itemInfo.getIsbn()).isEqualTo(null);
        assertThat(itemInfo.getPrice().getValue()).isEqualTo(21000);
    }

    @DisplayName("[성공][service] 상품명 검색 - v1: 쿼리 메서드 버전")
    @Test
    public void givenItemName_whenSearchItemsByName_V1_thenReturnsItemInfoList() {
        //given
        var itemName = "BoOk";

        //when
        var result = itemService.searchItemsByName_V1(itemName);

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getItemName()).isEqualTo("book1");
    }

    @DisplayName("[성공][service] 상품명 검색 - v2: JPQL 작성 버전")
    @Test
    public void givenItemName_whenSearchItemsByName_V2_thenReturnsItemInfoList() {
        //given
        var itemName = "BoOk";

        //when
        var result = itemService.searchItemsByName_V2(itemName);

        //then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getItemName()).isEqualTo("book1");
    }

    @DisplayName("[성공][service] 전체 상품 리스트 조회")
    @Test
    public void givenNothing_whenItemList_thenReturnsItemInfoList() {
        //given

        //when
        var result = itemService.itemList();

        //then
        assertThat(result.size()).isEqualTo(9);
    }

    @DisplayName("[성공][service] 전체 상품 리스트 조회 - 페이징/정렬/검색")
    @Test
    public void givenSearchConditionAndPageRequest_whenItems_thenReturnsItemInfoList() {
        //given
        var itemName = "alb";
        var minPrice = 20000;
        var maxPrice = 24000;
        var condition = ItemCommand.SearchCondition.builder()
                .name(itemName)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        var pageRequest = PageRequest.of(0, 5);

        //when
        var result = itemService.items(condition, pageRequest);

        //then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent().get(0).getItemId()).isEqualTo(7L);
    }
}
