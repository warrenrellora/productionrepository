package ph.com.lserv.production.overtimefilingclient.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;

public class OvertimeFilingClient extends MasterObject {
	Integer primaryKey;
	Timestamp dateTimeFrom, dateTimeTo;
	Date duration, dateFiled;
	Client client;
	Department department;
	EmploymentHistory employmentHistory;
	BigDecimal totalOtHours;
	
	public OvertimeFilingClient() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OvertimeFilingClient(Integer primaryKey, Timestamp dateTimeFrom, Timestamp dateTimeTo, Date duration,
			Date dateFiled, Client client, Department department, BigDecimal totalOtHours, EmploymentHistory employmentHistory) {
		super();
		this.primaryKey = primaryKey;
		this.dateTimeFrom = dateTimeFrom;
		this.dateTimeTo = dateTimeTo;
		this.duration = duration;
		this.dateFiled = dateFiled;
		this.client = client;
		this.department = department;
		this.totalOtHours = totalOtHours;
		this.employmentHistory = employmentHistory;
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

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Timestamp getDateTimeFrom() {
		return dateTimeFrom;
	}

	public void setDateTimeFrom(Timestamp dateTimeFrom) {
		this.dateTimeFrom = dateTimeFrom;
	}

	public Timestamp getDateTimeTo() {
		return dateTimeTo;
	}

	public void setDateTimeTo(Timestamp dateTimeTo) {
		this.dateTimeTo = dateTimeTo;
	}

	public Date getDuration() {
		return duration;
	}

	public void setDuration(Date duration) {
		this.duration = duration;
	}

	public Date getDateFiled() {
		return dateFiled;
	}

	public void setDateFiled(Date dateFiled) {
		this.dateFiled = dateFiled;
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
	
	public BigDecimal getTotalOtHours() {
		return totalOtHours;
	}

	public void setTotalOtHours(BigDecimal totalOtHours) {
		this.totalOtHours = totalOtHours;
	}
	
	public EmploymentHistory getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(EmploymentHistory employmentHistory) {
		this.employmentHistory = employmentHistory;
	}

	@Override
	public String toString() {
		return "OvertimeFilingClient [primaryKey=" + primaryKey + ", dateTimeFrom=" + dateTimeFrom + ", dateTimeTo="
				+ dateTimeTo + ", duration=" + duration + ", dateFiled=" + dateFiled + ", client=" + client
				+ ", department=" + department + ", employmentHistory=" + employmentHistory + ", totalOtHours="
				+ totalOtHours + "]";
	}

}
