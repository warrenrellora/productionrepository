package ph.com.lserv.production.employeescheduleencodingirregular.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lserv.production.employeescheduleencodingirregular.data.IDAO;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;

public interface EmployeeScheduleEncodingIrregularMapper extends IDAO<EmployeeScheduleEncodingIrregular> {

	int createMultipleData(
			@Param("createdObjectsToBeSavedList") List<EmployeeScheduleEncodingIrregular> createdObjectsToBeSavedList);

	List<EmployeeScheduleEncodingIrregular> getAllEmployeeIrregularScheduleByEmployeeID(Integer employeeID);

	EmployeeScheduleEncodingIrregular getEmployeeIrregularScheduleByPrikey(Integer prikey);

	EmployeeScheduleEncodingIrregular getEmployeeIrregularScheduleByDateAndEmployeeID(
			@Param("dateSchedule") String dateSchedule, Integer employeeID);

}
