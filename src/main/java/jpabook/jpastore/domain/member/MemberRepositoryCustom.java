package jpabook.jpastore.domain.member;

import jpabook.jpastore.domain.membership.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findMemberById(Long memberId);

    Optional<Member> findMemberByOAuthId(String oauthId);

    boolean existsMemberByEmail(String email);

    Optional<Member> findMemberWithMembership(Long memberId);

    Optional<Member> findMemberWithMembership(Long memberId, String username);

    List<Member> findAllWithMembership(String username, Grade grade);

    Page<Member> findAllWithMembership(String username, Grade grade, Pageable pageable);

    List<Member> findAllWithMembership();

    Page<Member> findAllWithMembership(Pageable pageable);
}
