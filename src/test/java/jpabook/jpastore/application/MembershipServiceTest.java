package jpabook.jpastore.application;

import jpabook.jpastore.application.membership.MembershipServiceImpl;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import jpabook.jpastore.domain.membership.MembershipRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class MembershipServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipServiceImpl membershipService;

    @BeforeEach
    void init() {
        memberRepository.deleteAll();

        Member member1 = Member.builder()
                .name("member1")
                .address(new Address("서울시", "송파구", "1111"))
                .membership(Membership.builder()
                        .grade(Grade.SILVER)
                        .totalSpending(new Money(150000)).build())
                .build();

        Member member2 = Member.builder()
                .name("member2")
                .address(new Address("서울시", "송파구", "2222"))
                .membership(Membership.builder()
                        .grade(Grade.SILVER)
                        .totalSpending(new Money(200000)).build())
                .build();

        Member member3 = Member.builder()
                .name("member3")
                .address(new Address("서울시", "강동구", "3333"))
                .membership(Membership.builder()
                        .grade(Grade.GOLD)
                        .totalSpending(new Money(100000))
                        .build())
                .build();

        Member member4 = Member.builder()
                .name("member4")
                .address(new Address("서울시", "강남구", "4444"))
                .membership(Membership.builder()
                        .grade(Grade.GOLD)
                        .totalSpending(new Money(350000))
                        .build())
                .build();

        Member member5 = Member.builder()
                .name("member5")
                .address(new Address("경기도", "분당구", "5555"))
                .membership(Membership.builder()
                        .grade(Grade.VIP)
                        .totalSpending(new Money(160000))
                        .build())
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

    }

    @Transactional
    @Test
    @DisplayName("1. 멤버십 업데이트 테이스")
    public void update_membership_test() {

        //when
        //log.info("memberships before updating...");
        List<Membership> before_update = membershipRepository.findAll();

        //log.info("start updating memberships...");
        membershipService.updateMembershipsByDirtyChecking();

        //then
        //log.info("updated memberships....");
        List<Membership> after_update = membershipRepository.findAll();

        System.out.println("Before assertions....");
        assertThat(after_update.get(0).getGrade()).isEqualTo(before_update.get(0).getGrade());
        assertThat(after_update.get(1).getGrade()).isEqualTo(Grade.GOLD);
        assertThat(after_update.get(2).getGrade()).isEqualTo(Grade.SILVER);
        assertThat(after_update.get(2).getTotalSpending().getValue()).isEqualTo(0);
    }

}