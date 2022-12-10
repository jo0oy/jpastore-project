package jpabook.jpastore.application.member;

import jpabook.jpastore.common.exception.DuplicateEmailException;
import jpabook.jpastore.common.exception.DuplicateNameException;
import jpabook.jpastore.common.exception.EntityNotFoundException;
import jpabook.jpastore.common.utils.PageRequestUtils;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.membership.Grade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 등록
     */
    @Override
    @Transactional
    public Long join(MemberCommand.RegisterReq command) {
        log.info("joining member...{}", command.getUsername());
        log.info("registerCommand={}", command);

        // 중복 이름 & 이메일 검증
        validateDuplicateMember(command.getUsername());
        validateDuplicateEmail(command.getEmail());

        Member member = command.toEntity();

        // 암호화된 비밀번호 설정
        member.setEncodedPassword(passwordEncoder.encode(command.getPassword()));

        return memberRepository.save(member).getId();
    }

    /**
     * 전체 회원 조회 : 검색조건 (아이디, 등급)
     * @return
     */
    @Override
    public List<MemberInfo.MainInfo> listMembers(String username, Grade grade) {
        log.info("selecting members by username, grade : {}, {}", username, grade);

        if (!StringUtils.hasText(username) && grade == null) {
            return memberRepository.findAllWithMembership().stream()
                    .map(MemberInfo.MainInfo::new).collect(Collectors.toList());
        }

        return memberRepository.findAllWithMembership(username, grade).stream()
                .map(MemberInfo.MainInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MemberInfo.MainInfo> members(String username, Grade grade, Pageable pageable) {
        log.info("searching members and paging by username : {}, grade : {}", username, grade);

        // 검색 데이터가 없는 경우
        if (!StringUtils.hasText(username) && grade == null) {
            return memberRepository.findAllWithMembership(PageRequestUtils.of(pageable)).map(MemberInfo.MainInfo::new);
        }

        return memberRepository.findAllWithMembership(username, grade, PageRequestUtils.of(pageable))
                .map(MemberInfo.MainInfo::new);
    }

    /**
     * 단일 회원 조회
     * @return
     */

    // 회원 id로 회원 상세 조회
    @Override
    public MemberInfo.MainInfo getMember(Long id) {
        log.info("selecting member by id : {}", id);
        Member member = memberRepository.findMemberWithMembership(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 회원입니다. id : " + id));

        return new MemberInfo.MainInfo(member);
    }

    // 회원 username 으로 회원 상세 조회
    @Override
    public MemberInfo.MainInfo getMember(String username) {
        log.info("selecting member by username : {}", username);

        // 조회할 Member 엔티티 조회
        var member = memberRepository.findMemberWithMembership(null, username)
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 회원입니다. username : " + username));

        return new MemberInfo.MainInfo(member);
    }

    // authUsername 을 통한 본인 확인 후 회원 조회 by id
    @Override
    public MemberInfo.MainInfo getMember(Long id, String authUsername) {
        log.info("selecting member by id : {}", id);
        // 회원 정보 조회할 회원 엔티티 조회
        var member = memberRepository.findMemberWithMembership(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 회원입니다. id : " + id));

        // 현재 로그인된 회원 엔티티 조회 -> 권한 확인을 위해
        var authenticatedMember = memberRepository.findByUsername(authUsername)
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 회원입니다. authUsername : " + authUsername));

        member.hasAuthority(authenticatedMember); // 상세 조회 권한 없을 경우 에러 발생.

        return new MemberInfo.MainInfo(member);
    }

    /**
     * 회원 정보 변경
     */
    // 회원 개인 정보 변경
    @Override
    @Transactional
    public void updateMemberInfo(Long id, MemberCommand.UpdateInfoReq command) {
        log.info("updating member info... id : {}", id);
        Member member = memberRepository.findMemberById(id).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 회원입니다. id = " + id));

        if(StringUtils.hasText(command.getEmail()))
             validateDuplicateEmail(command.getEmail());

        member.update(command.getPhoneNumber(), command.getEmail(),
                command.getCity(), command.getStreet(), command.getZipcode());
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        log.info("deleting member id : {}", id);

        Member member = memberRepository.findMemberWithMembership(id).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 회원입니다. id = " + id));

        // 삭제 권한 확인
        // 현재 로그인된 회원 엔티티 조회 -> 권한 확인을 위해
        var authenticatedMember = memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 회원입니다. authUsername : " + username));

        member.hasAuthority(authenticatedMember);

        // 권한 확인 후 삭제 처리
        member.delete();
    }

    // 중복 이메일 확인 메서드
    private void validateDuplicateEmail(String email) {
        log.info("checking duplicate member email...{}", email);
        if (memberRepository.existsMemberByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }

    // 중복 이름 확인 메서드
    private void validateDuplicateMember(String username) {
        log.info("checking duplicate member by username...{}", username);
        memberRepository.findByUsername(username)
                .ifPresent(o -> {
                    throw new DuplicateNameException();
                });
    }
}
