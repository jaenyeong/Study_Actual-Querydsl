package com.jaenyeong.study_actualquerydsl;

import com.jaenyeong.study_actualquerydsl.entity.Member;
import com.jaenyeong.study_actualquerydsl.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.jaenyeong.study_actualquerydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    private EntityManager em;

    // JPAQueryFactory를 필드에 선언하여 사용 가능
    // 스프링에서는 스레드마다 각각 다른 EntityManager 인스턴스를 사용하게 되어 있기 때문
    private JPAQueryFactory queryFactory;

    @BeforeEach
    void setup() {
        queryFactory = new JPAQueryFactory(em);

        final Team teamA = new Team("Team A");
        final Team teamB = new Team("Team B");
        em.persist(teamA);
        em.persist(teamB);

        final Member member1 = new Member("member1", 21, teamA);
        final Member member2 = new Member("member2", 22, teamA);
        final Member member3 = new Member("member3", 23, teamB);
        final Member member4 = new Member("member4", 24, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    void findMemberByJpql() {
        final String memberName = "member1";

        final Member foundedMember = em.createQuery(
                "select m from Member m where m.username = :username",
                Member.class
            )
            .setParameter("username", memberName)
            .getSingleResult();

        assertThat(foundedMember.getUsername()).isEqualTo(memberName);
    }

    @Test
    void findMemberByQueryDsl() {
        // 테스트 실행 전 QClass 생성 후 실행할 것 (compileQuerydsl)
        final String memberName = "member1";

        // Q-type 직접 별칭을 지정하여 사용하는 방법
//        final QMember member = new QMember("member");

        final Member findMember1 = queryFactory
            .select(member)
            .from(member)
            .where(member.username.eq(memberName))
            .fetchOne();

        assertThat(findMember1).isNotNull();
        assertThat(findMember1.getUsername()).isEqualTo(memberName);
    }

    @Test
    void search() {
        final String givenMemberName = "member1";

        final Member foundedMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq(givenMemberName)
                .and(member.age.eq(21))
            ).fetchOne();

        assertThat(foundedMember).isNotNull();
        assertThat(foundedMember.getUsername()).isEqualTo(givenMemberName);
    }

    @Test
    void searchWithParam() {
        final String givenMemberName = "member1";

        // search 테스트 메서드와 동일한 쿼리
        // 파라미터가 null인 경우 무시하기 때문에 편리하게 동적 쿼리 생성 가능
        final Member foundedMember = queryFactory
            .selectFrom(member)
            .where(
                member.username.eq(givenMemberName),
                member.age.eq(21)
            ).fetchOne();

        assertThat(foundedMember).isNotNull();
        assertThat(foundedMember.getUsername()).isEqualTo(givenMemberName);
    }
}
