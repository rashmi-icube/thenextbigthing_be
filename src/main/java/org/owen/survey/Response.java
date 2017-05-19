package org.owen.survey;

public class Response {

	private int companyId;
	private int employeeId;
	private int questionId;
	private QuestionType questionType;
	private int responseValue;
	private String responseString;
	private int targetEmployee;

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public int getResponseValue() {
		return responseValue;
	}

	public void setResponseValue(int responseValue) {
		this.responseValue = responseValue;
	}

	public int getTargetEmployee() {
		return targetEmployee;
	}

	public void setTargetEmployee(int targetEmployee) {
		this.targetEmployee = targetEmployee;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

}
