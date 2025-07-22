package com.attendance.service;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 출석 코드 관리 서비스
 * 실제 구현 시에는 Redis나 데이터베이스를 사용하는 것을 권장
 */
public class AttendanceCodeService {
    
    // 임시로 메모리에 저장 (실제로는 Redis 사용 권장)
    private final ConcurrentHashMap<LocalDate, String> dailyCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    private static AttendanceCodeService instance = new AttendanceCodeService();
    private AttendanceCodeService() {}
    public static AttendanceCodeService getInstance() {
        return instance;
    }
    /**
     * 당일 출석 코드 생성
     * @param date 날짜
     * @return 생성된 코드
     */
    public String generateDailyCode(LocalDate date) {
        String code = String.format("%04d", random.nextInt(10000));
//        dailyCodes.put(date, code);
        return code;
    }
    
    /**
     * 오늘의 출석 코드 조회 (없으면 생성)
     * @return 출석 코드
     */
    public String getTodayCode() {
        LocalDate today = LocalDate.now();
        return dailyCodes.computeIfAbsent(today, this::generateDailyCode);
    }
    
    /**
     * 당일 출석 코드 검증
     * @param inputCode 입력된 코드
     * @return 검증 결과
     */
    public boolean validateTodayCode(String inputCode) {
        String todayCode = getTodayCode();
        return todayCode.equals(inputCode);
    }
    
    /**
     * 특정 날짜의 출석 코드 조회
     * @param date 날짜
     * @return 출석 코드 (없으면 null)
     */
    public String getCodeByDate(LocalDate date) {
        return dailyCodes.get(date);
    }
}
