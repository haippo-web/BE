package com.board.dao;

public class BoardSQL {
	// 시퀀스 생성
	public static final String CREATE_BOARD_SEQUENCE =
			"""
				CREATE SEQUENCE board_seq
				    START WITH 1
				    INCREMENT BY 1
				    NOCACHE
				    NOCYCLE
			""";

	public static final String CREATE_TABLE =
			"""
				CREATE TABLE board (
				    id        NUMBER PRIMARY KEY,
				    title     VARCHAR2(255),
				    content   VARCHAR2(255),
				    type      VARCHAR2(255) CHECK (type IN ('NOTICE', 'DATA_ROOM', 'BOARD')),
				    author 	  VARCHAR2(255),
				    user_id   NUMBER,
				    file_id   NUMBER,
				    created_at DATE,
				    updated_at DATE,
				    del_yn CHAR(1) DEFAULT 'N' CHECK (del_yn IN ('Y', 'N')),
				    CONSTRAINT fk_board_user FOREIGN KEY (user_id) REFERENCES user_info(id)
				)
			""";

	// 게시글 목록 조회용 View 생성
	public static final String CREATE_BOARD_LIST_VIEW =
			"""
				CREATE OR REPLACE VIEW V_BOARD_LIST AS
				SELECT 
				    ROW_NUMBER() OVER (ORDER BY b.created_at DESC) AS no,
				    b.id,
				    b.title,
				    b.author,
				    b.content,
				    b.type,
				    b.created_at,
				    b.user_id,
				    NVL(comment_count, 0) as comment_count,
				    NVL(file_count, 0) as file_count
				FROM board b
				LEFT JOIN (
				    SELECT board_id, COUNT(*) as comment_count
				    FROM comments 
				    WHERE del_yn = 'N'
				    GROUP BY board_id
				) c ON b.id = c.board_id
				LEFT JOIN (
				    SELECT submit_id, COUNT(*) as file_count
				    FROM file_location 
				    GROUP BY submit_id
				) f ON b.id = f.submit_id
				WHERE b.del_yn = 'N'
			""";

	
	public static final String CREATE_BOARD = 
			"""
				INSERT INTO BOARD (id, title, content, type, user_id, file_id, created_at, updated_at, del_yn, author) 
				VALUES (board_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE, SYSDATE, 'N', ?)
			""";

	
	public static final String GET_BOARD =
			"""
				SELECT * FROM BOARD 
				WHERE id = ? AND del_yn = 'N'
			""";

	
	public static final String GET_BOARD_FROM_TYPE =
			"""
				SELECT * FROM V_BOARD_LIST 
				WHERE type = ?
			""";
			
	// 게시글 수정을 위한 SQL 쿼리들
	// TODO:: 오류 해결  (NVL를 통한 유효한 값에 따른 update 처리)
	public static final String UPDATE_BOARD =
			"""
            UPDATE BOARD SET 
                title = NVL(?, title),
                content = NVL(?, content),
                file_id = NVL(?, file_id),
                updated_at = SYSDATE 
            WHERE id = ? AND del_yn = 'N'
            """;
	
	// 게시글 삭제
	public static final String DELETE_BOARD =
			"""
			UPDATE BOARD 
			SET
				del_yn = 'Y'
			WHERE id = ?
			""";
}