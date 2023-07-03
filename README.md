# Study_Actual-Querydsl
### 인프런 실전! Querydsl (김영한)
https://www.inflearn.com/course/querydsl-%EC%8B%A4%EC%A0%84/dashboard
-----

## [Settings]
#### Project Name
* Study_Actual-Querydsl
#### java
* temurin 17
#### gradle
* IntelliJ IDEA gradle
#### Spring boot
* 3.0.5
-----

## [환경설정]

### IDEA 설정
* IDEA 기본 빌드 설정
  * 빌드 옵션을 기본 `Gradle`이 아닌 `IDEA`로 변경 (`Gradle` 사용하면 조금 느릴 수 있음)
  * `,` + `;` 단축키로 프로젝트 설정 진입
  * `Build, Execution, Deployment` > `Build Tools` > `Gradle` 경로
    * `Build and run using`, `Run tests using`을 모두 `Gradle (Defalut)` > `Intellij IDEA`로 변경
* `Lombok` 설정
  * 롬복 플러그인 사용을 위해 애너테이션 설정
  * `Build, Execution, Deployment` > `Compiler` > `Annotation Processors` 경로
    * `Enable annotation processing` 설정 선택

### H2 설정
* 설치
  * `brew install h2`
* 최초 접속 설정
  * 터미널에서 `h2 web` 명령을 통해 출력되는 URL을 브라우저로 복사하여 접속
    * `http://218.38.137.28:8082?key=bb92fef093e10753eeddc14add3b45805ce8d040b3f6b1de506059dca24bb239`
    * 접속이 안되는 경우 host 부분을 `localhost`로 변경하여 실행해볼 것
  * 브라우저에서 아래 설정 입력 후 접속(`connection`)
    * 설정명 : `Generic H2 (Embedded)` (또는 `Generic H2 (Server)`)
    * JDBC URL: `jdbc:h2:~/querydsl` (이렇게 접속할 경우 원격이 아닌 파일로 접속하게 됨)
  * 위와 같이 하지 않는 경우 에러 발생
    * `Database "/Users/jaenyeong/querydsl" not found, either pre-create it or allow remote database creation (not recommended in secure environments) [90149-214] 90149/90149`
* 설정 완료 확인
  * root 경로에 `querydsl.mv.db` 파일 생성 여부 확인
* H2 종료 후 재시작
  * `brew services start h2`
  * 재시작 시 JDBC URL은 `jdbc:h2:tcp://localhost/~/querydsl`으로 설정하여 접속
    * 기존 파일 모드로 실행하면 락이 걸리기 때문에 tcp를 통해 접속

### `application.yml` 파일 설정
* 확장자를 `.properties`에서 `.yml`로 변경
* 데이터소스 설정
  ```yaml
  spring:
    datasource:
      url: jdbc:h2:tcp://localhost/~/querydsl
      username: sa
      password:
      driver-class-name: org.h2.Driver
  
    jpa:
      hibernate:
        ddl-auto: create
      properties:
        hibernate:
          # 실행되는 모든 쿼리를 콘솔에 출력
  #        show_sql: true
          # 콘솔에 출력되는 쿼리에 포매팅을 가독성 좋게 변경
          format_sql: true
          # SQL 쿼리에 대한 정보를 주석으로 확인
          use_sql_comments: true
  
  logging.level:
    org.hibernate.SQL: debug
    org.hibernate.type: trace
  ```
* `StudyActualQuerydslApplicationTests` 파일에 `@Commit` 애너테이션 태깅

## QClass 생성(컴파일)
* gradle의 `other` > `compileQuerydsl`을 실행하여 JPA Entity에 대한 QClass 생성

### QClass 생성 에러 발생
에러 메시지 : `Attempt to recreate a file for type com.jaenyeong.study_actualquerydsl.entity.QTeam`
* 일반적으로 이미 생성되어 있는 경우에 재 생성 실패로 발생하는 에러
* gradle의 `build` > `clean`을 실행 후에 다시 생성 시도
---

## QueryDSL

### JPAQueryFactory
QueryDSL에서 쿼리를 생성 시 사용하는 객체로 `EntityManager`를 파라미터로 받음
* 멀티 스레드 환경에서도 동시성 문제 없이 사용할 수 있음
  * 스프링에서 `EntityManager`를 스레드 당 하나의 인스턴스를 사용하도록 관리하기 때문
  * 하지만 원래 `EntityManager` 자체는 스레드 세이프하지 않음

### Q-Type
사용 방법
* 직접 별칭 지정 `QMember qMember = new QMember("qMember");`
* 기본 인스턴스 사용 `QMember qMember = QMember.member;`

### 검색 조건
JPQL이 제공하는 모든 검색 조건 제공
* `.and()`, `or()`를 체이닝할 수 있음
* `select`, `from`을 `selectFrom`으로 대체해 사용 가능
* `where()` 파라미터로 추가할 시 `and` 조건이 추가됨
  * 이때 `null` 값인 파라미터는 무시되어 동적 쿼리를 편하게 생성할 수 있음

```
member.username.eq("member1") // username = 'member1'
member.username.ne("member1") //username != 'member1'
member.username.eq("member1").not() // username != 'member1'

member.username.isNotNull() // username is not null

member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
member.age.between(10,30) //between 10, 30

member.age.goe(30) // age >= 30

member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30

member.username.like("member%") // like 검색
member.username.contains("member") // like ‘%member%’ 검색
member.username.startsWith("member") //like ‘member%’ 검색
```

### 결과 조회
* `fetch()`
  * 리스트 조회 (데이터가 없다면 빈 리스트 반환)
* `fetchOne()`
  * 단 건 조회 (결과가 없으면 null, 여러 건이면 `com.querydsl.core.NonUniqueResultException`)
* `fetchFirst()`
  * `limit(1).fetchOne()`과 같음
* `fetchResults()`
  * 페이징 정보를 포함, `total count` 쿼리 추가 실행
* `fetchCount()`
  * `count` 쿼리로 변경해서 `count` 결과 조회

```
queryFactory.selectFrom(member).fetch();
queryFactory.selectFrom(member).fetchOne();

// limit(1).fetchOne() == fetchFirst()
queryFactory.selectFrom(member).limit(1).fetchOne();
queryFactory.selectFrom(member).fetchFirst();

// deprecated
// queryFactory.selectFrom(member).fetchResults();
// queryFactory.selectFrom(member).fetchCount();
```

* `fetchResults()`, `fetchCount()
  * 복잡한 쿼리에서는 제대로 동작하지 않을 수 있음
  * [persistence.blazebit 참조](https://persistence.blazebit.com/documentation/1.5/core/manual/en_US/index.html#querydsl-integration)

### 정렬
* `desc()`, `asc()` 일반 정렬
* `nullsLast()`, `nullsFirst()` null 데이터 순서 부여

```
em.persist(new Member(null, 100));
em.persist(new Member("member5", 100));
em.persist(new Member("member6", 100));

final List<Member> members = queryFactory
    .selectFrom(member)
    .where(member.age.eq(100))
    .orderBy(
        member.age.desc(),
        member.username.asc().nullsLast()
    )
    .fetch();

final Member member5 = members.get(0);
final Member member6 = members.get(1);
final Member memberNull = members.get(2);

assertThat(member5.getUsername()).isEqualTo("member5");
assertThat(member6.getUsername()).isEqualTo("member6");
assertThat(memberNull.getUsername()).isNull();
```

### 페이징
* 성능 최적화를 위해 `count` 쿼리를 별도로 작성하는 것을 고려해야 함
  * `fetchResults()`는 deprecated 상태이기 때문에 가급적 사용하지 않는 방향으로 구현할 것

```
final List<Member> members = queryFactory
    .selectFrom(member)
    .orderBy(member.username.desc())
    .offset(1)
    .limit(2)
    .fetch();

assertThat(members.size()).isEqualTo(2);
```

### 집합
* JPQL이 제공하는 모든 집합 함수 지원

```
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
```

### groupBy
* `groupBy`의 결과를 제한할 때 `having` 사용 가능

```
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
```

### 조인
* `join()`, `innerJoin()`
  * 내부 조인(inner join)
* `leftJoin()`
  * left 외부 조인(left outer join)
* `rightJoin()`
  * right 외부 조인(right outer join)

```
final List<Member> members = queryFactory
    .selectFrom(member)
    .join(member.team, team)
    .where(team.name.eq("Team A"))
    .fetch();

assertThat(members).extracting("username").containsExactly("member1", "member2");
```

### 세타 조인
* 연관관계가 없는 필드로 조인

```
em.persist(new Member("Team A"));
em.persist(new Member("Team B"));
em.persist(new Member("Team C"));

final List<Member> members = queryFactory
    .select(member)
    .from(member, team)
    .where(member.username.eq(team.name))
    .fetch();

assertThat(members).extracting("username").containsExactly("Team A", "Team B");
```

### 조인 필터링
* `on` 절은 `outer join`과 함께 사용할 때와는 다르게 `inner join`과 함께 사용하면 `where` 절에 사용한 것과 동일

```
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
```

### 연관관계가 없는 엔티티 아우터 조인
* 하이버네이트 5.1부터 서로 관계가 없는 필드로 외부 조인하는 기능이 추가됨
* `leftJoin()`은 엔티티 하나만 들어감
  * 일반 조인 : `leftJoin(member.team, team)`
  * on 조인 : `from(member).leftJoin(team).on(member.username.eq(team.name))`

```
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
```

### 페치 조인
* `join()`, `leftJoin()` 등 조인 뒤에 `fetchJoin()` 추가

```
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
```

### 서브 쿼리
* JPA JPQL은 `from` 절 서브 쿼리(인라인 뷰)를 지원하지 않음 (따라서 Querydsl도 지원하지 않음)
* `select` 절 서브 쿼리는 지원
* `from` 절 서브 쿼리는 조인을 통해 해결하거나 쿼리를 분리해 구현 또는 네이티브 쿼리를 사용

```
### eq
final QMember subQMember = new QMember("memberSubQ");

final List<Member> foundMembers = queryFactory
    .selectFrom(member)
    .where(member.age.eq(
        JPAExpressions
            .select(subQMember.age.max())
            .from(subQMember)
    )).fetch();

final Member foundMember = foundMembers.get(0);

assertThat(foundMembers).extracting("age").containsExactly(24);
assertThat(foundMember.getAge()).isEqualTo(24);

### Goe
final QMember subQMember = new QMember("memberSubQ");

final List<Member> foundMembers = queryFactory
    .selectFrom(member)
    .where(member.age.goe(
        JPAExpressions
            .select(subQMember.age.avg())
            .from(subQMember)
    )).fetch();

assertThat(foundMembers).extracting("age").containsExactly(23, 24);

### In
final QMember subQMember = new QMember("memberSubQ");

final List<Member> foundMembers = queryFactory
    .selectFrom(member)
    .where(member.age.in(
        JPAExpressions
            .select(subQMember.age)
            .from(subQMember)
            .where(subQMember.age.gt(21))
    )).fetch();

assertThat(foundMembers).extracting("age").containsExactly(22, 23, 24);

### select SubQuery
```

### Case
* `select`, `where`, `order by`에서 사용할 수 있음
* 복잡한 경우 `CaseBuilder` 사용

```
### Basic case
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

### Complex case
final List<String> ageResults = queryFactory
    .select(new CaseBuilder()
        .when(member.age.between(0, 21)).then("0 ~ 21살")
        .when(member.age.between(21, 23)).then("21 ~ 23살")
        .otherwise("24살 이상")
    )
    .from(member)
    .fetch();

assertThat(ageResults).containsExactly("0 ~ 21살", "21 ~ 23살", "21 ~ 23살", "24살 이상");

### Case with rankPath
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
```

### 상수, 문자 더하기
* `Expressions.constant()` 사용
* `member.age.stringValue()`는 문자가 아닌 타입을 문자열로 변환
  * `enum` 타입을 처리할 때도 사용됨

```
### Constant
final List<Tuple> queryResults = queryFactory
    .select(member.username, Expressions.constant("A"))
    .from(member)
    .fetch();

assertThat(queryResults).extracting(result -> result.get(0, String.class))
    .containsExactly("member1", "member2", "member3", "member4");
assertThat(queryResults).extracting(result -> result.get(1, String.class))
    .containsExactly("A", "A", "A", "A");

### Concat String
final String username = queryFactory
    .select(member.username.concat("_").concat(member.age.stringValue()))
    .from(member)
    .where(member.username.eq("member1"))
    .fetchOne();

assertThat(username).isEqualTo("member1_21");
```

### 프로젝션
* `select` 대상 지정
  * 타입이 하나면 명확하게 지정가능하나 둘 이상이면 튜플, DTO 등으로 조회
* `jpql`에서는 `new` 키워드로 생성해서 사용해야하며 생성자 방식만 지원
* `querydsl`에서는 `setter`, `field`, `constructor` 3가지 방식 지원
  * `record`클래스는 기본적으로 불변이기 때문에 `setter`, `field` 방식으로 사용할 수 없고 `constructor` 방식만 사용 가능
* DTO의 프로퍼티명과 다를 때 별칭을 주어 처리
  * `ExpressionUtils.as(#{source}, #{alias})` 방식은 필드, 서브 쿼리 별칭에 사용
  * `username.as("name")` 방식은 필드 별칭에 사용
* `MemberDto` 클래스 생성자에 `@QueryProjection` 애너테이션을 태깅해서 QClass로 컴파일해 사용 가능
  * 하지만 DTO 클래스에 Querydsl에 대한 의존성이 생김
  * DTO 자체가 어떠한 레이어에서도 사용할 수 있기 때문에 querydsl

```
## assert 구문 생략

### projection
final List<String> queryResults = queryFactory
    .select(member.username)
    .from(member)
    .fetch();

### tupleProjection
final List<Tuple> queryResults = queryFactory
    .select(member.username, member.age)
    .from(member)
    .fetch();

### jpqlDtoProjection
final List<MemberDto> memberDtos = em.createQuery(
        "select new com.jaenyeong.study_actualquerydsl.dto.MemberDto(m.username, m.age) from Member m ",
        MemberDto.class
    )
    .getResultList();

### querydslDtoBySetterProjection
final List<MemberDto> memberDtos = queryFactory
    .select(Projections.bean(MemberDto.class, member.username, member.age))
    .from(member)
    .fetch();

### querydslDtoByFieldProjection
final List<MemberDto> memberDtos = queryFactory
    .select(Projections.fields(MemberDto.class, member.username, member.age))
    .from(member)
    .fetch();
            
### querydslDtoByConstructorProjection
final List<MemberRecordDto> memberRecordDtos = queryFactory
    .select(Projections.constructor(MemberRecordDto.class, member.username, member.age))
    .from(member)
    .fetch();
    
### querydslOtherDtoByFieldProjection
final QMember subQMember = new QMember("subQMember");

final List<UserDto> memberDtos = queryFactory
    .select(Projections.fields(
        UserDto.class,
        member.username.as("name"),
        ExpressionUtils.as(JPAExpressions.select(subQMember.age.max()).from(subQMember), "age"))
    )
    .from(member)
    .fetch();

### querydslOtherDtoByConstructorProjection
final List<UserDto> userDtos = queryFactory
    .select(Projections.constructor(UserDto.class, member.username, member.age))
    .from(member)
    .fetch();

### querydslDtoByQueryProjection
final List<MemberDto> memberDtos = queryFactory
    .select(new QMemberDto(member.username, member.age))
    .from(member)
    .fetch();
```
