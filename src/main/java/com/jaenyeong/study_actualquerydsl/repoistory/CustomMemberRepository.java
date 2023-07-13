package com.jaenyeong.study_actualquerydsl.repoistory;

import com.jaenyeong.study_actualquerydsl.dto.MemberSearchCondition;
import com.jaenyeong.study_actualquerydsl.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomMemberRepository {
    List<MemberTeamDto> search(MemberSearchCondition condition);
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);

    List<MemberTeamDto> searchBySupport(MemberSearchCondition condition);
    Page<MemberTeamDto> searchPageSimpleBySupport(MemberSearchCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchPageComplexBySupport(MemberSearchCondition condition, Pageable pageable);
}
