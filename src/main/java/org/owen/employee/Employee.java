package org.owen.employee;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.owen.helper.DatabaseConnectionHelper;

public class Employee {

	private int employeeId;
	private String companyEmployeeId;
	private String firstName;
	private String lastName;
	private String reportingManagerId;
	private double score;
	private boolean active;
	private int companyId;
	private String companyName;
	private String grade; // can be high/medium/low
	private String function;
	private String position;
	private String location;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getCompanyEmployeeId() {
		return companyEmployeeId;
	}

	public void setCompanyEmployeeId(String companyEmployeeId) {
		this.companyEmployeeId = companyEmployeeId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getReportingManagerId() {
		return reportingManagerId;
	}

	public void setReportingManagerId(String reportingManagerId) {
		this.reportingManagerId = reportingManagerId;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Returns an employee object based on the employee ID given
	 * 
	 * @param employeeId - ID of the employee that needs to be retrieved
	 * @param companyId - Company ID of the employee 
	 * @return employee object
	 */
	public Employee get(int companyId, int employeeId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		EmployeeList el = new EmployeeList();
		Employee e = new Employee();
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getEmployeeDetails(?)}")) {
			Logger.getLogger(Employee.class).debug("get method started");
			cstmt.setInt(1, employeeId);
			try (ResultSet res = cstmt.executeQuery()) {
				Logger.getLogger(Employee.class).debug("query : " + cstmt);
				res.next();
				e = el.setEmployeeDetails(companyId, res);
				Logger.getLogger(Employee.class).debug(
						"Employee  : " + e.getEmployeeId() + "-" + e.getFirstName() + "-" + e.getLastName());
			}

		} catch (SQLException e1) {
			Logger.getLogger(Employee.class).error("Exception while retrieving employee object with employeeId : " + employeeId, e1);

		}
		return e;
	}

	public List<Employee> get(int companyId, List<Integer> employeeIdList) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		EmployeeList el = new EmployeeList();
		List<Employee> empList = new ArrayList<>();
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getEmployeeDetails(?)}")) {
			String empIdListStr = employeeIdList.toString();
			Logger.getLogger(Employee.class).debug("get method started");
			cstmt.setString("empid", (empIdListStr.substring(1, empIdListStr.length() - 2)).replace(" ", ""));
			try (ResultSet res = cstmt.executeQuery()) {
				Logger.getLogger(Employee.class).debug("query : " + cstmt);
				while (res.next()) {
					empList.add(el.setEmployeeDetails(companyId, res));
				}
			}
		} catch (SQLException e1) {
			Logger.getLogger(Employee.class).error("Exception while retrieving employee object with employeeId : " + employeeId, e1);

		}
		return empList;
	}

	public List<String> getEmployeeRoleList(int companyId, int employeeId) {
		List<String> roleList = new ArrayList<>();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);

		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
				"{call getEmployeeRoleList(?)}")) {

			Logger.getLogger(Employee.class).debug("get method started");
			cstmt.setInt("empid", employeeId);
			try (ResultSet res = cstmt.executeQuery()) {
				Logger.getLogger(Employee.class).debug("query : " + cstmt);
				while (res.next()) {
					roleList.add(res.getString("role"));
				}
			}
		} catch (SQLException e1) {
			Logger.getLogger(Employee.class).error("Exception while retrieving employee role list with employeeId : " + employeeId,
					e1);

		}
		return roleList;
	}
}