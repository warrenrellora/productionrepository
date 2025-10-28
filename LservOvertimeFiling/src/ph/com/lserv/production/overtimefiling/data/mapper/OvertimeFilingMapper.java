package ph.com.lserv.production.overtimefiling.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lserv.production.overtimefiling.data.IDAO;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;

public interface OvertimeFilingMapper extends IDAO<OvertimeFiling>{
	public List<OvertimeFiling> getUserData(@Param("prikeyUser") int id);
	
	public OvertimeFiling getMaxFiledOvertime();
	
	public List<OvertimeFiling> getAllOvertimeSortedAscending();
	
	public List<OvertimeFiling> getOvertimeByCutoffDate(String dateTimeFrom, String dateTimeTo);
	
	public List<OvertimeFiling> getAllOvertimeByOvertimeFromAndPrikeyUser(String dateFrom, Integer prikeyUser);
	
	public List<OvertimeFiling> getAllOvertimeByOvertimeToAndPrikeyUser(String dateTo, Integer prikeyUser);
}
