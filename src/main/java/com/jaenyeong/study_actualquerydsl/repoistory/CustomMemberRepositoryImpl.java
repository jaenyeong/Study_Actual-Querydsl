package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.dto.MemberSearchCondition;
import com.jaenyeong.study_actualquerydsl.dto.MemberTeamDto;
import com.jaenyeong.study_actualquerydsl.dto.QMemberTeamDto;
import com.jaenyeong.study_actualquerydsl.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.jaenyeong.study_actualquerydsl.entity.QMember.member;
import static com.jaenyeong.study_actualquerydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

// 리포지터리 네이밍 컨벤션을 지켜줘야 함
// ${구현할 인터페이스명} + Impl
public class CustomMemberRepositoryImpl extends QuerydslRepositorySupport implements CustomMemberRepository {
    private final JPAQueryFactory queryFactory;

    public CustomMemberRepositoryImpl(EntityManager em) {
        // QuerydslRepositorySupport 상속으로 추가
        super(Member.class);
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
            .select(new QMemberTeamDto(
                member.id,
                member.username,
                member.age,
                team.id,
                team.name
            ))
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            )
            .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        final List<MemberTeamDto> results = fetchMemberTeamDtosQuery(condition, pageable);

        return new PageImpl<>(results, pageable, results.size());
    }

    private List<MemberTeamDto> fetchMemberTeamDtosQuery(MemberSearchCondition condition, Pageable pageable) {
        return queryFactory
            .select(new QMemberTeamDto(
                member.id,
                member.username,
                member.age,
                team.id,
                team.name
            ))
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        final List<MemberTeamDto> results = fetchMemberTeamDtosQuery(condition, pageable);

        final JPAQuery<Long> countQuery = queryFactory
            .select(member.count())
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            );

        // count 쿼리 생략 가능 기준에 해당하지 않을 때만 카운트 쿼리가 호출됨
        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    @Override
    public List<MemberTeamDto> searchBySupport(MemberSearchCondition condition) {
        // Sort 기능이 정상 동작하지 않을 수 있으니 주의할 것
        return from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            )
            .select(
                new QMemberTeamDto(
                    member.id,
                    member.username,
                    member.age,
                    team.id,
                    team.name
                )
            )
            .fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageSimpleBySupport(MemberSearchCondition condition, Pageable pageable) {
        final List<MemberTeamDto> results = fetchMemberTeamDtosQueryBySupport(condition, pageable);

        return new PageImpl<>(results, pageable, results.size());
    }

    private List<MemberTeamDto> fetchMemberTeamDtosQueryBySupport(MemberSearchCondition condition, Pageable pageable) {
        final JPQLQuery<MemberTeamDto> searchQuery = from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            )
            .select(
                new QMemberTeamDto(
                    member.id,
                    member.username,
                    member.age,
                    team.id,
                    team.name
                )
            );

        return Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, searchQuery).fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageComplexBySupport(MemberSearchCondition condition, Pageable pageable) {
        final List<MemberTeamDto> results = fetchMemberTeamDtosQueryBySupport(condition, pageable);

        final JPQLQuery<Long> countQuery = from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            )
            .select(member.count());

        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }
}
