package jpabook.jpastore.application.review;

import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.common.utils.PageRequestUtils;
import jpabook.jpastore.domain.item.ItemRepository;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.review.Review;
import jpabook.jpastore.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public Long registerReview(ReviewCommand.RegisterReq command) {
        log.info("register review 로직 실행...");

        // 1. Member 엔티티 조회
        var member = memberRepository.findMemberById(command.getMemberId())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회원입니다. memberId={}", command.getMemberId());
                    throw new EntityNotFoundException("존재하지 않는 회원입니다. memberId=" + command.getMemberId());
                });

        // 2. Item 엔티티 조회
        var item = itemRepository.findItemById(command.getItemId())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 상품입니다. id={}", command.getItemId());
                    throw new EntityNotFoundException("존재하지 않는 상품입니다. id=" + command.getItemId());
                });

        // 3. 등록할 Review 엔티티 생성
        var review = Review.createReview(command.getReviewBody(), member, item);

        return reviewRepository.save(review).getId();
    }

    @Override
    public ReviewInfo.MainInfo getReviewInfo(Long id) {
        var review = reviewRepository.findByIdFetchJoin(id)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 리뷰입니다. id={}", id);
                    throw new EntityNotFoundException("존재하지 않는 리뷰입니다. id=" + id);
                });
        return new ReviewInfo.MainInfo(review);
    }

    @Override
    public List<ReviewInfo.MainInfo> listReview(ReviewCommand.SearchCondition condition) {
        return reviewRepository.findAllByCondition(condition.toSearchCondition())
                .stream()
                .map(ReviewInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReviewInfo.MainInfo> listReview(ReviewCommand.SearchCondition condition, Pageable pageable) {
        return reviewRepository.findAllByCondition(condition.toSearchCondition(), PageRequestUtils.of(pageable))
                .map(ReviewInfo.MainInfo::new);
    }

    @Override
    @Transactional
    public void updateReview(Long id, ReviewCommand.UpdateReq command, String authUsername) {
        log.info("updating review... id : {}", id);
        var review = reviewRepository.findReviewById(id).orElseThrow(() -> {
            log.error("존재하지 않는 리뷰입니다. id={}", id);
            throw new EntityNotFoundException("존재하지 않는 리뷰입니다. id=" + id);
        });

        checkAuthorityToUpdate(review, authUsername); // 수정 권한 체크: 작성자 '본인'만 가능

        review.updateReviewBody(command.getReviewBody());
    }

    @Override
    @Transactional
    public void deleteReview(Long id, String authUsername) {
        log.info("deleting review... id : {}", id);
        var review = reviewRepository.findReviewById(id).orElseThrow(() -> {
            log.error("존재하지 않는 리뷰입니다. id={}", id);
            throw new EntityNotFoundException("존재하지 않는 리뷰입니다. id=" + id);
        });

        checkAuthorityToDelete(review, authUsername); // 삭제 권한 체크: 작성자 '본인' 혹은 관리자 계정 가능

        review.delete();
    }

    void checkAuthorityToUpdate(Review review, String username) {
        log.info("checking authority to update review...");
        var member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회원입니다. username={}", username);
                    throw new EntityNotFoundException("존재하지 않는 회원입니다. username=" + username);
                });

        if (!review.hasAuthorityToUpdate(member)) {
            log.error("리뷰를 수정할 권한이 없습니다. reviewId={}, username={}", review.getId(), username);
            throw new AccessDeniedException("리뷰에 대한 수정 권한이 없습니다.");
        }
    }

    void checkAuthorityToDelete(Review review, String username) {
        log.info("checking authority to delete review...");
        var member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회원입니다. username={}", username);
                    throw new EntityNotFoundException("존재하지 않는 회원입니다. username=" + username);
                });

        if (!review.hasAuthorityToDelete(member)) {
            log.error("리뷰를 삭제할 권한이 없습니다. reviewId={}, username={}", review.getId(), username);
            throw new AccessDeniedException("리뷰에 대한 삭제 권한이 없습니다.");
        }
    }
}
