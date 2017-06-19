package org.owen.survey;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.owen.helper.DatabaseConnectionHelper;

public class QuestionList {

	/**
	 * Retrieves the list of all the questions
	 * @param companyId - Company ID
	 * @return - A list of questions
	 */

	public List<Question> getCurrentQuestionList(int companyId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		List<Question> questionList = new ArrayList<>();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection()
				.prepareCall("{call getCurrentQuestionList()}");
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
			Logger.getLogger(QuestionList.class).error("Exception while retrieving the list of questions", e);
		}
		return questionList;
	}

	public List<Question> getQuestionMasterList(int companyId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		List<Question> questionList = new ArrayList<>();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection()
				.prepareCall("{call getQuestionMasterList()}");
				ResultSet rs = cstmt.executeQuery()) {

			while (rs.next()) {
				Question q = new Question();
				q.setQuestionId(rs.getInt("que_id"));
				q.setQuestionText(rs.getString("question"));
				q.setQuestionType(QuestionType.values()[rs.getInt("que_type")]);
				q.setRelationshipTypeId(rs.getInt("rel_id"));
				questionList.add(q);
			}
		} catch (SQLException e) {
			Logger.getLogger(QuestionList.class).error("Exception while retrieving the list of questions", e);
		}
		return questionList;
	}
	
	public boolean addQuestion(int companyId, List<Integer> questionIdList){
		return false;
	}
	
	public boolean removeQuestion(int companyId, List<Integer> questionIdList){
		return false;
	}
	
	
}
