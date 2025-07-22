package com.board.dao;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.board.model.Board;
import com.board.model.BoardCategory;
import com.util.DBConnection;

public class BoardDao {
   private final String DBURL = "";
   private final String DBUSER = "";
   private final String DBPASSWORD = "";

   // 1. Singleton & DB connection
   private static BoardDao instance;
   
   private Connection getConnection() throws SQLException {
       return DBConnection.getConnection();
   }
   

   public static BoardDao getInstance() {
      if (instance == null) {
         instance = new BoardDao();
      }
      return instance;
   }
   
   
   
   // 테이블 생성
   public void createTable() throws SQLException{
	   String createTableSQL = BoardSQL.CREATE_TABLE;
	   try(Connection conn = getConnection();
			   CallableStatement psmt = conn.prepareCall(createTableSQL)){
		   psmt.executeUpdate();
	   }catch(Exception e) {
		   e.getStackTrace();
	   }
   }
   
   // 게시물 작성
   public Long createBoard(Board board) {
	   
	   String createBoardSQL = BoardSQL.CREATE_BOARD;
	   try(Connection conn = getConnection();
			   PreparedStatement psmt = conn.prepareStatement(createBoardSQL)){
		   
		   psmt.setString(1, board.getTitle());
		   psmt.setString(2, board.getContent());
		   psmt.setString(3,  board.getType().name());
		   psmt.setLong(4, board.getUserId());
		   psmt.setLong(5, board.getFileId());
		   
		   psmt.executeUpdate();
		   
		   // 2. 생성된 ID 가져오기
		    ResultSet rs = psmt.getGeneratedKeys();
		    long generatedId = 0;
		    if (rs.next()) {
		        generatedId = rs.getLong(1);
		    }
		   
		   return generatedId;
		   
	   }catch(Exception e) {
		   e.getStackTrace();
		   e.printStackTrace();
	   }
	   return null;
   }
   
   
   //게시물 호출
   public Board getBoard(Long boardId) {
	   String getBoardSQL = BoardSQL.GET_BOARD;
	   try(Connection conn = getConnection();
			PreparedStatement psmt = conn.prepareStatement(getBoardSQL)){
		   
		    ResultSet selectRs = psmt.executeQuery();
		    Board entity = null;
			
		    if (selectRs.next()) {
		        entity = Board.builder()
		        		.title(selectRs.getString("title"))
		        		.content(selectRs.getString("content"))
		        		.type(selectRs.getString("type").equals(BoardCategory.BOARD) ? BoardCategory.BOARD : selectRs.getString("type").equals(BoardCategory.DATA_ROOM) ? BoardCategory.DATA_ROOM :  BoardCategory.NOTICE )
		        		.fileId(selectRs.getLong("file_id"))
		        		.userId(selectRs.getLong("user_id"))
		        		.build();
		    }
		   
		   psmt.executeUpdate();
		   
		   return entity;
	   }catch(Exception e) {
		   e.getStackTrace();
		   e.printStackTrace();
	   }
	   return null;
   }
	
   
   
   //게시물 호출
   public List<Board> getBoard(BoardCategory type) {
	   String getBoardSQL = BoardSQL.GET_BOARD_FROM_TYPE;
	   try(Connection conn = getConnection();
			PreparedStatement psmt = conn.prepareStatement(getBoardSQL)){
		   
		    ResultSet selectRs = psmt.executeQuery();
		    List<Board> boardList = new ArrayList<Board>();
		    
		    Board entity = null;
			
		    if (selectRs.next()) {
		        entity = Board.builder()
		        		.title(selectRs.getString("title"))
		        		.content(selectRs.getString("content"))
		        		.type(selectRs.getString("type").equals(BoardCategory.BOARD) ? BoardCategory.BOARD : selectRs.getString("type").equals(BoardCategory.DATA_ROOM) ? BoardCategory.DATA_ROOM :  BoardCategory.NOTICE )
		        		.fileId(selectRs.getLong("file_id"))
		        		.userId(selectRs.getLong("user_id"))
		        		.build();
		    }
		   
		   psmt.executeUpdate();
		   
		   return entity;
	   }catch(Exception e) {
		   e.getStackTrace();
		   e.printStackTrace();
	   }
	   return null;
   }
}
