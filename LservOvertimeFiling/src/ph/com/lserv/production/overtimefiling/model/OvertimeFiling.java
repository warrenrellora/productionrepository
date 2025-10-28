package ph.com.lserv.production.overtimefiling.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lserv.production.timeconfigfoxpro.model.TimeConfigFoxPro;

public class OvertimeFiling extends MasterObject {
	Integer primaryKey;
	Timestamp dateTimeFrom, dateTimeTo;
	Date duration, dateFiled;
	String workDone;
	BigDecimal totalOtHours;
	String dateTimeFromString;
	Timestamp bypassDateTimeTo;
	Integer isExceeded;
	Integer prikeyTrail;
	String fullName;
	TimeConfigFoxPro timeConfigFoxPro;
	String sortDepartment;

	public OvertimeFiling() {
		super();
	}

	public OvertimeFiling(Integer primaryKey, Timestamp dateTimeFrom, Timestamp dateTimeTo, Date duration,
			Date dateFiled, String workDone, BigDecimal totalOtHours, User user, String changedByUser,
			String changedInComputer, Date changedOnDate, String dateTimeFromString, Timestamp bypassDateTimeTo,
			Integer isExceeded, Integer prikeyTrail, String fullName, TimeConfigFoxPro timeConfigFoxPro,
			String sortDepartment) {
		super();
		this.primaryKey = primaryKey;
		this.dateTimeFrom = dateTimeFrom;
		this.dateTimeTo = dateTimeTo;
		this.duration = duration;
		this.dateFiled = dateFiled;
		this.workDone = workDone;
		this.totalOtHours = totalOtHours;
		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.dateTimeFromString = dateTimeFromString;
		this.bypassDateTimeTo = bypassDateTimeTo;
		this.isExceeded = isExceeded;
		this.prikeyTrail = prikeyTrail;
		this.fullName = fullName;
		this.timeConfigFoxPro = timeConfigFoxPro;
		this.sortDepartment = sortDepartment;
	}

	public String getSortDepartment() {
		if (user.getEmploymentHistory().getEmployee().getDepartment().toString() != null) {
			String department = user.getEmploymentHistory().getDepartment().getDepartmentName();
			String[] nameDepartment = department.split("-");
			return nameDepartment[1].trim();
		}
		return null;
	}

	public void setSortDepartment(String sortDepartment) {
		this.sortDepartment = sortDepartment;
	}

	public TimeConfigFoxPro getTimeConfigFoxPro() {
		return timeConfigFoxPro;
	}

	public void setTimeConfigFoxPro(TimeConfigFoxPro timeConfigFoxPro) {
		this.timeConfigFoxPro = timeConfigFoxPro;
	}

	public String getFullName() {
		return user.getEmploymentHistory().getEmployee().getEmployeeFullName();
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Integer getPrikeyTrail() {
		return prikeyTrail;
	}

	public void setPrikeyTrail(Integer prikeyTrail) {
		this.prikeyTrail = prikeyTrail;
	}

	public String getDateTimeFromString() {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		return dateTimeFormat.format(dateTimeFrom);
	}

	public String getDateTimeToString() {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		String dateTimeToTemp = dateTimeFormat.format(dateTimeTo);
		return dateTimeToTemp;
	}

	public String getDateFiledString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		String dateFiledTemp = dateFormat.format(dateFiled);
		return dateFiledTemp;
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

	public Date getDateFiled() {
		return dateFiled;
	}

	public void setDateFiled(Date dateFiled) {
		this.dateFiled = dateFiled;
	}

	public Date getDuration() {
		return duration;
	}

	public void setDuration(Date duration) {
		this.duration = duration;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getWorkDone() {
		return workDone;
	}

	public void setWorkDone(String workDone) {
		this.workDone = workDone;
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

	public BigDecimal getTotalOtHours() {
		return totalOtHours;
	}

	public void setTotalOtHours(BigDecimal totalOtHours) {
		this.totalOtHours = totalOtHours;
	}

	public Timestamp getBypassDateTimeTo() {
		return bypassDateTimeTo;
	}

	public void setBypassDateTimeTo(Timestamp bypassDateTimeTo) {
		this.bypassDateTimeTo = bypassDateTimeTo;
	}

	public Integer getIsExceeded() {
		return isExceeded;
	}

	public void setIsExceeded(Integer isExceeded) {
		this.isExceeded = isExceeded;
	}

	@Override
	public String toString() {
		return "OvertimeFiling [primaryKey=" + primaryKey + ", dateTimeFrom=" + dateTimeFrom + ", dateTimeTo="
				+ dateTimeTo + ", duration=" + duration + ", dateFiled=" + dateFiled + ", workDone=" + workDone
				+ ", totalOtHours=" + totalOtHours + ", user=" + user + ", changedByUser=" + changedByUser
				+ ", changedInComputer=" + changedInComputer + ", changedOnDate=" + changedOnDate
				+ ", dateTimeFromString=" + dateTimeFromString + ", bypassDateTimeTo=" + bypassDateTimeTo
				+ ", isExceeded=" + isExceeded + ", prikeyTrail=" + prikeyTrail + ", fullName=" + fullName
				+ ", timeConfigFoxPro=" + timeConfigFoxPro + ", sortDepartment=" + sortDepartment + "]";
	}

}
