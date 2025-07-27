package com.board.model;

import com.util.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*		[					]
 * 		[	배지원   담당   	]
 * 		[					]
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment extends BaseEntity {
    private Long id;
    private Long boardId;
    private Long parentId;  // 0이면 최상위 댓글, 아니면 대댓글
    private String author;
    private String content;
    private Long userId;
    private boolean isOwner;  // 현재 사용자가 작성자인지 여부 (UI용)
    
    // 계층 구조 관련 필드들 (View에서 추가되는 정보)
    private Integer commentLevel;  // 댓글 깊이 (1: 최상위, 2: 대댓글, 3: 대대댓글...)
    private Long rootCommentId;    // 최상위 댓글 ID
    private String commentPath;    // 댓글 경로 (예: /1/5/10)
    private String commentType;    // 댓글 타입 (PARENT: 최상위, REPLY: 대댓글)
}
