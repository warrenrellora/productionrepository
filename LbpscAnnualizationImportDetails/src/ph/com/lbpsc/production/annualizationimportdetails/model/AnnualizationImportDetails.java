package ph.com.lbpsc.production.annualizationimportdetails.model;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.annualizationitems.model.AnnualizationItems;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;

public class AnnualizationImportDetails extends MasterObject implements Serializable {
	private static final long serialVersionUID = -3241692463029033914L;
	private Employee employee;
	private Date payFrom;
	private Date payTo;
	private BigDecimal amount;
	private BigDecimal days;
	private AnnualizationItems annualizationItems;
	private Annualization annualization;
	private Integer primaryKey;

	public AnnualizationImportDetails() {
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public AnnualizationImportDetails(Employee employee, Date payFrom, Date payTo, BigDecimal amount, BigDecimal days,
			AnnualizationItems annualizationItems, Annualization annualization, User user, String changedInComputer,
			Date changedOnDate, Integer primaryKey) {
		this.employee = employee;
		this.payFrom = payFrom;
		this.payTo = payTo;
		this.amount = amount;
		this.days = days;
		this.annualizationItems = annualizationItems;
		this.annualization = annualization;
		this.user = user;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.primaryKey = primaryKey;
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public AnnualizationImportDetails(Employee employee, Date payFrom, Date payTo, BigDecimal amount, BigDecimal days,
			AnnualizationItems annualizationItems, Annualization annualization, User user, String changedInComputer,
			Date changedOnDate) {
		this.employee = employee;
		this.payFrom = payFrom;
		this.payTo = payTo;
		this.amount = amount;
		this.days = days;
		this.annualizationItems = annualizationItems;
		this.annualization = annualization;
		this.user = user;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		Employee oldEmployee = this.employee;
		this.employee = employee;
		this.propertySupport.firePropertyChange("employee", oldEmployee, employee);
	}

	public Date getPayFrom() {
		return payFrom;
	}

	public void setPayFrom(Date payFrom) {
		Date oldPayFrom = this.payFrom;
		this.payFrom = payFrom;
		this.propertySupport.firePropertyChange("payFrom", oldPayFrom, payFrom);
	}

	public Date getPayTo() {
		return payTo;
	}

	public void setPayTo(Date payTo) {
		Date oldPayTo = this.payTo;
		this.payTo = payTo;
		this.propertySupport.firePropertyChange("payTo", oldPayTo, payTo);
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		BigDecimal oldAmount = this.amount;
		this.amount = amount;
		this.propertySupport.firePropertyChange("amount", oldAmount, amount);
	}

	public BigDecimal getDays() {
		return days;
	}

	public void setDays(BigDecimal days) {
		BigDecimal oldDays = this.days;
		this.days = days;
		this.propertySupport.firePropertyChange("days", oldDays, days);
	}

	public AnnualizationItems getAnnualizationItems() {
		return annualizationItems;
	}

	public void setAnnualizationItems(AnnualizationItems annualizationItems) {
		AnnualizationItems oldAnnualizationItems = this.annualizationItems;
		this.annualizationItems = annualizationItems;
		this.propertySupport.firePropertyChange("annualizationItems", oldAnnualizationItems, annualizationItems);
	}

	public Annualization getAnnualization() {
		return annualization;
	}

	public void setAnnualization(Annualization annualization) {
		Annualization oldAnnualization = this.annualization;
		this.annualization = annualization;
		this.propertySupport.firePropertyChange("annualization", oldAnnualization, annualization);
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		Integer oldPrimaryKey = this.primaryKey;
		this.primaryKey = primaryKey;
		this.propertySupport.firePropertyChange("primaryKey", oldPrimaryKey, primaryKey);
	}

	public String getAnnualizationImportDetails() {
		return "employee:" + this.employee + "\r\n" + "payFrom:" + this.payFrom + "\r\n" + "payTo:" + this.payTo
				+ "\r\n" + "amount:" + this.amount + "\r\n" + "days:" + this.days + "\r\n" + "annualizationItems:"
				+ this.annualizationItems + "\r\n" + "annualization:" + this.annualization + "\r\n" + "user:"
				+ this.user + "\r\n" + "changedInComputer:" + this.changedInComputer + "\r\n" + "changedOnDate:"
				+ this.changedOnDate + "\r\n" + "primaryKey:" + this.primaryKey + "\r\n";
	}
}
