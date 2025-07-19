package com.mypage.domain;

import java.sql.Date;

public class AttendanceApprovalRequest {
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getProofFile() {
		return proofFile;
	}
	public void setProofFile(String proofFile) {
		this.proofFile = proofFile;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getRequestedAt() {
		return requestedAt;
	}
	public void setRequestedAt(Date requestedAt) {
		this.requestedAt = requestedAt;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	private Long id;
    private String reason;
    private String proofFile;
    private String status;      // 신청시 기본값: "progressing"
    private Date requestedAt;
    private Date startDate;
    private Date endDate;
    private Long userId;

}
