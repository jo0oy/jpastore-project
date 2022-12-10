package jpabook.jpastore.application.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    // 리뷰 등록
    Long registerReview(ReviewCommand.RegisterReq command);

    // 리뷰 조회 by id
    ReviewInfo.MainInfo getReviewInfo(Long reviewId);

    // 리뷰 리스트 조회 by 검색 조건
    List<ReviewInfo.MainInfo> listReview(ReviewCommand.SearchCondition condition);

    // 리뷰 리스트 페이징 조회 by 검색 조건
    Page<ReviewInfo.MainInfo> listReview(ReviewCommand.SearchCondition condition, Pageable pageable);

    // 리뷰 정보 수정
    void updateReview(Long id, ReviewCommand.UpdateReq command, String authUsername);

    // 리뷰 삭제
    void deleteReview(Long reviewId, String authUsername);
}
