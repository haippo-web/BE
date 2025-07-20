package com;

import com.login.dao.UserDAO;

public class TestQueryMain {
    public static void main(String[] args) {
        UserDAO dao = UserDAO.getInstance();
        dao.testDirectQuery();  // 직접 쿼리 테스트 실행
        dao.printAllActiveUsers();  // del_yn='N' 사용자 출력
        dao.checkCurrentSchema();  // 현재 스키마 확인
        dao.printAllUsers();       // 전체 데이터 확인
    }
}
