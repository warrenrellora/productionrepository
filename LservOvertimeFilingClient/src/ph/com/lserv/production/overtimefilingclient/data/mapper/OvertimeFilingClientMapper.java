package ph.com.lserv.production.overtimefilingclient.data.mapper;

import java.util.Date;
import java.util.List;

import ph.com.lserv.production.overtimefilingclient.data.IDAO;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;

public interface OvertimeFilingClientMapper extends IDAO<OvertimeFilingClient> {
	public List<OvertimeFilingClient> getDataByEmpIdAndOvertimeDateFrom(int employeeCode, Date overtimeDateStart);
	
	public List<OvertimeFilingClient> getDataByEmpIdAndOvertimeDateTo(int employeeCode, Date overtimeDateEnd);
}
