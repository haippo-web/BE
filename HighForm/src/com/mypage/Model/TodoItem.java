package com.mypage.Model;

import java.time.LocalDate;

public class TodoItem {
    private Long id;
    private String title;
    private String content;
    private LocalDate dueDate;
    private boolean done;

    // ▽ Lombok 대신 수동 작성
    public TodoItem() {}
    public TodoItem(Long id, String t, String c, LocalDate d, boolean done) {
        this.id = id; this.title = t; this.content = c; this.dueDate = d; this.done = done;
    }
    public Long getId() { return id; }                        public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }                public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }            public void setContent(String c) { this.content = c; }
    public LocalDate getDueDate() { return dueDate; }         public void setDueDate(LocalDate d) { this.dueDate = d; }
    public boolean isDone() { return done; }                  public void setDone(boolean done) { this.done = done; }
}
