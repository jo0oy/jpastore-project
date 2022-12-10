package jpabook.jpastore.domain;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.Embeddable;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Address {

    private String city;
    private String street;
    private String zipcode;

    public Address(String city, String street, String zipcode) {

        if(!(StringUtils.hasText(city) && StringUtils.hasText(street) && StringUtils.hasText(zipcode)))
            throw new IllegalArgumentException("Invalid Param. address");

        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // 주소 필요없는 경우(ROLE_ADMIN) or 주소 입력 받을 수 없는 임시 상황(소셜로그인)
    public static Address none() {
        return new Address("NONE", "NONE", "NONE");
    }
}
