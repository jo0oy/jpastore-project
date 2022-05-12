package jpabook.jpastore.dto.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Setter
@Getter
public class MemberSaveRequestDto {

    @NotBlank(message = "회원 이름은 필수 입니다.")
    private String name;

    @NotBlank(message = "휴대폰 번호는 필수 입니다.")
    private String phoneNumber;

    private String city;
    private String street;
    private String zipcode;

    @Builder
    public MemberSaveRequestDto(String name, String phoneNumber,
                                String city, String street, String zipcode) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .address(new Address(city, street, zipcode))
                .build();
    }
}
