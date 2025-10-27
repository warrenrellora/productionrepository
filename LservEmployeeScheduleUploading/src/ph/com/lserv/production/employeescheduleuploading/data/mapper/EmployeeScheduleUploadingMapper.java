package ph.com.lserv.production.employeescheduleuploading.data.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lserv.production.employeescheduleuploading.data.IDAO;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;

public interface EmployeeScheduleUploadingMapper extends IDAO<EmployeeScheduleUploading> {

	public int createMultipleData(@Param("biometricsToSaveList") List<EmployeeScheduleUploading> biometricsToSaveList);

	public int updateMultipleData(
			@Param("biometricsToUpdateList") List<EmployeeScheduleUploading> biometricsToUpdateList);

	public int deleteMultipleData(
			@Param("biometricsToDeleteList") List<EmployeeScheduleUploading> biometricsToDeleteList);

	public int updateDataByPayFromPayTo(Date payFrom, Date payTo);

	public List<Integer> getAllEmployeeIdByPayFromPayTo(Date payFrom, Date payTo);

	public List<Integer> getAllEmployeeIdByClientCodePayFromPayTo(String clientCode, Date payFrom, Date payTo);

	public List<Integer> getAllEmployeeIdByClientCodeDepartmentPayFromPayTo(String clientCode, Integer departmentNo,
			Date payFrom, Date payTo);

	public List<EmployeeScheduleUploading> getDataByPayFromPayTo(Date payFrom, Date payTo);

	public List<EmployeeScheduleUploading> getDataByClientPayFromPayTo(@Param("client") Client client, Date payFrom,
			Date payTo);

	public List<EmployeeScheduleUploading> getDataByClientPayFromPayToAll(@Param("client") Client client, Date payFrom,
			Date payTo);

	public List<EmployeeScheduleUploading> getDataByDateEntryEmployeePayFromPayTo(@Param("dateEntry") String dateEntry,
			@Param("employeeCode") Integer employeeCode, Date payFrom, Date payTo);

	public List<EmployeeScheduleUploading> getDataByClientDepartmentPayFromPayTo(@Param("client") Client client,
			@Param("department") Department department, Date payFrom, Date payTo, Integer isValidated);

	public List<EmployeeScheduleUploading> getDataByEmployeeIdPayFrom(@Param("employeeCode") Integer employeeCode,
			Date payFrom);

	public List<EmployeeScheduleUploading> getAllScheduleEntriesByPayFromPayTo(Date payFrom, Date payTo,
			Integer isRegularSchedule);

	public List<EmployeeScheduleUploading> getAllScheduleWithoutEntriesByPayFromPayTo(Date payFrom, Date payTo);

	public EmployeeScheduleUploading getDataByEmployeeIdAndDateEntry(Integer employeeID, Date dateEntry);

	public List<Integer> getAllValidatedEmployeeIdByPayFromPayTo(Date payFrom, Date payTo);

	public Integer countFrequencyOfLateByEmpIdPayFrom(Integer employeeID, Date payFrom);

	public Integer countTotalAbsentByEmpIdPayFrom(Integer employeeID, Date payFrom);

	public Integer computeTotalLateByEmpIdPayFrom(Integer employeeID, Date payFrom);

	public List<EmployeeScheduleUploading> getAllEntriesWithLateByEmpIdPayFrom(Integer employeeID, Date payFrom);

	public Integer countWorkingDaysByEmpIdPayFromPayTo(Integer employeeID, Date payFrom, Date payTo);

	public Integer countHolidaysByEmpIdPayFrom(Integer employeeID, Date payFrom);

	public EmployeeScheduleUploading getDataByEmpIdDateEntryPayFrom(Integer employeeCode, Date dateEntry, Date payFrom);

	public List<String> getAllClientCdByPayFrom(Date payFrom);

	public List<Integer> getAllEmpIdExcludingSelectedDeptPay(Integer deptNo, String clientCode, Date payFrom);

}
