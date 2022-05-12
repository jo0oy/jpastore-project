package jpabook.jpastore.application.membership;

import jpabook.jpastore.domain.membership.Grade;
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
public class MembershipServiceImpl implements MembershipService {
    private final MembershipRepository membershipRepository;

    /**
     * 분기별 전 회원 멤버십 업데이트 : 이전 분기의 회원 총 지출누적액에 따른 등급 조정 & 총 지출액 0으로 reset
     */

    // 1. Dirty Checking 을 이용한 업데이트.
    @Override
    @Transactional
    public void updateMembershipsByDirtyChecking() {
        log.info("updating memberships by dirty checking...");
        List<Membership> memberships = membershipRepository.findAll();

        for (Membership membership : memberships) {
            membership.updateMembership();
            log.info("membership id {} changed to : {}", membership.getId(), membership.getGrade());
        }
    }

    // 2. Bulk Update 을 통한 업데이트.
    @Override
    @Transactional
    public void updateMembershipsByBulkUpdate() {
        log.info("updating memberships by bulk update...");

        // SILVER 등급 업데이트
        membershipRepository.bulkUpdateGrade(Grade.SILVER.getGreaterEqual(),
                Grade.SILVER.getLessThan(), Grade.SILVER);

        // GOLD 등급 업데이트
        membershipRepository.bulkUpdateGrade(Grade.GOLD.getGreaterEqual(),
                Grade.GOLD.getLessThan(), Grade.GOLD);

        // VIP 등급 업데이트
        membershipRepository.bulkUpdateGrade(Grade.VIP.getGreaterEqual(), Grade.VIP);

        // 총지출 0으로 모두 RESET
        membershipRepository.bulkResetTotalSpending();
    }
}
