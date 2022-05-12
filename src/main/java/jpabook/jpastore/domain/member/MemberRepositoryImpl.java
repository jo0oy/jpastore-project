package jpabook.jpastore.domain.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpastore.domain.membership.Grade;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static jpabook.jpastore.domain.member.QMember.member;
import static jpabook.jpastore.domain.membership.QMembership.membership;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Member findMemberWithMembership(Long memberId) {
        return jpaQueryFactory
                .selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public Member findMemberWithMembership(String memberName) {
        return jpaQueryFactory
                .selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(member.name.eq(memberName))
                .fetchOne();
    }

    @Override
    public List<Member> findAllWithMembership() {
        return jpaQueryFactory
                .selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .fetch();
    }

    @Override
    public List<Member> findAllByGrade(Grade grade) {
        return jpaQueryFactory
                .selectFrom(member)
                .join(member.membership, membership).fetchJoin()
                .where(member.membership.grade.eq(grade))
                .fetch();
    }
}
