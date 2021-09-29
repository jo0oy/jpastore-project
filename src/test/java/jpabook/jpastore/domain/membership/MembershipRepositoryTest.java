package jpabook.jpastore.domain.membership;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "classpath:data/data-h2-test.sql")
@DataJpaTest
class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    @DisplayName("1. 일대일 양방향 매핑에서 주인이 아닌 쪽에서 조회할 경우 지연로딩 동작 안함.")
    public void lazy_loading_not_working() {
        //given

        //when
        List<Membership> memberships = membershipRepository.findAll();

        System.out.println("findAll() method ends...");

//        System.out.println("extracting member name...");
//        List<String> members = memberships.stream().map(o -> o.getMember().getName())
//                .collect(Collectors.toList());
//        System.out.println("stream ends....");
//
//        //then
//        assertThat(members.get(0)).isEqualTo("member1");
//        assertThat(members.size()).isEqualTo(3);
    }

}