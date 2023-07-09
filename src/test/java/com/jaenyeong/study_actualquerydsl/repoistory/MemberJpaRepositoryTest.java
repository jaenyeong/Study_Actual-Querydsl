package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void basicJpqlTest() {
        final Member member1 = new Member("member1", 10);
        memberJpaRepository.save(member1);

        final Member foundMember = memberJpaRepository.findById(member1.getId()).get();
        assertThat(foundMember).isEqualTo(member1);

        final List<Member> allMembers = memberJpaRepository.findAll();
        assertThat(allMembers).containsExactly(member1);

        final List<Member> foundMembersByUsername = memberJpaRepository.findByUsername(member1.getUsername());
        assertThat(foundMembersByUsername).containsExactly(member1);
    }

    @Test
    void basicQuerydslTest() {
        final Member member1 = new Member("member1", 10);
        memberJpaRepository.save(member1);

        final Member foundMember = memberJpaRepository.findById(member1.getId()).get();
        assertThat(foundMember).isEqualTo(member1);

        final List<Member> allMembers = memberJpaRepository.findAll_querydsl();
        assertThat(allMembers).containsExactly(member1);

        final List<Member> foundMembersByUsername = memberJpaRepository.findByUsername_querydsl(member1.getUsername());
        assertThat(foundMembersByUsername).containsExactly(member1);
    }
}
