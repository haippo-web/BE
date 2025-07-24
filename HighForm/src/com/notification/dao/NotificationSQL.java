package com.notification.dao;

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
}