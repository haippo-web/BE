package com.manager.service;

import com.manager.dao.ApprovalListDAO;
import com.manager.model.Approval;
import java.util.List;

public class ApprovalListService {
	private ApprovalListDAO approvalListDAO;

	public ApprovalListService() {
        this.approvalListDAO = new ApprovalListDAO();
    }

	public List<Approval> getAllApprovals() {
		return approvalListDAO.getAllApproval();
	}

	public boolean updateApproval(Approval approval) {
		return approvalListDAO.updateApproval(approval);
	}
}