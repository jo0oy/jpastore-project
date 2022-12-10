package jpabook.jpastore.web.api.v1.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import jpabook.jpastore.security.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

@DisplayName("회원 조회 API 테스트")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberQueryApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private Member userMember;
    private Member adminMember;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {

        userMember = saveUserMember("member1", "Member10000!@", "010-1111-1111", null);
        userToken = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(userMember.getUsername(), null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))).getAccessToken();

        adminMember = saveAdminMember("admin", "Admin0000!@");
        adminToken = tokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(adminMember.getUsername(), null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))).getAccessToken();

        for (int i = 2; i <= 10; i++) {
            saveUserMember("member" + i, "Member" + i + "pw!",
                    "010-1111-11" + ((i / 10 > 0) ? i : "0" + i), null);
        }

        for (int i = 11; i <= 20; i++) {
            saveUserMember("member" + i, "Member" + i + "pw!",
                    "010-1111-11" + i, Membership.builder().grade(Grade.GOLD).totalSpending(new Money(250000)).build());
        }

        for (int i = 21; i <= 30; i++) {
            saveUserMember("member" + i, "Member" + i + "pw!",
                    "010-1111-11" + i, Membership.builder().grade(Grade.VIP).totalSpending(new Money(400000)).build());
        }
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        adminToken = null;
        userToken = null;
    }

    @DisplayName("[성공][api] 로그인한 단일 일반 회원 조회 - auth 정보 이용")
    @Test
    void givenNothingAndUsingAuthenticationPrincipal_whenGetMapping_thenReturnMemberInfo() {

        // given
        var url = "http://localhost:" + port + "/api/v1/members/my-info";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(userToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.username").isEqualTo("member1")
                .jsonPath("$.data.phoneNumber").isEqualTo("010-1111-1111");
    }

    @DisplayName("[실패][api] 미인증 회원 정보 조회")
    @Test
    void givenNothingWithUnauthenticatedUser_WhenGetMapping_thenReturnUnauthorizedError() {

        // given
        var url = "http://localhost:" + port + "/api/v1/members/my-info";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @DisplayName("[성공][api] 로그인한 단일 일반 회원 조회 - pathVariable: id")
    @Test
    void givenLoggedInUserId_whenGetMapping_thenReturnMemberInfo() {

        // given
        var url = "http://localhost:" + port + "/api/v1/members/" + userMember.getId();

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(userToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.username").isEqualTo("member1")
                .jsonPath("$.data.phoneNumber").isEqualTo("010-1111-1111");
    }

    @DisplayName("[성공][api] 관리자 권한이 있으면, id 로 회원 정보 조회")
    @Test
    void givenIdWithAdminUser_whenGetMapping_thenReturnMemberInfo() {

        // given
        var url = "http://localhost:" + port + "/api/v1/members/" + userMember.getId();

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.username").isEqualTo("member1")
                .jsonPath("$.data.phoneNumber").isEqualTo("010-1111-1111");
    }

    @DisplayName("[성공][api] 전체 회원 리스트 조회(페이징, 정렬 X) - 관리자 계정")
    @Test
    void givenAdminUserToken_whenGetMappingForMemberList_thenReturnMemberInfoList() {

        // given
        var url = "http://localhost:" + port + "/api/v1/members/list";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(31);
    }

    @DisplayName("[실패][api] 관리자 권한이 아닌 사용자가 회원 리스트 조회 요청")
    @Test
    void givenNoneAdminToken_WhenGetMappingMemberList_thenReturnForbiddenError() {

        // given
        var url = "http://localhost:" + port + "/api/v1/members/list";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(userToken))
                .exchange()
                .expectStatus().isForbidden();
    }

    @DisplayName("[성공][api] 검색 조건에 따른 회원 리스트 조회 by 아이디, 등급")
    @Test
    void givenUsernameAndGradeWithAdminToken_whenGetMappingForMemberList_thenReturnFilteredMemberInfoList() {

        // given
        var url = "/api/v1/members/list";
        var username = "member";
        var grade = "silver";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("username", username)
                                .queryParam("grade", grade)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(10);
    }

    @DisplayName("[실패][api] 관리자 권한이 아닌 사용자가 회원 리스트 페이징 조회 요청")
    @Test
    void givenNoneAdminToken_WhenGetMappingMemberPagingList_thenReturnForbiddenError() {

        // given
        var url = "http://localhost:" + port + "/api/v1/members";

        // when
        webTestClient
                .get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(userToken))
                .exchange()
                .expectStatus().isForbidden();
    }

    @DisplayName("[실패][api] 올바르지 않은 'grade' 쿼리 파라메터로 회원 리스트 페이징 조회 요청")
    @Test
    void givenNotValidGradeParam_WhenGetMappingMemberPagingList_thenReturnIllegalArgumentError() {

        // given
        var url = "/api/v1/members";
        var grade = "sil";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("grade", grade).build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("illegal_argument");
    }

    @DisplayName("[성공][api] 검색 조건에 따른 회원 리스트 페이징 조회 by 아이디, 등급")
    @Test
    void givenUsernameAndGradeWithAdminUser_whenGetMappingMembers_thenReturnMemberInfoPagingList() {

        // given
        var url = "/api/v1/members";
        var username = "member2";
        var grade = "vip";

        // when
        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(url)
                                .queryParam("username", username)
                                .queryParam("grade", grade)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.data.totalElements").isEqualTo(9)
                .jsonPath("$.data.totalPages").isEqualTo(1);
    }

    private Member saveUserMember(String username,
                                  String password,
                                  String phoneNumber,
                                  Membership membership) {

        Member member = Member.LocalUserMemberBuilder()
                .username(username)
                .password(password)
                .phoneNumber(phoneNumber)
                .email(username + "@naver.com")
                .address(new Address("서울시", "송파구", "00001"))
                .build();

        if (membership != null) {
            member.setMembership(membership);
        }

        return memberRepository.save(member);
    }

    private Member saveAdminMember(String username, String password) {

        return memberRepository.save(
                Member.AdminMemberBuilder()
                        .username(username)
                        .password(password)
                        .build()
        );
    }
}
