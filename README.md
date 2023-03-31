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
