package com.notification.model;

import java.sql.Date;
import com.util.BaseEntity;

/*		[					]
 * 		[	배지원    담당   	]
 * 		[					]
 */

public class Notification extends BaseEntity {
    private Long id;
    private String title;
    private String content;
    private String type; // "COMMENT", "ASSIGNMENT", "ATTENDANCE" 등
    private Long userId;
    private boolean isRead;
    private Date createdAt; // 추가
    
    // 기본 생성자
    public Notification() {}
    
    // 전체 생성자
    public Notification(Long id, String title, String content, String type, 
                       Long userId, boolean isRead, Date createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.userId = userId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
    
    // Getter와 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public boolean isRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", userId=" + userId +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}