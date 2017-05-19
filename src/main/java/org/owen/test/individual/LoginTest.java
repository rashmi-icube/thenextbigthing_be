package org.owen.test.individual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.owen.employee.Employee;
import org.owen.individual.Login;

public class LoginTest {
	Login l = new Login();

	@Test
	public void testLoginEmployee() {
		try {
			Employee e = l.login("101-005734@owenlite.com", "abc123", "114.9.1.2");
			assertNotNull(e.getEmployeeId());		
			assertNotNull(e.getCompanyEmployeeId());
			assertNotNull(e.getFirstName());
			assertNotNull(e.getLastName());
			assertNotNull(e.getReportingManagerId());
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Invalid credentials!!!");
		}
	}

}
