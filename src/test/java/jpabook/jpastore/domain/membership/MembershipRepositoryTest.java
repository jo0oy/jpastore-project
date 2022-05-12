package jpabook.jpastore.domain.membership;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static jpabook.jpastore.domain.membership.Grade.*;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = "classpath:data/data-h2-membership-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
@DataJpaTest
class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    @DisplayName("bulk update membership 성공 테스트")
    public void bulkUpdateMembership_성공_테스트() {
        //when
        int silver = membershipRepository.bulkUpdateGrade(SILVER.getGreaterEqual(),
                SILVER.getLessThan(), SILVER);

        int gold = membershipRepository.bulkUpdateGrade(GOLD.getGreaterEqual(), GOLD.getLessThan(), GOLD);

        int vip = membershipRepository.bulkUpdateGrade(VIP.getGreaterEqual(), VIP);

        //then
        assertThat(silver).isEqualTo(4);
        assertThat(gold).isEqualTo(3);
        assertThat(vip).isEqualTo(3);
    }

    @Test
    @DisplayName("bulk reset 총지출액 성공 테스트")
    public void bulkResetTotalSpending_성공_테스트() {

        //when
        int total = membershipRepository.bulkResetTotalSpending();

        Optional<Membership> findOne = membershipRepository.findById(1L);


        //then
        assertThat(total).isEqualTo(10);
        assertThat(findOne).isPresent();
        assertThat(findOne.get().getTotalSpending().getValue()).isEqualTo(0);
    }

    @TestConfiguration
    static class TestConfig {

        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

}