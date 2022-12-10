package jpabook.jpastore.domain.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.membership.Membership;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members", indexes = {
        @Index(name = "idx_mem_username", columnList = "username"),
        @Index(name = "idx_mem_email", columnList = "email"),
        @Index(name = "idx_mem_createdDate", columnList = "createdDate")
})
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    private boolean isDeleted;

    private LocalDateTime deletedAt;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 1 : 1 단방향
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @Embedded
    private OAuthInfo oAuthInfo;

    @Builder(builderClassName = "LocalUserMemberBuilder", builderMethodName = "LocalUserMemberBuilder")
    public Member(String username,
                  String password,
                  String phoneNumber,
                  String email,
                  Address address) {

        if(!StringUtils.hasText(username)) throw new IllegalArgumentException("Invalid Param. username");
        if(!StringUtils.hasText(password)) throw new IllegalArgumentException("Invalid Param. password");
        if(!StringUtils.hasText(phoneNumber)) throw new IllegalArgumentException("Invalid Param. phoneNumber");
        if(!StringUtils.hasText(email)) throw new IllegalArgumentException("Invalid Param. email");
        if(Objects.isNull(address)) throw new IllegalArgumentException("Invalid Param. address");

        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.role = Role.USER;
        this.membership = Membership.createMembership(); // 새 멤버십 생성
        this.oAuthInfo = new OAuthInfo(); // 홈 회원
    }

    @Builder(builderClassName = "AdminMemberBuilder", builderMethodName = "AdminMemberBuilder")
    public Member(String username,
                  String password) {

        if(!StringUtils.hasText(username)) throw new IllegalArgumentException("Invalid Param. username");
        if(!StringUtils.hasText(password)) throw new IllegalArgumentException("Invalid Param. password");

        this.username = username;
        this.password = password;
        this.phoneNumber = "DEFAULT_VALUE";
        this.email = "DEFAULT_VALUE";
        this.address = Address.none();
        this.role = Role.ADMIN;
        this.membership = Membership.createMembership(); // 새 멤버십 생성
        this.oAuthInfo = new OAuthInfo(); // 홈 회원
    }

    @Builder(builderClassName = "OAuthMemberBuilder", builderMethodName = "OAuthMemberBuilder")
    public Member(String username,
                  String phoneNumber,
                  String email,
                  OAuthInfo oAuthInfo) {

        if(!StringUtils.hasText(username)) throw new IllegalArgumentException("Invalid Param. username");
//        if(!StringUtils.hasText(phoneNumber)) throw new IllegalArgumentException("Invalid Param. phoneNumber");
        if(!StringUtils.hasText(email)) throw new IllegalArgumentException("Invalid Param. email");
        if(Objects.isNull(oAuthInfo)) throw new IllegalArgumentException("Invalid Param. oAuthInfo");

        this.username = username;
        this.password = "DEFAULT_PASSWORD";
        this.phoneNumber = StringUtils.hasText(phoneNumber) ? phoneNumber : "DEFAULT_VALUE";
        this.email = email;
        this.address = Address.none();
        this.role = Role.USER;
        this.membership = Membership.createMembership(); // 새 멤버십 생성
        this.oAuthInfo = oAuthInfo; // 소셜로그인 회원
    }

    //==연관관계 메서드==//
    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    //== 비즈니스 로직==//
    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void addOAuthInfo(OAuthInfo oAuthInfo) {
        this.oAuthInfo = oAuthInfo;
    }

    public void update(String phoneNumber,
                       String email) {

        if (StringUtils.hasText(phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }

        if (StringUtils.hasText(email)) {
            this.email = email;
        }
    }

    public void update(String phoneNumber,
                       String email,
                       String city, String street, String zipcode) {

        log.info("Member.update....");

        if (StringUtils.hasText(phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }

        if (StringUtils.hasText(email)) {
            this.email = email;
        }

        if (StringUtils.hasText(city) && StringUtils.hasText(street) && StringUtils.hasText(zipcode)) {
            this.address = new Address(city, street, zipcode);
        }
    }

    // 회원 상세 조회 | 수정 | 삭제 권한 있는지 체크 -> 없으면 exception 발생.
    public void hasAuthority(Member authenticatedMember) {
        if (!this.equals(authenticatedMember) // 본인도 아니고, 관리자 계정도 아닌 경우 에러 발생.
                && authenticatedMember.getRole() != Role.ADMIN) {
            log.error("회원 상세 조회 권한이 없습니다. username={}" , username);
            throw new AccessDeniedException(username + " 회원 상세 조회 권한이 없습니다.");
        }
    }

    // 회원 삭제
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();

        // 연관관계 membership 삭제 처리
        this.getMembership().delete();
    }
}
