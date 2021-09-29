package jpabook.jpastore.application;

import jpabook.jpastore.domain.membership.Membership;
import jpabook.jpastore.domain.membership.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;

    // 1. 분기별 전 회원 멤버십 업데이트 : 이전 분기의 회원 총 지출누적액에 따른 등급 조정 & 총 지출액 0으로 reset.
    @Transactional
    public void updateMembership() {
        log.info("updating memberships...");
        List<Membership> memberships = membershipRepository.findAll();

        for (Membership membership : memberships) {
            membership.updateMembership();
            log.info("membership id {} changed to : {}", membership.getId(), membership.getGrade());
        }
    }
}
