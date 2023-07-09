package com.jaenyeong.study_actualquerydsl.controller;

import com.jaenyeong.study_actualquerydsl.entity.Member;
import com.jaenyeong.study_actualquerydsl.entity.Team;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {
    private final InitMemberService initMemberService;

    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {
        @PersistenceContext
        private EntityManager em;

        // 스프링 Bean 라이프 사이클로 인해 @PostConstruct(init) 메서드에 직접 넣을 수 없음
        @Transactional
        public void init() {
            final Team teamA = new Team("Team A");
            final Team teamB = new Team("Team B");

            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                final Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }
}
