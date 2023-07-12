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
}
