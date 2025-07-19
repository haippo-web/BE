package com.mypage.service;



import java.sql.SQLException;
import java.util.List;

import com.mypage.dao.AttendanceDAO;
import com.mypage.domain.Attendance;

public class AttendanceService {
    private AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = AttendanceDAO.getInstance();
    }

    // 7일 단위 출결 내역 페이징 조회
    public List<Attendance> getAttendanceList(Long userId, int page, int size) throws SQLException {
        int offset = (page - 1) * size;
        return attendanceDAO.getAttendanceList(userId, offset, size);
    }

    // 출결률 조회
    public double getAttendanceRate(Long userId) throws SQLException {
        return attendanceDAO.getAttendanceRate(userId);
    }
}
