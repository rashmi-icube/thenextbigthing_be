package org.owen.survey;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.owen.helper.DatabaseConnectionHelper;

public class QuestionList {

	/**
	 * Retrieves the list of all the questions
	 * @param companyId - Company ID
	 * @return - A list of questions
	 */

	public List<Question> getQuestionList(int companyId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		List<Question> questionList = new ArrayList<>();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection()
				.prepareCall("{call getQuestionList()}");
				ResultSet rs = cstmt.executeQuery()) {

			while (rs.next()) {
				Question q = new Question();
				q.setQuestionId(rs.getInt("que_id"));
				q.setQuestionText(rs.getString("question"));
				q.setQuestionType(QuestionType.values()[rs.getInt("que_type")]);
				q.setStartDate(rs.getDate("start_date"));
				q.setEndDate(rs.getDate("end_date"));
				q.setRelationshipTypeId(rs.getInt("rel_id"));
				questionList.add(q);
			}
		} catch (SQLException e) {
			org.apache.log4j.Logger.getLogger(QuestionList.class).error("Exception while retrieving the list of questions", e);
		}
		return questionList;
	}

	/**
	 * Retrieves the list of questions of a particular batch and status (Upcoming/Completed)
	 * @param companyId - Company ID
	 * @param batchId - batch ID 
	 * @param filter - the status of the questions to be retrieved 
	 * @return - A list of Question objects based on the filter(status) and the batch ID 
	 */

	public List<Question> getQuestionListByStatus(int companyId,  String filter) {
		List<Question> questionList = getQuestionList(companyId);
		List<Question> questionListByStatus = new ArrayList<Question>();

		for (Question q1 : questionList) {
			if ((q1.getQuestionStatus(q1.getStartDate(), q1.getEndDate())).equalsIgnoreCase(filter)) {
				questionListByStatus.add(q1);
			}
		}
		org.apache.log4j.Logger.getLogger(QuestionList.class).debug(
				"Retrieved " + questionListByStatus.size() + " questions for " + filter + " status.");
		if (filter.equalsIgnoreCase("Completed")) {
			Collections.reverse(questionListByStatus);
		}
		return questionListByStatus;
	}

}
