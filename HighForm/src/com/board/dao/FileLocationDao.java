// src/com/board/dao/FileLocationDao.java
package com.board.dao;

import com.board.model.FileLocation;
import com.util.DBConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileLocationDao {
    
    private static FileLocationDao instance;
    
    private FileLocationDao() {}
    
    public static FileLocationDao getInstance() {
        if (instance == null) {
            instance = new FileLocationDao();
        }
        return instance;
    }
    
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    // 파일 업로드 및 DB 저장
    public Long saveFile(File file, Long userId, Long boardId) throws Exception {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // 파일을 서버에 저장
            Path savedPath = saveFileToServer(file);
            
            // DB에 파일 정보 저장
            String sql = """
                INSERT INTO file_location (id, user_id, submit_id, file_path, file_type, file_size, uploaded_at)
                VALUES (file_location_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE)
            """;
            
            try (PreparedStatement psmt = conn.prepareStatement(sql, new String[] { "ID" })) {
                psmt.setLong(1, userId);
                psmt.setLong(2, boardId); // submit_id 대신 board_id 사용
                psmt.setString(3, savedPath.toString());
                psmt.setString(4, Files.probeContentType(savedPath));
                psmt.setLong(5, Files.size(savedPath));
                
                psmt.executeUpdate();
                
                ResultSet rs = psmt.getGeneratedKeys();
                if (rs.next()) {
                    conn.commit();
                    return rs.getLong(1);
                }
            }catch(Exception e) {
            	e.getStackTrace();
                conn.rollback();
            }
            

            return null;
        }
    }
    
    // 게시글의 첨부파일 조회
    public List<FileLocation> getFilesByBoardId(Long boardId) {
        List<FileLocation> files = new ArrayList<>();
        String sql = """
            SELECT id, user_id, submit_id, file_path, file_type, file_size, uploaded_at
            FROM file_location 
            WHERE submit_id = ?
            ORDER BY uploaded_at DESC
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, boardId);
            ResultSet rs = psmt.executeQuery();
            
            while (rs.next()) {
                FileLocation file = new FileLocation();
                file.setId(rs.getLong("id"));
                file.setUserId(rs.getLong("user_id"));
                file.setBoardId(rs.getLong("submit_id")); // submit_id를 board_id로 사용
                file.setFilePath(rs.getString("file_path"));
                file.setFileType(rs.getString("file_type"));
                file.setFileSize(rs.getLong("file_size"));
                file.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                files.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
    
    // 파일 정보 조회
    public FileLocation getFileById(Long fileId) {
        String sql = """
            SELECT id, user_id, submit_id, file_path, file_type, file_size, uploaded_at
            FROM file_location 
            WHERE id = ?
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, fileId);
            ResultSet rs = psmt.executeQuery();
            
            if (rs.next()) {
                FileLocation file = new FileLocation();
                file.setId(rs.getLong("id"));
                file.setUserId(rs.getLong("user_id"));
                file.setBoardId(rs.getLong("submit_id"));
                file.setFilePath(rs.getString("file_path"));
                file.setFileType(rs.getString("file_type"));
                file.setFileSize(rs.getLong("file_size"));
                file.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 파일 삭제
    public boolean deleteFile(Long fileId) {
        String sql = "DELETE FROM file_location WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setLong(1, fileId);
            int result = psmt.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 파일을 서버에 저장
    private static final Path UPLOAD_ROOT = Paths.get(System.getProperty("user.home"), "uploads");
    
    private Path saveFileToServer(File src) throws IOException {
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path destDir = UPLOAD_ROOT.resolve(dateDir);
        Files.createDirectories(destDir);
        
        Path dest = destDir.resolve(UUID.randomUUID() + "_" + src.getName());
        Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        return dest;
    }
}