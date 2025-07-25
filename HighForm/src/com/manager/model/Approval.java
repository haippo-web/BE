package com.manager.model;

public class Approval {
	private int approvalId;
	private String category;
	private String reason;
	private String proof_file;
	private String status;
	private String requested_at;
	private String decision_at;
	private String start_date;
	private String end_date;
	private int user_id;
	
	private String userName;
	private String userAffiliation;

	public Approval() {
	}

	public Approval(int approvalId, String category, String reason, String proof_file, String status, String requested_at,
			String decision_at, String start_date, String end_date, int user_id, String userName, String userAffiliation) {
		super();
		this.approvalId = approvalId;
		this.setCategory(category);
		this.reason = reason;
		this.proof_file = proof_file;
		this.status = status;
		this.requested_at = requested_at;
		this.decision_at = decision_at;
		this.start_date = start_date;
		this.end_date = end_date;
		this.user_id = user_id;
		this.userName = userName;
		this.setUserAffiliation(userAffiliation);
	}

	public int getApprovalId() {
		return approvalId;
	}

	public void setApprovalId(int approvalId) {
		this.approvalId = approvalId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProof_file() {
		return proof_file;
	}

	public void setProof_file(String proof_file) {
		this.proof_file = proof_file;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequested_at() {
		return requested_at;
	}

	public void setRequested_at(String requested_at) {
		this.requested_at = requested_at;
	}

	public String getDecision_at() {
		return decision_at;
	}

	public void setDecision_at(String decision_at) {
		this.decision_at = decision_at;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAffiliation() {
		return userAffiliation;
	}

	public void setUserAffiliation(String userAffiliation) {
		this.userAffiliation = userAffiliation;
	}

}