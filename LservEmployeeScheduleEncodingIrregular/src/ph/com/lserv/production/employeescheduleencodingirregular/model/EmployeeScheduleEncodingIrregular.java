package ph.com.lserv.production.employeescheduleencodingirregular.model;

import java.sql.Time;
import java.util.Date;

import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;

public class EmployeeScheduleEncodingIrregular extends MasterObject {
	Integer primaryKey;
	Date dateSchedule;
	EmploymentHistory employmentHistory;
	Time timeIn, timeOut, lunchIn, lunchOut;
	Integer offsetAllowed, totalMinPerDay, isBreakExempted;

//	User user;
//	String changedByUser;
//	String changedInComputer;
//	Date changedOnDate;

	public EmployeeScheduleEncodingIrregular() {
		super();
	}

	public EmployeeScheduleEncodingIrregular(Integer primaryKey, EmploymentHistory employmentHistory, Date dateSchedule,
			Time timeIn, Time timeOut, Time lunchIn, Time lunchOut, Integer offsetAllowed, Integer totalMinPerDay,
			User user, String changedByUser, String changedInComputer, Date changedOnDate, Integer isBreakExempted) {
		super();
		this.primaryKey = primaryKey;
		this.employmentHistory = employmentHistory;
		this.dateSchedule = dateSchedule;
		this.timeIn = timeIn;
		this.timeOut = timeOut;
		this.lunchIn = lunchIn;
		this.lunchOut = lunchOut;
		this.offsetAllowed = offsetAllowed;
		this.totalMinPerDay = totalMinPerDay;
		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.isBreakExempted = isBreakExempted;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public EmploymentHistory getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(EmploymentHistory employmentHistory) {
		this.employmentHistory = employmentHistory;
	}

	public Date getDateSchedule() {
		return dateSchedule;
	}

	public void setDateSchedule(Date dateSchedule) {
		this.dateSchedule = dateSchedule;
	}

	public Time getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Time timeIn) {
		this.timeIn = timeIn;
	}

	public Time getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Time timeOut) {
		this.timeOut = timeOut;
	}

	public Time getLunchIn() {
		return lunchIn;
	}

	public void setLunchIn(Time lunchIn) {
		this.lunchIn = lunchIn;
	}

	public Time getLunchOut() {
		return lunchOut;
	}

	public void setLunchOut(Time lunchOut) {
		this.lunchOut = lunchOut;
	}

	public Integer getOffsetAllowed() {
		return offsetAllowed;
	}

	public void setOffsetAllowed(Integer offsetAllowed) {
		this.offsetAllowed = offsetAllowed;
	}

	public Integer getTotalMinPerDay() {
		return totalMinPerDay;
	}

	public void setTotalMinPerDay(Integer totalMinPerDay) {
		this.totalMinPerDay = totalMinPerDay;
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
	
	
	public Integer getIsBreakExempted() {
		return isBreakExempted;
	}

	public void setIsBreakExempted(Integer isBreakExempted) {
		this.isBreakExempted = isBreakExempted;
	}

	@Override
	public String toString() {
		return "EmployeeScheduleEncodingIrregular [primaryKey=" + primaryKey + ", dateSchedule=" + dateSchedule
				+ ", employmentHistory=" + employmentHistory + ", timeIn=" + timeIn + ", timeOut=" + timeOut
				+ ", lunchIn=" + lunchIn + ", lunchOut=" + lunchOut + ", offsetAllowed=" + offsetAllowed
				+ ", totalMinPerDay=" + totalMinPerDay + ", isBreakExempted=" + isBreakExempted + "]";
	}

}
