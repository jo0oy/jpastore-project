package jpabook.jpastore.application.review;

import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.config.AopConfig;
import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("리뷰 서비스 테스트")
@Sql(
        scripts = "classpath:data/data-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import({AopConfig.class, TestDBConfig.class})
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUp() {
        databaseCleanUp.cleanUp();
    }


    @Test
    @DisplayName("[성공][service] 새 리뷰 등록")
    public void givenRegisterReq_whenRegisterReview_thenReturnRegisteredReviewId() {
        //given
        var reviewBody = "리뷰글 입니다.";
        var memberId = 2L;
        var itemId = 1L;

        var command = ReviewCommand.RegisterReq
                .builder()
                .itemId(itemId)
                .reviewBody(reviewBody)
                .memberId(memberId)
                .build();

        var registerReview = reviewService.registerReview(command);

        //when
        var findReview = reviewRepository.findByIdFetchJoin(registerReview);

        //then
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getReviewBody()).isEqualTo(reviewBody);
        assertThat(findReview.get().getMember().getId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("[성공][service] 리뷰 수정")
    public void givenReviewIdAndUpdateReqAndAuthUsername_whenUpdateReview_thenReturnUpdateReview () {
        //given
        var reviewId = 2L;
        var updateReviewBody = "수정한 리뷰글 입니다.";
        var authUsername = "member2";

        var command = ReviewCommand.UpdateReq
                .builder()
                .reviewBody(updateReviewBody)
                .build();

        //when
        reviewService.updateReview(reviewId, command, authUsername);

        //then
        var findReview = reviewRepository.findById(reviewId);
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getReviewBody()).isEqualTo(updateReviewBody);
    }

    @Test
    @DisplayName("[실패][service] 수정 권한 없는 사용자의 리뷰 수정 요청")
    public void givenReviewIdAndUpdateReqAndForbiddenUsername_whenUpdateReview_thenThrowAccessDeniedException() {
        //given
        var reviewId = 2L;
        var updateReviewBody = "수정한 리뷰글 입니다.";
        var authUsername = "member1";

        var command = ReviewCommand.UpdateReq
                .builder()
                .reviewBody(updateReviewBody)
                .build();

        //when & then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, command, authUsername))
                .isInstanceOf(AccessDeniedException.class);

        var findReview = reviewRepository.findById(reviewId);
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getReviewBody()).isNotEqualTo(updateReviewBody);
    }

    @Test
    @DisplayName("[성공][service] 리뷰 삭제 - 본인 요청")
    public void givenReviewIdAndAuthUsername_whenDeleteReview_thenDeleteReview () {
        //given
        var reviewId = 18L;
        var authUsername = "member1";

        //when
        reviewService.deleteReview(reviewId, authUsername);

        //then
        var findReview = reviewRepository.findReviewById(reviewId);
        assertThat(findReview).isNotPresent();
    }

    @Test
    @DisplayName("[성공][service] 리뷰 삭제 - 관리자가 삭제")
    public void givenReviewIdAndAdminUsername_whenDeleteReview_thenDeleteReview () {
        //given
        var reviewId = 5L;
        var authUsername = "admin";

        //when
        reviewService.deleteReview(reviewId, authUsername);

        //then
        var findReview = reviewRepository.findReviewById(reviewId);
        assertThat(findReview).isNotPresent();
    }

    @Test
    @DisplayName("[실패][service] 삭제 권한 없는 사용자의 리뷰 삭제 요청")
    public void givenReviewIdAndForbiddenUsername_whenDeleteReview_thenThrowAccessDeniedException () {
        //given
        var reviewId = 4L; // written by 'member2'
        var authUsername = "member1";

        //when & then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewId, authUsername))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("리뷰에 대한 삭제 권한이 없습니다.");

        var findReview = reviewRepository.findReviewById(reviewId);
        assertThat(findReview).isPresent();
        assertThat(findReview.get().isDeleted()).isFalse();
    }

    @Test
    @DisplayName("[성공][service] 단일 리뷰 조회")
    public void givenReviewId_whenGetReviewInfo_thenReturnReviewInfo () {
        //given
        var reviewId = 1L;

        //when
        var reviewInfo = reviewService.getReviewInfo(reviewId);

        //then
        assertThat(reviewInfo.getMemberInfo().getMemberId()).isEqualTo(1L);
        assertThat(reviewInfo.getItemInfo().getItemId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("[실패][service] 존재하지 않는 리뷰 조회")
    public void givenNonExistReviewId_whenGetReviewInfo_thenThrowEntityNotFoundException () {
        //given
        var reviewId = 100L;

        //when & then
        assertThatThrownBy(() -> reviewService.getReviewInfo(reviewId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("[성공][service] 전체 리뷰 리스트 페이징 검색 조회 - 검색조건, 페이징/정렬")
    public void givenSearchConditionAndPageRequest_whenListReview_thenReturnPagedReviewResult () {
        //given
        var itemId = 1L;
        var username = "member";
        var condition = ReviewCommand.SearchCondition.builder()
                .itemId(itemId)
                .username(username)
                .build();

        var pageRequest = PageRequest.of(1, 3, Sort.Direction.DESC, "id");

        //when
        var reviews = reviewService.listReview(condition, pageRequest);

        //then
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        assertThat(reviews.getTotalPages()).isEqualTo(1);
        assertThat(reviews.getSize()).isEqualTo(3);
        assertThat(reviews.getContent().get(0).getReviewId())
                .isGreaterThan(reviews.getContent().get(1).getReviewId());
    }

    @Test
    @DisplayName("[성공][service] 전체 리뷰 리스트 페이징 조회 - 페이징/정렬")
    public void givenPageRequest_whenListReview_thenReturnPagedReviewResult () {
        //given
        var size = 5;
        var pageRequest = PageRequest.of(1, 5, Sort.Direction.DESC, "id");

        //when
        var reviews = reviewService.listReview(ReviewCommand.SearchCondition.builder().build(), pageRequest);

        //then
        assertThat(reviews.getTotalElements()).isEqualTo(19);
        assertThat(reviews.getTotalPages()).isEqualTo(4);
        assertThat(reviews.getSize()).isEqualTo(size);
        assertThat(reviews.getContent().get(0).getReviewId())
                .isGreaterThan(reviews.getContent().get(1).getReviewId());
    }
}
