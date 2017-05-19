package org.owen.helper;

public class CompanyConfig {

	private boolean sendEmail;
	private boolean displayNetworkName;
	private String status;
	private String sqlUrl;
	private String sqlUserName;
	private String sqlPassword;
	private boolean runJobs;

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public boolean isDisplayNetworkName() {
		return displayNetworkName;
	}

	public void setDisplayNetworkName(boolean displayNetworkName) {
		this.displayNetworkName = displayNetworkName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSqlUrl() {
		return sqlUrl;
	}

	public void setSqlUrl(String sqlUrl) {
		this.sqlUrl = sqlUrl;
	}

	public String getSqlUserName() {
		return sqlUserName;
	}

	public void setSqlUserName(String sqlUserName) {
		this.sqlUserName = sqlUserName;
	}

	public String getSqlPassword() {
		return sqlPassword;
	}

	public void setSqlPassword(String sqlPassword) {
		this.sqlPassword = sqlPassword;
	}

	public boolean isRunJobs() {
		return runJobs;
	}

	public void setRunJobs(boolean runJobs) {
		this.runJobs = runJobs;
	}

}
