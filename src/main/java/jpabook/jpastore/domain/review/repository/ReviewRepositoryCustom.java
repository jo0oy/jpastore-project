package jpabook.jpastore.domain.review.repository;

import jpabook.jpastore.domain.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryCustom {

    // 리뷰 조회
    Optional<Review> findReviewById(Long reviewId);

    Optional<Review> findByIdFetchJoin(Long reviewId);

    Page<Review> findAllWithMemberAndItem(Pageable pageable);

    // 리뷰 리스트 조회 by 작성자
    List<Review> findAllByMember(Long memberId);

    List<Review> findAllByMember(String username);

    Page<Review> findAllByMember(Long memberId, String username, Pageable pageable);

    // 리뷰 리스트 조회 by 아이템
    List<Review> findAllByItem(Long itemId);

    Page<Review> findAllByItem(Long itemId, Pageable pageable);


    // 리뷰 리스트 조회 by 작성자 && 아이템
    List<Review> findAllByCondition(ReviewSearchCondition condition);

    Page<Review> findAllByCondition(ReviewSearchCondition condition, Pageable pageable);
}
