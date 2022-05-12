package jpabook.jpastore.application.member;

import jpabook.jpastore.application.dto.member.MemberListResponseDto;
import jpabook.jpastore.application.dto.member.MemberResponseDto;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import jpabook.jpastore.dto.member.MemberSaveRequestDto;
import jpabook.jpastore.dto.member.MemberUpdateRequestDto;
import jpabook.jpastore.exception.DuplicateUsernameException;
import jpabook.jpastore.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 등록
     */
    @Override
    @Transactional
    public Long join(MemberSaveRequestDto requestDto) {
        log.info("joining member...{}", requestDto.getName());

        // 중복 이름 검증
        validateDuplicateMember(requestDto.getName());

        Member member = requestDto.toEntity();

        // 최초 멤버십 생성
        Membership membership = Membership.builder()
                .grade(Grade.SILVER)
                .totalSpending(new Money(0))
                .build();

        member.setMembership(membership);

        return memberRepository.save(member).getId();
    }

    /**
     * 전체 회원 조회
     */

    @Override
    public MemberListResponseDto listMembers() {
        log.info("selecting members...");
        List<Member> members = memberRepository.findAllWithMembership();
        return new MemberListResponseDto(members);
    }

    /**
     * 멤버십별 회원 리스트 조회
     */
    @Override
    public MemberListResponseDto listMembers(Grade grade) {
        log.info("selecting members by grade : {}", grade);

        return new MemberListResponseDto(memberRepository.findAllByGrade(grade));
    }

    /**
     * 단일 회원 조회
     */
    // 회원 id로 회원 상세 조회
    @Override
    public MemberResponseDto getMember(Long id) {
        log.info("selecting member by id : {}", id);
        Member member = Optional.ofNullable(memberRepository.findMemberWithMembership(id))
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다. id : " + id));

        return new MemberResponseDto(member);
    }

    // 이름으로 회원 상세 조회
    @Override
    public MemberResponseDto getMember(String name) {
        log.info("selecting member by id : {}", name);
        Member member = Optional.ofNullable(memberRepository.findMemberWithMembership(name))
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다. name : " + name));

        return new MemberResponseDto(member);
    }

    /**
     * 회원 정보 변경
     */
    // 회원 개인 정보 변경
    @Override
    @Transactional
    public void updateMemberInfo(Long id, MemberUpdateRequestDto requestDto) {
        log.info("updating member info... id : {}", id);
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다. id = " + id));

        member.update(requestDto.getPhoneNumber(), requestDto.getCity(), requestDto.getStreet(), requestDto.getZipcode());
    }

    // 중복 이름 확인 메서드
    private void validateDuplicateMember(String name) {
        log.info("checking duplicate member...{}", name);
        memberRepository.findByName(name)
                .ifPresent(o -> {
                    throw new DuplicateUsernameException("이미 존재하는 회원 이름입니다. name : " + name);
                });
    }
}
