package jpabook.jpastore.domain.member;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.domain.membership.Grade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static jpabook.jpastore.domain.member.QMember.member;
import static jpabook.jpastore.domain.membership.QMembership.membership;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findMemberById(Long memberId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(member)
                        .where(eqMemberId(memberId),
                                notDeleted())
                        .fetchOne()
        );
    }

    @Override
    public Optional<Member> findMemberByOAuthId(String oauthId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .where(eqOAuthId(oauthId),
                        notDeleted())
                .fetchOne()
        );
    }

    @Override
    public boolean existsMemberByEmail(String email) {
        var exist
                = queryFactory.selectOne()
                .from(member)
                .where(eqEmail(email), notDeleted())
                .fetchFirst();

        return exist != null;
    }

    @Override
    public Optional<Member> findMemberWithMembership(Long memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(eqMemberId(memberId), notDeleted())
                .fetchOne());
    }

    @Override
    public Optional<Member> findMemberWithMembership(Long memberId, String username) {
        return Optional.ofNullable(queryFactory
                .selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(eqMemberId(memberId),
                        eqUsername(username),
                        notDeleted())
                .fetchOne());
    }

    @Override
    public List<Member> findAllWithMembership(String username, Grade grade) {
        return queryFactory.selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(containsUsername(username),
                        eqGrade(grade),
                        notDeleted())
                .fetch();
    }

    @Override
    public Page<Member> findAllWithMembership(String username, Grade grade, Pageable pageable) {

        List<Member> content = queryFactory.selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(containsUsername(username),
                        eqGrade(grade),
                        notDeleted())
                .orderBy(getSort(pageable))
                .fetch();

        JPAQuery<Member> count = queryFactory.selectFrom(member)
                .join(member.membership, membership)
                .where(containsUsername(username),
                        eqGrade(grade));

        return PageableExecutionUtils.getPage(content, pageable, () -> count.fetch().size());
    }

    @Override
    public List<Member> findAllWithMembership() {
        return queryFactory
                .selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(notDeleted())
                .fetch();
    }

    @Override
    public Page<Member> findAllWithMembership(Pageable pageable) {
        List<Member> content = queryFactory.selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(notDeleted())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSort(pageable))
                .fetch();

        JPAQuery<Member> count = queryFactory.selectFrom(member)
                .join(member.membership, membership)
                .where(notDeleted());

        return PageableExecutionUtils.getPage(content, pageable, () -> count.fetch().size());
    }

    private BooleanExpression notDeleted() {
        return member.isDeleted.eq(false);
    }

    private BooleanExpression containsUsername(String username) {
        return StringUtils.hasText(username) ? member.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression eqEmail(String email) {
        return StringUtils.hasText(email) ? member.email.eq(email) : null;
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return Objects.nonNull(memberId) ? member.id.eq(memberId) : null;
    }

    private BooleanExpression eqUsername(String username) {
        return Objects.nonNull(username) ? member.username.equalsIgnoreCase(username) : null;
    }

    private static BooleanExpression eqOAuthId(String oauthId) {
        return StringUtils.hasText(oauthId) ? member.oAuthInfo.oauthId.eq(oauthId) : null;
    }

    private BooleanExpression eqGrade(Grade grade) {
        return Objects.nonNull(grade) ? membership.grade.eq(grade) : null;
    }

    // 정렬 조건 구하는 메서드
    private OrderSpecifier<?> getSort(Pageable pageable) {
        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!pageable.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : pageable.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()) {
                    case "id":
                        return new OrderSpecifier<>(direction, member.id);
                    case "username":
                        return new OrderSpecifier<>(direction, member.username);
                    case "phoneNumber":
                        return new OrderSpecifier<>(direction, member.phoneNumber);
                    case "email":
                        return new OrderSpecifier<>(direction, member.email);
                    case "createdDate":
                        return new OrderSpecifier<>(direction, member.createdDate);
                }
            }
        }
        return null;
    }
}
