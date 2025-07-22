package com.mypage.dao.attendance;

public class AttendanceSQL {
    // 출결 리스트 (Oracle 페이징, ROW_NUMBER 사용, NULLS LAST 추가)
    public static final String SELECT_ATTENDANCE_LIST =
        "SELECT * FROM ( " +
        "   SELECT a.*, ROW_NUMBER() OVER (ORDER BY check_in DESC NULLS LAST) rn " +
        "   FROM attendance a " +
        "   WHERE user_id = ? " +
        ") WHERE rn > ? AND rn <= ?";

    // 출결률 조회
    public static final String SELECT_ATTENDANCE_RATE =
    	    "SELECT CASE WHEN COUNT(*) = 0 THEN 0 " +
    	    " ELSE ROUND(( " +
    	    "   SUM(CASE " +
    	    "     WHEN status IN ('PRESENT', 'EXCUSED') THEN 1 " +
    	    "     WHEN status IN ('LATE', 'EARLY_LEAVE') THEN 1.0/3 " +
    	    "     ELSE 0 " +
    	    "   END) / COUNT(*) " +
    	    " ) * 100, 2) END AS attendance_rate " +
    	    "FROM attendance WHERE user_id = ?";



}
