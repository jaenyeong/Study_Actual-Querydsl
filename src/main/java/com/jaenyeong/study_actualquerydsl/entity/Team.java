package com.jaenyeong.study_actualquerydsl.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private final List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

    public void join(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }

        this.members.add(member);
    }

    @Override
    public String toString() {
        return "Team{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
