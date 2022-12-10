package jpabook.jpastore.application.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import lombok.*;

public class MemberInfo {

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MainInfo {
        private Long memberId;
        private String username;
        private String phoneNumber;
        private AddressInfo address;
        private MembershipInfo membership;

        public MainInfo(Member entity) {
            this.memberId = entity.getId();
            this.username = entity.getUsername();
            this.phoneNumber = entity.getPhoneNumber();
            this.address = new AddressInfo(entity.getAddress());
            this.membership = new MembershipInfo(entity.getMembership());
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class AddressInfo {
        private String city;
        private String street;
        private String zipcode;

        public AddressInfo(Address address) {
            this.city = address.getCity();
            this.street = address.getStreet();;
            this.zipcode = address.getZipcode();
        }
    }

    @ToString
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MembershipInfo {
        private Long membershipId;
        private Grade grade;
        private Money totalSpending;

        public MembershipInfo(Membership entity) {
            this.membershipId = entity.getId();
            this.grade = entity.getGrade();
            this.totalSpending = entity.getTotalSpending();
        }
    }
}
