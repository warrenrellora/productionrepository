package ph.com.lserv.production.holidayencodingtrail.model;

import ph.com.lbpsc.production.recordprocedure.model.RecordProcedure;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;

public class HolidayEncodingTrail extends HolidayEncoding {
	Integer primaryKeyTrail;
	RecordProcedure recordProcedure;
	
	public HolidayEncodingTrail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HolidayEncodingTrail(Integer primaryKeyTrail, RecordProcedure recordProcedure) {
		super();
		this.primaryKeyTrail = primaryKeyTrail;
		this.recordProcedure = recordProcedure;
	}

	public Integer getPrimaryKeyTrail() {
		return primaryKeyTrail;
	}

	public void setPrimaryKeyTrail(Integer primaryKeyTrail) {
		this.primaryKeyTrail = primaryKeyTrail;
	}

	public RecordProcedure getRecordProcedure() {
		return recordProcedure;
	}

	public void setRecordProcedure(RecordProcedure recordProcedure) {
		this.recordProcedure = recordProcedure;
	}

	@Override
	public String toString() {
		return "HolidayEncodingTrail [primaryKeyTrail=" + primaryKeyTrail + ", recordProcedure=" + recordProcedure
				+ "]";
	}
	
}
