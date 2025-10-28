package ph.com.lserv.production.employeescheduleencoding.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lserv.production.employeescheduleencoding.data.IDAO;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;

public interface EmployeeScheduleEncodingMapper extends IDAO<EmployeeScheduleEncoding>{
	
	int createMultipleData(@Param("selectedEmployeesToBeSaveList") List<EmployeeScheduleEncoding> selectedEmployeesToBeSaveList);
	
	public EmployeeScheduleEncoding getEmployeeScheduleByEmployeeID(Integer employeeID);
	
	public List<EmployeeScheduleEncoding> getAllEmployeeScheduleByPrikeyReferenceClient(Integer prikeyReferenceClient);
	
	public List<EmployeeScheduleEncoding> getAllEmployeeScheduleByEmpId(Integer employeeID);
}
