package org.owen.survey;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owen.employee.Employee;
import org.owen.helper.DatabaseConnectionHelper;
import org.owen.helper.UtilHelper;

public class Question {

	private int questionMasterId;
	private Date startDate;
	private Date endDate;
	private String questionText;
	private QuestionType questionType;
	private int questionId;
	private int relationshipTypeId;
	private String relationshipName;

	public int getQuestionMasterId() {
		return questionMasterId;
	}

	public void setQuestionMasterId(int questionMasterId) {
		this.questionMasterId = questionMasterId;
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

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getRelationshipTypeId() {
		return relationshipTypeId;
	}

	public void setRelationshipTypeId(int relationshipTypeId) {
		this.relationshipTypeId = relationshipTypeId;
	}

	public String getRelationshipName() {
		return relationshipName;
	}

	public void setRelationshipName(String relationshipName) {
		this.relationshipName = relationshipName;
	}

	/**
	* Retrieves the list of questions for the employee
	* @param companyId - Company ID of the employee
	* @param employeeId - Employee ID
	* @return a list of Question objects
	*/
	public List<Question> getEmployeeQuestionList(int companyId, int employeeId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		List<Question> questionList = new ArrayList<>();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getEmpQuestionList(?,?)}")) {
			cstmt.setInt(1, employeeId);
			Date date = (Date) Date.from(Instant.now());
			cstmt.setDate(2, UtilHelper.convertJavaDateToSqlDate(date));
			try (ResultSet rs = cstmt.executeQuery()) {
				while (rs.next()) {
					Question q = new Question();
					q.setQuestionId(rs.getInt("que_id"));
					q.setQuestionText(rs.getString("question"));
					q.setStartDate(rs.getDate("start_date"));
					q.setEndDate(rs.getDate("end_date"));
					q.setRelationshipTypeId(rs.getInt("rel_id"));
					q.setQuestionType(QuestionType.get(rs.getInt("que_type")));
					Logger.getLogger(Question.class).debug(
							"Question for employee : " + q.getQuestionId() + " - " + q.getQuestionText() + " - " + q.getRelationshipTypeId());

					questionList.add(q);
				}
			}

		} catch (SQLException e) {
			Logger.getLogger(Question.class).error("Exception while retrieving the questionList", e);
		}

		return questionList;
	}

	public String getJsonEmployeeQuestionList(int companyId, int employeeId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		JSONArray arr = new JSONArray();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getEmpQuestionList(?,?)}")) {

			cstmt.setInt(1, employeeId);
			Date date = (Date) Date.from(Instant.now());
			cstmt.setDate(2, UtilHelper.convertJavaDateToSqlDate(date));
			try (ResultSet rs = cstmt.executeQuery()) {
				while (rs.next()) {
					JSONObject json = new JSONObject();
					json.put("questionId", rs.getInt("que_id"));
					json.put("questionText", rs.getString("question"));
					json.put("questionType", QuestionType.get(rs.getInt("que_type")));
					arr.put(json);
				}
			}

		} catch (SQLException | JSONException e) {
			Logger.getLogger(Question.class).error("Exception while retrieving the questionList", e);
		}
		return arr.toString();
	}

	/**
	 * Returns the default smart list for the employee on the we question page
	 * @param companyId - ID of the company to which the logged in user belongs
	 * @param employeeId - Employee ID of the individual who is logged in
	 * @param q - A question object
	 * @return List of employee objects - view of the employee list should be sorted by the rank
	 */
	public List<Employee> getSmartListForQuestion(int companyId, int employeeId, int questionId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		List<Employee> employeeList = new ArrayList<>();
		dch.refreshCompanyConnection(companyId);
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getListColleague(?)}")) {
			cstmt.setString("array", String.valueOf(employeeId));
			try (ResultSet rs = cstmt.executeQuery()) {
				List<Integer> empIdList = new ArrayList<>();
				while (rs.next()) {
					empIdList.add(rs.getInt("emp_id"));
				}
				Employee e = new Employee();
				employeeList = e.get(companyId, empIdList);
			}

		} catch (Exception e) {
			Logger.getLogger(Question.class).error("Error while trying to retrieve the smart list for employee from question", e);
		}
		return employeeList;
	}

}
