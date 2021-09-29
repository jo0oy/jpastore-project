package jpabook.jpastore.domain.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.BaseTimeEntity;
import jpabook.jpastore.domain.membership.Membership;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Embedded
    private Address address;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @Builder
    public Member(String name, Address address, Membership membership) {
        this.name = name;
        this.address = address;
        this.membership = membership;
    }

    //==연관관계 메서드==//
    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    // 비즈니스 로직
    public void update(String name, String city, String street, String zipcode) {
        this.name = name;
        this.address = new Address(city, street, zipcode);
    }
}
