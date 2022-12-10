package jpabook.jpastore.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    @Query("select m from Member m where m.username = :username and m.isDeleted = false")
    Optional<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.email = :email and m.isDeleted = false")
    Optional<Member> findByEmail(@Param("email") String email);
}
