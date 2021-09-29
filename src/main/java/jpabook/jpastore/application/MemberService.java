package jpabook.jpastore.application;

import jpabook.jpastore.dto.member.MemberUpdateRequestDto;
import jpabook.jpastore.domain.Money;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.application.dto.member.MemberListResponseDto;
import jpabook.jpastore.application.dto.member.MemberResponseDto;
import jpabook.jpastore.dto.member.MemberSaveRequestDto;
import jpabook.jpastore.domain.member.queryRepo.MemberQueryRepository;
import jpabook.jpastore.domain.membership.Grade;
import jpabook.jpastore.domain.membership.Membership;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;

    /**
     * 회원 등록
     */
    @Transactional
    public Long join(MemberSaveRequestDto requestDto) {
        log.info("joining member...{}", requestDto.getName());
        validateDuplicateMember(requestDto.getName());

        Member member = requestDto.toEntity();
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
    public MemberListResponseDto findMembers() {
        log.info("selecting members...");
        List<Member> members = memberQueryRepository.findMembersWithMembership();
        return new MemberListResponseDto(members);
    }

    /**
     * 단일 회원 조회
     */
    // 회원 id로 조회
    public MemberResponseDto findById(Long id) {
        log.info("selecting member by id : {}", id);
        Member member = memberQueryRepository.findMemberById(id);

        return new MemberResponseDto(member);
    }

    // 회원 이름으로 조회
    public MemberResponseDto findByName(String name) {
        log.info("selecting member by name : {}", name);
        Member member = memberQueryRepository.findMemberByName(name);

        return new MemberResponseDto(member);
    }

    /**
     * 회원 정보 변경
     */

    // 회원 개인 정보 변경
    @Transactional
    public void updateMemberInfo(Long id, MemberUpdateRequestDto requestDto) {
        log.info("updating member info... id : {}", id);
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + id));

        member.update(requestDto.getName(), requestDto.getCity(), requestDto.getStreet(), requestDto.getZipcode());
    }

    // 중복 이름 확인 메서드
    private void validateDuplicateMember(String name) {
        log.info("checking duplicate member...{}", name);
        Member findOne = memberRepository.findByName(name);

        if (findOne != null) {
            log.error("이미 존재하는 회원 이름입니다. {}", name);
            throw new IllegalStateException("이미 존재하는 회원 이름입니다.");
        }
    }
}
