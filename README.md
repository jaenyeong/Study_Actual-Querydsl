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
          show_sql: true
          format_sql: true
  
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
