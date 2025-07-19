package com.mypage.dao;

import com.DBConnection;
import com.mypage.domain.AssignmentSubmit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmitDAO {

    public static class AssignmentOption {
        private Long id;
        private String title;
        private String curriculumName; // 옵션에 커리큘럼명 추가(원한다면)

        public AssignmentOption(Long id, String title, String curriculumName) {
            this.id = id;
            this.title = title;
            this.curriculumName = curriculumName;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getCurriculumName() { return curriculumName; }

        @Override
        public String toString() {
            // 콤보박스에 과제명(커리큘럼) 같이 보여주고 싶으면 아래처럼
            return title + (curriculumName != null ? " (" + curriculumName + ")" : "");
        }
    }

    private static AssignmentSubmitDAO instance;
    private AssignmentSubmitDAO() {}
    public static AssignmentSubmitDAO getInstance() {
        if (instance == null) instance = new AssignmentSubmitDAO();
        return instance;
    }
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    // 1. 제출 리스트 (커리큘럼명 포함)
    public List<AssignmentSubmit> getSubmitList(Long userId, int offset, int limit) throws SQLException {
        String sql =
            "SELECT * FROM ( " +
            "  SELECT s.*, a.title AS assignment_title, c.name AS curriculum_name, " +
            "         ROW_NUMBER() OVER (ORDER BY s.submitted_at DESC) rn " +
            "    FROM assignment_submit s " +
            "    JOIN assignment a ON s.assignment_id = a.id " +
            "    JOIN curriculum c ON a.curriculum_id = c.id " +
            "   WHERE s.user_id = ? " +
            ") WHERE rn > ? AND rn <= ?";
        List<AssignmentSubmit> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, offset + limit - 1);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                AssignmentSubmit sub = new AssignmentSubmit();
                sub.setUserId(rs.getLong("user_id"));
                sub.setAssignmentId(rs.getLong("assignment_id"));
                sub.setTitle(rs.getString("title"));
                sub.setContent(rs.getString("content"));
                Timestamp ts = rs.getTimestamp("submitted_at");
                if (ts != null) sub.setSubmittedAt(ts.toLocalDateTime());
                sub.setCurriculumName(rs.getString("curriculum_name"));
                list.add(sub);
            }
        }
        return list;
    }

    // 2. 과제 제출 등록
    public void insert(AssignmentSubmit submit) throws SQLException {
        String sql = "INSERT INTO assignment_submit (user_id, assignment_id, title, content, submitted_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, submit.getUserId());
            pstmt.setLong(2, submit.getAssignmentId());
            pstmt.setString(3, submit.getTitle());
            pstmt.setString(4, submit.getContent());
            pstmt.setTimestamp(5, submit.getSubmittedAt() != null ? Timestamp.valueOf(submit.getSubmittedAt()) : null);
            pstmt.executeUpdate();
        }
    }

    // 3. 전체 제출 개수
    public int getSubmitCount(Long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM assignment_submit WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // 4. 과제 선택 콤보박스용 (회원이 수강중인 커리큘럼의 과제만)
    public List<AssignmentOption> getAvailableAssignmentsForUser(Long userId) throws SQLException {
        String sql =
            "SELECT a.id, a.title, c.name as curriculum_name " +
            "  FROM assignment a " +
            "  JOIN curriculum c ON a.curriculum_id = c.id " +
            "  JOIN enrollment e ON c.id = e.curriculum_id " +
            " WHERE e.user_id = ?";
        List<AssignmentOption> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new AssignmentOption(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("curriculum_name")
                ));
            }
        }
        return list;
    }
}
