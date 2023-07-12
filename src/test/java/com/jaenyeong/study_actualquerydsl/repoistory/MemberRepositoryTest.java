package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.dto.MemberSearchCondition;
import com.jaenyeong.study_actualquerydsl.dto.MemberTeamDto;
import com.jaenyeong.study_actualquerydsl.entity.Member;
import com.jaenyeong.study_actualquerydsl.entity.Team;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void basicJpqlTest() {
        final Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        final Member foundMember = memberRepository.findById(member1.getId()).get();
        assertThat(foundMember).isEqualTo(member1);

        final List<Member> allMembers = memberRepository.findAll();
        assertThat(allMembers).containsExactly(member1);

        final List<Member> foundMembersByUsername = memberRepository.findByUsername(member1.getUsername());
        assertThat(foundMembersByUsername).containsExactly(member1);
    }

    @Test
    void searchConditionTest() {
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
        condition.setAgeGoe(26);
        condition.setAgeLoe(27);
        condition.setTeamName("Team B");

        final List<MemberTeamDto> memberTeamDtos = memberRepository.search(condition);

        assertThat(memberTeamDtos).extracting("username").containsExactly("member4");
    }
}
