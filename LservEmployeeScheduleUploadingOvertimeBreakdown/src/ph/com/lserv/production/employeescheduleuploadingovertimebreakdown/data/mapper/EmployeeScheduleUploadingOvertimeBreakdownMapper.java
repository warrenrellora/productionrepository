package ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.data.mapper;

import java.util.Date;
import java.util.List;

import ph.com.lserv.production.employeescheduleuploading.data.IDAO;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;

public interface EmployeeScheduleUploadingOvertimeBreakdownMapper
		extends IDAO<EmployeeScheduleUploadingOvertimeBreakdown> {

	public List<EmployeeScheduleUploadingOvertimeBreakdown> getDataByPrikeyEmployeeScheduleUploading(
			Integer employeeScheduleUploadingPrikey);

	public List<Integer> getAllOvertimeTypeDistinctByPayFromPayToClientCd(Date payFrom, Date payTo, String clientCode);

	public List<Integer> getAllPrikeyUploadingDistinctByPayFromPayTo(Date payFrom, Date payTo);

	public List<EmployeeScheduleUploadingOvertimeBreakdown> getAllDataByEmployeeCodePayFromPayTo(Integer employeeCode,
			Date payFrom, Date payTo);
}
