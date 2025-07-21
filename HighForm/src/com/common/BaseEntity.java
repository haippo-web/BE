package com.common;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEntity {
    protected Date createdAt;
    protected Date updatedAt;
    protected char deleted;


    public void markAsDeleted() { this.deleted = 'Y'; }
}
