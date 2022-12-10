package jpabook.jpastore.application.category;

import jpabook.jpastore.common.exception.CannotDeleteException;
import jpabook.jpastore.common.exception.DuplicateNameException;
import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.domain.category.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("카테고리 서비스 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUp() {
        databaseCleanUp.cleanUp();
    }

    @DisplayName("[성공][service] 카테고리 등록")
    @Test
    public void givenRegisterReq_whenRegisterCategory_thenReturnRegisteredId () {
        //given
        var name = "해외DVD";
        var parentId = 3L;

        var command = CategoryCommand.RegisterReq.builder()
                .name(name)
                .parentId(parentId)
                .build();

        //when
        var categoryId = categoryService.registerCategory(command);
        var findCategory = categoryRepository.findCategoryById(categoryId);

        //then
        assertThat(findCategory).isPresent();
        assertThat(findCategory.get().getId()).isEqualTo(categoryId);
        assertThat(findCategory.get().getName()).isEqualTo(name);
        assertThat(findCategory.get().getParent().getId()).isEqualTo(parentId);
    }

    @DisplayName("[성공][service] 카테고리 단일 조회 by id")
    @Test
    public void givenCategoryId_whenGetCategory_thenReturnCategoryInfo () {
        //given
        var categoryId = 1L;

        //when
        var categoryInfo = categoryService.getCategory(categoryId);

        //then
        assertThat(categoryInfo.getCategoryId()).isEqualTo(categoryId);
        assertThat(categoryInfo.getName()).isEqualTo("도서");
        assertThat(categoryInfo.getParent()).isNull();
        assertThat(categoryInfo.getChildList().size()).isEqualTo(2);
    }

    @DisplayName("[실패][service] 카테고리 단일 조회 by id")
    @Test
    public void givenNonExistCategoryId_whenGetCategory_thenThrowEntityNotFoundException() {
        // given
        var categoryId = 100L;

        // when & then
        assertThatThrownBy(() -> categoryService.getCategory(categoryId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 카테고리입니다. id = " + categoryId);
    }

    @DisplayName("[성공][service] 카테고리-상품리스트 조회")
    @Test
    public void givenCategoryId_whenGetCategoryWithItems_thenReturnDetailWithItemInfo() {
        // given
        var categoryId = 6L;

        // when
        var resultInfo = categoryService.getCategoryWithItems(categoryId);

        // then
        assertThat(resultInfo.getCategoryId()).isEqualTo(categoryId);
        assertThat(resultInfo.getName()).isEqualTo("국내소설");
        assertThat(resultInfo.getParent()).isNotNull();
        assertThat(resultInfo.getParent().getCategoryId()).isEqualTo(4L);
        assertThat(resultInfo.getCategoryItems().size()).isEqualTo(2);
    }

    @DisplayName("[성공][service] 카테고리-상품리스트 부모없는 경우 조회")
    @Test
    public void givenRootCategoryId_whenGetCategoryWithItems_thenReturnDetailWithItemInfo() {
        // given
        var categoryId = 1L;

        // when
        var resultInfo = categoryService.getCategoryWithItems(categoryId);

        // then
        assertThat(resultInfo.getCategoryId()).isEqualTo(categoryId);
        assertThat(resultInfo.getName()).isEqualTo("도서");
        assertThat(resultInfo.getParent()).isNull();
        assertThat(resultInfo.getCategoryItems().size()).isEqualTo(0);
    }

    @DisplayName("[성공][service] 전체 카테고리 계층 리스트 조회")
    @Test
    public void givenNothing_whenCategoryHierarchicalList_thenReturnHierarchicalList() {

        // when
        var list = categoryService.categoryHierarchicalList();
        var firstCategory = list.get(0);
        var firstChildList = firstCategory.getChildList();
        var grandChildList = firstChildList.get(0).getChildList();
        var grandGrandChildList = grandChildList.get(0).getChildList();

        // then
        assertThat(list.size()).isEqualTo(3);
        assertThat(firstCategory.getCategoryId()).isEqualTo(1L);
        assertThat(firstChildList.size()).isEqualTo(2);
        assertThat(grandChildList.size()).isEqualTo(2);
        assertThat(grandGrandChildList.size()).isEqualTo(0);
    }

    @DisplayName("[성공][service] 카테고리 상품 리스트 조회 by categoryId")
    @Test
    public void givenCategoryId_whenCategoryItemListByCategoryId_thenReturnCategoryItemList() {
        // given
        var categoryId = 12L;

        // when
        var list = categoryService.categoryItemListByCategoryId(categoryId);

        // then
        assertThat(list.getTotalItemsCount()).isEqualTo(2);
        assertThat(list.getName()).isEqualTo("KPOP");
        assertThat(list.getCategoryItems().get(0).getItemId()).isEqualTo(4L);
    }

    @DisplayName("[성공][service] 카테고리 정보 수정")
    @Test
    public void givenUpdateReq_whenUpdate_thenUpdateCategoryInfo() {
        // given
        var categoryId = 12L;
        var updateName = "국내POP";
        var updateParentId = 1L;

        var command = CategoryCommand.UpdateInfoReq.builder()
                .name(updateName)
                .parentId(updateParentId)
                .build();

        // when
        categoryService.update(categoryId, command);

        // then
        var updated = categoryRepository.findCategoryById(categoryId).orElse(null);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo(updateName);
        assertThat(updated.getParent().getId()).isEqualTo(updateParentId);
    }

    @DisplayName("[성공][service] 카테고리 이름만 부분 수정")
    @Test
    public void givenUpdateReqWithPartialInfo_whenUpdate_thenUpdateCategoryInfo() {
        // given
        var categoryId = 12L;
        var updateName = "국내POP";

        var command = CategoryCommand.UpdateInfoReq.builder()
                .name(updateName)
                .build();

        // when
        categoryService.update(categoryId, command);

        // then
        var updated = categoryRepository.findCategoryById(categoryId).orElse(null);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo(updateName);
        assertThat(updated.getParent().getId()).isEqualTo(9L);
    }

    @DisplayName("[성공][service] 카테고리 부모 정보만 부분 수정")
    @Test
    public void givenUpdateReqToUpdateParentIdNull_whenUpdate_thenUpdateParentInfo() {
        // given
        Long categoryId = 12L;

        var command = CategoryCommand.UpdateInfoReq.builder().build();

        // when
        categoryService.update(categoryId, command);

        // then
        var updated = categoryRepository.findCategoryById(categoryId).orElse(null);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("KPOP");
        assertThat(updated.getParent().getId()).isEqualTo(9L);
    }

    @DisplayName("[성공][service] 수정내용 null 일때 미수정")
    @Test
    public void givenNullUpdateReq_whenUpdate_thenNotUpdateCategoryInfo() {
        // given
        Long categoryId = 12L;

        var command = CategoryCommand.UpdateInfoReq.builder().build();

        // when
        categoryService.update(categoryId, command);

        // then
        var updated = categoryRepository.findCategoryById(categoryId).get();

        assertThat(updated.getName()).isEqualTo("KPOP");
        assertThat(updated.getParent().getId()).isEqualTo(9L);
    }

    @DisplayName("[실패][service] 중복된 이름으로 카테고리 정보 수정 요청")
    @Test
    public void givenUpdateReqWithDuplicateName_whenUpdate_thenThrowDuplicateNameException() {
        // given
        Long categoryId = 12L;
        var updateParentId = 0L; // root 카테고리로 변경.
        var updateName = "도서";

        var command = CategoryCommand.UpdateInfoReq.builder()
                .parentId(updateParentId)
                .name(updateName)
                .build();

        // when & then
        assertThatThrownBy(() -> categoryService.update(categoryId, command))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessage("중복된 카테고리 이름입니다. name=" + updateName);
    }

    @Test
    @DisplayName("[성공][service] 카테고리 삭제")
    public void givenCategoryId_whenDelete_thenDeleteCategory() {
        //given
        var categoryId = 12L;

        //when
        categoryService.delete(categoryId);

        //then
        var findCategory = categoryRepository.findCategoryById(categoryId).orElse(null);

        assertThat(findCategory).isNull();
    }

    @Test
    @DisplayName("[실패][service] 존재하지 않는 카테고리 삭제")
    public void givenNonExistCategoryId_whenDelete_thenThrowEntityNotFoundException() {
        // given
        var categoryId = 20L;

        // when & then
        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("[실패][service] 자식이 존재하는 카테고리 삭제")
    public void givenCategoryIdThatHasChildList_whenDelete_thenThrowCannotDeleteException() {
        // given
        var categoryId = 1L;

        // when & then
        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(CannotDeleteException.class)
                .hasMessage("삭제할 수 없는 카테고리 입니다. 자식 카테고리가 존재합니다. id = " + categoryId);
    }
}
