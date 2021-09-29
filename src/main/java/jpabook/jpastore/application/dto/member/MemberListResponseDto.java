package jpabook.jpastore.application.dto.member;

import jpabook.jpastore.domain.member.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MemberListResponseDto {
    private int totalCount;
    private List<MemberResponseDto> members = new ArrayList<>();

    public MemberListResponseDto(List<Member> entities) {
        this.totalCount = entities.size();
        for (Member entity : entities) {
            members.add(new MemberResponseDto(entity));
        }
    }
}
