package jpabook.jpastore.application.member;

import jpabook.jpastore.domain.membership.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberService {

    @Transactional
    Long join(MemberCommand.RegisterReq command);

    @Transactional(readOnly = true)
    List<MemberInfo.MainInfo> listMembers(String username, Grade grade);

    @Transactional(readOnly = true)
    Page<MemberInfo.MainInfo> members(String username, Grade grade, Pageable pageable);

    // 회원 id or username 으로 회원 상세 조회
    @Transactional(readOnly = true)
    MemberInfo.MainInfo getMember(Long id);

    @Transactional(readOnly = true)
    MemberInfo.MainInfo getMember(String username);

    @Transactional(readOnly = true)
    MemberInfo.MainInfo getMember(Long id, String authUsername);

    // 회원 개인 정보 변경
    @Transactional
    void updateMemberInfo(Long id, MemberCommand.UpdateInfoReq command);

    // 회원 삭제
    @Transactional
    void delete(Long id, String username);
}
