package com.manager.service;

import com.manager.dao.EnrollmentDAO;

/*		[					]
 * 		[	이지민    담당   	]
 * 		[					]
 */

public class EnrollmentService {

    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public boolean enrollMemberToCourse(int memberId, int courseId) {
        return enrollmentDAO.insertEnrollment(memberId, courseId);
    }
}