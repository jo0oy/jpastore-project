package jpabook.jpastore.domain.review;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.config.TestQuerydslConfig;
import jpabook.jpastore.domain.item.ItemRepository;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.review.repository.ReviewRepository;
import jpabook.jpastore.domain.review.repository.ReviewSearchCondition;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Import({TestQuerydslConfig.class, TestDBConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.cleanUp();
    }

    @Test
    @DisplayName("[성공][repo] 리뷰 등록")
    public void givenSaveReqData_whenSave_thenWorksFine() {
        //given
        var item = itemRepository.findItemById(1L).orElse(null);
        var member = memberRepository.findMemberById(1L).orElse(null);
        var reviewBody = "리뷰 작성글입니다!!!";

        Review review = Review.createReview(reviewBody, member, item);

        //when
        var savedReview = reviewRepository.save(review);

        //then
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getReviewBody()).isEqualTo(reviewBody);
        assertThat(savedReview.getMember().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("[성공][repo] 리뷰 페치조인 단건 조회 by Id")
    public void givenReviewId_whenFindByIdFetchJoin_thenReturnsReviewEntity() {
        //given
        var reviewId = 5L;

        //when
        var review = reviewRepository.findByIdFetchJoin(reviewId).orElse(null);

        //then
        assertThat(review).isNotNull();
        assertThat(review.getReviewBody()).isEqualTo("리뷰5 입니다.");
        assertThat(review.getItem().getId()).isEqualTo(3L);
        assertThat(review.getMember().getId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("[실패][repo] 리뷰 페치조인 단건 조회 by Id - 존재하지 않는 리뷰")
    public void givenReviewIdThatNotExist_whenFindByIdFetchJoin_thenReturnsReviewEntity() {
        //given
        var reviewId = 20L;

        //when
        var review = reviewRepository.findByIdFetchJoin(reviewId);

        //then
        assertThat(review).isNotPresent();
    }

    @Test
    @DisplayName("[성공][repo] 리뷰 리스트 조회 - 페이징/정렬")
    public void givenPageRequest_whenFindAllWithMemberAndItem_thenWorksFine() {
        //given
        var pageRequest = PageRequest.of(0, 15, Sort.Direction.DESC, "id");

        //when
        var list = reviewRepository.findAllWithMemberAndItem(pageRequest);

        //then
        assertThat(list.getTotalElements()).isEqualTo(19);
        assertThat(list.getTotalPages()).isEqualTo(2);
        assertThat(list.getContent().get(0).getId()).isEqualTo(19L);
    }

    @Test
    @DisplayName("[성공][repo] 리뷰 리스트 조회 - 검색(memberId)")
    public void givenConditionMemberId_whenFindAllByCondition_thenWorksFine() {
        //given
        var condition = ReviewSearchCondition.builder()
                .memberId(1L)
                .build();

        //when
        var list = reviewRepository.findAllByCondition(condition);

        //then
        assertThat(list.size()).isEqualTo(7);
        assertThat(list.get(0).getMember().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("[성공][repo] 리뷰 리스트 조회 - 검색(username)")
    public void givenConditionUsername_whenFindAllByCondition_thenWorksFine() {
        //given
        var username = "member2";

        var condition = ReviewSearchCondition.builder()
                .username(username)
                .build();

        //when
        var list = reviewRepository.findAllByCondition(condition);

        //then
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0).getMember().getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("[성공][repo] 리뷰 리스트 조회 - 검색(itemId)")
    public void givenConditionItemId_whenFindAllByCondition_thenWorksFine() {
        //given
        var itemId = 6L;

        var condition = ReviewSearchCondition.builder()
                .itemId(itemId)
                .build();

        //when
        var list = reviewRepository.findAllByCondition(condition);

        //then
        assertThat(list.size()).isEqualTo(4);
        assertThat(list.get(0).getItem().getId()).isEqualTo(itemId);
    }

    @Test
    @DisplayName("[성공][repo] 리뷰 리스트 조회 - 다중 검색 조건")
    public void givenComplexCondition_whenFindAllByCondition_thenWorksFine() {
        //given
        var memberId = 2L;
        var itemId = 3L;

        var condition = ReviewSearchCondition.builder()
                .memberId(memberId)
                .itemId(itemId)
                .build();

        //when
        var list = reviewRepository.findAllByCondition(condition);

        //then
        assertThat(list).isNotEmpty();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getMember().getId()).isEqualTo(memberId);
        assertThat(list.get(0).getItem().getId()).isEqualTo(itemId);
    }
}
