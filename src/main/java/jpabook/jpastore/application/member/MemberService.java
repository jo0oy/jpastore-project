package jpabook.jpastore.application.member;

import jpabook.jpastore.application.dto.member.MemberListResponseDto;
import jpabook.jpastore.application.dto.member.MemberResponseDto;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.dto.member.MemberSaveRequestDto;
import jpabook.jpastore.dto.member.MemberUpdateRequestDto;

public interface MemberService {

    Long join(MemberSaveRequestDto requestDto);

    MemberListResponseDto listMembers();

    MemberListResponseDto listMembers(Grade grade);

    MemberResponseDto getMember(Long id);

    MemberResponseDto getMember(String name);

    void updateMemberInfo(Long id, MemberUpdateRequestDto requestDto);
}
