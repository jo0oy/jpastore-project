package jpabook.jpastore.domain.member.queryRepo;

import jpabook.jpastore.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class MemberQueryRepository {

    private final EntityManager em;

    public Member findMemberById(Long memberId) {
        return em.createQuery("select m from Member m" +
                " join fetch m.membership" +
                " where m.id = :memberId", Member.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    public Member findMemberByName(String memberName) {
        return em.createQuery("select m from Member m" +
                " join fetch m.membership" +
                " where m.name = :memberName", Member.class)
                .setParameter("memberName", memberName)
                .getSingleResult();
    }

    public List<Member> findMembersWithMembership() {
        return em.createQuery("select m from Member m" +
                " join fetch m.membership", Member.class)
                .getResultList();
    }
}
