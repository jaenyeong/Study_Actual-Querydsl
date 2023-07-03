package com.jaenyeong.study_actualquerydsl;

import com.jaenyeong.study_actualquerydsl.dto.MemberDto;
import com.jaenyeong.study_actualquerydsl.dto.MemberRecordDto;
import com.jaenyeong.study_actualquerydsl.dto.QMemberDto;
import com.jaenyeong.study_actualquerydsl.dto.UserDto;
import com.jaenyeong.study_actualquerydsl.entity.Member;
import com.jaenyeong.study_actualquerydsl.entity.QMember;
import com.jaenyeong.study_actualquerydsl.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jaenyeong.study_actualquerydsl.entity.QMember.member;
import static com.jaenyeong.study_actualquerydsl.entity.QTeam.team;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    private EntityManager em;

    // JPAQueryFactory를 필드에 선언하여 사용 가능
    // 스프링에서는 스레드마다 각각 다른 EntityManager 인스턴스를 사용하게 되어 있기 때문
    private JPAQueryFactory queryFactory;

    // EntityManager를 생성하는 객체
    @PersistenceUnit
    private EntityManagerFactory emf;

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

        final Member foundMember = em.createQuery(
                "select m from Member m where m.username = :username",
                Member.class
            )
            .setParameter("username", memberName)
            .getSingleResult();

        assertThat(foundMember.getUsername()).isEqualTo(memberName);
    }

    @Test
    void findMemberByQueryDsl() {
        // 테스트 실행 전 QClass 생성 후 실행할 것 (compileQuerydsl)
        final String memberName = "member1";

        // Q-type 직접 별칭을 지정하여 사용하는 방법
//        final QMember member = new QMember("member");

        final Member foundMember1 = queryFactory
            .select(member)
            .from(member)
            .where(member.username.eq(memberName))
            .fetchOne();

        assertThat(foundMember1).isNotNull();
        assertThat(foundMember1.getUsername()).isEqualTo(memberName);
    }

    @Test
    void search() {
        final String givenMemberName = "member1";

        final Member foundMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq(givenMemberName)
                .and(member.age.eq(21))
            ).fetchOne();

        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getUsername()).isEqualTo(givenMemberName);
    }

    @Test
    void searchWithParam() {
        final String givenMemberName = "member1";

        // search 테스트 메서드와 동일한 쿼리
        // 파라미터가 null인 경우 무시하기 때문에 편리하게 동적 쿼리 생성 가능
        final Member foundMember = queryFactory
            .selectFrom(member)
            .where(
                member.username.eq(givenMemberName),
                member.age.eq(21)
            ).fetchOne();

        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getUsername()).isEqualTo(givenMemberName);
    }

    @Test
    void resultFetch() {
        final List<Member> fetchMembers = queryFactory.selectFrom(member).fetch();
        // `com.querydsl.core.NonUniqueResultException` 예외 발생
//        final Member fetchOneMember = queryFactory.selectFrom(member).fetchOne();

        // limit(1).fetchOne() == fetchFirst()
        final Member limitFetchFirstMember = queryFactory.selectFrom(member).limit(1).fetchOne();
        final Member fetchFirstMember = queryFactory.selectFrom(member).fetchFirst();

        // fetchResults()는 deprecated
//        final QueryResults<Member> fetchResultsMember = queryFactory.selectFrom(member).fetchResults();
//        final List<Member> fetchResultsMembers = fetchResultsMember.getResults();
//        final long total = fetchResultsMember.getTotal();

        // fetchCount()는 deprecated
//        final long fetchCountResult = queryFactory.selectFrom(member).fetchCount();

        assertThat(fetchMembers).isNotEmpty();
        assertThat(limitFetchFirstMember).isEqualTo(fetchFirstMember);
    }

    @Test
    void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        final List<Member> foundMembers = queryFactory
            .selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(
                member.age.desc(),
                member.username.asc().nullsLast()
            )
            .fetch();

        final Member member5 = foundMembers.get(0);
        final Member member6 = foundMembers.get(1);
        final Member memberNull = foundMembers.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    void paging() {
        final List<Member> foundMembers = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetch();

        // fetchResults()는 deprecated
//        final QueryResults<Member> queryResults = queryFactory
//            .selectFrom(member)
//            .orderBy(member.username.desc())
//            .offset(1)
//            .limit(2)
//            .fetchResults();

        assertThat(foundMembers.size()).isEqualTo(2);
    }

    @Test
    void aggregation() {
        final NumberExpression<Long> countExp = member.count();
        final NumberExpression<Integer> ageSumExp = member.age.sum();
        final NumberExpression<Double> ageAvgExp = member.age.avg();
        final NumberExpression<Integer> ageMaxExp = member.age.max();
        final NumberExpression<Integer> ageMinExp = member.age.min();

        final List<Tuple> memberSubQueryResults = queryFactory
            .select(
                countExp,
                ageSumExp,
                ageAvgExp,
                ageMaxExp,
                ageMinExp
            )
            .from(member)
            .fetch();

        final Tuple resultTuple = memberSubQueryResults.get(0);

        assertThat(resultTuple.get(countExp)).isEqualTo(4);
        assertThat(resultTuple.get(ageSumExp)).isEqualTo(90);
        assertThat(resultTuple.get(ageAvgExp)).isEqualTo(22.5);
        assertThat(resultTuple.get(ageMaxExp)).isEqualTo(24);
        assertThat(resultTuple.get(ageMinExp)).isEqualTo(21);
    }

    @Test
    void groupBy() {
        final List<Tuple> queryResults = queryFactory
            .select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch();

        final Tuple teamA = queryResults.get(0);
        final Tuple teamB = queryResults.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("Team A");
        assertThat(teamA.get(member.age.avg())).isEqualTo(21.5);

        assertThat(teamB.get(team.name)).isEqualTo("Team B");
        assertThat(teamB.get(member.age.avg())).isEqualTo(23.5);
    }

    @Test
    void join() {
        final List<Member> foundMembers = queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("Team A"))
            .fetch();

        assertThat(foundMembers).extracting("username").containsExactly("member1", "member2");
    }

    @Test
    void thetaJoin() {
        em.persist(new Member("Team A"));
        em.persist(new Member("Team B"));
        em.persist(new Member("Team C"));

        final List<Member> foundMembers = queryFactory
            .select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch();

        assertThat(foundMembers).extracting("username").containsExactly("Team A", "Team B");
    }

    @Test
    void outerJoinOnFiltering() {
        final List<Tuple> leftJoinOnResults = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(member.team, team)
            .on(team.name.eq("Team A"))
            .fetch();

        final Member member1 = leftJoinOnResults.get(0).get(member);
        final Member member2 = leftJoinOnResults.get(1).get(member);
        final Member member3 = leftJoinOnResults.get(2).get(member);
        final Member member4 = leftJoinOnResults.get(3).get(member);

        final Team teamA = leftJoinOnResults.get(0).get(team);
        final Team teamB = leftJoinOnResults.get(2).get(team);

        assertThat(member1).isNotNull();
        assertThat(member1.getUsername()).isEqualTo("member1");
        assertThat(member2).isNotNull();
        assertThat(member2.getUsername()).isEqualTo("member2");
        assertThat(member3).isNotNull();
        assertThat(member3.getUsername()).isEqualTo("member3");
        assertThat(member4).isNotNull();
        assertThat(member4.getUsername()).isEqualTo("member4");

        assertThat(teamA).isNotNull();
        assertThat(teamA.getName()).isEqualTo("Team A");
        assertThat(teamB).isNull();
    }

    @Test
    void innerJoinOnFiltering() {
        // inner join 사용 후에 on은 where 절에 필터링 조건을 넣는 것과 동일
        final List<Tuple> joinWhereResults = queryFactory
            .select(member, team)
            .from(member)
            .join(member.team, team)
            .where(team.name.eq("Team A"))
            .fetch();

        final List<Tuple> joinOnResults = queryFactory
            .select(member, team)
            .from(member)
            .join(member.team, team)
            .on(team.name.eq("Team A"))
            .fetch();

        final Member whereMember1 = joinWhereResults.get(0).get(member);
        final Member whereMember2 = joinWhereResults.get(1).get(member);
        final Member onMember1 = joinOnResults.get(0).get(member);
        final Member onMember2 = joinOnResults.get(1).get(member);

        assertThat(joinWhereResults.size()).isEqualTo(joinOnResults.size());

        assertThat(whereMember1).isEqualTo(onMember1);
        assertThat(whereMember2).isEqualTo(onMember2);
    }

    @Test
    void unrelatedOuterJoinOnFiltering() {
        em.persist(new Member("Team A"));
        em.persist(new Member("Team B"));
        em.persist(new Member("Team C"));

        final List<Tuple> queryResults = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(team)
            .on(member.username.eq(team.name))
            .fetch();

        final Tuple tuple4 = queryResults.get(4);
        final Tuple tuple5 = queryResults.get(5);
        final Tuple tuple6 = queryResults.get(6);

        final Member teamAMember = tuple4.get(member);
        final Member teamBMember = tuple5.get(member);
        final Member teamCMember = tuple6.get(member);

        final Team teamA = tuple4.get(team);
        final Team teamB = tuple5.get(team);
        final Team nullTeam = tuple6.get(team);

        assertThat(teamAMember).isNotNull();
        assertThat(teamBMember).isNotNull();
        assertThat(teamCMember).isNotNull();
        assertThat(teamA).isNotNull();
        assertThat(teamB).isNotNull();
        assertThat(nullTeam).isNull();

        assertThat(teamAMember.getUsername()).isEqualTo("Team A");
        assertThat(teamBMember.getUsername()).isEqualTo("Team B");
        assertThat(teamCMember.getUsername()).isEqualTo("Team C");

        assertThat(teamA.getName()).isEqualTo("Team A");
        assertThat(teamB.getName()).isEqualTo("Team B");
    }

    @Test
    void fetchJoin() {
        em.flush();
        em.clear();

        // 페치 조인 미적용
        final Member foundMember1 = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne();
        // 엔티티의 초기화 상태 확인
        final boolean foundMember1Loaded = emf.getPersistenceUnitUtil().isLoaded(foundMember1.getTeam());

        // 페치 조인 적용
        final Member foundMember2 = queryFactory
            .selectFrom(member)
            .join(member.team, team).fetchJoin()
            .where(member.username.eq("member2"))
            .fetchOne();
        // 엔티티의 초기화 상태 확인
        final boolean foundMember2Loaded = emf.getPersistenceUnitUtil().isLoaded(foundMember2.getTeam());

        assertThat(foundMember1Loaded).as("페치 조인 미적용").isFalse();
        assertThat(foundMember2Loaded).as("페치 조인 적용").isTrue();
    }

    @Test
    void subQueryEq() {
        final QMember subQMember = new QMember("memberSubQ");

        final List<Member> foundMembers = queryFactory
            .selectFrom(member)
            .where(member.age.eq(
                select(subQMember.age.max())
                    .from(subQMember)
            )).fetch();

        final Member foundMember = foundMembers.get(0);

        assertThat(foundMembers).extracting("age").containsExactly(24);
        assertThat(foundMember.getAge()).isEqualTo(24);
    }

    @Test
    void subQueryGoe() {
        final QMember subQMember = new QMember("memberSubQ");

        final List<Member> foundMembers = queryFactory
            .selectFrom(member)
            .where(member.age.goe(
                select(subQMember.age.avg())
                    .from(subQMember)
            )).fetch();

        assertThat(foundMembers).extracting("age").containsExactly(23, 24);
    }

    @Test
    void subQueryIn() {
        final QMember subQMember = new QMember("memberSubQ");

        final List<Member> foundMembers = queryFactory
            .selectFrom(member)
            .where(member.age.in(
                select(subQMember.age)
                    .from(subQMember)
                    .where(subQMember.age.gt(21))
            )).fetch();

        assertThat(foundMembers).extracting("age").containsExactly(22, 23, 24);
    }

    @Test
    void selectSubQuery() {
        final QMember subQMember = new QMember("memberSubQ");

        final List<Tuple> queryResults = queryFactory
            .select(member.username,
                select(subQMember.age.avg())
                    .from(subQMember)
            )
            .from(member)
            .fetch();

        assertThat(queryResults).extracting(result -> result.get(0, String.class))
            .containsExactly("member1", "member2", "member3", "member4");
        assertThat(queryResults).extracting(result -> result.get(1, Double.class))
            .containsExactly(22.5, 22.5, 22.5, 22.5);
    }

    @Test
    void basicCase() {
        final List<String> ageResults = queryFactory
            .select(
                member.age
                    .when(21).then("스물한 살")
                    .when(22).then("스물 두살")
                    .otherwise("스물셋 이상")
            )
            .from(member)
            .fetch();

        assertThat(ageResults).containsExactly("스물한 살", "스물 두살", "스물셋 이상", "스물셋 이상");
    }

    @Test
    void complexCase() {
        final List<String> ageResults = queryFactory
            .select(new CaseBuilder()
                .when(member.age.between(0, 21)).then("0 ~ 21살")
                .when(member.age.between(22, 23)).then("22 ~ 23살")
                .otherwise("24살 이상")
            )
            .from(member)
            .fetch();

        assertThat(ageResults).containsExactly("0 ~ 21살", "22 ~ 23살", "22 ~ 23살", "24살 이상");
    }

    @Test
    void caseWithRankPath() {
        // 조회 데이터의 정렬 순서를 변경할 때
        // [1] 24살 이상
        // [2] 0 ~ 21살
        // [3] 21 ~ 23살

        final NumberExpression<Integer> rankPath = new CaseBuilder()
            .when(member.age.between(0, 21)).then(2)
            .when(member.age.between(22, 23)).then(1)
            .otherwise(3);

        final List<Tuple> queryResults = queryFactory
            .select(member.username, member.age, rankPath)
            .from(member)
            .orderBy(rankPath.desc())
            .fetch();

        assertThat(queryResults).extracting(result -> result.get(0, String.class))
            .containsExactly("member4", "member1", "member2", "member3");
        assertThat(queryResults).extracting(result -> result.get(1, Integer.class))
            .containsExactly(24, 21, 22, 23);
    }

    @Test
    void constant() {
        // JPQL에서는 "A"가 없고 가져온 결과값에서 처리됨
        final List<Tuple> queryResults = queryFactory
            .select(member.username, Expressions.constant("A"))
            .from(member)
            .fetch();

        assertThat(queryResults).extracting(result -> result.get(0, String.class))
            .containsExactly("member1", "member2", "member3", "member4");
        assertThat(queryResults).extracting(result -> result.get(1, String.class))
            .containsExactly("A", "A", "A", "A");
    }

    @Test
    void concatString() {
        // {username}_{age}
        final String username = queryFactory
            .select(member.username.concat("_").concat(member.age.stringValue()))
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne();

        assertThat(username).isEqualTo("member1_21");
    }

    @Test
    void projection() {
        final List<String> queryResults = queryFactory
            .select(member.username)
            .from(member)
            .fetch();

        assertThat(queryResults.size()).isEqualTo(4);
        assertThat(queryResults.get(0)).isEqualTo("member1");
        assertThat(queryResults.get(1)).isEqualTo("member2");
        assertThat(queryResults.get(2)).isEqualTo("member3");
        assertThat(queryResults.get(3)).isEqualTo("member4");
    }

    @Test
    void tupleProjection() {
        final List<Tuple> queryResults = queryFactory
            .select(member.username, member.age)
            .from(member)
            .fetch();

        assertThat(queryResults.size()).isEqualTo(4);
        assertThat(queryResults.get(0).get(member.username)).isEqualTo("member1");
        assertThat(queryResults.get(0).get(member.age)).isEqualTo(21);
        assertThat(queryResults.get(1).get(member.username)).isEqualTo("member2");
        assertThat(queryResults.get(1).get(member.age)).isEqualTo(22);
        assertThat(queryResults.get(2).get(member.username)).isEqualTo("member3");
        assertThat(queryResults.get(2).get(member.age)).isEqualTo(23);
        assertThat(queryResults.get(3).get(member.username)).isEqualTo("member4");
        assertThat(queryResults.get(3).get(member.age)).isEqualTo(24);
    }

    @Test
    void jpqlDtoProjection() {
        final List<MemberDto> memberDtos = em.createQuery(
                "select new com.jaenyeong.study_actualquerydsl.dto.MemberDto(m.username, m.age) from Member m ",
                MemberDto.class
            )
            .getResultList();

        assertThat(memberDtos.size()).isEqualTo(4);
        assertThat(memberDtos.get(0).getUsername()).isEqualTo("member1");
        assertThat(memberDtos.get(0).getAge()).isEqualTo(21);
        assertThat(memberDtos.get(1).getUsername()).isEqualTo("member2");
        assertThat(memberDtos.get(1).getAge()).isEqualTo(22);
        assertThat(memberDtos.get(2).getUsername()).isEqualTo("member3");
        assertThat(memberDtos.get(2).getAge()).isEqualTo(23);
        assertThat(memberDtos.get(3).getUsername()).isEqualTo("member4");
        assertThat(memberDtos.get(3).getAge()).isEqualTo(24);
    }

    @Test
    void querydslDtoBySetterProjection() {
        final List<MemberDto> memberDtos = queryFactory
            .select(Projections.bean(MemberDto.class, member.username, member.age))
            .from(member)
            .fetch();

        assertThat(memberDtos.size()).isEqualTo(4);
        assertThat(memberDtos.get(0).getUsername()).isEqualTo("member1");
        assertThat(memberDtos.get(0).getAge()).isEqualTo(21);
        assertThat(memberDtos.get(1).getUsername()).isEqualTo("member2");
        assertThat(memberDtos.get(1).getAge()).isEqualTo(22);
        assertThat(memberDtos.get(2).getUsername()).isEqualTo("member3");
        assertThat(memberDtos.get(2).getAge()).isEqualTo(23);
        assertThat(memberDtos.get(3).getUsername()).isEqualTo("member4");
        assertThat(memberDtos.get(3).getAge()).isEqualTo(24);
    }

    @Test
    void querydslDtoByFieldProjection() {
        final List<MemberDto> memberDtos = queryFactory
            .select(Projections.fields(MemberDto.class, member.username, member.age))
            .from(member)
            .fetch();

        assertThat(memberDtos.size()).isEqualTo(4);
        assertThat(memberDtos.get(0).getUsername()).isEqualTo("member1");
        assertThat(memberDtos.get(0).getAge()).isEqualTo(21);
        assertThat(memberDtos.get(1).getUsername()).isEqualTo("member2");
        assertThat(memberDtos.get(1).getAge()).isEqualTo(22);
        assertThat(memberDtos.get(2).getUsername()).isEqualTo("member3");
        assertThat(memberDtos.get(2).getAge()).isEqualTo(23);
        assertThat(memberDtos.get(3).getUsername()).isEqualTo("member4");
        assertThat(memberDtos.get(3).getAge()).isEqualTo(24);
    }

    @Test
    void querydslDtoByConstructorProjection() {
        final List<MemberRecordDto> memberRecordDtos = queryFactory
            .select(Projections.constructor(MemberRecordDto.class, member.username, member.age))
            .from(member)
            .fetch();

        assertThat(memberRecordDtos.size()).isEqualTo(4);
        assertThat(memberRecordDtos.get(0).username()).isEqualTo("member1");
        assertThat(memberRecordDtos.get(0).age()).isEqualTo(21);
        assertThat(memberRecordDtos.get(1).username()).isEqualTo("member2");
        assertThat(memberRecordDtos.get(1).age()).isEqualTo(22);
        assertThat(memberRecordDtos.get(2).username()).isEqualTo("member3");
        assertThat(memberRecordDtos.get(2).age()).isEqualTo(23);
        assertThat(memberRecordDtos.get(3).username()).isEqualTo("member4");
        assertThat(memberRecordDtos.get(3).age()).isEqualTo(24);
    }

    @Test
    void querydslOtherDtoByFieldProjection() {
        final QMember subQMember = new QMember("subQMember");

        final List<UserDto> memberDtos = queryFactory
            .select(Projections.fields(
                UserDto.class,
                member.username.as("name"),
                ExpressionUtils.as(JPAExpressions.select(subQMember.age.max()).from(subQMember), "age"))
            )
            .from(member)
            .fetch();

        assertThat(memberDtos.size()).isEqualTo(4);
        assertThat(memberDtos.get(0).getName()).isEqualTo("member1");
        assertThat(memberDtos.get(0).getAge()).isEqualTo(24);
        assertThat(memberDtos.get(1).getName()).isEqualTo("member2");
        assertThat(memberDtos.get(1).getAge()).isEqualTo(24);
        assertThat(memberDtos.get(2).getName()).isEqualTo("member3");
        assertThat(memberDtos.get(2).getAge()).isEqualTo(24);
        assertThat(memberDtos.get(3).getName()).isEqualTo("member4");
        assertThat(memberDtos.get(3).getAge()).isEqualTo(24);
    }

    @Test
    void querydslOtherDtoByConstructorProjection() {
        final List<UserDto> userDtos = queryFactory
            .select(Projections.constructor(UserDto.class, member.username, member.age))
            .from(member)
            .fetch();

        assertThat(userDtos.size()).isEqualTo(4);
        assertThat(userDtos.get(0).getName()).isEqualTo("member1");
        assertThat(userDtos.get(0).getAge()).isEqualTo(21);
        assertThat(userDtos.get(1).getName()).isEqualTo("member2");
        assertThat(userDtos.get(1).getAge()).isEqualTo(22);
        assertThat(userDtos.get(2).getName()).isEqualTo("member3");
        assertThat(userDtos.get(2).getAge()).isEqualTo(23);
        assertThat(userDtos.get(3).getName()).isEqualTo("member4");
        assertThat(userDtos.get(3).getAge()).isEqualTo(24);
    }

    @Test
    void querydslDtoByQueryProjection() {
        final List<MemberDto> memberDtos = queryFactory
            .select(new QMemberDto(member.username, member.age))
            .from(member)
            .fetch();

        assertThat(memberDtos.size()).isEqualTo(4);
        assertThat(memberDtos.get(0).getUsername()).isEqualTo("member1");
        assertThat(memberDtos.get(0).getAge()).isEqualTo(21);
        assertThat(memberDtos.get(1).getUsername()).isEqualTo("member2");
        assertThat(memberDtos.get(1).getAge()).isEqualTo(22);
        assertThat(memberDtos.get(2).getUsername()).isEqualTo("member3");
        assertThat(memberDtos.get(2).getAge()).isEqualTo(23);
        assertThat(memberDtos.get(3).getUsername()).isEqualTo("member4");
        assertThat(memberDtos.get(3).getAge()).isEqualTo(24);
    }
}
