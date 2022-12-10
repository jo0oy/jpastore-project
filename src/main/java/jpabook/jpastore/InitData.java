//package jpabook.jpastore;
//
//import jpabook.jpastore.domain.Address;
//import jpabook.jpastore.domain.Money;
//import jpabook.jpastore.domain.item.Book;
//import jpabook.jpastore.domain.item.Item;
//import jpabook.jpastore.domain.member.Member;
//import jpabook.jpastore.domain.membership.Grade;
//import jpabook.jpastore.domain.membership.Membership;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//
//@Profile("local")
//@Component
//@RequiredArgsConstructor
//public class InitData {
//
//    private final InitDataService initDataService;
//
//    @PostConstruct
//    public void init() {
//
//    }
//
//    @Component
//    static class InitDataService {
//
//        @PersistenceContext
//        private EntityManager em;
//
//        @Transactional
//        public void init() {
//
//            // 멤버 저장
//            for (int i = 1; i <= 5; i++) {
//                em.persist(Member.builder()
//                                .username("member" + i)
//                                .address(new Address("서울시", "송파" + i +"동", "01010"))
//                                .phoneNumber(String.format("010-%s%s%s%s-%s%s%s%s", i, i, i, i, i, i, i, i))
//                                .membership(Membership.builder().grade(Grade.SILVER)
//                                        .totalSpending(new Money(50000))
//                                        .build()).build());
//            }
//
//            for (int i = 0; i < 10; i++) {
//                em.persist();
//            }
//
//            }
//        }
//    }
//}
