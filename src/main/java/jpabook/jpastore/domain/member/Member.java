package jpabook.jpastore.domain.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.membership.Membership;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString(of = {"id", "name", "phoneNumber", "address"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Embedded
    private Address address;

    private LocalDateTime lastLoginAt;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @Builder
    public Member(String name, String phoneNumber, Address address, Membership membership) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.membership = membership;
    }

    //==연관관계 메서드==//
    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    // 비즈니스 로직
    public void update(String phoneNumber, String city, String street, String zipcode) {
        if (StringUtils.hasText(phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }

        if (StringUtils.hasText(city) && StringUtils.hasText(street) && StringUtils.hasText(zipcode)) {
            this.address = new Address(city, street, zipcode);
        }
    }
}
