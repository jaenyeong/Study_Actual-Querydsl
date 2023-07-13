package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.dto.MemberSearchCondition;
import com.jaenyeong.study_actualquerydsl.dto.MemberTeamDto;
import com.jaenyeong.study_actualquerydsl.entity.Member;
import com.jaenyeong.study_actualquerydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberSupportRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private MemberSupportRepository memberSupportRepository;

    @Test
    void basicSelectTest() {
        final Team teamA = new Team("Team A");
        final Team teamB = new Team("Team B");
        em.persist(teamA);
        em.persist(teamB);

        final Member member1 = new Member("member1", 21, teamA);
        final Member member2 = new Member("member2", 23, teamA);
        final Member member3 = new Member("member3", 25, teamB);
        final Member member4 = new Member("member4", 27, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        final List<Member> members = memberSupportRepository.basicSelect();

        assertThat(members.size()).isEqualTo(4);
        assertThat(members).extracting("username").containsExactly("member1", "member2", "member3", "member4");
    }

    @Test
    void searchPageByApplyPageTest() {
        final Team teamA = new Team("Team A");
        final Team teamB = new Team("Team B");
        em.persist(teamA);
        em.persist(teamB);

        final Member member1 = new Member("member1", 21, teamA);
        final Member member2 = new Member("member2", 23, teamA);
        final Member member3 = new Member("member3", 25, teamB);
        final Member member4 = new Member("member4", 27, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        final MemberSearchCondition condition = new MemberSearchCondition();
        final PageRequest pageRequest = PageRequest.of(0, 3);

        final Page<Member> foundMembers = memberSupportRepository.searchPageByApplyPage(condition, pageRequest);

        assertThat(foundMembers.getSize()).isEqualTo(3);
        assertThat(foundMembers).extracting("username").containsExactly("member1", "member2", "member3");
    }

    @Test
    void searchPageByApplySimplePaginationTest() {
        final Team teamA = new Team("Team A");
        final Team teamB = new Team("Team B");
        em.persist(teamA);
        em.persist(teamB);

        final Member member1 = new Member("member1", 21, teamA);
        final Member member2 = new Member("member2", 23, teamA);
        final Member member3 = new Member("member3", 25, teamB);
        final Member member4 = new Member("member4", 27, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        final MemberSearchCondition condition = new MemberSearchCondition();
        final PageRequest pageRequest = PageRequest.of(0, 3);

        final Page<Member> foundMembers = memberSupportRepository.applySimplePagination(condition, pageRequest);

        assertThat(foundMembers.getSize()).isEqualTo(3);
        assertThat(foundMembers).extracting("username").containsExactly("member1", "member2", "member3");
    }

    @Test
    void searchPageByApplyComplexPaginationTest() {
        final Team teamA = new Team("Team A");
        final Team teamB = new Team("Team B");
        em.persist(teamA);
        em.persist(teamB);

        final Member member1 = new Member("member1", 21, teamA);
        final Member member2 = new Member("member2", 23, teamA);
        final Member member3 = new Member("member3", 25, teamB);
        final Member member4 = new Member("member4", 27, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        final MemberSearchCondition condition = new MemberSearchCondition();
        final PageRequest pageRequest = PageRequest.of(0, 3);

        final Page<Member> foundMembers = memberSupportRepository.applyComplexPagination(condition, pageRequest);

        assertThat(foundMembers.getSize()).isEqualTo(3);
        assertThat(foundMembers).extracting("username").containsExactly("member1", "member2", "member3");
    }
}
