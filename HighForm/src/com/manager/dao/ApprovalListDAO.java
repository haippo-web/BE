package com.manager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.manager.model.Approval;
import com.util.DBConnection;

public class ApprovalListDAO {
	private Connection getConnection() throws SQLException {
		return DBConnection.getConnection();
	}

	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Oracle JDBC 드라이버를 찾을 수 없습니다.", e);
		}
	}

	// 모든 결재 조회
	public List<Approval> getAllApproval() {
	    List<Approval> approvals = new ArrayList<>();
	    String sql ="SELECT A.ID, '휴가' AS REQUEST_TYPE, A.REASON, "
	    		+ "M.NAME AS USER_NAME, "
	    		+ "A.PROOF_FILE, A.START_DATE, A.END_DATE, A.REQUESTED_AT, A.STATUS, "
	    		+ "C.COURSE_NAME "
	    		+ "FROM attendance_approval_request A "
	    		+ "INNER JOIN Members M ON A.USER_ID = M.ID "
	    		+ "LEFT JOIN enrollment E ON A.USER_ID = E.MEMBER_ID "
	    		+ "LEFT JOIN course C ON E.COURSE_ID = C.COURSE_ID "
	    		+ "ORDER BY CASE WHEN A.STATUS = 'progressing' THEN 0 ELSE 1 END, A.REQUESTED_AT DESC";

	    try (Connection conn = getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            Approval approval = new Approval();
	            
	            approval.setApprovalId(rs.getInt("ID"));
	            approval.setCategory(rs.getString("REQUEST_TYPE"));
	            approval.setReason(rs.getString("REASON"));
	            approval.setProof_file(rs.getString("PROOF_FILE"));
	            approval.setStatus(rs.getString("STATUS"));
	            approval.setRequested_at(rs.getString("REQUESTED_AT"));
	            approval.setStart_date(rs.getString("START_DATE"));
	            approval.setEnd_date(rs.getString("END_DATE"));
	            approval.setUserName(rs.getString("USER_NAME"));
	            approval.setUserAffiliation(rs.getString("COURSE_NAME"));

	            approvals.add(approval);
	        }

	        System.out.println("결재 목록 조회 완료: " + approvals.size() + "건");

	    } catch (SQLException e) {
	        System.err.println("결재 목록 조회 중 오류 발생: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return approvals;
	}
	
	// 결재 확인(결정)
	public boolean updateApproval(Approval approval) {
		String sql = "UPDATE attendance_approval_request SET STATUS=?, DECISION_AT=SYSDATE WHERE ID=?";

		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, approval.getStatus());
			pstmt.setInt(2, approval.getApprovalId());

			int result = pstmt.executeUpdate();
			System.out.println("결재 승인 결과: " + result + "건 완료");
			return result > 0;

		} catch (SQLException e) {
			System.err.println("결재 승인 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
