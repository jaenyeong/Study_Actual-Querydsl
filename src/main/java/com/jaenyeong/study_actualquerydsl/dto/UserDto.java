package com.jaenyeong.study_actualquerydsl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private String name;
    private int age;

    public UserDto(final String name, final int age) {
        this.name = name;
        this.age = age;
    }
}
