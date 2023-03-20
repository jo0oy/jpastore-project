package jpabook.jpastore.application.member;

import jpabook.jpastore.domain.membership.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {

    Long join(MemberCommand.RegisterReq command);

    // 전체 회원 리스트 조회
    List<MemberInfo.MainInfo> listMembers(String username, Grade grade);

    // 전체 회원 리스트 조회 (페이징/정렬/검색(username, 등급))
    Page<MemberInfo.MainInfo> members(String username, Grade grade, Pageable pageable);

    // 회원 id or username 으로 회원 상세 조회
    MemberInfo.MainInfo getMember(Long id);

    MemberInfo.MainInfo getMember(String username);

    MemberInfo.MainInfo getMember(Long id, String authUsername);

    // 회원 개인 정보 변경
    void updateMemberInfo(Long id, MemberCommand.UpdateInfoReq command);

    // 회원 삭제
    void delete(Long id, String username);
}
