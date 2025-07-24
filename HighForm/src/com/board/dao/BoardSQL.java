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
				SELECT 
				    ROW_NUMBER() OVER (ORDER BY created_at DESC) AS no,
				    b.*
				FROM 
			    	BOARD b
				WHERE 
			    	type = ? AND del_yn = 'N'
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