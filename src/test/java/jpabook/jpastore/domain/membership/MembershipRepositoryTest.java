package jpabook.jpastore.domain.membership;

import jpabook.jpastore.config.DatabaseCleanUp;
import jpabook.jpastore.config.TestDBConfig;
import jpabook.jpastore.config.TestQuerydslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static jpabook.jpastore.domain.membership.Grade.*;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = "classpath:data/data-membership-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@Import({TestQuerydslConfig.class, TestDBConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.cleanUp();
    }

    @Test
    @DisplayName("[성공][repo] 등급별 벌크 업데이트 테스트")
    public void givenTestData_whenBulkUpdateGrade_thenWorksFine() {
        //when
        int silver = membershipRepository.bulkUpdateGrade(SILVER.getGreaterEqual(),
                SILVER.getLessThan(), SILVER);

        int gold = membershipRepository.bulkUpdateGrade(GOLD.getGreaterEqual(), GOLD.getLessThan(), GOLD);

        int vip = membershipRepository.bulkUpdateGrade(VIP.getGreaterEqual(), VIP);

        //then
        assertThat(silver).isEqualTo(48);
        assertThat(gold).isEqualTo(36);
        assertThat(vip).isEqualTo(36);
    }

    @Test
    @DisplayName("[성공][repo] 모든 멤버십 총 지출액 리셋 테스트")
    public void givenTestData_whenBulkUpdateTotalSpending_thenWorksFine() {

        //when
        var total = membershipRepository.bulkResetTotalSpending();

        var findOne = membershipRepository.findById(1L);


        //then
        assertThat(total).isEqualTo(120);
        assertThat(findOne).isPresent();
        assertThat(findOne.get().getTotalSpending().getValue()).isEqualTo(0);
    }
}