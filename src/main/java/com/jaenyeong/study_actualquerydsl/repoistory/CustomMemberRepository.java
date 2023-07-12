package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.dto.MemberSearchCondition;
import com.jaenyeong.study_actualquerydsl.dto.MemberTeamDto;

import java.util.List;

public interface CustomMemberRepository {
    List<MemberTeamDto> search(MemberSearchCondition condition);
}
