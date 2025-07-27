package com.util;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

/*		[					]
 * 		[	배지원    담당   	]
 * 		[					]
 */


@Getter
@Setter
public abstract class BaseEntity {
    protected Date createdAt;
    protected Date updatedAt;

    protected char del_yn;

    public void markAsDeleted() { this.del_yn = 'Y'; }
}


