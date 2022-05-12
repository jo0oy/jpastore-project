package jpabook.jpastore.application.membership;

public interface MembershipService {

    void updateMembershipsByDirtyChecking();

    void updateMembershipsByBulkUpdate();
}
