package ph.com.lserv.production.remarksreference.model;

import java.util.Date;

import ph.com.lbpsc.production.masterobject.MasterObject;
import ph.com.lbpsc.production.user.model.User;

public class RemarksReference extends MasterObject {
	Integer primaryKey;
	String remarks;
//	User user;
//	String changedByUser;
//	String changedInComputer;
//	Date changedOnDate;
	
	public RemarksReference(Integer primaryKey, String remarks, User user, String changedByUser,
			String changedInComputer, Date changedOnDate) {
		super();
		this.primaryKey = primaryKey;
		this.remarks = remarks;

		this.user = user;
		this.changedByUser = changedByUser;
		this.changedInComputer = changedInComputer;
		this.changedOnDate = changedOnDate;
	}

	public RemarksReference() {
		super();
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	@Override
	public String toString() {
		return "RemarksReference [primaryKey=" + primaryKey + ", remarks=" + remarks + ", user=" + user
				+ ", changedByUser=" + changedByUser + ", changedInComputer=" + changedInComputer + ", changedOnDate="
				+ changedOnDate + "]";
	}
	
}
