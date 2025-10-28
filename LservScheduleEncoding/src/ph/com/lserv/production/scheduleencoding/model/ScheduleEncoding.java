package ph.com.lserv.production.scheduleencoding.model;

import java.sql.Time;
import java.util.Date;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class ScheduleEncoding extends MasterObject {
	Integer primaryKey;
	Integer primaryKeyReferenceClient;
	String scheduleName;
	Time timeIn;
	Time timeOut;
	Time lunchIn;
	Time lunchOut;
	Integer offsetAllowed;
	String scheduleDay;
	User user;
	String changedByUser;
	String changedInComputer;
	Date changedOnDate;
	Client client;
	String clientCode;
	ScheduleEncodingReference scheduleEncodingReference;
	Integer totalMinPerDay;
	Integer isBreakExempted;
	Employee employee;

	String regularScheduleHeader;
	String irregularScheduleHeader;

	public ScheduleEncoding() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ScheduleEncoding(Integer primaryKey, String scheduleName, Time timeIn, Time timeOut, Time lunchIn,
			Time lunchOut, Integer offsetAllowed, String scheduleDay, User user, String changedByUser,
			String changedInComputer, Date changedOnDate, Client client, String clientCode,
			Integer primaryKeyReferenceClient, ScheduleEncodingReference scheduleEncodingReference,
			Integer totalMinPerDay, Integer isBreakExempted, Employee employee) {
		super();
		this.primaryKey = primaryKey;
		this.scheduleName = scheduleName;
		this.timeIn = timeIn;
		this.timeOut = timeOut;
		this.lunchIn = lunchIn;
		this.lunchOut = lunchOut;
		this.offsetAllowed = offsetAllowed;
		this.scheduleDay = scheduleDay;
		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.client = client;
		this.clientCode = clientCode;
		this.primaryKeyReferenceClient = primaryKeyReferenceClient;
		this.scheduleEncodingReference = scheduleEncodingReference;
		this.totalMinPerDay = totalMinPerDay;
		this.isBreakExempted = isBreakExempted;
		this.employee = employee;
	}

	public Integer getTotalMinPerDay() {
		return totalMinPerDay;
	}

	public void setTotalMinPerDay(Integer totalMinPerDay) {
		this.totalMinPerDay = totalMinPerDay;
	}

	public ScheduleEncodingReference getScheduleEncodingReference() {
		return scheduleEncodingReference;
	}

	public void setScheduleEncodingReference(ScheduleEncodingReference scheduleEncodingReference) {
		this.scheduleEncodingReference = scheduleEncodingReference;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public Date getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Time timeIn) {
		this.timeIn = timeIn;
	}

	public Date getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Time timeOut) {
		this.timeOut = timeOut;
	}

	public String getScheduleDay() {
		return scheduleDay;
	}

	public void setScheduleDay(String scheduleDay) {
		this.scheduleDay = scheduleDay;
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

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Integer getPrimaryKeyReferenceClient() {
		return primaryKeyReferenceClient;
	}

	public void setPrimaryKeyReferenceClient(Integer primaryKeyReferenceClient) {
		this.primaryKeyReferenceClient = primaryKeyReferenceClient;
	}

	public Integer getIsBreakExempted() {
		return isBreakExempted;
	}

	public void setIsBreakExempted(Integer isBreakExempted) {
		this.isBreakExempted = isBreakExempted;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getRegularScheduleHeader() {
		this.regularScheduleHeader = "EMPID,TIMEINHR,TIMEINMIN,TIMEOUTHR,TIMEOUTMIN,LUNCHINHR,LUNCHINMIN,LUNCHOUTHR,LUNCHOUTMIN,OFFSETHR,OFFSETMIN,ISBREAKEXEMPTED,MONRD,TUERD,WEDRD,THURD,FRIRD,SATRD,SUNRD,OT10STARTHR,OT10STARTMIN,OT10ENDHR,OT10ENDMIN,OT25STARTHR,OT25STARTMIN,OT25ENDHR,OT25ENDMIN,OT30STARTHR,OT30STARTMIN,OT30ENDHR,OT30ENDMIN,OT50STARTHR,OT50STARTMIN,OT50ENDHR,OT50ENDMIN,OT100STARTHR,OT100STARTMIN,OT100ENDHR,OT100ENDMIN,OT125STARTHR,OT125STARTMIN,OT125ENDHR,OT125ENDMIN,OT130STARTHR,OT130STARTMIN,OT130ENDHR,OT130ENDMIN,OT150STARTHR,OT150STARTMIN,OT150ENDHR,OT150ENDMIN,OT160STARTHR,OT160STARTMIN,OT160ENDHR,OT160ENDMIN,OT169STARTHR,OT169STARTMIN,OT169ENDHR,OT169ENDMIN,OT195STARTHR,OT195STARTMIN,OT195ENDHR,OT195ENDMIN,OT200STARTHR,OT200STARTMIN,OT200ENDHR,OT200ENDMIN,OT230STARTHR,OT230STARTMIN,OT230ENDHR,OT230ENDMIN,OT260STARTHR,OT260STARTMIN,OT260ENDHR,OT260ENDMIN,OT338STARTHR,OT338STARTMIN,OT338ENDHR,OT338ENDMIN,OT12510STARTHR,OT12510STARTMIN,OT12510ENDHR,OT12510ENDMIN,OT13010STARTHR,OT13010STARTMIN,OT13010ENDHR,OT13010ENDMIN,OTND125STARTHR,OTND125STARTMIN,OTND125ENDHR,OTND125ENDMIN,OTND130STARTHR,OTND130STARTMIN,OTND130ENDHR,OTND130ENDMIN,OTND200STARTHR,OTND200STARTMIN,OTND200ENDHR,OTND200ENDMIN,OTXS130STARTHR,OTXS130STARTMIN,OTXS130ENDHR,OTXS130ENDMIN,OTXS150STARTHR,OTXS150STARTMIN,OTXS150ENDHR,OTXS150ENDMIN,OTXS200STARTHR,OTXS200STARTMIN,OTXS200ENDHR,OTXS200ENDMIN,OTXS260STARTHR,OTXS260STARTMIN,OTXS260ENDHR,OTXS260ENDMIN";
		return this.regularScheduleHeader;
	}

	public void setRegularScheduleHeader(String regularScheduleHeader) {
		this.regularScheduleHeader = regularScheduleHeader;
	}

	public String getIrregularScheduleHeader() {
		irregularScheduleHeader = "EMPID,DATE,TIMEINHR,TIMEINMIN,TIMEOUTHR,TIMEOUTMIN,LUNCHINHR,LUNCHINMIN,LUNCHOUTHR,LUNCHOUTMIN,OFFSETHR,OFFSETMIN,ISBREAKEXEMPTED,OT10STARTHR,OT10STARTMIN,OT10ENDHR,OT10ENDMIN,OT25STARTHR,OT25STARTMIN,OT25ENDHR,OT25ENDMIN,OT30STARTHR,OT30STARTMIN,OT30ENDHR,OT30ENDMIN,OT50STARTHR,OT50STARTMIN,OT50ENDHR,OT50ENDMIN,OT100STARTHR,OT100STARTMIN,OT100ENDHR,OT100ENDMIN,OT125STARTHR,OT125STARTMIN,OT125ENDHR,OT125ENDMIN,OT130STARTHR,OT130STARTMIN,OT130ENDHR,OT130ENDMIN,OT150STARTHR,OT150STARTMIN,OT150ENDHR,OT150ENDMIN,OT160STARTHR,OT160STARTMIN,OT160ENDHR,OT160ENDMIN,OT169STARTHR,OT169STARTMIN,OT169ENDHR,OT169ENDMIN,OT195STARTHR,OT195STARTMIN,OT195ENDHR,OT195ENDMIN,OT200STARTHR,OT200STARTMIN,OT200ENDHR,OT200ENDMIN,OT230STARTHR,OT230STARTMIN,OT230ENDHR,OT230ENDMIN,OT260STARTHR,OT260STARTMIN,OT260ENDHR,OT260ENDMIN,OT338STARTHR,OT338STARTMIN,OT338ENDHR,OT338ENDMIN,OT12510STARTHR,OT12510STARTMIN,OT12510ENDHR,OT12510ENDMIN,OT13010STARTHR,OT13010STARTMIN,OT13010ENDHR,OT13010ENDMIN,OTND125STARTHR,OTND125STARTMIN,OTND125ENDHR,OTND125ENDMIN,OTND130STARTHR,OTND130STARTMIN,OTND130ENDHR,OTND130ENDMIN,OTND200STARTHR,OTND200STARTMIN,OTND200ENDHR,OTND200ENDMIN,OTXS130STARTHR,OTXS130STARTMIN,OTXS130ENDHR,OTXS130ENDMIN,OTXS150STARTHR,OTXS150STARTMIN,OTXS150ENDHR,OTXS150ENDMIN,OTXS200STARTHR,OTXS200STARTMIN,OTXS200ENDHR,OTXS200ENDMIN,OTXS260STARTHR,OTXS260STARTMIN,OTXS260ENDHR,OTXS260ENDMIN";
		return irregularScheduleHeader;
	}

	public void setIrregularScheduleHeader(String irregularScheduleHeader) {
		this.irregularScheduleHeader = irregularScheduleHeader;
	}

	@Override
	public String toString() {
		return "ScheduleEncoding [primaryKey=" + primaryKey + ", primaryKeyReferenceClient=" + primaryKeyReferenceClient
				+ ", scheduleName=" + scheduleName + ", timeIn=" + timeIn + ", timeOut=" + timeOut + ", lunchIn="
				+ lunchIn + ", lunchOut=" + lunchOut + ", offsetAllowed=" + offsetAllowed + ", scheduleDay="
				+ scheduleDay + ", user=" + user + ", changedByUser=" + changedByUser + ", changedInComputer="
				+ changedInComputer + ", changedOnDate=" + changedOnDate + ", client=" + client + ", clientCode="
				+ clientCode + ", scheduleEncodingReference=" + scheduleEncodingReference + ", totalMinPerDay="
				+ totalMinPerDay + ", isBreakExempted=" + isBreakExempted + "]";
	}

}
