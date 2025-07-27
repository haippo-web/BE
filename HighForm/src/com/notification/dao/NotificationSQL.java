package com.notification.dao;


/*		[					]
 * 		[	배지원    담당   	]
 * 		[					]
 */

public class NotificationSQL {
    
	// 시퀀스 생성
	public static final String CREATE_NOTIFICATION_SEQUENCE =
			"""
				CREATE SEQUENCE notification_seq
				    START WITH 1
				    INCREMENT BY 1
				    NOCACHE
				    NOCYCLE
			""";
	
    public static final String CREATE_TABLE =
            """
                CREATE TABLE notification (
                    id NUMBER PRIMARY KEY,
                    title VARCHAR2(255) NOT NULL,
                    content VARCHAR2(1000),
                    type VARCHAR2(50),
                    user_id NUMBER NOT NULL,
                    is_read CHAR(1) DEFAULT 'N' CHECK (is_read IN ('Y', 'N')),
                    created_at DATE DEFAULT SYSDATE,
                    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES user_info(id)
                )
            """;
    
    // 오래된 알림 자동 삭제 프로시저 생성
    public static final String CREATE_CLEANUP_PROCEDURE =
            """
            CREATE OR REPLACE PROCEDURE CLEANUP_OLD_NOTIFICATIONS(
                p_days_old IN NUMBER DEFAULT 30,
                p_result OUT NUMBER
            ) AS
                v_deleted_count NUMBER := 0;
            BEGIN
                -- 지정된 일수보다 오래된 읽은 알림 삭제
                DELETE FROM notification 
                WHERE is_read = 'Y' 
                AND created_at < SYSDATE - p_days_old;
                
                v_deleted_count := SQL%ROWCOUNT;
                p_result := v_deleted_count;
                
                -- 정리 로그 기록 (선택사항)
                IF v_deleted_count > 0 THEN
                    INSERT INTO notification_cleanup_log (cleanup_date, days_old, deleted_count)
                    VALUES (SYSDATE, p_days_old, v_deleted_count);
                END IF;
                
                DBMS_OUTPUT.PUT_LINE('오래된 알림 ' || v_deleted_count || '건 삭제 완료');
                
            EXCEPTION
                WHEN OTHERS THEN
                    p_result := 0;
                    DBMS_OUTPUT.PUT_LINE('알림 정리 중 오류: ' || SQLERRM);
                    RAISE;
            END CLEANUP_OLD_NOTIFICATIONS;
            """;
    
    public static final String CREATE_NOTIFICATION =
            """
                INSERT INTO notification (id, title, content, type, user_id, created_at, is_read)
                VALUES (notification_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE, 'N')
            """;
    
    public static final String GET_LATEST_NOTIFICATION =
            """
                SELECT * FROM notification
                WHERE user_id = ? 
                ORDER BY created_at DESC 
                FETCH FIRST 1 ROW ONLY
            """;
    
    public static final String GET_ALL_NOTIFICATIONS =
            """
                SELECT * FROM notification 
                WHERE user_id = ? 
                ORDER BY is_read ASC, created_at DESC
            """;
    
    public static final String MARK_AS_READ =
            """
                UPDATE notification 
                SET is_read = 'Y' 
                WHERE id = ?
            """;
    
    public static final String DELETE_NOTIFICATION =
            """
                DELETE FROM notification
                WHERE id = ?
            """;
    
    public static final String GET_UNREAD_COUNT =
            """
                SELECT COUNT(*) FROM notification 
                WHERE user_id = ? AND is_read = 'N'
            """;
    
    // 오래된 알림 정리 프로시저 호출
    public static final String CLEANUP_OLD_NOTIFICATIONS = "{call CLEANUP_OLD_NOTIFICATIONS(?, ?)}";
}