package org.owen.individual;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

import org.owen.employee.Employee;
import org.owen.helper.DatabaseConnectionHelper;
import org.owen.helper.UtilHelper;


public class Login {

	/**
	 * Validates user name and password for login page
	 * @param emailId - email id of the user
	 * @param password - password of the user
	 * @param ipAddress - ip address of the machine from where the user logs in
	 * @param roleId - 1/2 depending on either Individual or HR (1:Individual 2:HR)
	 * @return Employee object
	 * @throws Exception - thrown when provided with invalid credentials
	 */
	public Employee login(String emailId, String password, String ipAddress) throws Exception {

		Employee e = new Employee();
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		Connection companySqlCon = null;

		int index = emailId.indexOf('@');
		String companyDomain = emailId.substring(index + 1);
		int companyId = 0;
		String companyName = "";
		try (CallableStatement cstmt = dch.masterDS.getConnection().prepareCall("{call getCompanyDb(?)}")) {

			cstmt.setString(1, companyDomain);
			try (ResultSet rs = cstmt.executeQuery()) {
				while (rs.next()) {
					companyId = rs.getInt("comp_id");
					dch.refreshCompanyConnection(companyId);
					companySqlCon = dch.companyConnectionMap.get(companyId).getDataSource().getConnection();
					companyName = rs.getString("comp_name");
					org.apache.log4j.Logger.getLogger(Login.class).debug("Company Name : " + companyName);
				}
				try (CallableStatement cstmt1 = companySqlCon.prepareCall("{call verifyLogin(?,?,?,?)}");) {

					cstmt1.setString("loginid", emailId);
					cstmt1.setString("pass", password);
					cstmt1.setTimestamp("curr_time", UtilHelper.convertJavaDateToSqlTimestamp(Date.from(Instant.now())));
					cstmt1.setString("ip", ipAddress);
					try (ResultSet rs1 = cstmt1.executeQuery()) {
						while (rs1.next()) {
							if (rs1.getInt("emp_id") == 0) {
								org.apache.log4j.Logger.getLogger(Login.class).error("Invalid username/password");
								cstmt1.close();
								rs1.close();
								companySqlCon.close();
								throw new Exception("Invalid credentials!!!");
							} else {
								e = e.get(companyId, rs1.getInt("emp_id"));
								e.setCompanyId(companyId);
								e.setCompanyName(companyName);
								org.apache.log4j.Logger.getLogger(Login.class).debug("Successfully validated user with userID : " + emailId);
							}
						}
					}
				}

			}

		} catch (SQLException e1) {
			org.apache.log4j.Logger.getLogger(Login.class).error("Exception while retrieving the company database", e1);
		}
		companySqlCon.close();
		return e;
	}

	/**
	 * Retrieves the role list
	 * @param companyId - ID of the company
	 * @return Map of role ID and role
	 */
	/*public Map<Integer, String> getUserRoleMap(int companyId) {
		Map<Integer, String> userRoleMap = new HashMap<>();
		DatabaseConnectionHelper dch = ObjectFactory.getDBHelper();
		dch.getCompanyConnection(companyId);
		try {
			CallableStatement cstmt1 = dch.companyConnectionMap.get(companyId).getSqlConnection().prepareCall("{call getRoleList()}");
			ResultSet res = cstmt1.executeQuery();
			while (res.next()) {
				userRoleMap.put(res.getInt("role_id"), res.getString("role"));
			}
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(Login.class).error("Exception while retrieving the user role map", e);
		}
		return userRoleMap;
	}*/

}
