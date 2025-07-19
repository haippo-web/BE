package com.mypage.domain;

import java.time.LocalDateTime;

public class Attendance {
    private Long id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String status;
    private Long userId;

    public Attendance() {}

    public Attendance(Long id, LocalDateTime checkIn, LocalDateTime checkOut, String status, Long userId) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Attendance [id=" + id + ", checkIn=" + checkIn + ", checkOut=" + checkOut +
               ", status=" + status + ", userId=" + userId + "]";
    }
}

