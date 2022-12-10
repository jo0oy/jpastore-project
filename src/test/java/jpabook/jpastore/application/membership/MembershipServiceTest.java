package jpabook.jpastore.application.membership;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DisplayName("멤버십 업데이트 서비스 테스트")
@Sql(
        scripts = "classpath:data/data-membership-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import(TestDBConfig.class)
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MembershipServiceTest {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUp() {
        databaseCleanUp.cleanUp();
    }

    @DisplayName("멤버십 업데이트 by 더티 체킹")
    @Test
    void updateMembershipsByDirtyChecking() {
        //given

        //when
        long startTime = System.currentTimeMillis();
        membershipService.updateMembershipsByDirtyChecking();
        long totalTime = System.currentTimeMillis() - startTime;

        log.info("totalTime={}ms", totalTime);
        //then
    }


    @DisplayName("멤버십 업데이트 by 일괄 수정")
    @Test
    void updateMembershipsByBulkUpdate() {
        //given

        //when
        long startTime = System.currentTimeMillis();
        membershipService.updateMembershipsByBulkUpdate();
        long totalTime = System.currentTimeMillis() - startTime;

        log.info("totalTime={}ms", totalTime);

        //then
    }
}