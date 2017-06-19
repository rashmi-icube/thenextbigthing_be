package org.owen.employee;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.owen.helper.DatabaseConnectionHelper;
import org.owen.helper.UtilHelper;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;

public class EmployeeList {

	/**
	 * Returns the employee smart list for initiatives of type Individual based on the filter objects provided
	 * @param companyId - Company ID of the employee
	 * @param partOfEmployeeList - List of employee objects which are part of the initiative
	 * @param initiativeType - ID of the type of initiative
	 * @return List of employee objects
	 */

	public List<Employee> getEmployeeSmartListForIndividual(int companyId, List<Employee> partOfEmployeeList, int initiativeType) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		List<Employee> individualSmartList = new ArrayList<Employee>();
		List<Integer> partOfEmployeeIdList = new ArrayList<>();
		for (Employee e : partOfEmployeeList) {
			partOfEmployeeIdList.add(e.getEmployeeId());
		}
		try {
			RConnection rCon = dch.getRConn();
			Logger.getLogger(EmployeeList.class).debug("R Connection Available : " + rCon.isConnected());
			Logger.getLogger(EmployeeList.class).debug("Filling up parameters for rscript function");
			rCon.assign("company_id", new int[] { companyId });
			rCon.assign("emp_id", new int[] { partOfEmployeeIdList.get(0) });
			rCon.assign("init_type_id", new int[] { initiativeType });
			Logger.getLogger(EmployeeList.class).debug("Calling the actual function in RScript IndividualSmartList");
			REXP employeeSmartList = rCon.parseAndEval("try(eval(IndividualSmartList(company_id, emp_id, init_type_id)))");
			if (employeeSmartList.inherits("try-error")) {
				Logger.getLogger(EmployeeList.class).error("Error: " + employeeSmartList.asString());
				dch.releaseRcon();
				throw new Exception("Error: " + employeeSmartList.asString());
			} else {
				Logger.getLogger(EmployeeList.class).debug(
						"Retrieval of the employee smart list completed " + employeeSmartList.asList());
			}

			RList result = employeeSmartList.asList();
			REXPInteger empIdResult = (REXPInteger) result.get("emp_id");
			int[] empIdArray = empIdResult.asIntegers();
			REXPString gradeRseult = (REXPString) result.get("flag");
			String[] gradeArray = gradeRseult.asStrings();
			REXPInteger rank = (REXPInteger) result.get("Rank");
			int[] rankArray = rank.asIntegers();
			Map<Integer, Employee> empMap = new TreeMap<>();

			for (int i = 0; i < empIdArray.length; i++) {
				Employee e = new Employee();
				e = e.get(companyId, empIdArray[i]);
				e.setGrade(gradeArray[i]);
				empMap.put(rankArray[i], e);
			}

			for (Employee e : empMap.values()) {
				individualSmartList.add(e);
			}
		} catch (Exception e) {
			Logger.getLogger(EmployeeList.class).error("Error while trying to retrieve the smart list for employee ", e);
		} finally {
			dch.releaseRcon();
		}

		return individualSmartList;
	}

	/**
	 * Retrieves the employee master list from the company db
	 * @param companyId - Company ID of the employee
	 * @return list of employee objects
	 */

	public List<Employee> getEmployeeMasterList(int companyId) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		List<Employee> employeeList = new ArrayList<>();
		try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection()
				.prepareCall("{call getEmployeeList()}");
				ResultSet res = cstmt.executeQuery()) {
			Logger.getLogger(EmployeeList.class).debug("getEmployeeMasterList method started");
			Logger.getLogger(EmployeeList.class).debug("query : " + cstmt);
			while (res.next()) {
				Employee e = setEmployeeDetails(companyId, res);
				e.setCompanyId(companyId);
				employeeList.add(e);
			}

			Logger.getLogger(EmployeeList.class).debug("employeeList : " + employeeList.toString());
		} catch (SQLException e) {
			Logger.getLogger(EmployeeList.class).error("Exception while getting the employee master list", e);

		}

		return employeeList;

	}

	/**
	 * Set the employee details based on the result from sql
	 * @param companyId - Company ID of the employee
	 * @param res - actual result from sql
	 * @return employee object
	 * @throws SQLException - if employee details are not set
	 */
	public Employee setEmployeeDetails(int companyId, ResultSet res) throws SQLException {
		Employee e = new Employee();
		e.setEmployeeId(res.getInt("emp_id"));
		e.setCompanyEmployeeId(res.getString("emp_int_id"));
		e.setFirstName(res.getString("first_name"));
		e.setLastName(res.getString("last_name"));
		e.setReportingManagerId(res.getString("reporting_emp_id"));
		if (res.getString("status") != null && res.getString("status").equalsIgnoreCase("active")) {
			e.setActive(true);
		} else {
			e.setActive(false);
		}

		e.setFunction(res.getString("Function"));
		e.setPosition(res.getString("Position"));
		e.setLocation(res.getString("Location"));
		if (UtilHelper.hasColumn(res, "score") && res.getDouble("score") >= 0) {
			e.setScore(res.getDouble("score"));
		}
		e.setCompanyId(companyId);
		return e;
	}

	/**
	 * Returns a list employee objects based on the employee IDs given
	 * @param companyId - List of employee objects
	 * @param employeeIdList - List of IDs of the employees that need to be retrieved
	 * @return employee object list
	 */
	public List<Employee> get(int companyId, List<Integer> employeeIdList) {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		dch.refreshCompanyConnection(companyId);
		List<Employee> empList = new ArrayList<>();

		// sub listing the employee ID list for every 100 employees due to db constraints
		int subListSize = 100;
		int empSubListCount = ((employeeIdList.size() % subListSize) > 0) ? (employeeIdList.size() / subListSize) + 1 : employeeIdList.size()
				/ subListSize;
		int listIndex = 0;
		List<Integer> empSubList = new ArrayList<>();
		for (int i = 0; i < empSubListCount; i++) {
			if ((listIndex + subListSize) > employeeIdList.size()) {
				empSubList = employeeIdList.subList(listIndex, employeeIdList.size());
			} else {
				empSubList = employeeIdList.subList(listIndex, listIndex + subListSize);
			}

			try (CallableStatement cstmt = dch.companyConnectionMap.get(companyId).getDataSource().getConnection().prepareCall(
					"{call getEmployeeDetails(?)}")) {
				Logger.getLogger(EmployeeList.class).debug("get method started");
				cstmt.setString(1, empSubList.toString().substring(1, empSubList.toString().length() - 1).replaceAll(" ", ""));
				try (ResultSet res = cstmt.executeQuery()) {
					Logger.getLogger(EmployeeList.class).debug("query : " + cstmt);
					while (res.next()) {
						Employee e = setEmployeeDetails(companyId, res);
						Logger.getLogger(EmployeeList.class).debug(
								"Employee  : " + e.getEmployeeId() + "-" + e.getFirstName() + "-" + e.getLastName());
						empList.add(e);
					}
					listIndex = listIndex + subListSize;
				}

			} catch (SQLException e1) {
				Logger.getLogger(EmployeeList.class).error(
						"Exception while retrieving employee object with employeeIds : " + employeeIdList, e1);
			}
		}
		return empList;
	}
}
