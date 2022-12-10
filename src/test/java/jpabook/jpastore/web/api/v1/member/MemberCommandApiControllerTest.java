package jpabook.jpastore.web.api.v1.member;

import jpabook.jpastore.common.exception.handler.ErrorCode;
import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.member.ProviderType;
import jpabook.jpastore.security.JwtTokenProvider;
import jpabook.jpastore.web.dto.member.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("회원 등록/수정/삭제 API 테스트")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberCommandApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private Long savedMemberId1;

    private Long savedMemberId2;

    @BeforeEach
    void setUp() {
        savedMemberId1 = memberRepository.save(
            Member.LocalUserMemberBuilder()
                    .username("memberTest")
                    .password("memberTest12!@")
                    .email("memberTest@naver.com")
                    .phoneNumber("010-1111-1111")
                    .address(new Address("서울시", "송파구", "가락동"))
                    .build()
        ).getId();

        savedMemberId2 = memberRepository.save(
                Member.LocalUserMemberBuilder()
                        .username("memberTest2")
                        .password("memberTest212!@")
                        .email("memberTest2@naver.com")
                        .phoneNumber("010-2222-2222")
                        .address(new Address("서울시", "송파구", "가락동"))
                        .build()
        ).getId();
    }

    @AfterEach
    public void cleanUp() {
        memberRepository.deleteAll();
        savedMemberId1 = null;
        savedMemberId2 = null;
    }

    @DisplayName("[성공][api] 회원 등록 테스트")
    @Test
    void givenRegisterReq_whenPostNewMember_thenReturnRegisteredMemberId() {
        // given
        var username = "member1";
        var password = "Member10000!@";
        var phoneNumber = "010-1111-1111";
        var email = "member1@naver.com";
        var city = "서울시"; var street = "송파구"; var zipcode = "00001";

        var requestDto = getRequestDto(username, password, phoneNumber, email, city, street, zipcode);

        var url = "http://localhost:" + port + "/api/v1/members";

        // when
        this.webTestClient
                .post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);


        // then
        var members = memberRepository.findAll();
        var lastIdx = members.size() - 1;
        assertThat(members.get(lastIdx).getUsername()).isEqualTo(username);
        assertThat(members.get(lastIdx).getEmail()).isEqualTo(email);
        assertThat(members.get(lastIdx).getOAuthInfo().getProviderType()).isEqualTo(ProviderType.NONE);
    }

    @DisplayName("[실패][api] 회원 등록 요청 - 요청 값 에러")
    @Test
    void givenInvalidReqData_whenPostNewMember_thenReturnBadRequestError() {
        // given
        var username = "member1";
        var password = "Member10000!@";
        var email = "member1@naver.com";
        var city = "서울시"; var street = "송파구"; var zipcode = "00001";

        // phoneNumber 요청 값 누락
        var requestDto = getRequestDto(username, password, null, email, city, street, zipcode);

        var url = "http://localhost:" + port + "/api/v1/members";

        // when
        this.webTestClient
                .post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.error.ex").isEqualTo(ErrorCode.NOT_VALID_ARGUMENT.getErrorName());
    }

    @DisplayName("[실패][api] 회원 등록 - 중복 아이디")
    @Test
    void givenDuplicateUsername_whenPostNewMember_thenReturnsBadRequestError() {
        // given
        var username = "member1";
        var password = "Member10000!@";
        var phoneNumber = "010-1111-1111";
        var email = "member1@naver.com";
        var city = "서울시"; var street = "송파구"; var zipcode = "00001";

        var memberId = saveMember(username, password, phoneNumber, "member1@gmail.com", city, street, zipcode);

        // 중복 이름 요청
        var requestDto
                = getRequestDto(username, password, phoneNumber, email, city, street, zipcode);


        var url = "http://localhost:" + port + "/api/v1/members";

        // when
        this.webTestClient
                .post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.error.message").isEqualTo("이미 존재하는 회원입니다.");
    }

    @DisplayName("[성공][api] 회원 정보 수정 테스트")
    @Test
    void givenUpdateReq_whenPutUpdateMemberInfo_thenWorksFine() {

        // given
        var username = "member1";
        var memberId = saveMember(username, "Member10000!@", "010-1111-1111",
                "member1@naver.com", "서울시", "송파구", "00001");

        var url = "http://localhost:" + port + "/api/v1/members/" + memberId;

        var updateEmail = "member1@gmail.com";
        var requestDto
                = MemberDto.UpdateInfoReq.builder()
                .username(username)
                .email(updateEmail)
                .build();

        var token
                = tokenProvider.generateToken(
                        new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        ).getAccessToken();

        // when
        this.webTestClient
                .put()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.message").isEqualTo("회원정보 수정 성공");

        // then
        var member = memberRepository.findById(memberId);
        assertThat(member).isPresent();
        assertThat(member.get().getEmail()).isEqualTo(updateEmail);
        assertThat(member.get().getPhoneNumber()).isEqualTo("010-1111-1111");
    }

    @DisplayName("[성공][api] 회원 삭제 테스트")
    @Test
    void givenMemberId_whenDeleteMember_thenWorksFine() {

        // given
        var memberId = savedMemberId2;

        var url = "http://localhost:" + port + "/api/v1/members/" + memberId;

        var token
                = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("memberTest2", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        ).getAccessToken();

        // when
        this.webTestClient
                .delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.message").isEqualTo("회원 탈퇴 성공");

        // then
        var member = memberRepository.findById(memberId);
        assertThat(member).isPresent();
        assertThat(member.get().isDeleted()).isTrue();
    }

    @DisplayName("[실패][api] 회원 삭제 - 존재하지 않는 회원")
    @Test
    void givenNotExistMemberId_whenDeleteMember_thenReturnsBadRequestError() {

        // given
        var memberId = 10L;

        var url = "http://localhost:" + port + "/api/v1/members/" + memberId;

        var token
                = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("memberTest2", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        ).getAccessToken();

        // when & then
        this.webTestClient
                .delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.error.message").isEqualTo("존재하지 않는 회원입니다. id = " + memberId);
    }

    @DisplayName("[실패][api] 회원 삭제 - 권한없는 사용자")
    @Test
    void givenMemberIdAndForbiddenUsername_whenDeleteMember_thenReturnsForbiddenError() {

        // given
        var memberId = savedMemberId1;

        var url = "http://localhost:" + port + "/api/v1/members/" + memberId;

        var token
                = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken("memberTest2", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        ).getAccessToken();

        // when & then
        this.webTestClient
                .delete()
                .uri(url)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);
    }

    private Long saveMember(String username,
                            String password,
                            String phoneNumber,
                            String email,
                            String city, String street, String zipcode) {

        return memberRepository.save(
                Member.LocalUserMemberBuilder()
                        .username(username)
                        .password(password)
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .address(new Address(city, street, zipcode))
                        .build()).getId();
    }

    private MemberDto.RegisterReq getRequestDto(String username,
                                                String password,
                                                String phoneNumber,
                                                String email,
                                                String city, String street, String zipcode) {
        return MemberDto.RegisterReq.builder()
                .username(username)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(email)
                .addressInfo(
                        MemberDto.AddressInfo.builder()
                                .city(city).street(street).zipcode(zipcode).build()
                ).build();
    }
}
