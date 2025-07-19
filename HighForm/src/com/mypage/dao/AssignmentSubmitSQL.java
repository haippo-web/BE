package com.mypage.dao;

public class AssignmentSubmitSQL {
    // 나의 제출 리스트 페이징
    public static final String SELECT_SUBMIT_LIST =
        "SELECT * FROM ( " +
        "  SELECT s.*, ROW_NUMBER() OVER (ORDER BY submitted_at DESC) rn " +
        "  FROM assignment_submit s " +
        "  WHERE user_id = ? " +
        ") WHERE rn > ? AND rn <= ?";

    // 제출 등록
    public static final String INSERT_SUBMIT =
        "INSERT INTO assignment_submit (user_id, assignment_id, title, content, submitted_at) VALUES (?, ?, ?, ?, ?)";

    // 전체 제출 개수
    public static final String COUNT_SUBMIT =
        "SELECT COUNT(*) FROM assignment_submit WHERE user_id = ?";
}
