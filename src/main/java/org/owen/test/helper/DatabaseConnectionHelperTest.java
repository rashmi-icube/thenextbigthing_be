package org.owen.test.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.owen.helper.DatabaseConnectionHelper;

public class DatabaseConnectionHelperTest {

	@Test
	public void testDatabaseConnectionHelper() {
		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		Assert.assertTrue(dch != null);
	}

	@Test
	public void testRefreshCompanyConnection() throws SQLException {

		DatabaseConnectionHelper dch = DatabaseConnectionHelper.getDBHelper();
		try (CallableStatement cstmt = dch.masterDS.getConnection().prepareCall("{call getCompanyDb(?)}")) {

			cstmt.setString(1, "owenlite.com");
			try (ResultSet rs = cstmt.executeQuery()) {
				while (rs.next()) {
					int companyId = rs.getInt("comp_id");
					dch.refreshCompanyConnection(companyId);
					Connection companySqlCon = dch.companyConnectionMap.get(companyId).getDataSource().getConnection();
					Assert.assertTrue(companySqlCon != null);
				}
			}
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
	}
}
