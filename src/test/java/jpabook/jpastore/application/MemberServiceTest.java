package jpabook.jpastore.application;

import jpabook.jpastore.application.dto.member.MemberListResponseDto;
import jpabook.jpastore.application.dto.member.MemberResponseDto;
import jpabook.jpastore.application.member.MemberService;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import jpabook.jpastore.domain.membership.MembershipRepository;
import jpabook.jpastore.dto.member.MemberSaveRequestDto;
import jpabook.jpastore.dto.member.MemberUpdateRequestDto;
import jpabook.jpastore.exception.DuplicateUsernameException;
import jpabook.jpastore.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    @DisplayName("request dto 회원가입 테스트")
    public void 회원가입_테스트(){
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .name("kim")
                .build();

        // when
        Long savedId = memberService.join(requestDto);
        Member member = memberRepository.findById(savedId).orElse(null);

        // then
        assertThat(savedId).isEqualTo(1L);
        assertThat(member.getName()).isNotNull().isEqualTo("kim");
    }

    @Test
    @DisplayName("중복 이름 예외 테스트")
    public void 중복_이름_예외_테스트(){
        // given
        MemberSaveRequestDto requestDto1 = MemberSaveRequestDto.builder()
                .name("kim")
                .build();

        MemberSaveRequestDto requestDto2 = MemberSaveRequestDto.builder()
                .name("kim")
                .build();

        // when
        Long savedId1 = memberService.join(requestDto1);

        Exception exception = assertThrows(DuplicateUsernameException.class,
                () -> {memberService.join(requestDto2);});

        // then
        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 회원 이름입니다. name : kim");
    }

    @Test
    @DisplayName("전체 회원 리스트 조회")
    public void listMembers_테스트() {
        //given
        for (int i = 1; i <= 5; i++) {
            memberRepository.save(Member.builder()
                    .name("member" + i)
                    .phoneNumber(String.format("010-000%d-00%d%d", i, i, i))
                    .address(new Address("서울시", "송파구", String.format("%d%d%d%d", i, i, i, i)))
                    .membership(Membership.builder()
                            .grade(Grade.SILVER)
                            .totalSpending(new Money(0))
                            .build())
                    .build());
        }


        //when
        MemberListResponseDto result = memberService.listMembers();

        //then
        assertThat(result.getTotalCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("회원 리스트 조회 By 멤버십 등급 테스트")
    public void listMembers_ByGrade_테스트() {
        //given
        for (int i = 1; i <= 3; i++) {
            memberRepository.save(Member.builder()
                    .name("member" + i)
                    .phoneNumber(String.format("010-000%d-00%d%d", i, i, i))
                    .address(new Address("서울시", "송파구", String.format("%d%d%d%d", i, i, i, i)))
                    .membership(Membership.builder()
                            .grade(Grade.SILVER)
                            .totalSpending(new Money(0))
                            .build())
                    .build());
        }

        for (int i = 4; i <= 7; i++) {
            memberRepository.save(Member.builder()
                    .name("member" + i)
                    .phoneNumber(String.format("010-000%d-00%d%d", i, i, i))
                    .address(new Address("경기도", "분당구", String.format("%d%d%d%d", i, i, i, i)))
                    .membership(Membership.builder()
                            .grade(Grade.GOLD)
                            .totalSpending(new Money(200_000))
                            .build())
                    .build());
        }

        //when
        MemberListResponseDto resultBySliver = memberService.listMembers(Grade.SILVER);
        MemberListResponseDto resultByGold = memberService.listMembers(Grade.GOLD);
        MemberListResponseDto resultByVIP = memberService.listMembers(Grade.VIP);

        //then
        assertThat(resultBySliver.getTotalCount()).isEqualTo(3);
        assertThat(resultByGold.getTotalCount()).isEqualTo(4);
        assertThat(resultByVIP.getTotalCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("단일 회원 조회 By Id 실패 테스트")
    public void getMember_byId_실패_테스트() {
        //given
        Long savedId = saveMember("member1");

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.getMember(savedId + 1));
    }

    @Test
    @DisplayName("단일 회원 조회 By Id 성공 테스트")
    public void getMember_byId_성공_테스트() {
        //given
        String name = "member1";
        Long savedId = saveMember(name);

        //when
        MemberResponseDto findMember = memberService.getMember(savedId);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getId()).isNotZero();
        assertThat(findMember.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("단일 회원 조회 By Name 실패 테스트")
    public void getMember_byName_실패_테스트() {
        //given
        Long savedId = saveMember("member1");

        //then
        assertThrows(MemberNotFoundException.class, () -> memberService.getMember("member2"));
    }

    @Test
    @DisplayName("단일 회원 조회 By Name 성공 테스트")
    public void getMember_byName_성공_테스트() {
        //given
        String name = "member1";
        Long savedId = saveMember(name);

        //when
        MemberResponseDto findMember = memberService.getMember(name);

        //then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getId()).isNotZero();
        assertThat(findMember.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("회원정보 부분 수정 성공 테스트")
    public void updateMember_부분_수정_성공_테스트() {
        //given
        String name = "member1";
        Long savedId = saveMember(name);

        String updateCity = "경기도";
        String updateStreet = "분당구";
        String updateZipcode = "2222";

        MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto(null, updateCity,
                updateStreet, updateZipcode);

        //when
        memberService.updateMemberInfo(savedId, requestDto);

        Member updatedMember = memberRepository.findById(savedId).orElse(null);
        System.out.println(updatedMember);

        //then
        assertThat(updatedMember).isNotNull();
        assertThat(updatedMember.getId()).isEqualTo(savedId);
        assertThat(updatedMember.getName()).isEqualTo(name);
        assertThat(updatedMember.getPhoneNumber()).isEqualTo("010-1111-1111");
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(updateCity);
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(updateStreet);
        assertThat(updatedMember.getAddress().getZipcode()).isEqualTo(updateZipcode);
    }

    @Test
    @DisplayName("회원정보 전체 수정 성공 테스트")
    public void updateMember_전체_수정_성공_테스트() {
        //given
        String name = "member1";
        Long savedId = saveMember(name);

        String updatePhoneNumber = "010-1111-1112";
        String updateCity = "경기도";
        String updateStreet = "분당구";
        String updateZipcode = "2222";

        MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto(updatePhoneNumber, updateCity,
                updateStreet, updateZipcode);

        //when
        memberService.updateMemberInfo(savedId, requestDto);

        Member updatedMember = memberRepository.findById(savedId).orElse(null);

        //then
        assertThat(updatedMember).isNotNull();
        assertThat(updatedMember.getId()).isEqualTo(savedId);
        assertThat(updatedMember.getName()).isEqualTo(name);
        assertThat(updatedMember.getPhoneNumber()).isEqualTo(updatePhoneNumber);
        assertThat(updatedMember.getAddress().getCity()).isEqualTo(updateCity);
        assertThat(updatedMember.getAddress().getStreet()).isEqualTo(updateStreet);
        assertThat(updatedMember.getAddress().getZipcode()).isEqualTo(updateZipcode);
    }

    @Test
    @DisplayName("member-membership cascase all 작동 테스트")
    public void cascade_옵션_정상작동_테스트() {
        //given
        Long savedId = saveMember("member1");

        //when
        Member member = memberRepository.findById(savedId).get();
        memberRepository.delete(member);

        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
        assertThat(membershipRepository.findAll().size()).isEqualTo(0);
    }

    private Long saveMember(String name) {
        return memberRepository.save(Member.builder()
                .name(name)
                .phoneNumber("010-1111-1111")
                .address(new Address("서울시", "송파구", "1111"))
                .membership(Membership.builder()
                        .grade(Grade.SILVER)
                        .totalSpending(new Money(0))
                        .build())
                .build()).getId();
    }

}