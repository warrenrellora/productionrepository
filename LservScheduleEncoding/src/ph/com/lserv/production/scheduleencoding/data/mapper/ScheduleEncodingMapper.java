package ph.com.lserv.production.scheduleencoding.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lserv.production.scheduleencoding.data.IDAO;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;

public interface ScheduleEncodingMapper extends IDAO<ScheduleEncoding> {

	ScheduleEncoding getSchedBySchedNameAndDay(String schedName, String day);

	List<ScheduleEncoding> getAllDistinctScheduleEncodingByClientName(String clientName);

	List<ScheduleEncoding> getAllScheduleEncodingByEncodingName(String scheduleName);

	int createMultipleData(@Param("scheduleEncodingToSave") List<ScheduleEncoding> scheduleEncodingToSave);

	int updateMultipleData(@Param("scheduleEncodingToSave") List<ScheduleEncoding> scheduleEncodingToSave);

	int deleteDataByClientNameAndSchedName(String clientName, String schedName);

	public int deleteDataByClientNameAndSchedNameAndDay(String schedName, String schedDay, String clientName);

	int deleteDataByPrikeyReferenceClient(Integer prikeyReferenceClient);

	List<ScheduleEncoding> getAllScheduleEncodingByClientName(String clientName);

	List<ScheduleEncoding> getAllScheduleEncodingByClientNameAndScheduleName(String clientName, String scheduleName);

	ScheduleEncoding getScheduleEncodingByClientNameAndScheduleNameAndScheduleDay(String clientName,
			String scheduleName, String scheduleDay);

	List<ScheduleEncoding> getAllScheduleByClientCodeDistinct(@Param("client") Client client);

	List<ScheduleEncoding> getAllScheduleByClientCode(@Param("client") Client client);

	List<ScheduleEncoding> getAllScheduleByPrikeyReferenceClient(Integer prikeyReferenceClient);

	ScheduleEncoding getAllScheduleByPrikeyReferenceClientAndScheduleDay(Integer prikeyReferenceClient,
			String schedDay);

	List<ScheduleEncoding> getAllScheduleByEmployeeID(Integer employeeID);

	int deleteDataByPrikeyReferenceClientAndSchedDay(Integer prikeyReferenceClient, String schedDay);

	public List<ScheduleEncoding> getAllEmployeeScheduleByEmpId(Integer employeeID);

	public List<ScheduleEncoding> getAllScheduleUploadedByEmployeeId(Integer employeeID);
	
	public ScheduleEncoding getScheduleImportedByEmpIdAndDay(Integer employeeID, String dayOfDate);
}
