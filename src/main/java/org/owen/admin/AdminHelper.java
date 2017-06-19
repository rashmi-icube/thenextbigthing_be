package org.owen.admin;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.owen.employee.EmployeeList;
import org.owen.helper.DatabaseConnectionHelper;
import org.owen.helper.UtilHelper;
import org.owen.survey.Question;
import org.owen.survey.QuestionType;

public class AdminHelper {

	public List<Question> getVisibleQuestionList(int companyId) {
		List<Question> qList = new ArrayList<>();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getVisibleQuestionList()}");
				ResultSet res = cstmt.executeQuery()) {
			while (res.next()) {
				Question q = new Question();
				q.setQuestionId(res.getInt("que_id"));
				q.setQuestionText(res.getString("question"));
				q.setQuestionType(QuestionType.get(res.getInt("que_type")));
				q.setRelationshipTypeId(res.getInt("rel_id"));
				q.setRelationshipName(res.getString("rel_name"));
				q.setStartDate(res.getDate("start_date"));
				q.setEndDate(res.getDate("end_date"));
				qList.add(q);
			}
		} catch (SQLException e) {
			Logger.getLogger(EmployeeList.class).error("Exception while getting the visible question list", e);
		}
		return qList;
	}

	public boolean addQuestion(int companyId, List<Integer> qIdList, Date startDate, Date endDate) {
		boolean flag = false;
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		for (int i = 0; i < qIdList.size(); i++) {
			flag = false;
			try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
					"{call addQuestion(?,?,?)}")) {
				cstmt.setInt("queMasterId", qIdList.get(i));
				cstmt.setDate("startDate", UtilHelper.convertJavaDateToSqlDate(startDate));
				cstmt.setDate("endDate", UtilHelper.convertJavaDateToSqlDate(endDate));
				try (ResultSet res = cstmt.executeQuery()) {
					if (res.next()) {
						flag = true;
					}
				}
			} catch (SQLException e) {
				Logger.getLogger(EmployeeList.class).error("Exception while adding question", e);
			}
		}
		return flag;
	}

	public boolean removeQuestion(int companyId, List<Integer> qIdList) {
		boolean flag = false;
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection()
				.prepareCall("{call removeQuestion(?)}")) {
			cstmt.setString("queId", qIdList.toString().substring(1, qIdList.toString().length() - 1).replaceAll(" ", ""));
			try (ResultSet res = cstmt.executeQuery()) {
				if (res.next()) {
					flag = true;
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(EmployeeList.class).error("Exception while removing question", e);
		}

		return flag;
	}

	public boolean updateQuestionDate(int companyId, Date startDate, Date endDate) {
		boolean flag = false;
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		List<Question> qList = getVisibleQuestionList(companyId);
		List<Integer> qIdList = new ArrayList<>();
		for (int i = 0; i < qList.size(); i++) {
			qIdList.add(qList.get(i).getQuestionId());
		}
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call updateQuestionDate(?,?,?)}")) {
			cstmt.setString("queIds", qIdList.toString().substring(1, qIdList.toString().length() - 1).replaceAll(" ", ""));
			cstmt.setDate("startDate", UtilHelper.convertJavaDateToSqlDate(startDate));
			cstmt.setDate("endDate", UtilHelper.convertJavaDateToSqlDate(endDate));
			try (ResultSet res = cstmt.executeQuery()) {
				if (res.next()) {
					flag = true;
				}
			}
		} catch (SQLException e) {
			Logger.getLogger(EmployeeList.class).error("Exception while updating question date", e);
		}
		return flag;
	}

	public List<Question> getQuestionMasterList(int companyId) {
		List<Question> qList = new ArrayList<>();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getQuestionMasterList()}");
				ResultSet res = cstmt.executeQuery()) {
			while (res.next()) {
				Question q = new Question();
				q.setQuestionMasterId(res.getInt("que_master_id"));
				;
				q.setQuestionText(res.getString("question"));
				q.setQuestionType(QuestionType.get(res.getInt("que_type")));
				q.setRelationshipTypeId(res.getInt("rel_id"));
				q.setRelationshipName(res.getString("rel_name"));
				qList.add(q);
			}
		} catch (SQLException e) {
			Logger.getLogger(EmployeeList.class).error("Exception while getting the visible question list", e);
		}
		return qList;
	}
}
