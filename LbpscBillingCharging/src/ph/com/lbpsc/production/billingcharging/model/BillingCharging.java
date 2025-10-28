package ph.com.lbpsc.production.billingcharging.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ph.com.lbpsc.production.billing.model.Billing;
import ph.com.lbpsc.production.billingrate.model.BillingRate;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmentconfiguration.model.EmploymentConfiguration;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.overtime.model.Overtime;
import ph.com.lbpsc.production.payroll.model.Payroll;
import ph.com.lbpsc.production.statementofaccount.model.StatementOfAccount;
import ph.com.lbpsc.production.user.model.User;

public class BillingCharging extends MasterObject implements Serializable {
	private static final long serialVersionUID = 3657582767692163466L;
	private EmploymentConfiguration employmentConfiguration;
	private Client client;
	private Department department;
	private Billing billing;
	private BillingRate billingRate;
	private BillingRate billingRateOvertime;
	private boolean daily;
	private Date payFrom;
	private Date payTo;
	private Date batchDate;
	private Integer regularDays;
	private Integer undertimeHours;
	private Integer undertimeMinutes;
	private Integer holidays;
	private Integer primaryKey;
	private Payroll payroll;
	private Date dateRendered;
	private boolean isTransfer;
	private String remarks;
	private ClientGroup clientGroup;
	private List<Overtime> overtimeList;
	private BigDecimal workHours;
	private BigDecimal periodAWorkHrs;
	private BigDecimal periodBWorkHrs;
	private BigDecimal periodATotal;
	private BigDecimal periodBTotal;
	private StatementOfAccount statementOfAccount;
	private StatementOfAccount statementOfAccountOvertime;
	private Employee employee;

	public BillingCharging() {
		// this.propertySupport = new PropertyChangeSupport(this);
		// super();
	}

	public BillingCharging(EmploymentConfiguration employmentConfiguration, Client client, Department department,
			Billing billing, BillingRate billingRate, BillingRate billingRateOvertime, boolean daily, Date payFrom,
			Date payTo, Date batchDate, Integer regularDays, Integer undertimeHours, Integer undertimeMinutes,
			Integer holidays, Integer primaryKey, Payroll payroll, Date dateRendered, boolean isTransfer,
			String remarks, ClientGroup clientGroup, List<Overtime> overtimeList, BigDecimal workHours,
			BigDecimal periodAWorkHrs, BigDecimal periodBWorkHrs, BigDecimal periodATotal, BigDecimal periodBTotal,
			StatementOfAccount statementOfAccount, StatementOfAccount statementOfAccountOvertime, Employee employee,
			User user, Date changedOnDate, String changedInComputer) {
		super();
		this.employmentConfiguration = employmentConfiguration;
		this.client = client;
		this.department = department;
		this.billing = billing;
		this.billingRate = billingRate;
		this.billingRateOvertime = billingRateOvertime;
		this.daily = daily;
		this.payFrom = payFrom;
		this.payTo = payTo;
		this.batchDate = batchDate;
		this.regularDays = regularDays;
		this.undertimeHours = undertimeHours;
		this.undertimeMinutes = undertimeMinutes;
		this.holidays = holidays;
		this.primaryKey = primaryKey;
		this.payroll = payroll;
		this.dateRendered = dateRendered;
		this.isTransfer = isTransfer;
		this.remarks = remarks;
		this.clientGroup = clientGroup;
		this.overtimeList = overtimeList;
		this.workHours = workHours;
		this.periodAWorkHrs = periodAWorkHrs;
		this.periodBWorkHrs = periodBWorkHrs;
		this.periodATotal = periodATotal;
		this.periodBTotal = periodBTotal;
		this.statementOfAccount = statementOfAccount;
		this.statementOfAccountOvertime = statementOfAccountOvertime;
		this.employee = employee;
		this.user = user;
		this.changedOnDate = changedOnDate;
		this.changedInComputer = changedInComputer;
		// this.propertySupport = new PropertyChangeSupport(this);
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

	public EmploymentConfiguration getEmploymentConfiguration() {
		return employmentConfiguration;
	}

	public void setEmploymentConfiguration(EmploymentConfiguration employmentConfiguration) {
		this.employmentConfiguration = employmentConfiguration;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		// Client oldClient = this.client;
		this.client = client;
		// this.propertySupport.firePropertyChange("client", oldClient, client);
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		// Department oldDepartment = this.department;
		this.department = department;
		// this.propertySupport.firePropertyChange("department", oldDepartment,
		// department);
	}

	public BillingRate getBillingRate() {
		return billingRate;
	}

	public void setBillingRate(BillingRate billingRate) {
		this.billingRate = billingRate;
	}

	public boolean getDaily() {
		return daily;
	}

	public void setDaily(boolean daily) {
		// boolean oldDaily = this.daily;
		this.daily = daily;
		// this.propertySupport.firePropertyChange("daily", oldDaily, daily);
	}

	public Date getPayFrom() {
		return payFrom;
	}

	public void setPayFrom(Date payFrom) {
		// Date oldPayFrom = this.payFrom;
		this.payFrom = payFrom;
		// this.propertySupport.firePropertyChange("payFrom", oldPayFrom, payFrom);
	}

	public Date getPayTo() {
		return payTo;
	}

	public void setPayTo(Date payTo) {
		// Date oldPayTo = this.payTo;
		this.payTo = payTo;
		// this.propertySupport.firePropertyChange("payTo", oldPayTo, payTo);
	}

	public Date getBatchDate() {
		return batchDate;
	}

	public void setBatchDate(Date batchDate) {
		// Date oldBatchDate = this.batchDate;
		this.batchDate = batchDate;
		// this.propertySupport.firePropertyChange("batchDate", oldBatchDate,
		// batchDate);
	}

	public Integer getRegularDays() {
		return regularDays;
	}

	public void setRegularDays(Integer regularDays) {
		// Integer oldRegularDays = this.regularDays;
		this.regularDays = regularDays;
		// this.propertySupport.firePropertyChange("regularDays", oldRegularDays,
		// regularDays);
	}

	public Integer getUndertimeHours() {
		return undertimeHours;
	}

	public void setUndertimeHours(Integer undertimeHours) {
		// Integer oldUndertimeHours = this.undertimeHours;
		this.undertimeHours = undertimeHours;
		// this.propertySupport.firePropertyChange("undertimeHours", oldUndertimeHours,
		// undertimeHours);
	}

	public Integer getUndertimeMinutes() {
		return undertimeMinutes;
	}

	public void setUndertimeMinutes(Integer undertimeMinutes) {
		// Integer oldUndertimeMinutes = this.undertimeMinutes;
		this.undertimeMinutes = undertimeMinutes;
		// this.propertySupport.firePropertyChange("undertimeMinutes",
		// oldUndertimeMinutes, undertimeMinutes);
	}

	public Integer getHolidays() {
		return holidays;
	}

	public void setHolidays(Integer holidays) {
		// Integer oldHolidays = this.holidays;
		this.holidays = holidays;
		// this.propertySupport.firePropertyChange("holidays", oldHolidays, holidays);
	}

	public boolean isTransfer() {
		return isTransfer;
	}

	public void setIsTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	// public Payroll getPayroll() {
	// return this.payroll;
	// }
	//
	// public void setPayroll(Payroll payroll) {
	// Payroll oldPayroll = this.payroll;
	// this.payroll = payroll;
	// this.propertySupport.firePropertyChange("payroll", oldPayroll, payroll);
	// }

	public Payroll getPayroll() {
		return payroll;
	}

	public void setPayroll(Payroll payroll) {
		this.payroll = payroll;
	}

	public Date getDateRendered() {
		return dateRendered;
	}

	public void setDateRendered(Date dateRendered) {
		// Date oldDateRendered = this.dateRendered;
		this.dateRendered = dateRendered;
		// this.propertySupport.firePropertyChange("dateRendered", oldDateRendered,
		// dateRendered);
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		// Integer oldPrimaryKey = this.primaryKey;
		this.primaryKey = primaryKey;
		// this.propertySupport.firePropertyChange("primaryKey", oldPrimaryKey,
		// primaryKey);

	}

	public List<Overtime> getOvertimeList() {
		return overtimeList;
	}

	public void setOvertimeList(List<Overtime> overtimeList) {
		// List<Overtime> oldOvertimeList = this.overtimeList;
		this.overtimeList = overtimeList;
		// this.propertySupport.firePropertyChange("overtimeList", oldOvertimeList,
		// overtimeList);
	}

	public ClientGroup getClientGroup() {
		return clientGroup;
	}

	public void setClientGroup(ClientGroup clientGroup) {
		this.clientGroup = clientGroup;
	}

	public BigDecimal getWorkHours() {
		return workHours;
	}

	public void setWorkHours(BigDecimal workHours) {
		this.workHours = workHours;
	}

	public BigDecimal getPeriodAWorkHrs() {
		return periodAWorkHrs;
	}

	public void setPeriodAWorkHrs(BigDecimal periodAWorkHrs) {
		this.periodAWorkHrs = periodAWorkHrs;
	}

	public BigDecimal getPeriodBWorkHrs() {
		return periodBWorkHrs;
	}

	public void setPeriodBWorkHrs(BigDecimal periodBWorkHrs) {
		this.periodBWorkHrs = periodBWorkHrs;
	}

	public StatementOfAccount getStatementOfAccount() {
		return statementOfAccount;
	}

	public void setStatementOfAccount(StatementOfAccount statementOfAccount) {
		this.statementOfAccount = statementOfAccount;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public BillingRate getBillingRateOvertime() {
		return billingRateOvertime;
	}

	public void setBillingRateOvertime(BillingRate billingRateOvertime) {
		this.billingRateOvertime = billingRateOvertime;
	}

	public BigDecimal getPeriodATotal() {
		return periodATotal;
	}

	public void setPeriodATotal(BigDecimal periodATotal) {
		this.periodATotal = periodATotal;
	}

	public BigDecimal getPeriodBTotal() {
		return periodBTotal;
	}

	public void setPeriodBTotal(BigDecimal periodBTotal) {
		this.periodBTotal = periodBTotal;
	}

	public Billing getBilling() {
		return billing;
	}

	public void setBilling(Billing billing) {
		this.billing = billing;
	}

	public StatementOfAccount getStatementOfAccountOvertime() {
		return statementOfAccountOvertime;
	}

	public void setStatementOfAccountOvertime(StatementOfAccount statementOfAccountOvertime) {
		this.statementOfAccountOvertime = statementOfAccountOvertime;
	}

	@Override
	public String toString() {
		return "BillingCharging [employmentConfiguration=" + "]";
	}

}
