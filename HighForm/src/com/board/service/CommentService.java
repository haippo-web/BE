package com.board.service;

import com.board.dao.CommentDao;
import com.board.model.Comment;
import com.util.RedisLoginService;

import java.util.List;
import java.util.Map;

/*		[					]
 * 		[	배지원   담당   	]
 * 		[					]
 */
public class CommentService {
    
    private static CommentService instance;
    private final CommentDao commentDao;
    private final RedisLoginService redisService;
    
    private CommentService() {
        this.commentDao = CommentDao.getInstance();
        this.redisService = new RedisLoginService();
    }
    
    public static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }
    
    // 게시글별 댓글 조회
    public List<Comment> getCommentsByBoardId(Long boardId) {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        String userName = userInfo.get("name");
        return commentDao.getCommentsByBoardId(boardId, userName);
    }
    
    // 댓글 생성
    public Long createComment(Comment comment) {
        return commentDao.createComment(comment);
    }
    
    // 댓글 수정
    public boolean updateComment(Long commentId, String content) {
        return commentDao.updateComment(commentId, content);
    }
    
    // 댓글과 대댓글 삭제
    public boolean deleteCommentWithReplies(Long commentId) {
        return commentDao.deleteCommentWithReplies(commentId);
    }
    
    // 대댓글 생성
    public Long createReply(Comment reply) {
        return commentDao.createComment(reply);
    }
    
    // 새 댓글 생성 (사용자 정보 포함)
    public Long createNewComment(Long boardId, String content, Long parentId) {
        Map<String, String> userInfo = redisService.getLoginUserFromRedis();
        String userName = userInfo.get("name");
        Long userId = Long.valueOf(userInfo.get("id"));
        
        Comment newComment = Comment.builder()
                .boardId(boardId)
                .parentId(parentId != null ? parentId : 0L)
                .author(userName)
                .content(content)
                .userId(userId)
                .build();
        
        return commentDao.createComment(newComment);
    }
    
    // 댓글 작성자 권한 확인
    public boolean isCommentAuthor(Long commentId) {
        // 실제 구현에서는 commentId로 댓글을 조회하여 작성자 확인
        // 현재는 간단히 구현
        return true;
    }
    
    // 댓글 내용 검증
    public boolean validateCommentContent(String content) {
        return content != null && !content.trim().isEmpty();
    }
}
