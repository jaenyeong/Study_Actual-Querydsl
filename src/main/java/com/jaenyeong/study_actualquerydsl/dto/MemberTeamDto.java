package com.jaenyeong.study_actualquerydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberTeamDto {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    // 해당 애너테이션 사용하면 DTO가 Querydsl을 의존하게 됨
    // 이 대신 애너테이션 제거 후 `Projection.bean()`, `fields()`, `constructor()` 등을 사용할 수 있음
    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
