package jpabook.jpastore.domain.member;

import jpabook.jpastore.domain.membership.Grade;

import java.util.List;

public interface MemberRepositoryCustom {
    Member findMemberWithMembership(Long memberId);

    Member findMemberWithMembership(String memberName);

    List<Member> findAllWithMembership();

    List<Member> findAllByGrade(Grade grade);
}
