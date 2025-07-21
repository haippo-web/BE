package com.attendance.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 사용자 정보를 담는 VO 클래스
 * user_info 테이블과 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    
    private Long id;                    // 사용자 ID (PK)
    private String loginId;             // 로그인 ID
    private String password;            // 비밀번호
    private String phone;               // 전화번호
    private String email;               // 이메일
    private String name;                // 이름
    private UserRole role;              // 사용자 역할 (ENUM)
    private LocalDateTime createdAt;    // 생성 시간
    private String delYn;               // 삭제 여부 (Y/N)
    
    // 사용자 역할을 나타내는 ENUM
    public enum UserRole {
        STUDENT("STUDENT", "학생"),
        PROFESSOR("PROFESSOR", "교수"),
        MANAGER("MANAGER", "관리자");
        
        private final String code;
        private final String description;
        
        UserRole(String code, String description) {
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
        public static UserRole fromCode(String code) {
            for (UserRole role : values()) {
                if (role.code.equals(code)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Invalid user role code: " + code);
        }
    }
    
    // 생성자 메서드 (새 사용자 생성용)
    public static UserInfo createNewUser(String loginId, String password, String name, UserRole role) {
        return UserInfo.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .role(role)
                .createdAt(LocalDateTime.now())
                .delYn("N")
                .build();
    }
    
    // 비즈니스 메서드들
    
    /**
     * 학생 역할인지 확인
     */
    public boolean isStudent() {
        return this.role == UserRole.STUDENT;
    }
    
    /**
     * 교수 역할인지 확인
     */
    public boolean isProfessor() {
        return this.role == UserRole.PROFESSOR;
    }
    
    /**
     * 관리자 역할인지 확인
     */
    public boolean isManager() {
        return this.role == UserRole.MANAGER;
    }
    
    /**
     * 승인 권한이 있는지 확인 (교수 또는 관리자)
     */
    public boolean canApproveRequest() {
        return this.role == UserRole.PROFESSOR || this.role == UserRole.MANAGER;
    }
    
    /**
     * 삭제된 사용자인지 확인
     */
    public boolean isDeleted() {
        return "Y".equals(this.delYn);
    }
    
    /**
     * 활성 사용자인지 확인
     */
    public boolean isActive() {
        return "N".equals(this.delYn);
    }
    
    /**
     * 사용자 소프트 삭제
     */
    public void softDelete() {
        this.delYn = "Y";
    }
    
    /**
     * 사용자 복구
     */
    public void restore() {
        this.delYn = "N";
    }
    
    /**
     * 연락처 정보가 있는지 확인
     */
    public boolean hasContactInfo() {
        return (phone != null && !phone.trim().isEmpty()) || 
               (email != null && !email.trim().isEmpty());
    }
    
    /**
     * 이메일 주소가 유효한 형식인지 간단 검증
     */
    public boolean hasValidEmail() {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }
}