package jpabook.jpastore.application.dto.member;

import jpabook.jpastore.domain.Address;
import jpabook.jpastore.domain.member.Member;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private Long id;
    private String name;
    private Address address;
    private MembershipDto membership;

    public MemberResponseDto(Member entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.address = entity.getAddress();
        this.membership = new MembershipDto(entity.getMembership());
    }
}
