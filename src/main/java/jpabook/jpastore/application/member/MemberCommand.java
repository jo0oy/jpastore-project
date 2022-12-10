package jpabook.jpastore.application.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class MemberCommand {

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class RegisterReq {
        private String username;
        private String password;
        private String phoneNumber;
        private String email;
        private AddressInfo addressInfo;

        public Member toEntity() {

            return Member.LocalUserMemberBuilder()
                    .username(username)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .email(email)
                    .address(addressInfo.toAddress())
                    .build();
        }
    }

    @ToString
    @AllArgsConstructor
    @Builder
    @Getter
    public static class UpdateInfoReq {
        private String phoneNumber;
        private String email;
        private String city;
        private String street;
        private String zipcode;
    }

    @ToString
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AddressInfo {
        private String city;
        private String street;
        private String zipcode;

        public Address toAddress() {
            return new Address(city, street, zipcode);
        }
    }
}
