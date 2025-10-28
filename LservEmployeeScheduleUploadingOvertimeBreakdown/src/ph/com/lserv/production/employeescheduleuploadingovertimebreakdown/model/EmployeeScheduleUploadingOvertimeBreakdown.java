package ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model;

import java.sql.Time;
import java.util.Date;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lserv.production.employeescheduleencodingovertime.model.EmployeeScheduleEncodingOvertime;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;

public class EmployeeScheduleUploadingOvertimeBreakdown extends MasterObject {
	Integer primaryKey;
	EmployeeScheduleUploading employeeScheduleUploading;
	EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime;
	OvertimeFiling overtimeFiling;
	OvertimeFiling earlyOvertimeFiling;
	OvertimeFilingClient overtimeFilingClient;
	OvertimeFilingClient earlyOvertimeFilingClient;
	OvertimeType overtimeType;
	Client client;
	Department department;
	EmploymentHistory employmentHistory;
	Integer totalMin;
	Time allowedOvertime; // na approve na overtime filing, get time end ng overtime
	Date payFrom;
	Date payTo;

	public EmployeeScheduleUploadingOvertimeBreakdown() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EmployeeScheduleUploadingOvertimeBreakdown(Integer primaryKey,
			EmployeeScheduleUploading employeeScheduleUploading, EmploymentHistory employmentHistory, Integer totalMin,
			Date payFrom, Date payTo, Time allowedOvertime,
			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime, OvertimeType overtimeType, Client client,
			Department department, OvertimeFiling overtimeFiling, User user, String changedByUser,
			String changedInComputer, Date changedOnDate, OvertimeFilingClient overtimeFilingClient,
			OvertimeFilingClient earlyOvertimeFilingClient, OvertimeFiling earlyOvertimeFiling) {
		super();
		this.primaryKey = primaryKey;
		this.employeeScheduleUploading = employeeScheduleUploading;
		this.employmentHistory = employmentHistory;
		this.totalMin = totalMin;
		this.payFrom = payFrom;
		this.payTo = payTo;
		this.allowedOvertime = allowedOvertime;
		this.employeeScheduleEncodingOvertime = employeeScheduleEncodingOvertime;
		this.overtimeFiling = overtimeFiling;
		this.overtimeType = overtimeType;
		this.client = client;
		this.department = department;
		this.overtimeFilingClient = overtimeFilingClient;
		this.earlyOvertimeFilingClient = earlyOvertimeFilingClient;
		this.earlyOvertimeFiling = earlyOvertimeFiling;

		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getChangedByUser() {
		return changedByUser;
	}

	public void setChangedByUser(String changedByUser) {
		this.changedByUser = changedByUser;
	}

	public String getChangedInComputer() {
		return changedInComputer;
	}

	public void setChangedInComputer(String changedInComputer) {
		this.changedInComputer = changedInComputer;
	}

	public Date getChangedOnDate() {
		return changedOnDate;
	}

	public void setChangedOnDate(Date changedOnDate) {
		this.changedOnDate = changedOnDate;
	}

	public EmployeeScheduleUploading getEmployeeScheduleUploading() {
		return employeeScheduleUploading;
	}

	public void setEmployeeScheduleUploading(EmployeeScheduleUploading employeeScheduleUploading) {
		this.employeeScheduleUploading = employeeScheduleUploading;
	}

	public EmploymentHistory getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(EmploymentHistory employmentHistory) {
		this.employmentHistory = employmentHistory;
	}

	public Time getAllowedOvertime() {
		return allowedOvertime;
	}

	public void setAllowedOvertime(Time allowedOvertime) {
		this.allowedOvertime = allowedOvertime;
	}

	public Integer getTotalMin() {
		return totalMin;
	}

	public void setTotalMin(Integer totalMin) {
		this.totalMin = totalMin;
	}

	public Date getPayFrom() {
		return payFrom;
	}

	public void setPayFrom(Date payFrom) {
		this.payFrom = payFrom;
	}

	public Date getPayTo() {
		return payTo;
	}

	public void setPayTo(Date payTo) {
		this.payTo = payTo;
	}

	public EmployeeScheduleEncodingOvertime getEmployeeScheduleEncodingOvertime() {
		return employeeScheduleEncodingOvertime;
	}

	public void setEmployeeScheduleEncodingOvertime(EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime) {
		this.employeeScheduleEncodingOvertime = employeeScheduleEncodingOvertime;
	}

	public OvertimeType getOvertimeType() {
		return overtimeType;
	}

	public void setOvertimeType(OvertimeType overtimeType) {
		this.overtimeType = overtimeType;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public OvertimeFiling getOvertimeFiling() {
		return overtimeFiling;
	}

	public void setOvertimeFiling(OvertimeFiling overtimeFiling) {
		this.overtimeFiling = overtimeFiling;
	}

	public OvertimeFilingClient getOvertimeFilingClient() {
		return overtimeFilingClient;
	}

	public void setOvertimeFilingClient(OvertimeFilingClient overtimeFilingClient) {
		this.overtimeFilingClient = overtimeFilingClient;
	}
	
	public OvertimeFilingClient getEarlyOvertimeFilingClient() {
		return earlyOvertimeFilingClient;
	}

	public void setEarlyOvertimeFilingClient(OvertimeFilingClient earlyOvertimeFilingClient) {
		this.earlyOvertimeFilingClient = earlyOvertimeFilingClient;
	}
	
	public OvertimeFiling getEarlyOvertimeFiling() {
		return earlyOvertimeFiling;
	}

	public void setEarlyOvertimeFiling(OvertimeFiling earlyOvertimeFiling) {
		this.earlyOvertimeFiling = earlyOvertimeFiling;
	}

	@Override
	public String toString() {
		return "EmployeeScheduleUploadingOvertimeBreakdown [primaryKey=" + primaryKey + ", employeeScheduleUploading="
				+ employeeScheduleUploading + ", employeeScheduleEncodingOvertime=" + employeeScheduleEncodingOvertime
				+ ", overtimeFiling=" + overtimeFiling + ", earlyOvertimeFiling=" + earlyOvertimeFiling
				+ ", overtimeFilingClient=" + overtimeFilingClient + ", earlyOvertimeFilingClient="
				+ earlyOvertimeFilingClient + ", overtimeType=" + overtimeType + ", client=" + client + ", department="
				+ department + ", employmentHistory=" + employmentHistory + ", totalMin=" + totalMin
				+ ", allowedOvertime=" + allowedOvertime + ", payFrom=" + payFrom + ", payTo=" + payTo + "]";
	}

}
