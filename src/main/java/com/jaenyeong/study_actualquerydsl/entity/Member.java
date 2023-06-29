package com.jaenyeong.study_actualquerydsl.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(final String username) {
        this(username, 0);
    }

    public Member(final String username, final int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        changeTeam(team);
    }

    public void changeTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team must not be null");
        }

        this.team = team;
        this.team.join(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "id = " + id + ", " +
            "username = " + username + ", " +
            "age = " + age + ")";
    }
}
