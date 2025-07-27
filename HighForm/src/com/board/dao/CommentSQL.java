package com.board.dao;

public class CommentSQL {
	
	public static final String CREATE_COMMENT_TABLE =
			"""
				CREATE TABLE comments (
				    id NUMBER PRIMARY KEY,
				    board_id NUMBER NOT NULL,
				    parent_id NUMBER DEFAULT 0,  -- 0이면 최상위 댓글, 아니면 대댓글
				    author VARCHAR2(255) NOT NULL,
				    content VARCHAR2(1000) NOT NULL,
				    user_id NUMBER,
				    created_at DATE DEFAULT SYSDATE,
				    updated_at DATE DEFAULT SYSDATE,
				    del_yn CHAR(1) DEFAULT 'N' CHECK (del_yn IN ('Y', 'N')),
				    CONSTRAINT fk_comments_board FOREIGN KEY (board_id) REFERENCES board(id),
				    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES user_info(id)
				)
			""";

	// 계층형 댓글 조회용 View 생성
	public static final String CREATE_HIERARCHICAL_COMMENTS_VIEW =
			"""
				CREATE OR REPLACE VIEW V_HIERARCHICAL_COMMENTS AS
				SELECT 
				    c.id,
				    c.board_id,
				    c.parent_id,
				    c.author,
				    c.content,
				    c.user_id,
				    c.created_at,
				    c.updated_at,
				    c.del_yn,
				    LEVEL as comment_level,
				    CONNECT_BY_ROOT c.id as root_comment_id,
				    SYS_CONNECT_BY_PATH(c.id, '/') as comment_path,
				    CASE 
				        WHEN LEVEL = 1 THEN 'PARENT'
				        ELSE 'REPLY'
				    END as comment_type
				FROM comments c
				WHERE c.del_yn = 'N'
				START WITH c.parent_id = 0
				CONNECT BY PRIOR c.id = c.parent_id
			""";
	
//	parent_id NUMBER DEFAULT 0,  -- 0이면 최상위 댓글, 아니면 대댓글
	
	// 시퀀스 생성
	public static final String CREATE_COMMENT_SEQUENCE =
			"""
				CREATE SEQUENCE comments_seq
				    START WITH 1
				    INCREMENT BY 1
				    NOCACHE
				    NOCYCLE
			""";

    
    public static final String CREATE_COMMENT =
            """
            INSERT INTO comments (id, board_id, parent_id, author, content, user_id, created_at, updated_at, del_yn)
            VALUES (comments_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, SYSDATE, 'N')
            """;
    
    public static final String GET_COMMENTS_BY_BOARD_ID =
            """
            SELECT * FROM V_HIERARCHICAL_COMMENTS 
            WHERE board_id = ?
            ORDER BY root_comment_id, comment_level, created_at
            """;
    
    public static final String UPDATE_COMMENT =
            """
            UPDATE comments
            SET content = ?, updated_at = SYSDATE 
            WHERE id = ? AND del_yn = 'N'
            """;
    
    public static final String DELETE_COMMENT =
            """
            UPDATE comments 
            SET del_yn = 'Y', updated_at = SYSDATE 
            WHERE id = ?
            """;
    
    public static final String DELETE_COMMENT_WITH_REPLIES =
            """
            UPDATE comments
            SET del_yn = 'Y', updated_at = SYSDATE 
            WHERE id = ? OR parent_id = ?
            """;
    
    // 댓글 및 대댓글 삭제 프로시저 생성
    public static final String CREATE_DELETE_COMMENT_PROCEDURE =
            """
            CREATE OR REPLACE PROCEDURE DELETE_COMMENT_CASCADE(
                p_comment_id IN NUMBER,
                p_result OUT NUMBER
            ) AS
                v_affected_rows NUMBER := 0;
                v_comment_exists NUMBER := 0;
            BEGIN
                -- 댓글 존재 여부 확인
                SELECT COUNT(*) INTO v_comment_exists
                FROM comments 
                WHERE id = p_comment_id AND del_yn = 'N';
                
                IF v_comment_exists = 0 THEN
                    p_result := 0;
                    RETURN;
                END IF;
                
                -- 대댓글 먼저 삭제 (논리 삭제)
                UPDATE comments 
                SET del_yn = 'Y', updated_at = SYSDATE 
                WHERE parent_id = p_comment_id AND del_yn = 'N';
                
                v_affected_rows := v_affected_rows + SQL%ROWCOUNT;
                
                -- 원본 댓글 삭제 (논리 삭제)
                UPDATE comments 
                SET del_yn = 'Y', updated_at = SYSDATE 
                WHERE id = p_comment_id AND del_yn = 'N';
                
                v_affected_rows := v_affected_rows + SQL%ROWCOUNT;
                
                p_result := v_affected_rows;
                
                -- 삭제 로그 기록 (선택사항)
                IF v_affected_rows > 0 THEN
                    INSERT INTO comment_delete_log (comment_id, deleted_at, affected_rows)
                    VALUES (p_comment_id, SYSDATE, v_affected_rows);
                END IF;
                
            EXCEPTION
                WHEN OTHERS THEN
                    p_result := 0;
                    DBMS_OUTPUT.PUT_LINE('댓글 삭제 중 오류: ' || SQLERRM);
                    RAISE;
            END DELETE_COMMENT_CASCADE;
            """;
    
    // 댓글 삭제 프로시저 호출용 SQL
    public static final String DELETE_COMMENT_WITH_REPLIES_PROCEDURE =
            """
            {call DELETE_COMMENT_CASCADE(?, ?)}
            """;
    
    
    // 댓글 알림 트리거 생성
    public static final String COMMENT_NOTICE_TRIGGER =
    		"""
    		CREATE OR REPLACE TRIGGER comment_notification_trigger
    		AFTER INSERT ON comments
    		FOR EACH ROW
    		DECLARE
    		    v_board_author VARCHAR2(255);
    		    v_board_title VARCHAR2(255);
    		    v_comment_author VARCHAR2(255);
    		    v_notification_title VARCHAR2(255);
    		    v_notification_content VARCHAR2(1000);
    		BEGIN
    		    SELECT b.author, b.title 
    		    INTO v_board_author, v_board_title
    		    FROM board b 
    		    WHERE b.id = :NEW.board_id;
    		    
    		    SELECT u.name 
    		    INTO v_comment_author
    		    FROM user_info u 
    		    WHERE u.id = :NEW.user_id;
    		    
    		    IF v_board_author != v_comment_author THEN
    		        -- 알림 제목과 내용 생성
    		        v_notification_title := '새로운 댓글';
    		        v_notification_content := v_comment_author || '님이 "' || 
    		                                 SUBSTR(v_board_title, 1, 20) || 
    		                                 CASE WHEN LENGTH(v_board_title) > 20 THEN '...' ELSE '' END ||
    		                                 '" 게시물에 댓글을 남겼습니다.';
    		        
    		        INSERT INTO notification (
    		            id, 
    		            title, 
    		            content, 
    		            type, 
    		            user_id, 
    		            created_at, 
    		            is_read
    		        ) VALUES (
    		            notification_seq.NEXTVAL,
    		            v_notification_title,
    		            v_notification_content,
    		            'COMMENT',
    		            (SELECT user_id FROM board WHERE id = :NEW.board_id), -- 게시글 작성자 ID
    		            SYSDATE,
    		            'N'
    		        );
    		        
    		        DBMS_OUTPUT.PUT_LINE('알림 생성 완료: ' || v_notification_content);
    		    END IF;
    		    
    		EXCEPTION
    		    WHEN OTHERS THEN
    		        DBMS_OUTPUT.PUT_LINE('알림 생성 중 오류: ' || SQLERRM);
    		END;
    		/
    		""";
}
