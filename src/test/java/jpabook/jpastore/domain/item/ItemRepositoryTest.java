package jpabook.jpastore.domain.item;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.config.TestQuerydslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import({TestQuerydslConfig.class, TestDBConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.cleanUp();
    }

    @Test
    @DisplayName("[성공][repo] Item 엔티티 단일 조회 by Id")
    public void givenItemId_whenFindItemById_thenReturnsItemEntity() {
        //given
        var itemId = 1L;

        //when
        var itemEntity = itemRepository.findItemById(itemId);

        //then
        assertThat(itemEntity).isPresent();
        assertThat(itemEntity.get().getName()).isEqualTo("book1");
        assertThat(itemEntity.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("[성공][repo] Item 엔티티 전체 조회: 페이징/정렬")
    public void givenPageRequest_whenFindAll_thenReturnsAllItemsPagingResult() {
        //given
        var pageRequest = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        var condition = ItemSearchCondition.builder().build(); // 검색 조건 없음

        //when
        var items = itemRepository.findAll(condition, pageRequest);

        //then
        assertThat(items.getTotalElements()).isEqualTo(9);
        assertThat(items.getTotalPages()).isEqualTo(2);
        assertThat(items.getContent().get(0).getId()).isEqualTo(9L);
        assertThat(items.getContent().get(0).getName()).isEqualTo("movie2");
    }
}
