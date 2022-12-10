package jpabook.jpastore.web.api.v1.membership;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import jpabook.jpastore.domain.membership.MembershipRepository;
import jpabook.jpastore.security.JwtTokenProvider;
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

@DisplayName("멤버십 업데이트 API 테스트")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MembershipCommandApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        memberRepository.save(Member.LocalUserMemberBuilder()
                        .username("member1")
                        .phoneNumber("010-1111-1111")
                        .email("member1@gmail.com")
                        .password("member1Pw!@")
                        .address(new Address("서울시", "송파구", "오금동")).build());

        memberRepository.save(Member.AdminMemberBuilder()
                .username("admin")
                .password("adminPw!@")
                .build());

        membershipRepository.save(Membership.builder()
                .totalSpending(new Money(150000))
                .grade(Grade.GOLD).build());

        membershipRepository.save(Membership.builder()
                .totalSpending(new Money(350000))
                .grade(Grade.GOLD).build());

        membershipRepository.save(Membership.builder()
                .totalSpending(new Money(500000))
                .grade(Grade.SILVER).build());

        membershipRepository.save(Membership.builder()
                .totalSpending(new Money(100000))
                .grade(Grade.VIP).build());

        membershipRepository.save(Membership.builder()
                .totalSpending(new Money(150000))
                .grade(Grade.SILVER).build());

        membershipRepository.save(Membership.builder()
                .totalSpending(new Money(250000))
                .grade(Grade.SILVER).build());
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        membershipRepository.deleteAll();
    }

    @DisplayName("[성공][api] 전체 멤버십 Bulk 업데이트")
    @Test
    void givenAdminToken_whenPutRequestBulkUpdateMemberships_thenUpdateAllMemberships() {
        //given
        var url = "http://localhost:" + port + "/api/v1/memberships";

        //when & then
        webTestClient
                .put()
                .uri(url)
                .headers(httpHeaders ->
                        httpHeaders.setBearerAuth(tokenProvider.generateToken(
                                new UsernamePasswordAuthenticationToken("admin", null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))).getAccessToken())
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.message").isEqualTo("회원 멤버십 업데이트 성공");

        var memberships = membershipRepository.findAll();
        assertThat(memberships.size()).isEqualTo(8);
        assertThat(memberships.get(0).getGrade()).isEqualTo(Grade.SILVER);
        assertThat(memberships.get(0).getTotalSpending().getValue()).isEqualTo(0);
        assertThat(memberships.get(4).getGrade()).isEqualTo(Grade.VIP);
        assertThat(memberships.get(4).getTotalSpending().getValue()).isEqualTo(0);
        assertThat(memberships.get(6).getGrade()).isEqualTo(Grade.SILVER);
        assertThat(memberships.get(6).getTotalSpending().getValue()).isEqualTo(0);
    }

    @DisplayName("[실패][api] 멤버십 Bulk 업데이트 요청 - 권한없는 사용자")
    @Test
    void givenUserToken_whenPutRequestBulkUpdateMemberships_thenReturnForbiddenError() {
        //given
        var url = "http://localhost:" + port + "/api/v1/memberships";

        //when & then
        webTestClient
                .put()
                .uri(url)
                .headers(httpHeaders ->
                        httpHeaders.setBearerAuth(tokenProvider.generateToken(
                                new UsernamePasswordAuthenticationToken("member1", null,
                                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))).getAccessToken())
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.error.ex").isEqualTo("AccessDeniedException");
    }
}
