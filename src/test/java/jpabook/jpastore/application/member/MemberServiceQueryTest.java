package jpabook.jpastore.application.member;

import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.membership.Grade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@DisplayName("회원 조회 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class MemberServiceQueryTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("[성공][service] 단일 회원 조회 by Id")
    @Test
    void givenMemberId_whenGetMember_thenReturnMemberInfo() {
        //given
        var memberId = 1L;
        given(memberRepository.findMemberWithMembership(anyLong())).willReturn(Optional.ofNullable(getStubMember()));

        //when
        var memberInfo = memberService.getMember(memberId);

        //then
        assertThat(memberInfo.getMemberId()).isEqualTo(memberId);
        assertThat(memberInfo.getUsername()).isEqualTo("member1");

        then(memberRepository).should().findMemberWithMembership(anyLong());
    }

    @DisplayName("[성공][service] 로그인된 나의 회원 정보 조회")
    @Test
    void givenAuthenticatedUsername_whenGetMember_thenReturnMemberInfo() {
        //given
        var username = "member1";
        given(memberRepository.findMemberWithMembership(isNull(), anyString()))
                .willReturn(Optional.ofNullable(getStubMember()));

        //when
        var memberInfo = memberService.getMember(username);

        //then
        assertThat(memberInfo.getUsername()).isEqualTo("member1");

        then(memberRepository).should().findMemberWithMembership(isNull(), anyString());
    }

    @DisplayName("[성공][service] 단일 회원 조회 by memberId : 조회 권한 확인")
    @Test
    void givenMemberIdAndAuthenticatedUsername_whenGetMember_thenReturnMemberInfo() {
        //given
        var memberId = 1L;
        var authUsername = "member1";
        var stubMember = getStubMember();
        ReflectionTestUtils.setField(stubMember, "id", memberId);

        given(memberRepository.findMemberWithMembership(anyLong()))
                .willReturn(Optional.ofNullable(stubMember));
        given(memberRepository.findByUsername(anyString()))
                .willReturn(Optional.ofNullable(stubMember));

        //when
        var memberInfo = memberService.getMember(memberId, authUsername);

        //then
        assertThat(memberInfo.getMemberId()).isEqualTo(memberId);
        assertThat(memberInfo.getUsername()).isEqualTo("member1");

        then(memberRepository).should().findMemberWithMembership(anyLong());
        then(memberRepository).should().findByUsername(anyString());
    }

    @DisplayName("[실패][service] 존재하지 않는 회원 조회")
    @Test
    void givenNoneExistMemberId_whenGetMember_thenThrowEntityNotFoundException() {
        //given
        var memberId = 1L;
        given(memberRepository.findMemberWithMembership(anyLong()))
                .willThrow(new EntityNotFoundException("존재하지 않는 회원입니다. id = " + memberId));

        //when then
        assertThatThrownBy(() -> memberService.getMember(memberId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다. id = " + memberId);

        then(memberRepository).should().findMemberWithMembership(anyLong());
    }

    @DisplayName("[성공][service] 전체 회원 리스트 조회")
    @Test
    void givenNothing_whenListMembers_thenReturnMemberList() {
        //given
        given(memberRepository.findAllWithMembership()).willReturn(getStubMembers());

        //when
        var list = memberService.listMembers(null, null);

        //then
        assertThat(list.size()).isEqualTo(20);
        assertThat(list.get(1).getUsername()).isEqualTo("member2");
        assertThat(list.get(2).getMembership()).isNotNull();
        assertThat(list.get(2).getMembership().getGrade()).isEqualTo(Grade.SILVER);

        then(memberRepository).should().findAllWithMembership();
    }

    @DisplayName("[성공][service] 회원 리스트 검색 조회 by 멤버십 등급")
    @Test
    void givenGrade_whenListMembers_thenReturnSearchedMemberListByGrade() {
        //given
        var grade = Grade.GOLD;
        given(memberRepository.findAllWithMembership(isNull(), any(Grade.class))).willReturn(new ArrayList<>());

        //when
        var list = memberService.listMembers(null, grade);

        //then
        assertThat(list.size()).isEqualTo(0);

        then(memberRepository).should().findAllWithMembership(isNull(), any(Grade.class));
    }

    @DisplayName("[성공][service] 전체 회원 리스트 검색 조회 (페이징)")
    @Test
    void givenUsernameAndGradeAndPageRequest_whenMembers_thenReturnSearchedMemberPagingResult() {
        //given
        var grade = Grade.GOLD;
        var username = "member1";
        var pageRequest = PageRequest.of(0, 5);
        given(memberRepository.findAllWithMembership(anyString(), any(Grade.class), any(Pageable.class))).willReturn(Page.empty());

        //when
        var result = memberService.members(username, grade, pageRequest);

        //then
        assertThat(result.getTotalElements()).isEqualTo(0);

        then(memberRepository).should().findAllWithMembership(anyString(), any(Grade.class), any(Pageable.class));
    }

    private Member getStubMember() {
        var member = Member.LocalUserMemberBuilder()
                .username("member1")
                .password("Member10000!")
                .phoneNumber("010-0000-0001")
                .email("member1@naver.com")
                .address(new Address("서울시", "송파구", "00001"))
                .build();

        ReflectionTestUtils.setField(member, "id", 1L);

        return member;
    }

    private List<Member> getStubMembers() {
        List<Member> members = new ArrayList<>();
        for (long i = 1; i <= 20 ; i++) {
            var username = "member" + i;
            var password = "Member" + i + "0000!@";
            var email = username + "@naver.com";
            var phoneNum = "010-0000" + ((i >= 10) ? "-00" : "-000") + i;
            var zipcode = ((i >= 10) ? "000" : "0000") + i;
            var member = Member.LocalUserMemberBuilder()
                    .username(username)
                    .password(password)
                    .phoneNumber(phoneNum)
                    .email(email)
                    .address(new Address("서울시", "송파구", zipcode))
                    .build();
            ReflectionTestUtils.setField(member, "id", i);

            members.add(member);
        }

        return members;
    }
}
