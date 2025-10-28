package ph.com.lserv.production.employeescheduleencoding.model;

import java.util.Date;

import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class EmployeeScheduleEncoding extends MasterObject {
	Integer primaryKey;
	ScheduleEncodingReference scheduleEncodingReference;
	User user;
	String changedByUser;
	String changedInComputer;
	Date changedOnDate;
	EmploymentHistory employmentHistory;
	Integer totalMinPerDay;

	public EmployeeScheduleEncoding() {
		super();
	}

	public EmployeeScheduleEncoding(Integer primaryKey, User user, String changedByUser,
			String changedInComputer, Date changedOnDate, EmploymentHistory employmentHistory,
			ScheduleEncodingReference scheduleEncodingReference, Integer totalMinPerDay) {
		super();
		this.primaryKey = primaryKey;
		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.employmentHistory = employmentHistory;
		this.scheduleEncodingReference = scheduleEncodingReference;
		this.totalMinPerDay = totalMinPerDay;
	}
	
	public Integer getTotalMinPerDay() {
		return totalMinPerDay;
	}

	public void setTotalMinPerDay(Integer totalMinPerDay) {
		this.totalMinPerDay = totalMinPerDay;
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

	public EmploymentHistory getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(EmploymentHistory employmentHistory) {
		this.employmentHistory = employmentHistory;
	}

	public ScheduleEncodingReference getScheduleEncodingReference() {
		return scheduleEncodingReference;
	}

	public void setScheduleEncodingReference(ScheduleEncodingReference scheduleEncodingReference) {
		this.scheduleEncodingReference = scheduleEncodingReference;
	}

	@Override
	public String toString() {
		return "EmployeeScheduleEncoding [primaryKey=" + primaryKey + ", scheduleEncodingReference="
				+ scheduleEncodingReference + ", user=" + user + ", changedByUser=" + changedByUser
				+ ", changedInComputer=" + changedInComputer + ", changedOnDate=" + changedOnDate
				+ ", employmentHistory=" + employmentHistory + ", totalMinPerDay=" + totalMinPerDay + "]";
	}

}
