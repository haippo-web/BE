package com.mypage.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmit {
    private Long userId;              // 회원 ID
    private Long assignmentId;        // 과제 ID
    private String title;             // 제출 제목
    private String content;           // 제출 내용
    private LocalDateTime submittedAt; // 제출 일시
    private String curriculumName;
}
