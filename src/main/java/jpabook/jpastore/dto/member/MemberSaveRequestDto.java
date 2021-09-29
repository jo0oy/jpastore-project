package jpabook.jpastore.dto.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.member.Member;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@Setter
@Getter
public class MemberSaveRequestDto {

    @NotEmpty(message = "회원 이름은 필수 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;

    @Builder
    public MemberSaveRequestDto(String name, String city, String street, String zipcode) {
        this.name = name;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public Member toEntity() {

        return Member.builder()
                .name(name)
                .address(new Address(city, street, zipcode))
                .build();
    }
}
