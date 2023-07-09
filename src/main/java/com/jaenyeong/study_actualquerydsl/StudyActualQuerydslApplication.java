package com.jaenyeong.study_actualquerydsl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudyActualQuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyActualQuerydslApplication.class, args);
    }

    // JPAQueryFactory를 Bean으로 등록 후 필요한 곳에서 주입 받을 수 있음
//    @Bean
//    JPAQueryFactory jpaQueryFactory(EntityManager em) {
//        return new JPAQueryFactory(em);
//    }
}
