package ph.com.lbpsc.production.annualizationdetails.model;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import ph.com.lbpsc.production.annualization.model.Annualization;
import ph.com.lbpsc.production.annualizationbreakdown.model.AnnualizationBreakdown;
import ph.com.lbpsc.production.annualizationitems.model.AnnualizationItems;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;

public class AnnualizationDetails extends MasterObject implements Serializable {
	private static final long serialVersionUID = -3591161312864286106L;
	private Annualization annualization;
	private AnnualizationItems annualizationItems;
	private AnnualizationBreakdown annualizationBreakdown;
	private BigDecimal amount;
	private BigDecimal days;
	private Integer primaryKey;

	public AnnualizationDetails() {
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public AnnualizationDetails(Annualization annualization, AnnualizationItems annualizationItems,
			AnnualizationBreakdown annualizationBreakdown, User user, String changedInComputer, Date changedOnDate,
			Integer primaryKey) {
		this.annualization = annualization;
		this.annualizationItems = annualizationItems;
		this.annualizationBreakdown = annualizationBreakdown;
		this.user = user;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.primaryKey = primaryKey;
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public AnnualizationDetails(Annualization annualization, AnnualizationItems annualizationItems,
			AnnualizationBreakdown annualizationBreakdown, User user, String changedInComputer, Date changedOnDate) {
		this.annualization = annualization;
		this.annualizationItems = annualizationItems;
		this.annualizationBreakdown = annualizationBreakdown;
		this.user = user;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.propertySupport = new PropertyChangeSupport(this);
	}

	public Annualization getAnnualization() {
		return annualization;
	}

	public void setAnnualization(Annualization annualization) {
		Annualization oldAnnualization = this.annualization;
		this.annualization = annualization;
		this.propertySupport.firePropertyChange("annualization", oldAnnualization, annualization);
	}

	public AnnualizationItems getAnnualizationItems() {
		return annualizationItems;
	}

	public void setAnnualizationItems(AnnualizationItems annualizationItems) {
		AnnualizationItems oldAnnualizationItems = this.annualizationItems;
		this.annualizationItems = annualizationItems;
		this.propertySupport.firePropertyChange("annualizationItems", oldAnnualizationItems, annualizationItems);
	}

	public AnnualizationBreakdown getAnnualizationBreakdown() {
		return annualizationBreakdown;
	}

	public void setAnnualizationBreakdown(AnnualizationBreakdown annualizationBreakdown) {
		AnnualizationBreakdown oldAnnualizationBreakdown = this.annualizationBreakdown;
		this.annualizationBreakdown = annualizationBreakdown;
		this.propertySupport.firePropertyChange("annualizationBreakdown", oldAnnualizationBreakdown,
				annualizationBreakdown);
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

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		Integer oldPrimaryKey = this.primaryKey;
		this.primaryKey = primaryKey;
		this.propertySupport.firePropertyChange("primaryKey", oldPrimaryKey, primaryKey);
	}

	public String getAnnualizationDetails() {
		return "annualization:" + this.annualization + "\r\n" + "annualizationItems:" + this.annualizationItems + "\r\n"
				+ "annualizationBreakdown:" + this.annualizationBreakdown + "\r\n" + "user:" + this.user + "\r\n"
				+ "changedInComputer:" + this.changedInComputer + "\r\n" + "changedOnDate:" + this.changedOnDate
				+ "\r\n" + "primaryKey:" + this.primaryKey + "\r\n";
	}
}
