package jpabook.jpastore.web.dto.member;

import jpabook.jpastore.domain.membership.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class MemberDto {

    @ToString
    @Getter
    @Builder
    public static class RegisterReq {
        @NotBlank(message = "{NotBlank.username}")
        private String username;

        @NotBlank(message = "{NotBlank.auth.password}")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                message = "{Pattern.password}")
        private String password;

        @NotBlank(message = "{NotBlank.phoneNumber}")
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$",
                message = "{Pattern.phoneNumber}")
        private String phoneNumber;

        @NotBlank
        @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
                message = "{Pattern.email}")
        private String email;

        private AddressInfo addressInfo;
    }

    @ToString
    @Getter
    @Builder
    public static class AddressInfo {
        @NotBlank(message = "{NotBlank.address}")
        private String city;

        @NotBlank(message = "{NotBlank.address}")
        private String street;

        @NotBlank(message = "{NotBlank.address}")
        private String zipcode;
    }

    @ToString
    @Getter
    @Builder
    public static class UpdateInfoReq {

        @NotBlank(message = "{NotBlank.username}")
        private String username;
        private String phoneNumber;
        private String email;
        private String city;
        private String street;
        private String zipcode;
    }

    @ToString
    @Getter
    @Builder
    public static class RegisterSuccessResponse {
        private Long registeredMemberId;
    }

    @ToString
    @Getter
    @Builder
    public static class MainInfoResponse {
        private Long memberId;
        private String username;
        private String phoneNumber;
        private AddressInfoResponse address;
        private MembershipInfoResponse membership;
    }

    @ToString
    @Getter
    @Builder
    public static class AddressInfoResponse {
        private String city;
        private String street;
        private String zipcode;
    }

    @ToString
    @Getter
    @Builder
    public static class MembershipInfoResponse {
        private Long membershipId;
        private Grade grade;
        private Integer totalSpending;
    }

    @Getter
    public static class ListResponse<T> {
        private int totalCount;
        private List<T> members = new ArrayList<>();

        public ListResponse(List<T> members) {
            this.totalCount = members.size();
            this.members.addAll(members);
        }
    }
}
