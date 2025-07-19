package com.mypage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.DBConnection;
import com.mypage.domain.AttendanceApprovalRequest;

public class AttendanceApprovalRequestDAO {
    // 싱글톤 패턴 등은 생략 가능
    public void insert(AttendanceApprovalRequest req) throws SQLException {
        String sql = "INSERT INTO attendance_approval_request "
            + "(reason, proof_file, status, requested_at, start_date, end_date, user_id) "
            + "VALUES (?, ?, ?, SYSDATE, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, req.getReason());
            pstmt.setString(2, req.getProofFile());
            pstmt.setString(3, "progressing");  // 신청시 고정
            pstmt.setDate(4, new java.sql.Date(req.getStartDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(req.getEndDate().getTime()));
            pstmt.setLong(6, req.getUserId());
            pstmt.executeUpdate();
        }
    }
}

