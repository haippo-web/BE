// src/com/board/model/FileLocation.java
package com.board.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*		[					]
 * 		[	배지원   담당   	]
 * 		[					]
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileLocation {
    private Long id;
    private Long userId;
    private Long boardId;        // submit_id 대신 board_id 사용
    private String filePath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}