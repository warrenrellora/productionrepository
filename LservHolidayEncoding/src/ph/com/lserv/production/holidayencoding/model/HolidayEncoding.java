package ph.com.lserv.production.holidayencoding.model;

import java.util.Date;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lserv.production.holidaytypereference.model.HolidayTypeReference;

public class HolidayEncoding extends MasterObject {
	Integer primaryKey;
	Client client;
	Date date;
	String description;
	Integer fixed;
	Date overrideDate;
	User user;
	String changedByUser;
	String changedInComputer;
	Date changedOnDate;
	HolidayTypeReference holidayTypeReference;
	String clientCode;

	public HolidayEncoding() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HolidayEncoding(Integer primaryKey, Client client, Date date, String description, Integer fixed,
			Date overrideDate, User user, String changedByUser, String changedInComputer, Date changedOnDate,
			HolidayTypeReference holidayTypeReference, String clientCode) {
		super();
		this.primaryKey = primaryKey;
		this.client = client;
		this.date = date;
		this.description = description;
		this.fixed = fixed;
		this.overrideDate = overrideDate;
		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
		this.holidayTypeReference = holidayTypeReference;
		this.clientCode = clientCode;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getFixed() {
		return fixed;
	}

	public void setFixed(Integer fixed) {
		this.fixed = fixed;
	}

	public Date getOverrideDate() {
		return overrideDate;
	}

	public void setOverrideDate(Date overrideDate) {
		this.overrideDate = overrideDate;
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

	public HolidayTypeReference getHolidayTypeReference() {
		return holidayTypeReference;
	}

	public void setHolidayTypeReference(HolidayTypeReference holidayTypeReference) {
		this.holidayTypeReference = holidayTypeReference;
	}
	
	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	@Override
	public String toString() {
		return "HolidayEncoding [primaryKey=" + primaryKey + ", client=" + client + ", date=" + date + ", description="
				+ description + ", fixed=" + fixed + ", overrideDate=" + overrideDate + ", user=" + user
				+ ", changedByUser=" + changedByUser + ", changedInComputer=" + changedInComputer + ", changedOnDate="
				+ changedOnDate + ", holidayTypeReference=" + holidayTypeReference + ", clientCode=" + clientCode + "]";
	}

}
