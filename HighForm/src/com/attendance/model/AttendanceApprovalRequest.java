package com.attendance.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 휴가/병가 승인 요청 정보를 담는 VO 클래스
 * attendance_approval_request 테이블과 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceApprovalRequest {
    
    private Long id;                        // 승인 요청 ID (PK)
    private String reason;                  // 사유
    private String proofFile;              // 증빙 파일 경로
    private ApprovalStatus status;         // 승인 상태 (ENUM)
    private LocalDateTime requestedAt;     // 요청 시간
    private LocalDateTime decisionAt;      // 결정 시간
    private LocalDate startDate;           // 시작 날짜
    private LocalDate endDate;             // 종료 날짜
    private Long userId;                   // 요청자 ID (FK)
    private Long approverId;               // 승인자 ID (FK, nullable)
    
    // 승인 상태를 나타내는 ENUM
    public enum ApprovalStatus {
        PROGRESSING("progressing", "승인 대기"),
        APPROVE("approve", "승인"),
        REJECT("reject", "거부");
        
        private final String code;
        private final String description;
        
        ApprovalStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        // String 코드로부터 ENUM 찾기
        public static ApprovalStatus fromCode(String code) {
            for (ApprovalStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid approval status code: " + code);
        }
    }
    
    // 생성자 메서드 (새 요청 생성용)
    public static AttendanceApprovalRequest createNewRequest(Long userId, String reason, 
                                                           LocalDate startDate, LocalDate endDate) {
        return AttendanceApprovalRequest.builder()
                .userId(userId)
                .reason(reason)
                .startDate(startDate)
                .endDate(endDate)
                .status(ApprovalStatus.PROGRESSING)
                .requestedAt(LocalDateTime.now())
                .build();
    }
    
    // 비즈니스 메서드들
    
    /**
     * 승인 처리
     */
    public void approve(Long approverId) {
        this.status = ApprovalStatus.APPROVE;
        this.approverId = approverId;
        this.decisionAt = LocalDateTime.now();
    }
    
    /**
     * 거부 처리
     */
    public void reject(Long approverId) {
        this.status = ApprovalStatus.REJECT;
        this.approverId = approverId;
        this.decisionAt = LocalDateTime.now();
    }
    
    /**
     * 승인된 상태인지 확인
     */
    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVE;
    }
    
    /**
     * 거부된 상태인지 확인
     */
    public boolean isRejected() {
        return this.status == ApprovalStatus.REJECT;
    }
    
    /**
     * 승인 대기 중인지 확인
     */
    public boolean isProgressing() {
        return this.status == ApprovalStatus.PROGRESSING;
    }
    
    /**
     * 특정 날짜가 승인 기간에 포함되는지 확인
     */
    public boolean isDateInRange(LocalDate date) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    /**
     * 승인 기간 계산 (일 수)
     */
    public long getApprovalPeriodDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    /**
     * 증빙 파일이 있는지 확인
     */
    public boolean hasProofFile() {
        return proofFile != null && !proofFile.trim().isEmpty();
    }
}