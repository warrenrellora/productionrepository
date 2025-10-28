package ph.com.lbpsc.production.annualization.model;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ph.com.lbpsc.production.annualizationdetails.model.AnnualizationDetails;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterobject.MasterObject;

import ph.com.lbpsc.production.serviceincentiveleave.model.ServiceIncentiveLeave;
import ph.com.lbpsc.production.thirteenthmonthpay.model.ThirteenthMonthPay;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lserv.production.quitclaimprocess.model.Quitclaim;

public class Annualization extends MasterObject implements Serializable {
	private static final long serialVersionUID = 9151502625915364140L;
	private Employee employee;
	private Client client;
	private BigDecimal amountOfTaxableNetRegularPay;
	private BigDecimal amountOfNonTaxableNetRegularPay;
	private BigDecimal amountOfTaxableBasicPay;
	private BigDecimal amountOfNonTaxableBasicPay;
	private BigDecimal amountOfTaxableHolidayPay;
	private BigDecimal amountOfNonTaxableHolidayPay;
	private BigDecimal amountOfTaxableOvertimePay;
	private BigDecimal amountOfNonTaxableOvertimePay;
	private BigDecimal amountOfTaxableNightDifferentialPay;
	private BigDecimal amountOfNonTaxableNightDifferentialPay;
	private BigDecimal amountOfGrossPay;
	private BigDecimal amountOfWithholdingTax;
	private BigDecimal amountOfEmployeeShare;
	private BigDecimal amountOfCompanyShare;
	private BigDecimal amountOfEmployeeCompensation;
	private ThirteenthMonthPay thirteenthMonthPay;
	private ServiceIncentiveLeave serviceIncentiveLeave;
	private BigDecimal amountOfTaxableHazardPay;
	private BigDecimal amountOfNonTaxableHazardPay;
	private BigDecimal amountOfTaxableCostOfLivingAllowance;
	private BigDecimal amountOfNonTaxableCostOfLivingAllowance;
	private BigDecimal deminimis;
	private BigDecimal otherCompensation;
	private BigDecimal otherBenefitsAnd13thMonth;
	private BigDecimal taxableBonus;
	private BigDecimal taxableCompensation;
	private BigDecimal nonTaxableAmount;
	private BigDecimal taxableAmount;
	private BigDecimal totalAmountTaxes;
	private BigDecimal taxDueAmount;
	private BigDecimal integralRiceAllowance;
	private Annualization previousAnnualization;
	private boolean isPreviousAnnual;
	private EmploymentHistory employmentHistory;
	private Date startOnDate;
	private Date endOnDate;
	private Integer annualizationYear;
	private boolean isAnnualization;
	private boolean isQuitClaim;
	private boolean isUploaded;
	private Integer recordCount;
	private Date lockedOnDate;
	private String remarks;
	private Integer primaryKey;
	private Quitclaim quitclaim;

	private List<AnnualizationDetails> annualizationDetailsList;

	public Annualization() {
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public Annualization(Employee employee, Client client, BigDecimal amountOfTaxableHolidayPay,
			BigDecimal amountOfNonTaxableHolidayPay, BigDecimal amountOfTaxableNetRegularPay,
			BigDecimal amountOfNonTaxableNetRegularPay, BigDecimal amountOfTaxableOvertimePay,
			BigDecimal amountOfNonTaxableOvertimePay, BigDecimal amountOfTaxableNightDifferentialPay,
			BigDecimal amountOfNonTaxableNightDifferentialPay, BigDecimal amountOfGrossPay,
			BigDecimal amountOfWithholdingTax, BigDecimal amountOfEmployeeShare, BigDecimal amountOfCompanyShare,
			BigDecimal amountOfEmployeeCompensation, ThirteenthMonthPay thirteenthMonthPay,
			ServiceIncentiveLeave serviceIncentiveLeave, BigDecimal deminimis, BigDecimal otherCompensation,
			BigDecimal otherBenefitsAnd13thMonth, BigDecimal taxableBonus, BigDecimal nonTaxableAmount,
			BigDecimal taxableAmount, BigDecimal totalAmountTaxes, BigDecimal taxDueAmount,
			Annualization previousAnnualization, boolean isPreviousAnnual, EmploymentHistory employmentHistory,
			Date startOnDate, Date endOnDate, Integer annualizationYear, boolean isAnnualization, boolean isQuitClaim,
			boolean isUploaded, Integer recordCount, Date lockedOnDate, String remarks, String changedByUser,
			String changedInComputer, Date changedOnDate, User user, Integer primaryKey, Quitclaim quitclaim) {
		this.employee = employee;
		this.client = client;
		this.amountOfTaxableNetRegularPay = amountOfTaxableNetRegularPay;
		this.amountOfNonTaxableNetRegularPay = amountOfNonTaxableNetRegularPay;
		this.amountOfTaxableHolidayPay = amountOfTaxableHolidayPay;
		this.amountOfNonTaxableHolidayPay = amountOfNonTaxableHolidayPay;
		this.amountOfTaxableOvertimePay = amountOfTaxableOvertimePay;
		this.amountOfNonTaxableOvertimePay = amountOfNonTaxableOvertimePay;
		this.amountOfTaxableNightDifferentialPay = amountOfTaxableNightDifferentialPay;
		this.amountOfNonTaxableNightDifferentialPay = amountOfNonTaxableNightDifferentialPay;
		this.amountOfGrossPay = amountOfGrossPay;
		this.amountOfWithholdingTax = amountOfWithholdingTax;
		this.amountOfEmployeeShare = amountOfEmployeeShare;
		this.amountOfCompanyShare = amountOfCompanyShare;
		this.amountOfEmployeeCompensation = amountOfEmployeeCompensation;
		this.thirteenthMonthPay = thirteenthMonthPay;
		this.serviceIncentiveLeave = serviceIncentiveLeave;
		this.deminimis = deminimis;
		this.otherCompensation = otherCompensation;
		this.otherBenefitsAnd13thMonth = otherBenefitsAnd13thMonth;
		this.taxableBonus = taxableBonus;
		this.nonTaxableAmount = nonTaxableAmount;
		this.taxableAmount = taxableAmount;
		this.totalAmountTaxes = totalAmountTaxes;
		this.taxDueAmount = taxDueAmount;
		this.previousAnnualization = previousAnnualization;
		this.isPreviousAnnual = isPreviousAnnual;
		this.employmentHistory = employmentHistory;
		this.startOnDate = startOnDate;
		this.endOnDate = endOnDate;
		this.annualizationYear = annualizationYear;
		this.isAnnualization = isAnnualization;
		this.isQuitClaim = isQuitClaim;
		this.isUploaded = isUploaded;
		this.recordCount = recordCount;
		this.lockedOnDate = lockedOnDate;
		this.remarks = remarks;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.user = user;
		this.quitclaim = quitclaim;
		this.primaryKey = primaryKey;
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public Annualization(Employee employee, Client client, BigDecimal amountOfTaxableHolidayPay,
			BigDecimal amountOfNonTaxableHolidayPay, BigDecimal amountOfTaxableNetRegularPay,
			BigDecimal amountOfNonTaxableNetRegularPay, BigDecimal amountOfTaxableOvertimePay,
			BigDecimal amountOfNonTaxableOvertimePay, BigDecimal amountOfTaxableNightDifferentialPay,
			BigDecimal amountOfNonTaxableNightDifferentialPay, BigDecimal amountOfGrossPay,
			BigDecimal amountOfWithholdingTax, BigDecimal amountOfEmployeeShare, BigDecimal amountOfCompanyShare,
			BigDecimal amountOfEmployeeCompensation, ThirteenthMonthPay thirteenthMonthPay,
			ServiceIncentiveLeave serviceIncentiveLeave, BigDecimal deminimis, BigDecimal otherCompensation,
			BigDecimal otherBenefitsAnd13thMonth, BigDecimal taxableBonus, BigDecimal nonTaxableAmount,
			BigDecimal taxableAmount, BigDecimal totalAmountTaxes, BigDecimal taxDueAmount,
			Annualization previousAnnualization, boolean isPreviousAnnual, EmploymentHistory employmentHistory,
			Date startOnDate, Date endOnDate, Integer annualizationYear, boolean isAnnualization, boolean isQuitClaim,
			boolean isUploaded, Integer recordCount, Date lockedOnDate, String remarks, String changedByUser,
			String changedInComputer, Date changedOnDate, User user, Quitclaim quitclaim) {
		this.employee = employee;
		this.client = client;
		this.amountOfTaxableNetRegularPay = amountOfTaxableNetRegularPay;
		this.amountOfNonTaxableNetRegularPay = amountOfNonTaxableNetRegularPay;
		this.amountOfTaxableHolidayPay = amountOfTaxableHolidayPay;
		this.amountOfNonTaxableHolidayPay = amountOfNonTaxableHolidayPay;
		this.amountOfTaxableOvertimePay = amountOfTaxableOvertimePay;
		this.amountOfNonTaxableOvertimePay = amountOfNonTaxableOvertimePay;
		this.amountOfTaxableNightDifferentialPay = amountOfTaxableNightDifferentialPay;
		this.amountOfNonTaxableNightDifferentialPay = amountOfNonTaxableNightDifferentialPay;
		this.amountOfGrossPay = amountOfGrossPay;
		this.amountOfWithholdingTax = amountOfWithholdingTax;
		this.amountOfEmployeeShare = amountOfEmployeeShare;
		this.amountOfCompanyShare = amountOfCompanyShare;
		this.amountOfEmployeeCompensation = amountOfEmployeeCompensation;
		this.thirteenthMonthPay = thirteenthMonthPay;
		this.serviceIncentiveLeave = serviceIncentiveLeave;
		this.deminimis = deminimis;
		this.otherCompensation = otherCompensation;
		this.otherBenefitsAnd13thMonth = otherBenefitsAnd13thMonth;
		this.taxableBonus = taxableBonus;
		this.nonTaxableAmount = nonTaxableAmount;
		this.taxableAmount = taxableAmount;
		this.totalAmountTaxes = totalAmountTaxes;
		this.taxDueAmount = taxDueAmount;
		this.previousAnnualization = previousAnnualization;
		this.isPreviousAnnual = isPreviousAnnual;
		this.employmentHistory = employmentHistory;
		this.startOnDate = startOnDate;
		this.endOnDate = endOnDate;
		this.annualizationYear = annualizationYear;
		this.isAnnualization = isAnnualization;
		this.isQuitClaim = isQuitClaim;
		this.isUploaded = isUploaded;
		this.recordCount = recordCount;
		this.lockedOnDate = lockedOnDate;
		this.remarks = remarks;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.user = user;
		this.quitclaim = quitclaim;
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public Annualization(Employee employee, Client client, BigDecimal amountOfTaxableNetRegularPay,
			BigDecimal amountOfNonTaxableNetRegularPay, BigDecimal amountOfTaxableBasicPay,
			BigDecimal amountOfNonTaxableBasicPay, BigDecimal amountOfTaxableHolidayPay,
			BigDecimal amountOfNonTaxableHolidayPay, BigDecimal amountOfTaxableOvertimePay,
			BigDecimal amountOfNonTaxableOvertimePay, BigDecimal amountOfTaxableNightDifferentialPay,
			BigDecimal amountOfNonTaxableNightDifferentialPay, BigDecimal amountOfGrossPay,
			BigDecimal amountOfWithholdingTax, BigDecimal amountOfEmployeeShare, BigDecimal amountOfCompanyShare,
			BigDecimal amountOfEmployeeCompensation, ThirteenthMonthPay thirteenthMonthPay,
			ServiceIncentiveLeave serviceIncentiveLeave, BigDecimal amountOfTaxableHazardPay,
			BigDecimal amountOfNonTaxableHazardPay, BigDecimal amountOfTaxableCostOfLivingAllowance,
			BigDecimal amountOfNonTaxableCostOfLivingAllowance, BigDecimal deminimis, BigDecimal otherCompensation,
			BigDecimal otherBenefitsAnd13thMonth, BigDecimal taxableBonus, BigDecimal taxableCompensation,
			BigDecimal nonTaxableAmount, BigDecimal taxableAmount, BigDecimal totalAmountTaxes, BigDecimal taxDueAmount,
			BigDecimal integralRiceAllowance, Annualization previousAnnualization, boolean isPreviousAnnual,
			EmploymentHistory employmentHistory, Date startOnDate, Date endOnDate, Integer annualizationYear,
			boolean isAnnualization, boolean isQuitClaim, boolean isUploaded, Integer recordCount, Date lockedOnDate,
			String remarks, Integer primaryKey, Quitclaim quitclaim,
			List<AnnualizationDetails> annualizationDetailsList) {
		super();
		this.employee = employee;
		this.client = client;
		this.amountOfTaxableNetRegularPay = amountOfTaxableNetRegularPay;
		this.amountOfNonTaxableNetRegularPay = amountOfNonTaxableNetRegularPay;
		this.amountOfTaxableBasicPay = amountOfTaxableBasicPay;
		this.amountOfNonTaxableBasicPay = amountOfNonTaxableBasicPay;
		this.amountOfTaxableHolidayPay = amountOfTaxableHolidayPay;
		this.amountOfNonTaxableHolidayPay = amountOfNonTaxableHolidayPay;
		this.amountOfTaxableOvertimePay = amountOfTaxableOvertimePay;
		this.amountOfNonTaxableOvertimePay = amountOfNonTaxableOvertimePay;
		this.amountOfTaxableNightDifferentialPay = amountOfTaxableNightDifferentialPay;
		this.amountOfNonTaxableNightDifferentialPay = amountOfNonTaxableNightDifferentialPay;
		this.amountOfGrossPay = amountOfGrossPay;
		this.amountOfWithholdingTax = amountOfWithholdingTax;
		this.amountOfEmployeeShare = amountOfEmployeeShare;
		this.amountOfCompanyShare = amountOfCompanyShare;
		this.amountOfEmployeeCompensation = amountOfEmployeeCompensation;
		this.thirteenthMonthPay = thirteenthMonthPay;
		this.serviceIncentiveLeave = serviceIncentiveLeave;
		this.amountOfTaxableHazardPay = amountOfTaxableHazardPay;
		this.amountOfNonTaxableHazardPay = amountOfNonTaxableHazardPay;
		this.amountOfTaxableCostOfLivingAllowance = amountOfTaxableCostOfLivingAllowance;
		this.amountOfNonTaxableCostOfLivingAllowance = amountOfNonTaxableCostOfLivingAllowance;
		this.deminimis = deminimis;
		this.otherCompensation = otherCompensation;
		this.otherBenefitsAnd13thMonth = otherBenefitsAnd13thMonth;
		this.taxableBonus = taxableBonus;
		this.taxableCompensation = taxableCompensation;
		this.nonTaxableAmount = nonTaxableAmount;
		this.taxableAmount = taxableAmount;
		this.totalAmountTaxes = totalAmountTaxes;
		this.taxDueAmount = taxDueAmount;
		this.integralRiceAllowance = integralRiceAllowance;
		this.previousAnnualization = previousAnnualization;
		this.isPreviousAnnual = isPreviousAnnual;
		this.employmentHistory = employmentHistory;
		this.startOnDate = startOnDate;
		this.endOnDate = endOnDate;
		this.annualizationYear = annualizationYear;
		this.isAnnualization = isAnnualization;
		this.isQuitClaim = isQuitClaim;
		this.isUploaded = isUploaded;
		this.recordCount = recordCount;
		this.lockedOnDate = lockedOnDate;
		this.remarks = remarks;
		this.primaryKey = primaryKey;
		this.quitclaim = quitclaim;
		this.annualizationDetailsList = annualizationDetailsList;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		Employee oldEmployee = this.employee;
		this.employee = employee;
		this.propertySupport.firePropertyChange("employee", oldEmployee, employee);
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		Client oldClient = this.client;
		this.client = client;
		this.propertySupport.firePropertyChange("client", oldClient, client);
	}

	public BigDecimal getAmountOfTaxableHolidayPay() {
		return amountOfTaxableHolidayPay;
	}

	public void setAmountOfTaxableHolidayPay(BigDecimal amountOfTaxableHolidayPay) {
		BigDecimal oldAmountOfTaxableHolidayPay = this.amountOfTaxableHolidayPay;
		this.amountOfTaxableHolidayPay = amountOfTaxableHolidayPay;
		this.propertySupport.firePropertyChange("amountOfTaxableHolidayPay", oldAmountOfTaxableHolidayPay,
				amountOfTaxableHolidayPay);
	}

	public BigDecimal getAmountOfNonTaxableHolidayPay() {
		return amountOfNonTaxableHolidayPay;
	}

	public void setAmountOfNonTaxableHolidayPay(BigDecimal amountOfNonTaxableHolidayPay) {
		BigDecimal oldAmountOfNonTaxableHolidayPay = this.amountOfNonTaxableHolidayPay;
		this.amountOfNonTaxableHolidayPay = amountOfNonTaxableHolidayPay;
		this.propertySupport.firePropertyChange("amountOfNonTaxableHolidayPay", oldAmountOfNonTaxableHolidayPay,
				amountOfNonTaxableHolidayPay);
	}

	public BigDecimal getAmountOfTaxableNetRegularPay() {
		return amountOfTaxableNetRegularPay;
	}

	public void setAmountOfTaxableNetRegularPay(BigDecimal amountOfTaxableNetRegularPay) {
		BigDecimal oldAmountOfTaxableNetRegularPay = this.amountOfTaxableNetRegularPay;
		this.amountOfTaxableNetRegularPay = amountOfTaxableNetRegularPay;
		this.propertySupport.firePropertyChange("amountOfTaxableNetRegularPay", oldAmountOfTaxableNetRegularPay,
				amountOfTaxableNetRegularPay);
	}

	public BigDecimal getAmountOfNonTaxableNetRegularPay() {
		return amountOfNonTaxableNetRegularPay;
	}

	public void setAmountOfNonTaxableNetRegularPay(BigDecimal amountOfNonTaxableNetRegularPay) {
		BigDecimal oldAmountOfNonTaxableNetRegularPay = this.amountOfNonTaxableNetRegularPay;
		this.amountOfNonTaxableNetRegularPay = amountOfNonTaxableNetRegularPay;
		this.propertySupport.firePropertyChange("amountOfNonTaxableNetRegularPay", oldAmountOfNonTaxableNetRegularPay,
				amountOfNonTaxableNetRegularPay);
	}

	public BigDecimal getAmountOfNonTaxableBasicPay() {
		return amountOfNonTaxableBasicPay;
	}

	public void setAmountOfNonTaxableBasicPay(BigDecimal amountOfNonTaxableBasicPay) {
		BigDecimal oldAmountOfNonTaxableBasicPay = this.amountOfNonTaxableBasicPay;
		this.amountOfNonTaxableBasicPay = amountOfNonTaxableBasicPay;
		this.propertySupport.firePropertyChange("amountOfNonTaxableBasicPay", oldAmountOfNonTaxableBasicPay,
				amountOfNonTaxableBasicPay);
	}

	public BigDecimal getAmountOfTaxableBasicPay() {
		return amountOfTaxableBasicPay;
	}

	public void setAmountOfTaxableBasicPay(BigDecimal amountOfTaxableBasicPay) {
		BigDecimal oldAmountOfTaxableBasicPay = this.amountOfTaxableBasicPay;
		this.amountOfTaxableBasicPay = amountOfTaxableBasicPay;
		this.propertySupport.firePropertyChange("amountOfTaxableBasicPay", oldAmountOfTaxableBasicPay,
				amountOfTaxableBasicPay);
	}

	public BigDecimal getAmountOfTaxableOvertimePay() {
		return amountOfTaxableOvertimePay;
	}

	public void setAmountOfTaxableOvertimePay(BigDecimal amountOfTaxableOvertimePay) {
		BigDecimal oldAmountOfTaxableOvertimePay = this.amountOfTaxableOvertimePay;
		this.amountOfTaxableOvertimePay = amountOfTaxableOvertimePay;
		this.propertySupport.firePropertyChange("amountOfTaxableOvertimePay", oldAmountOfTaxableOvertimePay,
				amountOfTaxableOvertimePay);
	}

	public BigDecimal getAmountOfNonTaxableOvertimePay() {
		return amountOfNonTaxableOvertimePay;
	}

	public void setAmountOfNonTaxableOvertimePay(BigDecimal amountOfNonTaxableOvertimePay) {
		BigDecimal oldAmountOfNonTaxableOvertimePay = this.amountOfNonTaxableOvertimePay;
		this.amountOfNonTaxableOvertimePay = amountOfNonTaxableOvertimePay;
		this.propertySupport.firePropertyChange("amountOfNonTaxableOvertimePay", oldAmountOfNonTaxableOvertimePay,
				amountOfNonTaxableOvertimePay);
	}

	public BigDecimal getAmountOfTaxableNightDifferentialPay() {
		return amountOfTaxableNightDifferentialPay;
	}

	public void setAmountOfTaxableNightDifferentialPay(BigDecimal amountOfTaxableNightDifferentialPay) {
		BigDecimal oldAmountOfTaxableNightDifferentialPay = this.amountOfTaxableNightDifferentialPay;
		this.amountOfTaxableNightDifferentialPay = amountOfTaxableNightDifferentialPay;
		this.propertySupport.firePropertyChange("amountOfTaxableNightDifferentialPay",
				oldAmountOfTaxableNightDifferentialPay, amountOfTaxableNightDifferentialPay);
	}

	public BigDecimal getAmountOfNonTaxableNightDifferentialPay() {
		return amountOfNonTaxableNightDifferentialPay;
	}

	public void setAmountOfNonTaxableNightDifferentialPay(BigDecimal amountOfNonTaxableNightDifferentialPay) {
		this.amountOfNonTaxableNightDifferentialPay = amountOfNonTaxableNightDifferentialPay;
	}

	public BigDecimal getAmountOfGrossPay() {
		return amountOfGrossPay;
	}

	public void setAmountOfGrossPay(BigDecimal amountOfGrossPay) {
		BigDecimal oldAmountOfGrossPay = this.amountOfGrossPay;
		this.amountOfGrossPay = amountOfGrossPay;
		this.propertySupport.firePropertyChange("amountOfGrossPay", oldAmountOfGrossPay, amountOfGrossPay);
	}

	public BigDecimal getAmountOfWithholdingTax() {
		return amountOfWithholdingTax;
	}

	public void setAmountOfWithholdingTax(BigDecimal amountOfWithholdingTax) {
		BigDecimal oldAmountOfWithholdingTax = this.amountOfWithholdingTax;
		this.amountOfWithholdingTax = amountOfWithholdingTax;
		this.propertySupport.firePropertyChange("amountOfWithholdingTax", oldAmountOfWithholdingTax,
				amountOfWithholdingTax);
	}

	public BigDecimal getAmountOfEmployeeShare() {
		return amountOfEmployeeShare;
	}

	public void setAmountOfEmployeeShare(BigDecimal amountOfEmployeeShare) {
		BigDecimal oldAmountOfEmployeeShare = this.amountOfEmployeeShare;
		this.amountOfEmployeeShare = amountOfEmployeeShare;
		this.propertySupport.firePropertyChange("amountOfEmployeeShare", oldAmountOfEmployeeShare,
				amountOfEmployeeShare);
	}

	public BigDecimal getAmountOfCompanyShare() {
		return amountOfCompanyShare;
	}

	public void setAmountOfCompanyShare(BigDecimal amountOfCompanyShare) {
		BigDecimal oldAmountOfCompanyShare = this.amountOfCompanyShare;
		this.amountOfCompanyShare = amountOfCompanyShare;
		this.propertySupport.firePropertyChange("amountOfCompanyShare", oldAmountOfCompanyShare, amountOfCompanyShare);
	}

	public BigDecimal getAmountOfEmployeeCompensation() {
		return amountOfEmployeeCompensation;
	}

	public void setAmountOfEmployeeCompensation(BigDecimal amountOfEmployeeCompensation) {
		BigDecimal oldAmountOfEmployeeCompensation = this.amountOfEmployeeCompensation;
		this.amountOfEmployeeCompensation = amountOfEmployeeCompensation;
		this.propertySupport.firePropertyChange("amountOfEmployeeCompensation", oldAmountOfEmployeeCompensation,
				amountOfEmployeeCompensation);
	}

	public ThirteenthMonthPay getThirteenthMonthPay() {
		return thirteenthMonthPay;
	}

	public void setThirteenthMonthPay(ThirteenthMonthPay thirteenthMonthPay) {
		ThirteenthMonthPay oldThirteenthMonthPay = this.thirteenthMonthPay;
		this.thirteenthMonthPay = thirteenthMonthPay;
		this.propertySupport.firePropertyChange("thirteenthMonthPay", oldThirteenthMonthPay, thirteenthMonthPay);
	}

	public ServiceIncentiveLeave getServiceIncentiveLeave() {
		return serviceIncentiveLeave;
	}

	public void setServiceIncentiveLeave(ServiceIncentiveLeave serviceIncentiveLeave) {
		ServiceIncentiveLeave oldServiceIncentiveLeave = this.serviceIncentiveLeave;
		this.serviceIncentiveLeave = serviceIncentiveLeave;
		this.propertySupport.firePropertyChange("serviceIncentiveLeave", oldServiceIncentiveLeave,
				serviceIncentiveLeave);
	}

	public BigDecimal getAmountOfTaxableHazardPay() {
		return amountOfTaxableHazardPay;
	}

	public void setAmountOfTaxableHazardPay(BigDecimal amountOfTaxableHazardPay) {
		BigDecimal oldAmountOfTaxableHazardPay = this.amountOfTaxableHazardPay;
		this.amountOfTaxableHazardPay = amountOfTaxableHazardPay;
		this.propertySupport.firePropertyChange("amountOfTaxableHazardPay", oldAmountOfTaxableHazardPay,
				amountOfTaxableHazardPay);
	}

	public BigDecimal getAmountOfNonTaxableHazardPay() {
		return amountOfNonTaxableHazardPay;
	}

	public void setAmountOfNonTaxableHazardPay(BigDecimal amountOfNonTaxableHazardPay) {
		BigDecimal oldAmountOfNonTaxableHazardPay = this.amountOfNonTaxableHazardPay;
		this.amountOfNonTaxableHazardPay = amountOfNonTaxableHazardPay;
		this.propertySupport.firePropertyChange("amountOfNonTaxableHazardPay", oldAmountOfNonTaxableHazardPay,
				amountOfNonTaxableHazardPay);
	}

	public BigDecimal getAmountOfTaxableCostOfLivingAllowance() {
		return amountOfTaxableCostOfLivingAllowance;
	}

	public void setAmountOfTaxableCostOfLivingAllowance(BigDecimal amountOfTaxableCostOfLivingAllowance) {
		BigDecimal oldAmountOfTaxableCostOfLivingAllowance = this.amountOfTaxableCostOfLivingAllowance;
		this.amountOfTaxableCostOfLivingAllowance = amountOfTaxableCostOfLivingAllowance;
		this.propertySupport.firePropertyChange("amountOfTaxableCostOfLivingAllowance",
				oldAmountOfTaxableCostOfLivingAllowance, amountOfTaxableCostOfLivingAllowance);
	}

	public BigDecimal getAmountOfNonTaxableCostOfLivingAllowance() {
		return amountOfNonTaxableCostOfLivingAllowance;
	}

	public void setAmountOfNonTaxableCostOfLivingAllowance(BigDecimal amountOfNonTaxableCostOfLivingAllowance) {
		BigDecimal oldAmountOfNonTaxableCostOfLivingAllowance = this.amountOfNonTaxableCostOfLivingAllowance;
		this.amountOfNonTaxableCostOfLivingAllowance = amountOfNonTaxableCostOfLivingAllowance;
		this.propertySupport.firePropertyChange("amountOfNonTaxableCostOfLivingAllowance",
				oldAmountOfNonTaxableCostOfLivingAllowance, amountOfNonTaxableCostOfLivingAllowance);
	}

	public BigDecimal getDeminimis() {
		return deminimis;
	}

	public void setDeminimis(BigDecimal deminimis) {
		BigDecimal oldDeminimis = this.deminimis;
		this.deminimis = deminimis;
		this.propertySupport.firePropertyChange("deminimis", oldDeminimis, deminimis);
	}

	public BigDecimal getOtherCompensation() {
		return otherCompensation;
	}

	public void setOtherCompensation(BigDecimal otherCompensation) {
		BigDecimal oldOtherCompensation = this.otherCompensation;
		this.otherCompensation = otherCompensation;
		this.propertySupport.firePropertyChange("otherCompensation", oldOtherCompensation, otherCompensation);
	}

	public BigDecimal getOtherBenefitsAnd13thMonth() {
		return otherBenefitsAnd13thMonth;
	}

	public void setOtherBenefitsAnd13thMonth(BigDecimal otherBenefitsAnd13thMonth) {
		BigDecimal oldOtherBenefitsAnd13thMonth = this.otherBenefitsAnd13thMonth;
		this.otherBenefitsAnd13thMonth = otherBenefitsAnd13thMonth;
		this.propertySupport.firePropertyChange("otherBenefitsAnd13thMonth", oldOtherBenefitsAnd13thMonth,
				otherBenefitsAnd13thMonth);
	}

	public BigDecimal getTaxableBonus() {
		return taxableBonus;
	}

	public void setTaxableBonus(BigDecimal taxableBonus) {
		BigDecimal oldTaxableBonus = this.taxableBonus;
		this.taxableBonus = taxableBonus;
		this.propertySupport.firePropertyChange("taxableBonus", oldTaxableBonus, taxableBonus);
	}

	public BigDecimal getNonTaxableAmount() {
		return nonTaxableAmount;
	}

	public void setNonTaxableAmount(BigDecimal nonTaxableAmount) {
		BigDecimal oldNonTaxableAmount = this.nonTaxableAmount;
		this.nonTaxableAmount = nonTaxableAmount;
		this.propertySupport.firePropertyChange("nonTaxableAmount", oldNonTaxableAmount, nonTaxableAmount);
	}

	public BigDecimal getTaxableAmount() {
		return taxableAmount;
	}

	public void setTaxableAmount(BigDecimal taxableAmount) {
		BigDecimal oldTaxableAmount = this.taxableAmount;
		this.taxableAmount = taxableAmount;
		this.propertySupport.firePropertyChange("taxableAmount", oldTaxableAmount, taxableAmount);
	}

	public BigDecimal getTotalAmountTaxes() {
		return totalAmountTaxes;
	}

	public void setTotalAmountTaxes(BigDecimal totalAmountTaxes) {
		BigDecimal oldTotalAmountTaxes = this.totalAmountTaxes;
		this.totalAmountTaxes = totalAmountTaxes;
		this.propertySupport.firePropertyChange("totalAmountTaxes", oldTotalAmountTaxes, totalAmountTaxes);
	}

	public BigDecimal getTaxDueAmount() {
		return taxDueAmount;
	}

	public void setTaxDueAmount(BigDecimal taxDueAmount) {
		BigDecimal oldTaxDueAmount = this.taxDueAmount;
		this.taxDueAmount = taxDueAmount;
		this.propertySupport.firePropertyChange("taxDueAmount", oldTaxDueAmount, taxDueAmount);
	}

	public Annualization getPreviousAnnualization() {
		return previousAnnualization;
	}

	public void setPreviousAnnualization(Annualization previousAnnualization) {
		Annualization oldPreviousAnnualization = this.previousAnnualization;
		this.previousAnnualization = previousAnnualization;
		this.propertySupport.firePropertyChange("previousAnnualization", oldPreviousAnnualization,
				previousAnnualization);
	}

	public boolean getIsPreviousAnnual() {
		return isPreviousAnnual;
	}

	public void setIsPreviousAnnual(boolean isPreviousAnnual) {
		boolean oldIsPreviousAnnual = this.isPreviousAnnual;
		this.isPreviousAnnual = isPreviousAnnual;
		this.propertySupport.firePropertyChange("isPreviousAnnual", oldIsPreviousAnnual, isPreviousAnnual);
	}

	public EmploymentHistory getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(EmploymentHistory employmentHistory) {
		EmploymentHistory oldEmploymentHistory = this.employmentHistory;
		this.employmentHistory = employmentHistory;
		this.propertySupport.firePropertyChange("employmentHistory", oldEmploymentHistory, employmentHistory);
	}

	public Date getStartOnDate() {
		return startOnDate;
	}

	public void setStartOnDate(Date startOnDate) {
		Date oldStartOnDate = this.startOnDate;
		this.startOnDate = startOnDate;
		this.propertySupport.firePropertyChange("startOnDate", oldStartOnDate, startOnDate);
	}

	public Date getEndOnDate() {
		return endOnDate;
	}

	public void setEndOnDate(Date endOnDate) {
		Date oldEndOnDate = this.endOnDate;
		this.endOnDate = endOnDate;
		this.propertySupport.firePropertyChange("endOnDate", oldEndOnDate, endOnDate);
	}

	public Integer getAnnualizationYear() {
		return annualizationYear;
	}

	public void setAnnualizationYear(Integer annualizationYear) {
		Integer oldAnnualizationYear = this.annualizationYear;
		this.annualizationYear = annualizationYear;
		this.propertySupport.firePropertyChange("annualizationYear", oldAnnualizationYear, annualizationYear);
	}

	public boolean getIsAnnualization() {
		return isAnnualization;
	}

	public void setIsAnnualization(boolean isAnnualization) {
		boolean oldIsAnnualization = this.isAnnualization;
		this.isAnnualization = isAnnualization;
		this.propertySupport.firePropertyChange("isAnnualization", oldIsAnnualization, isAnnualization);
	}

	public boolean getIsQuitClaim() {
		return isQuitClaim;
	}

	public void setIsQuitClaim(boolean isQuitClaim) {
		boolean oldIsQuitClaim = this.isQuitClaim;
		this.isQuitClaim = isQuitClaim;
		this.propertySupport.firePropertyChange("isQuitClaim", oldIsQuitClaim, isQuitClaim);
	}

	public boolean getIsUploaded() {
		return isUploaded;
	}

	public void setIsUploaded(boolean isUploaded) {
		boolean oldIsUploaded = this.isUploaded;
		this.isUploaded = isUploaded;
		this.propertySupport.firePropertyChange("isUploaded", oldIsUploaded, isUploaded);
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		Integer oldRecordCount = this.recordCount;
		this.recordCount = recordCount;
		this.propertySupport.firePropertyChange("recordCount", oldRecordCount, recordCount);
	}

	public Date getLockedOnDate() {
		return lockedOnDate;
	}

	public void setLockedOnDate(Date lockedOnDate) {
		Date oldLockedOnDate = this.lockedOnDate;
		this.lockedOnDate = lockedOnDate;
		this.propertySupport.firePropertyChange("lockedOnDate", oldLockedOnDate, lockedOnDate);
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		String oldRemarks = this.remarks;
		this.remarks = remarks;
		this.propertySupport.firePropertyChange("remarks", oldRemarks, remarks);
	}

	public Quitclaim getQuitclaim() {
		return quitclaim;
	}

	public void setQuitclaim(Quitclaim quitclaim) {
		Quitclaim oldQuitclaim = this.quitclaim;
		this.quitclaim = quitclaim;
		this.propertySupport.firePropertyChange("quitclaim", oldQuitclaim, quitclaim);
	}

	public List<AnnualizationDetails> getAnnualizationDetailsList() {
		return annualizationDetailsList;
	}

	public void setAnnualizationDetailsList(List<AnnualizationDetails> annualizationDetailsList) {
		List<AnnualizationDetails> oldAnnualizationDetailsList = this.annualizationDetailsList;
		this.annualizationDetailsList = annualizationDetailsList;
		this.propertySupport.firePropertyChange("annualizationDetailsList", oldAnnualizationDetailsList,
				annualizationDetailsList);
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		Integer oldPrimaryKey = this.primaryKey;
		this.primaryKey = primaryKey;
		this.propertySupport.firePropertyChange("primaryKey", oldPrimaryKey, primaryKey);
	}

	public BigDecimal getIntegralRiceAllowance() {
		return integralRiceAllowance;
	}

	public void setIntegralRiceAllowance(BigDecimal integralRiceAllowance) {
		BigDecimal oldIntegralRiceAllowance = this.integralRiceAllowance;
		this.integralRiceAllowance = integralRiceAllowance;
		this.propertySupport.firePropertyChange("integralRiceAllowance", oldIntegralRiceAllowance,
				integralRiceAllowance);
	}

	public BigDecimal getTaxableCompensation() {
		return taxableCompensation;
	}

	public void setTaxableCompensation(BigDecimal taxableCompensation) {
		this.taxableCompensation = taxableCompensation;
	}

	public String getAnnualization() {
		return "employee:" + this.employee + "\r\n" + "client:" + this.client + "\r\n" + "amountOfTaxableHolidayPay:"
				+ this.amountOfTaxableHolidayPay + "\r\n" + "amountOfNonTaxableHolidayPay:"
				+ this.amountOfNonTaxableHolidayPay + "\r\n" + "amountOfTaxableNetRegularPay:"
				+ this.amountOfTaxableNetRegularPay + "\r\n" + "amountOfNonTaxableNetRegularPay:"
				+ this.amountOfNonTaxableNetRegularPay + "\r\n" + "amountOfTaxableOvertimePay:"
				+ this.amountOfTaxableOvertimePay + "\r\n" + "amountOfNonTaxableOvertimePay:"
				+ this.amountOfNonTaxableOvertimePay + "\r\n" + "amountOfTaxableNightDifferentialPay:"
				+ this.amountOfTaxableNightDifferentialPay + "\r\n" + "amountOfNonTaxableNightDifferentialPay:"
				+ this.amountOfNonTaxableNightDifferentialPay + "\r\n" + "amountOfGrossPay:" + this.amountOfGrossPay
				+ "\r\n" + "amountOfWithholdingTax:" + this.amountOfWithholdingTax + "\r\n" + "amountOfEmployeeShare:"
				+ this.amountOfEmployeeShare + "\r\n" + "amountOfCompanyShare:" + this.amountOfCompanyShare + "\r\n"
				+ "amountOfEmployeeCompensation:" + this.amountOfEmployeeCompensation + "\r\n" + "thirteenthMonthPay:"
				+ this.thirteenthMonthPay + "\r\n" + "serviceIncentiveLeave:" + this.serviceIncentiveLeave + "\r\n"
				+ "deminimis:" + this.deminimis + "\r\n" + "otherCompensation:" + this.otherCompensation + "\r\n"
				+ "otherBenefitsAnd13thMonth:" + this.otherBenefitsAnd13thMonth + "\r\n" + "taxableBonus:"
				+ this.taxableBonus + "\r\n" + "nonTaxableAmount:" + this.nonTaxableAmount + "\r\n" + "taxableAmount:"
				+ this.taxableAmount + "\r\n" + "totalAmountTaxes:" + this.totalAmountTaxes + "\r\n" + "taxDueAmount:"
				+ this.taxDueAmount + "\r\n" + "previousAnnualization:" + this.previousAnnualization + "\r\n"
				+ "isPreviousAnnual:" + this.isPreviousAnnual + "\r\n" + "employmentHistory:" + this.employmentHistory
				+ "\r\n" + "startOnDate:" + this.startOnDate + "\r\n" + "endOnDate:" + this.endOnDate + "\r\n"
				+ "annualizationYear:" + this.annualizationYear + "\r\n" + "isAnnualization:" + this.isAnnualization
				+ "\r\n" + "isQuitClaim:" + this.isQuitClaim + "\r\n" + "isUploaded:" + this.isUploaded + "\r\n"
				+ "recordCount:" + this.recordCount + "\r\n" + "lockedOnDate:" + this.lockedOnDate + "\r\n" + "remarks:"
				+ this.remarks + "\r\n" + "changedByUser:" + this.changedByUser + "\r\n" + "changedInComputer:"
				+ this.changedInComputer + "\r\n" + "changedOnDate:" + this.changedOnDate + "\r\n" + "user:" + this.user
				+ "\r\n" + "\r\n" + "quitclaim:" + this.quitclaim + "\r\n" + "primaryKey:" + this.primaryKey + "\r\n";
	}
}
