package jpabook.jpastore.domain.review.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.domain.review.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static jpabook.jpastore.domain.item.QItem.item;
import static jpabook.jpastore.domain.member.QMember.member;
import static jpabook.jpastore.domain.review.QReview.review;

@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(review)
                        .where(eqReviewId(reviewId),
                                notDeleted())
                        .fetchOne()
        );
    }

    @Override
    public Optional<Review> findByIdFetchJoin(Long reviewId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(review)
                        .innerJoin(review.member, member).fetchJoin()
                        .innerJoin(review.item, item).fetchJoin()
                        .where(eqReviewId(reviewId),
                                notDeleted())
                        .fetchOne()
        );
    }

    @Override
    public Page<Review> findAllWithMemberAndItem(Pageable pageable) {
        List<Review> content
                = queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(notDeleted())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(pageSort(pageable))
                .fetch();

        JPAQuery<Review> countQuery = queryFactory
                .selectFrom(review).where(notDeleted());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public List<Review> findAllByMember(Long memberId) {
        return queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(eqMemberId(memberId),
                        notDeleted())
                .fetch();
    }

    @Override
    public List<Review> findAllByMember(String username) {
        return queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(containsIgnoreCaseUsername(username),
                        notDeleted())
                .fetch();
    }

    @Override
    public Page<Review> findAllByMember(Long memberId, String username, Pageable pageable) {

        List<Review> content
                = queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(eqMemberId(memberId),
                        containsIgnoreCaseUsername(username),
                        notDeleted())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(pageSort(pageable))
                .fetch();

        JPAQuery<Review> countQuery = queryFactory.selectFrom(review)
                .innerJoin(review.member, member)
                .where(eqMemberId(memberId),
                        containsIgnoreCaseUsername(username),
                        notDeleted());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public List<Review> findAllByItem(Long itemId) {
        return queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(eqItemId(itemId),
                        notDeleted())
                .fetch();
    }

    @Override
    public Page<Review> findAllByItem(Long itemId, Pageable pageable) {
        List<Review> content = queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(eqItemId(itemId),
                        notDeleted())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(pageSort(pageable))
                .fetch();

        JPAQuery<Review> countQuery = queryFactory.selectFrom(review)
                .innerJoin(review.item, item)
                .where(eqItemId(itemId),
                        notDeleted());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    @Override
    public List<Review> findAllByCondition(ReviewSearchCondition condition) {
        return queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(eqMemberId(condition.getMemberId()),
                        containsIgnoreCaseUsername(condition.getUsername()),
                        eqItemId(condition.getItemId()),
                        notDeleted())
                .fetch();
    }

    @Override
    public Page<Review> findAllByCondition(ReviewSearchCondition condition, Pageable pageable) {
        List<Review> content = queryFactory.selectFrom(review)
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(review.item, item).fetchJoin()
                .where(eqMemberId(condition.getMemberId()),
                        containsIgnoreCaseUsername(condition.getUsername()),
                        eqItemId(condition.getItemId()),
                        notDeleted())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(pageSort(pageable))
                .fetch();

        JPAQuery<Review> countQuery = queryFactory.selectFrom(review)
                .innerJoin(review.member, member)
                .innerJoin(review.item, item)
                .where(eqMemberId(condition.getMemberId()),
                        containsIgnoreCaseUsername(condition.getUsername()),
                        eqItemId(condition.getItemId()),
                        notDeleted());


        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression notDeleted() {
        return review.isDeleted.eq(false);
    }

    private BooleanExpression eqReviewId(Long reviewId) {
        return Objects.nonNull(reviewId) ? review.id.eq(reviewId) : null;
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return Objects.nonNull(memberId) ? member.id.eq(memberId) : null;
    }

    private BooleanExpression containsIgnoreCaseUsername(String username) {
        return Objects.nonNull(username) ? member.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression eqItemId(Long itemId) {
        return Objects.nonNull(itemId) ? item.id.eq(itemId) : null;
    }

    /**
     * OrderSpecifier 를 쿼리로 반환하여 정렬조건을 맞춰준다.
     * 리스트 정렬
     * @param  pageable
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> pageSort(Pageable pageable) {
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!pageable.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()){
                    case "id":
                        return new OrderSpecifier<>(direction, review.id);
                    case "createdDate":
                        return new OrderSpecifier<>(direction, review.createdDate);
                    case "member.username":
                        return new OrderSpecifier<>(direction, review.member.username);
                }
            }
        }
        return null;
    }
}
