package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository, QuerydslPredicateExecutor<Member> {
    List<Member> findByUsername(String username);
}
