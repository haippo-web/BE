package com.board.service;

import com.board.dao.FileLocationDao;
import com.board.model.FileLocation;
import com.util.RedisLoginService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class FileService {
    
    private static FileService instance;
    private final FileLocationDao fileLocationDao;
    private final RedisLoginService redisService;
    
    private FileService() {
        this.fileLocationDao = FileLocationDao.getInstance();
        this.redisService = new RedisLoginService();
    }
    
    public static FileService getInstance() {
        if (instance == null) {
            instance = new FileService();
        }
        return instance;
    }
    
    // 파일 저장
    public Long saveFile(File file, Long userId, Long boardId) {
        try {
            return fileLocationDao.saveFile(file, userId, boardId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }
    
    // 게시글별 파일 조회
    public List<FileLocation> getFilesByBoardId(Long boardId) {
        return fileLocationDao.getFilesByBoardId(boardId);
    }
    
    // 파일 다운로드
    public void downloadFile(FileLocation fileLocation, File saveFile) {
        try {
            // 서버에서 파일 복사
            Path sourcePath = Paths.get(fileLocation.getFilePath());
            Path targetPath = saveFile.toPath();
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }
    }
    
    // 파일 삭제
    public boolean deleteFile(Long fileId) {
        return fileLocationDao.deleteFile(fileId);
    }
    
    // 파일 존재 여부 확인
    public boolean existsFile(Long fileId) {
        return fileLocationDao.getFileById(fileId) != null;
    }
    
    // 파일 정보 조회
    public FileLocation getFileById(Long fileId) {
        return fileLocationDao.getFileById(fileId);
    }
    
    // 원본 파일명 추출
    public String extractOriginalFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf("_") + 1);
    }
    
    // 파일 크기 검증
    public boolean validateFileSize(File file, long maxSize) {
        return file.length() <= maxSize;
    }
    
    // 파일 확장자 검증
    public boolean validateFileExtension(File file, String[] allowedExtensions) {
        String fileName = file.getName().toLowerCase();
        for (String extension : allowedExtensions) {
            if (fileName.endsWith(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    // 현재 로그인한 사용자 정보 조회
    public Map<String, String> getCurrentUserInfo() {
        return redisService.getLoginUserFromRedis();
    }
} 