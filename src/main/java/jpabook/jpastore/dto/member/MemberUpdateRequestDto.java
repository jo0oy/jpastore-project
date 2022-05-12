package jpabook.jpastore.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberUpdateRequestDto {
    private String phoneNumber;
    private String city;
    private String street;
    private String zipcode;

    public MemberUpdateRequestDto(String phoneNumber, String city, String street, String zipcode) {
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
