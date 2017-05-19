package org.owen.helper;

import org.apache.tomcat.jdbc.pool.DataSource;

public class CompanyConnection {

	private DataSource sqlDataSource;

	public DataSource getDataSource() {
		return sqlDataSource;
	}

	public void setDataSource(DataSource sqlDataSource) {
		this.sqlDataSource = sqlDataSource;
	}

}
