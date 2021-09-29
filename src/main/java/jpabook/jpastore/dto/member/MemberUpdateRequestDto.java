package jpabook.jpastore.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberUpdateRequestDto {
    private String name;
    private String city;
    private String street;
    private String zipcode;

    public MemberUpdateRequestDto(String name, String city, String street, String zipcode) {
        this.name = name;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
