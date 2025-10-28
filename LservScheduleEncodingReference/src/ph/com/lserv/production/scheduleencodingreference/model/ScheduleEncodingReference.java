package ph.com.lserv.production.scheduleencodingreference.model;

import java.util.Date;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;

public class ScheduleEncodingReference extends MasterObject {
	Integer primaryKeyReference;
	Client client;
	String scheduleName;
	User user;
	String changedByUser;
	String changedInComputer;
	Date changedOnDate;

	public ScheduleEncodingReference() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ScheduleEncodingReference(Integer primaryKeyReference, Client client, String scheduleName, User user,
			String changedByUser, String changedInComputer, Date changedOnDate) {
		super();
		this.primaryKeyReference = primaryKeyReference;
		this.client = client;
		this.scheduleName = scheduleName;
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

	public Integer getPrimaryKeyReference() {
		return primaryKeyReference;
	}

	public void setPrimaryKeyReference(Integer primaryKeyReference) {
		this.primaryKeyReference = primaryKeyReference;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	@Override
	public String toString() {
		return "ScheduleEncodingReference [primaryKeyReference=" + primaryKeyReference + ", client=" + client
				+ ", scheduleName=" + scheduleName + ", user=" + user + ", changedByUser=" + changedByUser
				+ ", changedInComputer=" + changedInComputer + ", changedOnDate=" + changedOnDate + "]";
	}


}
