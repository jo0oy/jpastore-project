package jpabook.jpastore.application.member;

import jpabook.jpastore.common.exception.DuplicateEmailException;
import jpabook.jpastore.common.exception.DuplicateNameException;
import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@DisplayName("회원 생성/수정/삭제 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceCommandTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {

    }

    @DisplayName("[성공][service] 회원 등록")
    @Test
    void givenRegisterReq_whenJoin_thenWorksFine() {

        //given
        var givenId = 1L;
        var command = defaultRegisterReq();

        var entity = command.toEntity();
        ReflectionTestUtils.setField(entity, "id", givenId);

        given(memberRepository.findByUsername(any(String.class))).willReturn(Optional.empty());
        given(memberRepository.existsMemberByEmail(any(String.class))).willReturn(false);
        given(memberRepository.save(any())).willReturn(entity);

        //when
        var joinId = memberService.join(command);

        //then
        assertThat(joinId).isEqualTo(givenId);

        then(memberRepository).should().save(any());
        then(memberRepository).should().existsMemberByEmail(any(String.class));
        then(memberRepository).should().findByUsername(any(String.class));
    }

    @DisplayName("[실패][service] 회원 등록: 중복 이름")
    @Test
    void givenDuplicateName_whenJoin_whenThrowDuplicateNameException() {

        //given
        given(memberRepository.findByUsername(any(String.class))).willThrow(new DuplicateNameException());

        //when & then
        assertThatThrownBy(() -> memberService.join(defaultRegisterReq()))
                .isInstanceOf(DuplicateNameException.class);

        then(memberRepository).should().findByUsername(any(String.class));
        then(memberRepository).should(times(0)).save(any());
        then(memberRepository).should(times(0)).existsMemberByEmail(any(String.class));
    }

    @DisplayName("[성공][service] 회원 정보 수정")
    @Test
    void givenUpdateReq_whenUpdateMemberInfo_thenWorksFine() {
        //given
        var id = 1L;

        var username = "member1";
        var updatePhoneNumber = "010-1111-1112";
        var updateEmail = "member1@gmail.com";
        var updateCity = "서울시";
        var updateStreet = "성북구";
        var updateZipcode = "11112";
        var updatedEntity = createMember(username, updatePhoneNumber, updateEmail, updateCity, updateStreet, updateZipcode);
        ReflectionTestUtils.setField(updatedEntity, "id", id);

        given(memberRepository.findMemberById(any(Long.class))).willReturn(Optional.of(updatedEntity));
        given(memberRepository.existsMemberByEmail(any(String.class))).willReturn(false);

        //when
        memberService.updateMemberInfo(id, defaultUpdateReq());

        //then
        then(memberRepository).should().findMemberById(any(Long.class));
        then(memberRepository).should().existsMemberByEmail(any(String.class));

        var updatedMember = memberRepository.findMemberById(id);

        assertThat(updatedMember).isPresent();
        assertThat(updatedMember.get().getPhoneNumber()).isEqualTo(updatePhoneNumber);
        assertThat(updatedMember.get().getEmail()).isEqualTo(updateEmail);

    }

    @DisplayName("[실패][service] 회원 정보 수정: 존재하지 않는 회원")
    @Test
    void givenNoneExistMemberId_whenUpdateMemberInfo_thenThrowEntityNotFoundException() {
        //given
        var id = 1L;

        given(memberRepository.findMemberById(any(Long.class))).willThrow(new EntityNotFoundException("존재하지 않는 회원입니다. id = " + id));

        //when & then
        assertThatThrownBy(() -> memberService.updateMemberInfo(id, defaultUpdateReq()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다. id = " + id);

        then(memberRepository).should().findMemberById(any(Long.class));
        then(memberRepository).should(times(0)).existsMemberByEmail(any(String.class));
    }

    @DisplayName("[실패][service] 수정할 이메일 중복 에러")
    @Test
    void givenDuplicateEmail_whenUpdateMemberInfo_thenThrowDuplicateEmailException() {

        //given
        var id = 1L;
        given(memberRepository.findMemberById(any(Long.class))).willReturn(Optional.ofNullable(defaultRegisterReq().toEntity()));
        given(memberRepository.existsMemberByEmail(any(String.class))).willReturn(true);

        //when then
        assertThatThrownBy(() -> memberService.updateMemberInfo(id, defaultUpdateReq()))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 존재하는 이메일입니다.");

        then(memberRepository).should().findMemberById(any(Long.class));
        then(memberRepository).should().existsMemberByEmail(any(String.class));
    }

    @DisplayName("[성공][service] 회원 삭제")
    @Test
    void givenMemberIdAndAuthMemberUsername_whenDeleteMember_thenWorksFine() {
        //given
        var id = 4L;
        var username = "member4";
        var member = Member.LocalUserMemberBuilder()
                .username(username)
                .phoneNumber("010-4040-4040")
                .password("member4Pw!@")
                .email("member4@gmail.com")
                .address(new Address("seoul", "song-pa", "11111")).build();

        ReflectionTestUtils.setField(member, "id", id);

        given(memberRepository.findMemberWithMembership(any(Long.class))).willReturn(Optional.of(member));
        given(memberRepository.findByUsername(anyString())).willReturn(Optional.of(member));

        //when & then
        memberService.delete(id, username);
        then(memberRepository).should().findMemberWithMembership(any(Long.class));
        then(memberRepository).should().findByUsername(anyString());
    }

    private Member createMember(String username,
                                String phoneNumber,
                                String email,
                                String city, String street, String zipcode) {

        return Member.LocalUserMemberBuilder()
                .username(username)
                .password("Member0000!@")
                .email(email)
                .phoneNumber(phoneNumber)
                .address(new Address(city, street, zipcode))
                .build();
    }

    private MemberCommand.RegisterReq defaultRegisterReq() {
        return MemberCommand.RegisterReq.builder()
                .username("member1")
                .password("Member1111!@")
                .phoneNumber("010-1111-1111")
                .email("member1@naver.com")
                .addressInfo(
                        MemberCommand.AddressInfo.builder()
                                .city("서울시")
                                .street("송파구")
                                .zipcode("00001")
                                .build()
                )
                .build();
    }

    private MemberCommand.UpdateInfoReq defaultUpdateReq() {
        return MemberCommand.UpdateInfoReq.builder()
                .phoneNumber("010-1111-1112")
                .email("member1@gmail.com")
                .city("서울시")
                .street("성북구")
                .zipcode("11122")
                .build();
    }
}
