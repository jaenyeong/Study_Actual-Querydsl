package com.jaenyeong.study_actualquerydsl;

import com.jaenyeong.study_actualquerydsl.entity.Hello;
import com.jaenyeong.study_actualquerydsl.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Commit
class StudyActualQuerydslApplicationTests {

    @Autowired
    private EntityManager em;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        final JPAQueryFactory query = new JPAQueryFactory(em);
        final QHello qHello = QHello.hello;

        final Hello result = query.selectFrom(qHello).fetchOne();

        assertThat(result).isEqualTo(hello);
        assertThat(Objects.requireNonNull(result).getId()).isEqualTo(hello.getId());
    }
}
