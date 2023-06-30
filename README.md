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
