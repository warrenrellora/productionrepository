package ph.com.lserv.production.employeescheduleuploading.model;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;
import ph.com.lserv.production.remarksreference.model.RemarksReference;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;

public class EmployeeScheduleUploading extends MasterObject {
	Integer primaryKey;
	Employee employee;
	Date dateEntry;
	Time timeEntry;
	String bioId;
	Timestamp timeInEntry;
	Timestamp lunchOutEntry;
	Timestamp lunchInEntry;
	Timestamp timeOutEntry;
	EmploymentHistory employmentHistory;
	String payPeriod;
	String schedule;
	String dayOfDate;
	Date payFrom;
	Date payTo;
	Date cutoffFrom;
	Date cutoffTo;
	Client client;
	Department department;
	Integer undertime;
	Integer isRegularSchedule;
	ScheduleEncoding scheduleEncoding;
	EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular;
	Integer isHoliday;
	Integer isRestDay;
	Time allowedOvertime;
	List<EmployeeScheduleUploadingOvertimeBreakdown> overtimeBreakdownList = new ArrayList<>();
	RemarksReference remarksReference;
	Integer isValidated;
	String bioArms;

	Integer employeeId;
	Integer hrsOfOvertimeRegDay;
	Integer hrsOfOvertimeRestDay;
	Integer hrsOfOvertimeNightPay;
	Integer minsOfOvertimeRegDay;
	Integer minsOfOvertimeRestDay;
	Integer minsOfOvertimeNightPay;
	Integer initialTotalMinsNightDiff;
	BigDecimal totalHrsRendered;
	Integer totalHrs;
	Integer totalMins;
	String totalNoOfHrs;

	Integer ND;
	Integer ROT;
	Integer SOT;
	Integer SOTE;
	Integer LOT;
	Integer LOTE;
	Integer RSOT;
	Integer RSOTE;
	Integer RLOT;
	Integer RLOTE;

	public EmployeeScheduleUploading() {
		super();
	}

	public EmployeeScheduleUploading(Integer primaryKey, Employee employee, Date dateEntry, Time timeEntry,
			Integer employeeId, String bioId, Timestamp timeInEntry, Timestamp lunchOutEntry, Timestamp lunchInEntry,
			Timestamp timeOutEntry, EmploymentHistory employmentHistory, String payPeriod, String schedule,
			String dayOfDate, User user, String changedByUser, String changedInComputer, Date changedOnDate,
			Date payFrom, Date payTo, Date cutoffFrom, Date cutoffTo, Integer isRegularSchedule, Integer isHoliday,
			Client client, Department department, Integer undertime, Integer isRestDay,
			RemarksReference remarksReference, Time allowedOvertime, ScheduleEncoding scheduleEncoding,
			EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular,
			List<EmployeeScheduleUploadingOvertimeBreakdown> overtimeBreakdownList, Integer isValidated,
			Integer hrsOfOvertimeRegDay, Integer minsOfOvertimeRegDay, Integer hrsOfOvertimeRestDay,
			Integer hrsOfOvertimeNightPay, Integer minsOfOvertimeRestDay, Integer minsOfOvertimeNightPay,
			Integer initialTotalMinsNightDiff, BigDecimal totalHrsRendered, Integer totalHrs, Integer totalMins,
			String totalNoOfHrs, Integer ND, Integer ROT, Integer SOT, Integer SOTE, Integer LOT, Integer LOTE,
			Integer RSOT, Integer RSOTE, Integer RLOT, Integer RLOTE, String bioArms) {
		super();
		this.primaryKey = primaryKey;
		this.employee = employee;
		this.dateEntry = dateEntry;
		this.timeEntry = timeEntry;
		this.employeeId = employeeId;
		this.bioId = bioId;
		this.bioArms = bioArms;
		this.timeInEntry = timeInEntry;
		this.lunchOutEntry = lunchOutEntry;
		this.lunchInEntry = lunchInEntry;
		this.timeOutEntry = timeOutEntry;
		this.employmentHistory = employmentHistory;
		this.payPeriod = payPeriod;
		this.schedule = schedule;
		this.dayOfDate = dayOfDate;
		this.isHoliday = isHoliday;
		this.payFrom = payFrom;
		this.payTo = payTo;
		this.cutoffFrom = cutoffFrom;
		this.cutoffTo = cutoffTo;
		this.isRegularSchedule = isRegularSchedule;
		this.scheduleEncoding = scheduleEncoding;
		this.employeeScheduleEncodingIrregular = employeeScheduleEncodingIrregular;
		this.client = client;
		this.department = department;
		this.undertime = undertime;
		this.isRestDay = isRestDay;
		this.allowedOvertime = allowedOvertime;
		this.overtimeBreakdownList = overtimeBreakdownList;
		this.remarksReference = remarksReference;
		this.isValidated = isValidated;
		this.hrsOfOvertimeRegDay = hrsOfOvertimeRegDay;
		this.minsOfOvertimeRegDay = minsOfOvertimeRegDay;
		this.hrsOfOvertimeRestDay = hrsOfOvertimeRestDay;
		this.minsOfOvertimeRestDay = minsOfOvertimeRestDay;
		this.hrsOfOvertimeNightPay = hrsOfOvertimeNightPay;
		this.minsOfOvertimeNightPay = minsOfOvertimeNightPay;
		this.initialTotalMinsNightDiff = initialTotalMinsNightDiff;
		this.totalHrsRendered = totalHrsRendered;
		this.totalHrs = totalHrs;
		this.totalMins = totalMins;
		this.totalNoOfHrs = totalNoOfHrs;
		this.ND = ND;
		this.ROT = ROT;
		this.SOT = SOT;
		this.SOTE = SOTE;
		this.LOT = LOT;
		this.LOTE = LOTE;
		this.RSOT =  RSOT;
		this.RSOTE = RSOTE;
		this.RLOT = RLOT;
		this.RLOTE = RLOTE;

		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
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

	public List<EmployeeScheduleUploadingOvertimeBreakdown> getOvertimeBreakdownList() {
		return overtimeBreakdownList;
	}

	public void setOvertimeBreakdownList(List<EmployeeScheduleUploadingOvertimeBreakdown> overtimeBreakdownList) {
		this.overtimeBreakdownList = overtimeBreakdownList;
	}

	public Integer getUndertime() {
		return undertime;
	}

	public void setUndertime(Integer undertime) {
		this.undertime = undertime;
	}

	public Time getAllowedOvertime() {
		return allowedOvertime;
	}

	public void setAllowedOvertime(Time allowedOvertime) {
		this.allowedOvertime = allowedOvertime;
	}

	public Integer getIsRegularSchedule() {
		return isRegularSchedule;
	}

	public void setIsRegularSchedule(Integer isRegularSchedule) {
		this.isRegularSchedule = isRegularSchedule;
	}

	public ScheduleEncoding getScheduleEncoding() {
		return scheduleEncoding;
	}

	public void setScheduleEncoding(ScheduleEncoding scheduleEncoding) {
		this.scheduleEncoding = scheduleEncoding;
	}

	public EmployeeScheduleEncodingIrregular getEmployeeScheduleEncodingIrregular() {
		return employeeScheduleEncodingIrregular;
	}

	public void setEmployeeScheduleEncodingIrregular(
			EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		this.employeeScheduleEncodingIrregular = employeeScheduleEncodingIrregular;
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

	public Date getCutoffFrom() {
		return cutoffFrom;
	}

	public void setCutoffFrom(Date cutoffFrom) {
		this.cutoffFrom = cutoffFrom;
	}

	public Date getCutoffTo() {
		return cutoffTo;
	}

	public void setCutoffTo(Date cutoffTo) {
		this.cutoffTo = cutoffTo;
	}

	public Integer getIsHoliday() {
		return isHoliday;
	}

	public void setIsHoliday(Integer isHoliday) {
		this.isHoliday = isHoliday;
	}

	public String getDayOfDate() {
		return dayOfDate;
	}

	public void setDayOfDate(String dayOfDate) {
		this.dayOfDate = dayOfDate;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getPayPeriod() {
		return payPeriod;
	}

	public void setPayPeriod(String payPeriod) {
		this.payPeriod = payPeriod;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public String getBioId() {
		return bioId;
	}

	public void setBioId(String bioId) {
		this.bioId = bioId;
	}

	public EmploymentHistory getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(EmploymentHistory employmentHistory) {
		this.employmentHistory = employmentHistory;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Date getDateEntry() {
		return dateEntry;
	}

	public void setDateEntry(Date dateEntry) {
		this.dateEntry = dateEntry;
	}

	public Time getTimeEntry() {
		return timeEntry;
	}

	public void setTimeEntry(Time timeEntry) {
		this.timeEntry = timeEntry;
	}

	public Timestamp getTimeInEntry() {
		return timeInEntry;
	}

	public void setTimeInEntry(Timestamp timeInEntry) {
		this.timeInEntry = timeInEntry;
	}

	public Timestamp getLunchOutEntry() {
		return lunchOutEntry;
	}

	public void setLunchOutEntry(Timestamp lunchOutEntry) {
		this.lunchOutEntry = lunchOutEntry;
	}

	public Timestamp getLunchInEntry() {
		return lunchInEntry;
	}

	public void setLunchInEntry(Timestamp lunchInEntry) {
		this.lunchInEntry = lunchInEntry;
	}

	public Timestamp getTimeOutEntry() {
		return timeOutEntry;
	}

	public void setTimeOutEntry(Timestamp timeOutEntry) {
		this.timeOutEntry = timeOutEntry;
	}

	public Integer getIsRestDay() {
		return isRestDay;
	}

	public void setIsRestDay(Integer isRestDay) {
		this.isRestDay = isRestDay;
	}

	public RemarksReference getRemarksReference() {
		return remarksReference;
	}

	public void setRemarksReference(RemarksReference remarksReference) {
		this.remarksReference = remarksReference;
	}

	public Integer getIsValidated() {
		return isValidated;
	}

	public void setIsValidated(Integer isValidated) {
		this.isValidated = isValidated;
	}

	public Integer getHrsOfOvertimeRegDay() {
		return hrsOfOvertimeRegDay;
	}

	public void setHrsOfOvertimeRegDay(Integer hrsOfOvertimeRegDay) {
		this.hrsOfOvertimeRegDay = hrsOfOvertimeRegDay;
	}

	public Integer getHrsOfOvertimeRestDay() {
		return hrsOfOvertimeRestDay;
	}

	public void setHrsOfOvertimeRestDay(Integer hrsOfOvertimeRestDay) {
		this.hrsOfOvertimeRestDay = hrsOfOvertimeRestDay;
	}

	public Integer getHrsOfOvertimeNightPay() {
		return hrsOfOvertimeNightPay;
	}

	public void setHrsOfOvertimeNightPay(Integer hrsOfOvertimeNightPay) {
		this.hrsOfOvertimeNightPay = hrsOfOvertimeNightPay;
	}

	public Integer getMinsOfOvertimeRegDay() {
		return minsOfOvertimeRegDay;
	}

	public void setMinsOfOvertimeRegDay(Integer minsOfOvertimeRegDay) {
		this.minsOfOvertimeRegDay = minsOfOvertimeRegDay;
	}

	public Integer getMinsOfOvertimeRestDay() {
		return minsOfOvertimeRestDay;
	}

	public void setMinsOfOvertimeRestDay(Integer minsOfOvertimeRestDay) {
		this.minsOfOvertimeRestDay = minsOfOvertimeRestDay;
	}

	public Integer getMinsOfOvertimeNightPay() {
		return minsOfOvertimeNightPay;
	}

	public void setMinsOfOvertimeNightPay(Integer minsOfOvertimeNightPay) {
		this.minsOfOvertimeNightPay = minsOfOvertimeNightPay;
	}

	public Integer getInitialTotalMinsNightDiff() {
		return initialTotalMinsNightDiff;
	}

	public void setInitialTotalMinsNightDiff(Integer initialTotalMinsNightDiff) {
		this.initialTotalMinsNightDiff = initialTotalMinsNightDiff;
	}

	public BigDecimal getTotalHrsRendered() {
		return totalHrsRendered;
	}

	public void setTotalHrsRendered(BigDecimal totalHrsRendered) {
		this.totalHrsRendered = totalHrsRendered;
	}

	public Integer getTotalHrs() {
		return totalHrs;
	}

	public void setTotalHrs(Integer totalHrs) {
		this.totalHrs = totalHrs;
	}

	public Integer getTotalMins() {
		return totalMins;
	}

	public void setTotalMins(Integer totalMins) {
		this.totalMins = totalMins;
	}

	public String getTotalNoOfHrs() {
		return totalNoOfHrs;
	}

	public void setTotalNoOfHrs(String totalNoOfHrs) {
		this.totalNoOfHrs = totalNoOfHrs;
	}
	
	public Integer getND() {
		return ND;
	}

	public void setND(Integer nD) {
		ND = nD;
	}

	public Integer getROT() {
		return ROT;
	}

	public void setROT(Integer rOT) {
		ROT = rOT;
	}

	public Integer getSOT() {
		return SOT;
	}

	public void setSOT(Integer sOT) {
		SOT = sOT;
	}

	public Integer getSOTE() {
		return SOTE;
	}

	public void setSOTE(Integer sOTE) {
		SOTE = sOTE;
	}

	public Integer getLOT() {
		return LOT;
	}

	public void setLOT(Integer lOT) {
		LOT = lOT;
	}

	public Integer getLOTE() {
		return LOTE;
	}

	public void setLOTE(Integer lOTE) {
		LOTE = lOTE;
	}

	public Integer getRSOT() {
		return RSOT;
	}

	public void setRSOT(Integer rSOT) {
		RSOT = rSOT;
	}

	public Integer getRSOTE() {
		return RSOTE;
	}

	public void setRSOTE(Integer rSOTE) {
		RSOTE = rSOTE;
	}

	public Integer getRLOT() {
		return RLOT;
	}

	public void setRLOT(Integer rLOT) {
		RLOT = rLOT;
	}

	public Integer getRLOTE() {
		return RLOTE;
	}

	public void setRLOTE(Integer rLOTE) {
		RLOTE = rLOTE;
	}
	
	public String getBioArms() {
		return bioArms;
	}

	public void setBioArms(String bioArms) {
		this.bioArms = bioArms;
	}

	@Override
	public String toString() {
		return "EmployeeScheduleUploading [primaryKey=" + primaryKey + ", employee=" + employee + ", dateEntry="
				+ dateEntry + ", timeEntry=" + timeEntry + ", bioId=" + bioId + ", timeInEntry=" + timeInEntry
				+ ", lunchOutEntry=" + lunchOutEntry + ", lunchInEntry=" + lunchInEntry + ", timeOutEntry="
				+ timeOutEntry + ", employmentHistory=" + employmentHistory + ", payPeriod=" + payPeriod + ", schedule="
				+ schedule + ", dayOfDate=" + dayOfDate + ", payFrom=" + payFrom + ", payTo=" + payTo + ", cutoffFrom="
				+ cutoffFrom + ", cutoffTo=" + cutoffTo + ", client=" + client + ", department=" + department
				+ ", undertime=" + undertime + ", isRegularSchedule=" + isRegularSchedule + ", scheduleEncoding="
				+ scheduleEncoding + ", employeeScheduleEncodingIrregular=" + employeeScheduleEncodingIrregular
				+ ", isHoliday=" + isHoliday + ", isRestDay=" + isRestDay + ", allowedOvertime=" + allowedOvertime
				+ ", overtimeBreakdownList=" + overtimeBreakdownList + ", remarksReference=" + remarksReference
				+ ", isValidated=" + isValidated + ", bioArms=" + bioArms + ", employeeId=" + employeeId
				+ ", hrsOfOvertimeRegDay=" + hrsOfOvertimeRegDay + ", hrsOfOvertimeRestDay=" + hrsOfOvertimeRestDay
				+ ", hrsOfOvertimeNightPay=" + hrsOfOvertimeNightPay + ", minsOfOvertimeRegDay=" + minsOfOvertimeRegDay
				+ ", minsOfOvertimeRestDay=" + minsOfOvertimeRestDay + ", minsOfOvertimeNightPay="
				+ minsOfOvertimeNightPay + ", initialTotalMinsNightDiff=" + initialTotalMinsNightDiff
				+ ", totalHrsRendered=" + totalHrsRendered + ", totalHrs=" + totalHrs + ", totalMins=" + totalMins
				+ ", totalNoOfHrs=" + totalNoOfHrs + ", ND=" + ND + ", ROT=" + ROT + ", SOT=" + SOT + ", SOTE=" + SOTE
				+ ", LOT=" + LOT + ", LOTE=" + LOTE + ", RSOT=" + RSOT + ", RSOTE=" + RSOTE + ", RLOT=" + RLOT
				+ ", RLOTE=" + RLOTE + "]";
	}

}
