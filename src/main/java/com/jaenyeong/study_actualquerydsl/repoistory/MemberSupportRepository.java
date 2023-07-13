package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.dto.MemberSearchCondition;
import com.jaenyeong.study_actualquerydsl.entity.Member;
import com.jaenyeong.study_actualquerydsl.repoistory.support.CustomQuerydslRepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jaenyeong.study_actualquerydsl.entity.QMember.member;
import static com.jaenyeong.study_actualquerydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class MemberSupportRepository extends CustomQuerydslRepositorySupport {

    public MemberSupportRepository() {
        super(Member.class);
    }

    public List<Member> basicSelect() {
        return select(member)
            .from(member)
            .fetch();
    }

    public List<Member> basicSelectFrom() {
        return selectFrom(member).fetch();
    }

    public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable) {
        final JPAQuery<Member> searchQuery = selectFrom(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            );

        final JPAQuery<Long> countQuery = select(member.count())
            .from(member)
            .leftJoin(member.team, team)
            .where(
                usernameEq(condition.getUsername()),
                teamNameEq(condition.getTeamName()),
                ageGoe(condition.getAgeGoe()),
                ageLoe(condition.getAgeLoe())
            );

        final List<Member> foundMembers = getQuerydsl().applyPagination(pageable, searchQuery).fetch();

        return PageableExecutionUtils.getPage(foundMembers, pageable, countQuery::fetchOne);
    }

    public Page<Member> applySimplePagination(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
            searchQuery -> searchQuery.selectFrom(member)
                .where(
                    usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe())
                ));
    }

    public Page<Member> applyComplexPagination(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
            searchQuery -> searchQuery.selectFrom(member)
                .where(
                    usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe())
                ),
            countQuery -> countQuery.select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(
                    usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe())
                )
        );
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
}
