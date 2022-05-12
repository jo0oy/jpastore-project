package jpabook.jpastore.domain;

import lombok.*;

import javax.persistence.Embeddable;

@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class Address {

    private String city;
    private String street;
    private String zipcode;
}
