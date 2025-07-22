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

public class BoardDao {
   private final String DBURL;
   private final String DBUSER;
   private final String DBPASSWORD;

   // 1. Singleton & DB connection
   private static BoardDao instance;
   
//   public class DBConnectionUtil {
//	   private static final Properties messages = new Properties();
//
//	   static {
//	      try (InputStream in = ClassLoader.getSystemResourceAsStream("com/common/util/db.properties");
//	              InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
//	             messages.load(reader);
//	      } catch (Exception e) {
//	         System.err.println("Error loading error messages: " + e.getMessage());
//	      }
//	   }
//
//	   public static String get(String key) {
//	      return messages.getProperty(key, key + "를 찾을 수 없습니다.");
//	   }
//	}
   
   public BoardDao() {
      // Properties 값 가져오기
      Properties props = new Properties();

      try(InputStream in = BoardDao.class.getResourceAsStream("/com/common/util/db.properties");
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)){
         props.load(reader);
         
         DBURL = props.getProperty("DB.URL");
         DBUSER = props.getProperty("DB.USER");
         DBPASSWORD = props.getProperty("DB.PASSWORD");
      }catch(Exception e) {
         System.out.println("message:" + e.getMessage());
         throw new RuntimeException("접속정보가 없거나 잘못됨...");
      }
   }

   public static BoardDao getInstance() {
      if (instance == null) {
         instance = new BoardDao();
      }
      return instance;
   }
   
   
   private Connection getConnection() throws SQLException {
	   return DriverManager.getConnection(DBURL, DBUSER,DBPASSWORD );
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
