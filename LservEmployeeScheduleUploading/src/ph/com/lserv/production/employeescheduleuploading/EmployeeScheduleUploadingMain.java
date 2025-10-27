package ph.com.lserv.production.employeescheduleuploading;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ph.com.lbpsc.production.client.ClientMain;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.department.DepartmentMain;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.EmployeeMain;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.EmploymentHistoryMain;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.miscellaneousadjustmentdetails.MiscellaneousAdjustmentDetailsMain;
import ph.com.lbpsc.production.overtimetype.OvertimeTypeMain;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.payroll.PayrollMain;
import ph.com.lbpsc.production.payrollencode.PayrollEncodeMain;
import ph.com.lbpsc.production.payrollstatusdetails.PayrollStatusDetailsMain;
import ph.com.lbpsc.production.position.PositionMain;
import ph.com.lbpsc.production.user.UserMain;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lserv.production.employeescheduleencoding.EmployeeScheduleEncodingMain;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;
import ph.com.lserv.production.employeescheduleencodingirregular.EmployeeScheduleEncodingIrregularMain;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;
import ph.com.lserv.production.employeescheduleencodingovertime.EmployeeScheduleEncodingOvertimeMain;
import ph.com.lserv.production.employeescheduleuploading.data.EmployeeScheduleUploadingDao;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.employeescheduleuploading.report.NoOvertimeConfigEmployeesReport;
import ph.com.lserv.production.employeescheduleuploading.report.NoScheduleEmployeesReport;
import ph.com.lserv.production.employeescheduleuploading.report.PayrollErrorSavingReport;
import ph.com.lserv.production.employeescheduleuploading.view.BrowseEmployeeScheduleUploadingController;
import ph.com.lserv.production.employeescheduleuploading.view.EditEmployeeScheduleUploadingController;
import ph.com.lserv.production.employeescheduleuploading.view.PrintEmployeeScheduleUploadingController;
import ph.com.lserv.production.employeescheduleuploading.view.SetEmployeeScheduleUploadingOvertimeController;
import ph.com.lserv.production.employeescheduleuploading.view.UploadEmployeeScheduleUploadingController;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.EmployeeScheduleUploadingOvertimeBreakdownMain;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;
import ph.com.lserv.production.holidayencoding.HolidayEncodingMain;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;
import ph.com.lserv.production.leavemonitoring.LeaveMonitoringMain;
import ph.com.lserv.production.leavemonitoring.model.LeaveMonitoring;
import ph.com.lserv.production.overtimefiling.OvertimeFilingMain;
import ph.com.lserv.production.overtimefilingclient.OvertimeFilingClientMain;
import ph.com.lserv.production.remarksreference.RemarksReferenceMain;
import ph.com.lserv.production.remarksreference.model.RemarksReference;
import ph.com.lserv.production.scheduleencoding.ScheduleEncodingMain;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;

public class EmployeeScheduleUploadingMain extends MasterMain<EmployeeScheduleUploading> {
	private EmploymentHistoryMain employmentHistoryMain;
	private EmployeeMain employeeMain;
	private ClientMain clientMain;
	private ScheduleEncodingMain scheduleEncodingMain;
	private EmployeeScheduleEncodingMain employeeScheduleEncodingMain;
	private EmployeeScheduleEncodingIrregularMain employeeScheduleEncodingIrregularMain;
	private EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain;
	private OvertimeFilingMain overtimeFilingMain;
	private OvertimeFilingClientMain overtimeFilingClientMain;
	private DepartmentMain departmentMain;
	private UserMain userMain;
	private HolidayEncodingMain holidayEncodingMain;
	private OvertimeTypeMain overtimeTypeMain;
	private LeaveMonitoringMain leaveMonitoringMain;
	private EmployeeScheduleUploadingOvertimeBreakdownMain employeeScheduleUploadingOvertimeBreakdownMain;
	private RemarksReferenceMain remarksReferenceMain;
	private PayrollEncodeMain payrollEncodeMain;
	private PayrollMain payrollMain;
	private PayrollStatusDetailsMain payrollStatusDetailsMain;
	private MiscellaneousAdjustmentDetailsMain miscellaneousAdjustmentDetailsMain;
	private PositionMain positionMain;

	private List<EmployeeScheduleUploading> listFinalizeBiometricsComplete;
	private List<EmployeeScheduleUploading> listFinalizeBiometricsOnlyBio;
	private List<EmployeeScheduleUploading> employeeScheduleUploadingList;
	private List<Employee> employeeList;
	private ObservableList<Employee> employeeObservableList;
	private ObservableList<Employee> rawEmployeeObservableList;
	private ObservableList<Client> clientObservableList;
	private List<Integer> listEmpIdNoSched;
	private List<EmployeeScheduleUploading> entryToBeRemoveList;
	private List<OvertimeType> overtimeHeaderList;

	private Date payPeriodDateFrom, payPeriodDateTo, cutoffDateFrom, cutoffDateTo;
	private Employee selectedEmployee;
	private Client selectedClient;
	private Department selectedDepartment;
	private String payPeriod;

	private BigDecimal withPay, withoutPay;
	private HashMap<Integer, HashSet<String>> employeesWithoutOvertimeConfigHashMap;

	private ObservableList<EmployeeScheduleUploadingOvertimeBreakdown> obsListOTBreakdown = FXCollections
			.observableArrayList();
	private ObservableList<OvertimeType> overtimeTypeByClientObsList = FXCollections.observableArrayList();

	private EmployeeScheduleUploadingOvertimeBreakdown selectedOvertimeBreakdown = new EmployeeScheduleUploadingOvertimeBreakdown();
	private Integer totalMin = 0;
	boolean isIntegralEmployee = true;

	private ObservableList<Client> obsListClient;
	private ObservableList<Department> obsListDepartment;
	private ObservableList<Integer> obsListEmpID;
	private ObservableList<Employee> obsListEmployee;

	ObservableList<Map.Entry<Employee, Boolean>> observableListHashMapEmployees;
	List<Integer> clientPrikeyCustomLateUndertimeList;
	HashMap<EmploymentHistory, String> employeeErrorSavingPayrollHashMap = new HashMap<>();
	List<Integer> clientPrikeyLunchEntrySameConfigList;

	public EmployeeScheduleUploadingMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(EmployeeScheduleUploading.class);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public boolean createMainObject(EmployeeScheduleUploading employeeScheduleUploading) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).createData(employeeScheduleUploading) > 0;
	}

	@Override
	public boolean updateMainObject(EmployeeScheduleUploading employeeScheduleUploading) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).updateData(employeeScheduleUploading) > 0;
	}

	@Override
	public boolean deleteMainObject(EmployeeScheduleUploading employeeScheduleUploading) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).deleteData(employeeScheduleUploading) > 0;
	}

	public boolean createMultipleData(List<EmployeeScheduleUploading> biometricsToSaveList) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).createMultipleData(biometricsToSaveList) > 0;
	}

	public boolean updateMultipleData(List<EmployeeScheduleUploading> biometricsToUpdateList) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).updateMultipleData(biometricsToUpdateList) > 0;
	}

	public boolean deleteMultipleData(List<EmployeeScheduleUploading> biometricsToDeleteList) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).deleteMultipleData(biometricsToDeleteList) > 0;
	}

	public boolean updateDataByPayFromPayTo(Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).updateDataByPayFromPayTo(payFrom, payTo) > 0;
	}

	public EmployeeScheduleUploading getDataById(int id) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataById(id);
	}

	public EmployeeScheduleUploading getDataByEmployeeIdAndDateEntry(Integer employeeID, Date dateEntry) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByEmployeeIdAndDateEntry(employeeID,
				dateEntry);
	}

	public List<EmployeeScheduleUploading> getAllData() {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllData();
	}

	public List<EmployeeScheduleUploading> getDataByPayFromPayTo(Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByPayFromPayTo(payFrom, payTo);
	}

	public List<EmployeeScheduleUploading> getDataByClientPayFromPayTo(Client client, Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByClientPayFromPayTo(client, payFrom, payTo);
	}

	public List<EmployeeScheduleUploading> getDataByClientPayFromPayToAll(Client client, Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByClientPayFromPayToAll(client, payFrom,
				payTo);
	}

	public List<EmployeeScheduleUploading> getAllScheduleEntriesByPayFromPayTo(Date payFrom, Date payTo,
			Integer isRegularSchedule) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllScheduleEntriesByPayFromPayTo(payFrom, payTo,
				isRegularSchedule);
	}

	public List<EmployeeScheduleUploading> getAllScheduleWithoutEntriesByPayFromPayTo(Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllScheduleWithoutEntriesByPayFromPayTo(payFrom,
				payTo);
	}

	public List<EmployeeScheduleUploading> getDataByClientDepartmentPayFromPayTo(Client client, Department department,
			Date payFrom, Date payTo, Integer isValidated) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByClientDepartmentPayFromPayTo(client,
				department, payFrom, payTo, isValidated);
	}

	public List<EmployeeScheduleUploading> getDataByEmployeeIdPayFrom(Integer employeeCode, Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByEmployeeIdPayFrom(employeeCode, payFrom);
	}

	public List<EmployeeScheduleUploading> getDataByDateEntryEmployeePayFromPayTo(String dateEntry,
			Integer employeeCode, Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByDateEntryEmployeePayFromPayTo(dateEntry,
				employeeCode, payFrom, payTo);
	}

	public List<Integer> getAllEmployeeIdByPayFromPayTo(Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllEmployeeIdByPayFromPayTo(payFrom, payTo);
	}

	public List<Integer> getAllEmployeeIdByClientCodePayFromPayTo(String clientCode, Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllEmployeeIdByClientCodePayFromPayTo(clientCode,
				payFrom, payTo);
	}

	public List<Integer> getAllEmployeeIdByClientCodeDepartmentPayFromPayTo(String clientCode, Integer departmentNo,
			Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory)
				.getAllEmployeeIdByClientCodeDepartmentPayFromPayTo(clientCode, departmentNo, payFrom, payTo);
	}

	public List<Integer> getAllValidatedEmployeeIdByPayFromPayTo(Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllValidatedEmployeeIdByPayFromPayTo(payFrom,
				payTo);
	}

	public List<Integer> getAllEmpIdExcludingSelectedDeptPay(Integer deptNo, String clientCode, Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllEmpIdExcludingSelectedDeptPay(deptNo,
				clientCode, payFrom);
	}

	public Integer countFrequencyOfLateByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).countFrequencyOfLateByEmpIdPayFrom(employeeID,
				payFrom);
	};

	public Integer countTotalAbsentByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).countTotalAbsentByEmpIdPayFrom(employeeID, payFrom);
	};

	public Integer computeTotalLateByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).computeTotalLateByEmpIdPayFrom(employeeID, payFrom);
	};

	public List<EmployeeScheduleUploading> getAllEntriesWithLateByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllEntriesWithLateByEmpIdPayFrom(employeeID,
				payFrom);
	};

	public Integer countWorkingDaysByEmpIdPayFromPayTo(Integer employeeID, Date payFrom, Date payTo) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).countWorkingDaysByEmpIdPayFromPayTo(employeeID,
				payFrom, payTo);
	};

	public Integer countHolidaysByEmpIdPayFrom(Integer employeeID, Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).countHolidaysByEmpIdPayFrom(employeeID, payFrom);
	}

	public EmployeeScheduleUploading getDataByEmpIdDateEntryPayFrom(Integer employeeCode, Date dateEntry,
			Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getDataByEmpIdDateEntryPayFrom(employeeCode,
				dateEntry, payFrom);
	}

	public List<String> getAllClientCdByPayFrom(Date payFrom) {
		return new EmployeeScheduleUploadingDao(sqlSessionFactory).getAllClientCdByPayFrom(payFrom);
	}

	public AnchorPane showBrowseEmployeeScheduleUploading() throws IOException {
		this.initializeObjects();
		FxmlUtil<BrowseEmployeeScheduleUploadingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseEmployeeScheduleUploading.fxml"));
		AnchorPane browseLayout = fxmlUtil.getFxmlPane();
		BrowseEmployeeScheduleUploadingController controller = fxmlUtil.getController();
		controller.setMainApplication(this);
		this.primaryStage.setTitle("Employee Schedule Uploading");
		// this.primaryStage.setResizable(false);
		this.primaryStage.setMaximized(true);
		return browseLayout;
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.showBrowseEmployeeScheduleUploading();
	}

	public void initializeClientCustomLateUndertimeList() {
		// clients with custom late undertime offset computation
		this.clientPrikeyCustomLateUndertimeList = new ArrayList<>();
		this.clientPrikeyCustomLateUndertimeList.add(323); // PHLSHSS
	}

	@Override
	public void initializeObjects() {
		this.initializeClientCustomLateUndertimeList();

		this.employeeList = new ArrayList<>();
		this.employeeScheduleUploadingList = new ArrayList<>();
		this.listEmpIdNoSched = new ArrayList<>();
		this.entryToBeRemoveList = new ArrayList<>();
		this.payPeriodDateFrom = new Date();
		this.payPeriodDateTo = new Date();
		this.cutoffDateFrom = new Date();
		this.cutoffDateTo = new Date();
		this.selectedEmployee = new Employee();
		this.employeeObservableList = FXCollections.observableArrayList();
		this.clientObservableList = FXCollections.observableArrayList();
		this.rawEmployeeObservableList = FXCollections.observableArrayList();
		this.listFinalizeBiometricsComplete = new ArrayList<>();
		this.listFinalizeBiometricsOnlyBio = new ArrayList<>();
		this.overtimeHeaderList = new ArrayList<>();
		this.payPeriod = "";
		this.employeesWithoutOvertimeConfigHashMap = new HashMap<>();
		this.obsListClient = FXCollections.observableArrayList();
		this.obsListDepartment = FXCollections.observableArrayList();
		this.obsListEmpID = FXCollections.observableArrayList();
		this.obsListEmployee = FXCollections.observableArrayList();
		this.observableListHashMapEmployees = FXCollections.observableArrayList();
		this.clientPrikeyLunchEntrySameConfigList = new ArrayList<>();
		this.setClientPrikeyLunchEntryCanBeSameList();
		try {
			this.userMain = new UserMain();
			this.departmentMain = new DepartmentMain();
			this.employmentHistoryMain = new EmploymentHistoryMain();
			this.employeeMain = new EmployeeMain();
			this.clientMain = new ClientMain();
			this.employeeScheduleEncodingMain = new EmployeeScheduleEncodingMain();
			this.scheduleEncodingMain = new ScheduleEncodingMain();
			this.employeeScheduleEncodingIrregularMain = new EmployeeScheduleEncodingIrregularMain();
			this.employeeScheduleEncodingOvertimeMain = new EmployeeScheduleEncodingOvertimeMain();
			this.overtimeFilingMain = new OvertimeFilingMain();
			this.overtimeFilingClientMain = new OvertimeFilingClientMain();
			this.holidayEncodingMain = new HolidayEncodingMain();
			this.overtimeTypeMain = new OvertimeTypeMain();
			this.leaveMonitoringMain = new LeaveMonitoringMain();
			this.employeeScheduleUploadingOvertimeBreakdownMain = new EmployeeScheduleUploadingOvertimeBreakdownMain();
			this.getEmployeeScheduleUploadingOvertimeBreakdownMain().initializeCustomRules();
			this.remarksReferenceMain = new RemarksReferenceMain();
			this.payrollEncodeMain = new PayrollEncodeMain();
			this.getPayrollEncodeMain().initializeObjects();
			this.leaveMonitoringMain = new LeaveMonitoringMain();
			this.payrollMain = new PayrollMain();
			this.payrollStatusDetailsMain = new PayrollStatusDetailsMain();
			this.getPayrollStatusDetailsMain().initializeObjects();
			this.miscellaneousAdjustmentDetailsMain = new MiscellaneousAdjustmentDetailsMain();
			this.getMiscellaneousAdjustmentDetailsMain().initializeObjects();
			this.positionMain = new PositionMain();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void setClientPrikeyLunchEntryCanBeSameList() {
		this.clientPrikeyLunchEntrySameConfigList.add(189); // PDIC
		this.clientPrikeyLunchEntrySameConfigList.add(16); // bsp
		this.clientPrikeyLunchEntrySameConfigList.add(315); // bsp
		this.clientPrikeyLunchEntrySameConfigList.add(387); // bsp
	}

	public Time timestampToTime(Timestamp timestamp) {

		LocalTime localTime = timestamp.toLocalDateTime().toLocalTime();
		Time time = Time.valueOf(localTime);

		return time;
	}

	public void computeUndertime(EmployeeScheduleUploading employeeScheduleUploading) {
		this.initializeClientCustomLateUndertimeList();

		Date dateEntry = employeeScheduleUploading.getDateEntry();
		Timestamp timeInSchedule = new Timestamp(new Date().getTime());
		Timestamp timeOutSchedule = new Timestamp(new Date().getTime());
		Timestamp lunchInSchedule = new Timestamp(new Date().getTime());
		Timestamp lunchOutSchedule = new Timestamp(new Date().getTime());
		timeInSchedule = null;
		timeOutSchedule = null;
		lunchInSchedule = null;
		lunchOutSchedule = null;

		// if (employeeScheduleUploading.getTimeInEntry() != null &&
		// employeeScheduleUploading.getTimeOutEntry() != null) {

		Timestamp timeInEntry = employeeScheduleUploading.getTimeInEntry();
		Timestamp timeOutEntry = employeeScheduleUploading.getTimeOutEntry();

		List<LeaveMonitoring> leaveMonitoringList = new ArrayList<>();
		leaveMonitoringList = this.getLeaveMonitoringMain().getAllApprovedDataByEmployeeCode(
				employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode());

		Boolean isDateWithLeave = leaveMonitoringList.stream()
				.filter(p -> p.getDateFrom().equals(employeeScheduleUploading.getDateEntry())).findFirst().isPresent();

		if (isDateWithLeave) {
			employeeScheduleUploading.setUndertime(null);
			return;
		}

		if (employeeScheduleUploading.getIsRegularSchedule() != null
				&& employeeScheduleUploading.getIsRegularSchedule() == 1) {

			if (employeeScheduleUploading.getScheduleEncoding() != null) {
				timeInSchedule = this.entryToTimestampConverter(dateEntry, Time.valueOf(new SimpleDateFormat("HH:mm:ss")
						.format(employeeScheduleUploading.getScheduleEncoding().getTimeIn())));
				timeOutSchedule = this.entryToTimestampConverter(dateEntry,
						Time.valueOf(new SimpleDateFormat("HH:mm:ss")
								.format(employeeScheduleUploading.getScheduleEncoding().getTimeOut())));

				if (employeeScheduleUploading.getScheduleEncoding().getLunchOut() != null
						&& employeeScheduleUploading.getScheduleEncoding().getLunchIn() != null) {
					lunchInSchedule = this.entryToTimestampConverter(dateEntry,
							Time.valueOf(new SimpleDateFormat("HH:mm:ss")
									.format(employeeScheduleUploading.getScheduleEncoding().getLunchIn())));
					lunchOutSchedule = this.entryToTimestampConverter(dateEntry,
							Time.valueOf(new SimpleDateFormat("HH:mm:ss")
									.format(employeeScheduleUploading.getScheduleEncoding().getLunchOut())));
				}

				Integer offsetAllowed = 0;
				offsetAllowed = employeeScheduleUploading.getScheduleEncoding().getOffsetAllowed();

				if (timeInEntry == null) {
					timeInEntry = employeeScheduleUploading.getLunchOutEntry() != null
							? employeeScheduleUploading.getLunchOutEntry()
							: employeeScheduleUploading.getLunchInEntry();
				}

				if (timeOutEntry == null) {
					timeOutEntry = employeeScheduleUploading.getLunchInEntry() != null
							? employeeScheduleUploading.getLunchInEntry()
							: employeeScheduleUploading.getLunchOutEntry();
				}

				Integer totalUndertimeInMin = this.startComputeUndertime(employeeScheduleUploading, timeInSchedule,
						timeOutSchedule, lunchInSchedule, lunchOutSchedule, timeInEntry, timeOutEntry, offsetAllowed);

				if (totalUndertimeInMin != 0) {
					employeeScheduleUploading.setUndertime(totalUndertimeInMin);
				} else {
					employeeScheduleUploading.setUndertime(null);
				}
			}
		} else {
			if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
				timeInSchedule = this.entryToTimestampConverter(dateEntry,
						employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeIn());
				timeOutSchedule = this.entryToTimestampConverter(dateEntry,
						employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeOut());

				if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getLunchOut() != null
						&& employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getLunchIn() != null) {
					lunchInSchedule = this.entryToTimestampConverter(dateEntry,
							employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getLunchIn());
					lunchOutSchedule = this.entryToTimestampConverter(dateEntry,
							employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getLunchOut());
				}

				Integer offsetAllowed = 0;
				offsetAllowed = employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getOffsetAllowed();

				if (timeInEntry == null) {
					timeInEntry = employeeScheduleUploading.getLunchOutEntry() != null
							? employeeScheduleUploading.getLunchOutEntry()
							: employeeScheduleUploading.getLunchInEntry();
				}

				if (timeOutEntry == null) {
					timeOutEntry = employeeScheduleUploading.getLunchInEntry() != null
							? employeeScheduleUploading.getLunchInEntry()
							: employeeScheduleUploading.getLunchOutEntry();
				}

				Integer totalUndertimeInMin = this.startComputeUndertime(employeeScheduleUploading, timeInSchedule,
						timeOutSchedule, timeInEntry, lunchInSchedule, lunchOutSchedule, timeOutEntry, offsetAllowed);

				if (totalUndertimeInMin != 0) {
					employeeScheduleUploading.setUndertime(totalUndertimeInMin);
				} else {
					employeeScheduleUploading.setUndertime(null);
				}
			}
		}
		// }

	}

	public void computeOvertimeBreakdown(Integer empId, Date payFrom, Date payTo) {
		this.employeesWithoutOvertimeConfigHashMap.putAll(this.getEmployeeScheduleUploadingOvertimeBreakdownMain()
				.breakdownOvertime(empId, payFrom, payTo, this.getUser()));
	}

	public Timestamp entryToTimestampConverter(Date dateEntry, Time timeEntry) {
		Timestamp result = null;

		if (dateEntry != null && timeEntry != null) {
			String StringInput = new SimpleDateFormat("yyyy-MM-dd").format(dateEntry)
					.concat(" ".concat(new SimpleDateFormat("HH:mm:ss").format(timeEntry)));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime localDateTimeInput = LocalDateTime.from(formatter.parse(StringInput));
			result = Timestamp.valueOf(localDateTimeInput);
		}

		return result;
	}

	public String[] getWorkDayAndDayOff(Integer employeeCode) {
		String scheduleTime = "";
		String dayOff = "";
		String workDay = "";
		String[] result = new String[3];
		result[1] = "";
		result[0] = "";
		result[2] = "";

		String[] dayOfWeekNames = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

		List<String> dayOfWeekList = new ArrayList<>(Arrays.asList(dayOfWeekNames));
		List<String> dayOfWeekListCopy = new ArrayList<>();
		List<String> workDayList = new ArrayList<>();
		dayOfWeekListCopy.addAll(dayOfWeekList);

		List<ScheduleEncoding> scheduleEncodingList = this.getScheduleEncodingMain()
				.getAllEmployeeScheduleByEmpId(employeeCode);
		String timeFormat = "HH:mm";

		if (scheduleEncodingList.isEmpty()) {
			scheduleEncodingList
					.addAll(this.getScheduleEncodingMain().getAllScheduleUploadedByEmployeeId(employeeCode));
		}

		if (scheduleEncodingList != null && !scheduleEncodingList.isEmpty()) {
			scheduleTime = new SimpleDateFormat(timeFormat).format(scheduleEncodingList.get(0).getTimeIn()) + "-"
					+ new SimpleDateFormat(timeFormat).format(scheduleEncodingList.get(0).getTimeOut());

			workDayList.addAll(scheduleEncodingList.stream().map(p -> p.getScheduleDay()).collect(Collectors.toList()));

			dayOfWeekListCopy.removeAll(workDayList);
			int dayOffCount = dayOfWeekListCopy.size();

			switch (dayOffCount) {
			case 1:
				dayOff = dayOfWeekListCopy.get(0).toUpperCase();
				break;
			case 2:
				dayOff = dayOfWeekListCopy.get(0).toUpperCase() + "-"
						+ dayOfWeekListCopy.get(dayOfWeekListCopy.size() - 1).toUpperCase();
				break;
			default:
				int loop = 0;
				for (String dayLeft : dayOfWeekListCopy) {
					if (loop == 0) {
						workDay = dayLeft.toUpperCase().substring(0, 3);
					} else {
						dayOff = dayOff.toUpperCase().substring(0, 3) + "-" + dayLeft.toUpperCase().substring(0, 3);
					}
					loop++;
				}
				break;
			}
		}

		int workDayCount = workDayList.size();
		switch (workDayCount) {
		case 1:
			workDay = workDayList.get(0).toUpperCase();
			break;
		case 2:
			workDay = workDayList.get(0).toUpperCase() + "-" + workDayList.get(workDayList.size() - 1).toUpperCase();
			break;
		default:
			int loop = 0;
			for (String dayLeft : workDayList) {
				if (loop == 0) {
					workDay = dayLeft.toUpperCase().substring(0, 3);
				} else {
					workDay = workDay.toUpperCase().substring(0, 3) + "-" + dayLeft.toUpperCase().substring(0, 3);
				}
				loop++;
			}
			break;
		}

		result[0] = scheduleTime;
		result[1] = dayOff;
		result[2] = workDay;
		return result;
	}

	public Integer startComputeUndertime(EmployeeScheduleUploading employeeScheduleUploading, Timestamp timeInSchedule,
			Timestamp timeOutSchedule, Timestamp lunchInSchedule, Timestamp lunchOutSchedule, Timestamp timeInEntry,
			Timestamp timeOutEntry, Integer offsetAllowed) {
		if (timeInEntry == null || timeOutEntry == null) {
			return 0;
		}

		Integer totalUndertimeInMin = 0;

		Integer late = 0;
		Integer undertime = 0;
		Integer lunchMins = 60; // default lunch duration in mins (12:00 pm - 01:00 pm [60 mins])

		if (!timeInEntry.equals(timeOutEntry) && timeInEntry.before(timeOutEntry)) {

			if (timeInEntry.after(timeInSchedule)) {
				late = this.computeTotalMinsEntry(timeInSchedule, timeInEntry);
				undertime = this.computeTotalMinsEntry(timeOutSchedule, timeOutEntry);

				if (late > offsetAllowed && undertime >= 0) {
					totalUndertimeInMin = late;
				} else {
					Integer offset = late - undertime;

					if (offset >= 0) {
						totalUndertimeInMin = offset;
					}
				}
			}

			if ((timeInEntry.before(timeInSchedule) || timeInEntry.equals(timeInSchedule))
					&& timeOutEntry.before(timeOutSchedule)) {

				totalUndertimeInMin = this.computeTotalMinsEntry(timeOutEntry, timeOutSchedule);

				if (totalUndertimeInMin == 300) {
					// if there's a lunch schedule, use that value instead of default lunchMins
					if (lunchInSchedule != null && lunchOutSchedule != null) {
						lunchMins = this.computeTotalMinsEntry(lunchInSchedule, lunchOutSchedule);
					}

					// half-day, exclude lunch out to lunch in mins
					totalUndertimeInMin = totalUndertimeInMin - lunchMins;
				}
			}
		}

		if (this.getClientPrikeyCustomLateUndertimeList()
				.contains(employeeScheduleUploading.getClient().getCompany().getCompanyCode())) {
			Integer renderedWorkTime = this.computeTotalMinsEntry(timeInEntry, timeOutEntry);
			if (renderedWorkTime >= 540) {
				totalUndertimeInMin = 0;
			}
		}

		return totalUndertimeInMin;
	}

	public Boolean checkDateNoLogs(EmployeeScheduleUploading employeeScheduleUploading, Date dateEntry) {
		Boolean isNoLog = false;

		boolean isLunchExempted = this.isLunchExempted(employeeScheduleUploading);

		if (isLunchExempted) {
			if (employeeScheduleUploading.getRemarksReference() == null) {
				if (employeeScheduleUploading.getTimeInEntry() == null
						|| employeeScheduleUploading.getTimeOutEntry() == null) {

					Integer prikeyRemarksNoLog = 12;
					RemarksReference remarksReference = new RemarksReference();
					remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksNoLog);

					employeeScheduleUploading.setRemarksReference(remarksReference);
					isNoLog = true;
				} else {
					// for reuploading purposes
					if (employeeScheduleUploading.getRemarksReference() != null) {
						employeeScheduleUploading.setRemarksReference(null);
					}
					isNoLog = false;
				}
			}
		} else {
			if (employeeScheduleUploading.getRemarksReference() == null) {
				if (employeeScheduleUploading.getTimeInEntry() == null
						|| employeeScheduleUploading.getTimeOutEntry() == null
						|| employeeScheduleUploading.getLunchOutEntry() == null
						|| employeeScheduleUploading.getLunchInEntry() == null) {

					Integer prikeyRemarksNoLog = 12;
					RemarksReference remarksReference = new RemarksReference();
					remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksNoLog);

					employeeScheduleUploading.setRemarksReference(remarksReference);
					isNoLog = true;
				} else {
					// for reuploading purposes
					if (employeeScheduleUploading.getRemarksReference() != null) {
						employeeScheduleUploading.setRemarksReference(null);
					}
					isNoLog = false;
				}
			}
		}

		return isNoLog;
	}

	public List<ScheduleEncoding> getRegularSchedule(EmployeeScheduleUploading employeeScheduleUploading) {
		EmployeeScheduleEncoding employeeScheduleEncoding = this.getEmployeeScheduleEncodingMain()
				.getEmployeeScheduleByEmployeeID(
						employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode());

		return this.getScheduleEncodingMain().getAllScheduleByPrikeyReferenceClient(
				employeeScheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference());
	}

	public boolean isLunchExempted(EmployeeScheduleUploading employeeScheduleUploading) {
		boolean result = true;

		boolean isRegularSchedule = employeeScheduleUploading.getIsRegularSchedule() == 1 ? true : false;
		boolean isRestDay = employeeScheduleUploading.getIsRestDay() == 1 ? true : false;
		List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();
		// all regular schedule is not rest day
		if (isRegularSchedule) {

			scheduleEncodingList = this.getRegularSchedule(employeeScheduleUploading);

			if (scheduleEncodingList != null && !scheduleEncodingList.isEmpty()) {
				ScheduleEncoding scheduleEncoding = scheduleEncodingList.get(0);
				result = scheduleEncoding.getIsBreakExempted() == 1 ? true : false;
			}
		} else if (!isRegularSchedule && !isRestDay) {
			EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = employeeScheduleUploading
					.getEmployeeScheduleEncodingIrregular();

			result = employeeScheduleEncodingIrregular.getIsBreakExempted() == 1 ? true : false;
		} else {
			// filler entries -- dates without bio from employee
			// check both reg & irreg sched
			scheduleEncodingList = this.getRegularSchedule(employeeScheduleUploading);

			if (scheduleEncodingList != null && !scheduleEncodingList.isEmpty()) {
				ScheduleEncoding scheduleEncoding = scheduleEncodingList.get(0);
				result = scheduleEncoding.getIsBreakExempted() == 1 ? true : false;
			} else {
				EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = employeeScheduleUploading
						.getEmployeeScheduleEncodingIrregular();

				if (employeeScheduleEncodingIrregular != null) {
					result = employeeScheduleEncodingIrregular.getIsBreakExempted() == 1 ? true : false;
				}
			}
		}

		return result;
	}

	public boolean setRemarksEntry(EmployeeScheduleUploading employeeScheduleUploading, Date dateEntry) {
		Boolean isRestDay = false;
		Boolean isHoliday = false;

		this.checkDateRestDay(employeeScheduleUploading, dateEntry);

		if (employeeScheduleUploading.getIsRestDay() == null
				&& employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() == null) {
			// no sched both regular & irregular -- no remarks - null isRestDay and
			// isHoliday
			return false;
		}

		isRestDay = employeeScheduleUploading.getIsRestDay() == 0 ? false : true;
		if (isRestDay) {
			this.checkDateHoliday(employeeScheduleUploading, dateEntry);
			isHoliday = employeeScheduleUploading.getIsHoliday() == 0 ? false : true;
			if (!isHoliday) {
				this.checkDateLeaveFiling(employeeScheduleUploading);
				if (employeeScheduleUploading.getRemarksReference() == null) {
					// rest day no entry, still no remarks
					if (employeeScheduleUploading.getTimeInEntry() == null
							&& employeeScheduleUploading.getTimeOutEntry() == null
							&& employeeScheduleUploading.getLunchInEntry() == null
							&& employeeScheduleUploading.getLunchOutEntry() == null) {
						Integer prikeyRemarksDayOff = 2;
						RemarksReference remarksReference = new RemarksReference();
						remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksDayOff);

						employeeScheduleUploading.setRemarksReference(remarksReference);
					} else {
						this.checkDateNoLogs(employeeScheduleUploading, dateEntry);
					}

					// rest day with entry, still no remarks
					if (employeeScheduleUploading.getRemarksReference() == null) {
						// Integer prikeyRemarksDayOff = 2;
						// RemarksReference remarksReference = new RemarksReference();
						// remarksReference =
						// this.getRemarksReferenceMain().getDataById(prikeyRemarksDayOff);
						//
						// employeeScheduleUploading.setRemarksReference(remarksReference);
					}

				}
			}
		} else {
			this.checkDateHoliday(employeeScheduleUploading, dateEntry);
			isHoliday = employeeScheduleUploading.getIsHoliday() == 0 ? false : true;
			if (!isHoliday) {
				this.checkDateLeaveFiling(employeeScheduleUploading);
				if (employeeScheduleUploading.getRemarksReference() == null) {
					if (employeeScheduleUploading.getTimeInEntry() == null
							&& employeeScheduleUploading.getTimeOutEntry() == null
							&& employeeScheduleUploading.getLunchInEntry() == null
							&& employeeScheduleUploading.getLunchOutEntry() == null) {
						Integer prikeyRemarksAbsent = 1;
						RemarksReference remarksReference = new RemarksReference();
						remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksAbsent);

						employeeScheduleUploading.setRemarksReference(remarksReference);
					} else {
						this.checkDateNoLogs(employeeScheduleUploading, dateEntry);
					}
				}
			}
		}
		return true;
	}

	public void checkDateRestDay(EmployeeScheduleUploading employeeScheduleUploading, Date dateEntry) {
		String dayOfDate = employeeScheduleUploading.getDayOfDate();

		EmployeeScheduleEncoding employeeScheduleEncoding = new EmployeeScheduleEncoding();
		employeeScheduleEncoding = this.getEmployeeScheduleEncodingMain().getEmployeeScheduleByEmployeeID(
				employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode());

		List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();

		if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
			employeeScheduleUploading.setIsRestDay(0);
		} else {
			if (employeeScheduleEncoding != null) {
				scheduleEncodingList = this.getScheduleEncodingMain().getAllScheduleByPrikeyReferenceClient(
						employeeScheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference());

				Boolean isWorkingDay = scheduleEncodingList.stream().filter(p -> p.getScheduleDay().equals(dayOfDate))
						.findAny().isPresent();

				if (isWorkingDay) {
					employeeScheduleUploading.setIsRestDay(0);
				} else {
					employeeScheduleUploading.setIsRestDay(1);

					Integer prikeyRemarksDayOff = 2;
					RemarksReference remarksReference = new RemarksReference();
					remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksDayOff);

					employeeScheduleUploading.setRemarksReference(remarksReference);
				}
			} else {
				// no sched reg and irreg
			}
		}

	}

	public void checkDateHoliday(EmployeeScheduleUploading employeeScheduleUploading, Date dateEntry) {
		List<HolidayEncoding> holidayStandardList = new ArrayList<>();
		List<HolidayEncoding> holidayByClientList = new ArrayList<>();
		HashSet<HolidayEncoding> holidayFinalList = new HashSet<>();

		holidayByClientList.addAll(this.getHolidayEncodingMain()
				.getAllHolidayByClientName(employeeScheduleUploading.getClient().getClientCode()));

		holidayByClientList.forEach(holidayClient -> {
			Date holiday = holidayClient.getDate();
			if (holidayClient.getOverrideDate() != null) {
				holiday = holidayClient.getOverrideDate();
			}

			holidayClient.setDate(holiday);
		});
		holidayFinalList.addAll(holidayByClientList);

		holidayStandardList.addAll(this.getHolidayEncodingMain().getAllHolidayRegular());
		holidayStandardList.forEach(holidayStandard -> {
			Date holiday = holidayStandard.getDate();
			if (holidayStandard.getOverrideDate() != null) {
				holiday = holidayStandard.getOverrideDate();
			}
			holidayStandard.setDate(holiday);
		});

		List<String> holidayClientMMddList = holidayByClientList.stream()
				.map(p -> new SimpleDateFormat("MM-dd").format(p.getDate())).collect(Collectors.toList());

		for (HolidayEncoding holidayStandard : holidayStandardList) {
			Date holidayDateStandard = holidayStandard.getDate();

			if (holidayStandard.getOverrideDate() != null) {
				holidayDateStandard = holidayStandard.getOverrideDate();
			}

			String holidayDateMMddStandard = new SimpleDateFormat("MM-dd").format(holidayDateStandard);

			boolean isPresent = holidayClientMMddList.stream().filter(p -> p.compareTo(holidayDateMMddStandard) == 0)
					.findAny().isPresent();

			if (!isPresent) {
				holidayFinalList.add(holidayStandard);
			}

		}

		if (holidayFinalList != null && !holidayFinalList.isEmpty()) {
			Boolean isHoliday = holidayFinalList.stream().filter(dateHoliday -> new SimpleDateFormat("MM-dd")
					.format(dateHoliday.getDate()).equals(new SimpleDateFormat("MM-dd").format(dateEntry))).findFirst()
					.isPresent();

			Integer isHolidayInt = isHoliday == false ? 0 : 1;

			employeeScheduleUploading.setIsHoliday(isHolidayInt);

			if (isHoliday) {
				HolidayEncoding holidayEncoding = holidayFinalList.stream()
						.filter(p -> new SimpleDateFormat("MM-dd").format(p.getDate())
								.equals(new SimpleDateFormat("MM-dd").format(employeeScheduleUploading.getDateEntry())))
						.findFirst().get();

				employeeScheduleUploading.setIsHoliday(holidayEncoding.getHolidayTypeReference().getPrimaryKey());

				Integer prikeyRemarksHoliday = 5;
				RemarksReference remarksReference = new RemarksReference();
				remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksHoliday);

				employeeScheduleUploading.setRemarksReference(remarksReference);
			} else {
				employeeScheduleUploading.setRemarksReference(null);
			}
		}

	}

	public void checkDateLeaveFiling(EmployeeScheduleUploading employeeScheduleUploading) {
		List<LeaveMonitoring> leaveMonitoringList = new ArrayList<>();
		leaveMonitoringList = this.getLeaveMonitoringMain().getAllApprovedDataByEmployeeCode(
				employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode());

		Boolean isDateWithLeave = leaveMonitoringList.stream()
				.filter(p -> p.getDateFrom().equals(employeeScheduleUploading.getDateEntry())).findFirst().isPresent();

		Integer prikeyRemarksLeaveWithPay = 6;
		Integer prikeyRemarksLeaveWithoutPay = 13;

		if (isDateWithLeave) {
			LeaveMonitoring leaveMonitoring = leaveMonitoringList.stream()
					.filter(p -> p.getDateFrom().equals(employeeScheduleUploading.getDateEntry())).findFirst().get();

			if (leaveMonitoring.getWithPay().compareTo(new BigDecimal(0)) > 0) {
				this.withPay = leaveMonitoring.getWithPay();
			}
			if (leaveMonitoring.getWithoutPay().compareTo(new BigDecimal(0)) > 0) {
				this.withoutPay = leaveMonitoring.getWithoutPay();
			}

			if (this.withPay != null && this.withPay.compareTo(new BigDecimal(0)) > 0) {
				if (this.withPay.compareTo(new BigDecimal(0.5)) == 0) {
					this.withPay = this.withPay.subtract(new BigDecimal(0.5));
				} else {
					this.withPay = this.withPay.subtract(new BigDecimal(1));
				}

				RemarksReference remarksReference = new RemarksReference();
				remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksLeaveWithPay);

				employeeScheduleUploading.setRemarksReference(remarksReference);
			} else {
				if (this.withoutPay != null && this.withoutPay.compareTo(new BigDecimal(0)) > 0) {
					if (this.withoutPay.compareTo(new BigDecimal(0.5)) == 0) {
						this.withoutPay = this.withoutPay.subtract(new BigDecimal(0.5));
					} else {
						this.withoutPay = this.withoutPay.subtract(new BigDecimal(1));
					}

					RemarksReference remarksReference = new RemarksReference();
					remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksLeaveWithoutPay);

					employeeScheduleUploading.setRemarksReference(remarksReference);
					return;
				}
				employeeScheduleUploading.setRemarksReference(null);
			}

		} else {
			// date has no leave filing, check with pay or without pay value from prev date
			if (this.withPay != null && this.withPay.compareTo(new BigDecimal(0)) > 0) {
				if (this.withPay.compareTo(new BigDecimal(0.5)) == 0) {
					this.withPay = this.withPay.subtract(new BigDecimal(0.5));
				} else {
					this.withPay = this.withPay.subtract(new BigDecimal(1));
				}

				RemarksReference remarksReference = new RemarksReference();
				remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksLeaveWithPay);

				employeeScheduleUploading.setRemarksReference(remarksReference);
				return;
			} else {
				if (this.withoutPay != null && this.withoutPay.compareTo(new BigDecimal(0)) > 0) {
					if (this.withoutPay.compareTo(new BigDecimal(0.5)) == 0) {
						this.withoutPay = this.withoutPay.subtract(new BigDecimal(0.5));
					} else {
						this.withoutPay = this.withoutPay.subtract(new BigDecimal(1));
					}

					RemarksReference remarksReference = new RemarksReference();
					remarksReference = this.getRemarksReferenceMain().getDataById(prikeyRemarksLeaveWithoutPay);

					employeeScheduleUploading.setRemarksReference(remarksReference);
					return;
				} else {
					employeeScheduleUploading.setRemarksReference(null);
				}
			}

		}
	}

	public BigDecimal computeTotalHrsEntry(Timestamp timeFrom, Timestamp timeTo) {

		long millisResult = timeTo.getTime() - timeFrom.getTime();
		BigDecimal seconds = BigDecimal.valueOf(millisResult).divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
		BigDecimal hours = seconds.divide(new BigDecimal(3600), 4, RoundingMode.HALF_UP);

		return hours;
	}

	public BigDecimal computeTotalHrsOvertime(Timestamp timeFrom, Timestamp timeTo) {

		long millisResult = timeTo.getTime() - timeFrom.getTime();
		BigDecimal seconds = BigDecimal.valueOf(millisResult).divide(new BigDecimal(1000), 4, RoundingMode.HALF_UP);
		BigDecimal hours = seconds.divide(new BigDecimal(3600), 4, RoundingMode.HALF_UP);

		if (hours.compareTo(new BigDecimal(3)) > 0) {
			BigDecimal newSeconds = seconds.subtract(new BigDecimal(1800));
			BigDecimal newHour = newSeconds.divide(new BigDecimal(3600), 4, RoundingMode.HALF_UP);
			return newHour;
		}

		return hours;
	}

	public Integer computeTotalMinsEntry(Timestamp timeFrom, Timestamp timeTo) {
		Integer totalMins = 0;

		long millisResult = timeTo.getTime() - timeFrom.getTime();
		// BigDecimal seconds = BigDecimal.valueOf(millisResult).divide(new
		// BigDecimal(1000), 4, RoundingMode.HALF_UP);
		// BigDecimal minutes = seconds.divide(new BigDecimal(60), 4,
		// RoundingMode.HALF_UP);
		Integer millisInt = Math.toIntExact(millisResult);
		Integer seconds = millisInt / 1000;
		Integer minutes = seconds / 60;

		totalMins = minutes;

		return totalMins;
	}

	public List<EmployeeScheduleUploading> setNoEntryDates(Integer employeeCode,
			List<EmployeeScheduleUploading> entryList) {
		List<Date> dateRangeList = new ArrayList<>();
		// List<Integer> empIdList = new ArrayList<>();

		// empIdList = entryList.stream().map(p ->
		// p.getEmploymentHistory().getEmployee().getEmployeeCode()).distinct()
		// .collect(Collectors.toList());

		List<EmployeeScheduleUploading> entryListByEmployee = new ArrayList<>();

		// for (Integer employeeCode : empIdList) {
		entryListByEmployee = entryList.stream()
				.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeCode))
				.collect(Collectors.toList());

		if (entryListByEmployee.size() != 0) {
			dateRangeList.clear();
			List<Date> entryListDates = new ArrayList<>();
			entryListDates = entryListByEmployee.stream().map(p -> p.getDateEntry()).distinct()
					.collect(Collectors.toList());

			Date startDate = entryListByEmployee.get(0).getCutoffFrom();
			Date endDate = entryListByEmployee.get(0).getCutoffTo();

			while (startDate.compareTo(endDate) <= 0) {
				dateRangeList.add(startDate);
				startDate = DateUtil.addDayOrMonthToDate(startDate, 0, 1);
			}

			List<Date> dateRangeListDistinct = new ArrayList<>();
			dateRangeListDistinct = dateRangeList.stream().distinct().collect(Collectors.toList());
			dateRangeListDistinct.removeAll(entryListDates);

			List<EmployeeScheduleUploading> newEntryList = new ArrayList<>();
			for (Date remainingDate : dateRangeListDistinct) {
				EmployeeScheduleUploading employeeScheduleUploadingNew = new EmployeeScheduleUploading();
				employeeScheduleUploadingNew.setDateEntry(remainingDate);

				employeeScheduleUploadingNew.setEmploymentHistory(entryListByEmployee.get(0).getEmploymentHistory());
				employeeScheduleUploadingNew.setDepartment(entryListByEmployee.get(0).getDepartment());
				employeeScheduleUploadingNew.setClient(entryListByEmployee.get(0).getClient());
				employeeScheduleUploadingNew.setBioId(entryListByEmployee.get(0).getBioId());
				employeeScheduleUploadingNew.setPayPeriod(entryListByEmployee.get(0).getPayPeriod());
				employeeScheduleUploadingNew.setSchedule(entryListByEmployee.get(0).getSchedule());

				employeeScheduleUploadingNew.setPayFrom(entryListByEmployee.get(0).getPayFrom());
				employeeScheduleUploadingNew.setPayTo(entryListByEmployee.get(0).getPayTo());
				employeeScheduleUploadingNew.setCutoffFrom(entryListByEmployee.get(0).getCutoffFrom());
				employeeScheduleUploadingNew.setCutoffTo(entryListByEmployee.get(0).getCutoffTo());
				employeeScheduleUploadingNew.setIsValidated(0);

				String dayOfDate = Instant.ofEpochMilli(remainingDate.getTime()).atZone(ZoneId.systemDefault())
						.toLocalDate().getDayOfWeek().toString();

				employeeScheduleUploadingNew.setDayOfDate(this.capitalizeFirstChar(dayOfDate));

				EmployeeScheduleEncoding employeeScheduleEncodingRegular = new EmployeeScheduleEncoding();
				EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();

				employeeScheduleEncodingRegular = this.getEmployeeScheduleEncodingMain()
						.getEmployeeScheduleByEmployeeID(employeeCode);

				ScheduleEncoding scheduleEncoding = new ScheduleEncoding();
				scheduleEncoding = null;

				employeeScheduleEncodingIrregular = this.getEmployeeScheduleEncodingIrregularMain()
						.getEmployeeIrregularScheduleByDateAndEmployeeID(
								new SimpleDateFormat("yyyy-MM-dd").format(remainingDate), employeeCode);

				if (employeeScheduleEncodingRegular != null) {
					scheduleEncoding = this.getScheduleEncodingMain()
							.getAllScheduleByPrikeyReferenceClientAndScheduleDay(employeeScheduleEncodingRegular
									.getScheduleEncodingReference().getPrimaryKeyReference(), dayOfDate);
				}

				if (scheduleEncoding == null && employeeScheduleEncodingIrregular == null) {
					employeeScheduleUploadingNew.setIsRegularSchedule(0);
				}

				if (employeeScheduleEncodingIrregular != null) {
					employeeScheduleUploadingNew.setIsRegularSchedule(0);
					employeeScheduleUploadingNew
							.setEmployeeScheduleEncodingIrregular(employeeScheduleEncodingIrregular);
				}

				if (scheduleEncoding != null) {
					employeeScheduleUploadingNew.setIsRegularSchedule(1);
					employeeScheduleUploadingNew.setScheduleEncoding(scheduleEncoding);
				}

				// this.setRemarksEntry(employeeScheduleUploadingNew,
				// employeeScheduleUploadingNew.getDateEntry(), false);

				boolean isAlreadyAdded = newEntryList
						.stream().filter(p -> p.getDateEntry().equals(remainingDate) && p.getEmploymentHistory()
								.getEmployee().getEmployeeCode().equals(employeeScheduleUploadingNew
										.getEmploymentHistory().getEmployee().getEmployeeCode()))
						.findFirst().isPresent();

				if (!isAlreadyAdded) {
					newEntryList.add(employeeScheduleUploadingNew);
				}
			}

			entryListByEmployee.addAll(newEntryList);

		}

		ObservableList<EmployeeScheduleUploading> sortObsList = FXCollections.observableArrayList();
		sortObsList.addAll(entryListByEmployee);

		ObservableListUtil.sort(sortObsList, p -> new SimpleDateFormat("yyyy-MM-dd").format(p.getDateEntry()));
		entryListByEmployee.clear();
		entryListByEmployee.addAll(sortObsList);

		return entryListByEmployee;
	}

	public ObservableList<Client> populateClient(Date payFrom, Date payTo) {
		ObservableList<Client> clientObservableList = FXCollections.observableArrayList();
		if (payFrom == null && payTo == null) {

			clientObservableList.setAll(this.getClientMain().getClientByUser(this.getUser()));

		} else {
			List<EmployeeScheduleUploading> listBio = new ArrayList<>();
			listBio = this.getDataByPayFromPayTo(payFrom, payTo);

			clientObservableList
					.setAll(listBio.stream().map(p -> p.getClient()).distinct().collect(Collectors.toList()));
		}

		return clientObservableList;
	}

	public ObservableList<Department> populateDepartment(Client selectedClient) {
		ObservableList<Department> obsListDepartmentByClient = FXCollections.observableArrayList();
		if (selectedClient != null) {
			obsListDepartmentByClient.setAll(this.getDepartmentMain().getDepartmentByClientCode(selectedClient));
		}

		return obsListDepartmentByClient;
	}

	public ObservableList<Employee> populateEmployee(Client selectedClient, Department selectedDepartment) {
		ObservableList<Employee> obsListEmployee = FXCollections.observableArrayList();

		if (this.getEmployeeObservableList() != null && !this.getEmployeeObservableList().isEmpty()) {
			if (selectedClient == null && selectedDepartment == null) {
				obsListEmployee.setAll(this.getEmployeeObservableList());
			}

			if (selectedClient != null && selectedDepartment == null) {
				obsListEmployee.setAll(this.getEmployeeObservableList());
				obsListEmployee.setAll(this.getEmployeeObservableList().stream()
						.filter(p -> p.getClient().compareTo(selectedClient.getClientCode()) == 0)
						.collect(Collectors.toList()));

			}

			if (selectedClient != null && selectedDepartment != null) {
				obsListEmployee.setAll(this.getEmployeeObservableList());

				obsListEmployee.setAll(this.getEmployeeObservableList().stream()
						.filter(p -> p.getDepartment().equals(selectedDepartment.getDepartmentCode().toString()))
						.collect(Collectors.toList()));

			}
		}
		return obsListEmployee;
	}

	public void exportExcel(Integer reportType) {
		switch (reportType) {
		case 1:
			NoScheduleEmployeesReport report1 = new NoScheduleEmployeesReport(this);
			report1.generateReport();
			break;
		case 2:
			NoOvertimeConfigEmployeesReport report2 = new NoOvertimeConfigEmployeesReport(this);
			report2.generateReport();
			break;
		case 3:
			PayrollErrorSavingReport report3 = new PayrollErrorSavingReport(this);
			report3.generateReport();
		default:
			break;
		}
	}

	public String capitalizeFirstChar(String string) {
		String result = "";

		if (string != null) {
			result = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
		}

		return result;
	}

	public String formatTimeAMPM(Timestamp timestamp) {
		String result = "";
		Calendar timeEntry = Calendar.getInstance();
		timeEntry.setTime(timestamp);

		String hour = String.valueOf(timeEntry.get(Calendar.HOUR) == 0 ? "12"
				: timeEntry.get(Calendar.HOUR) <= 9 ? "0".concat(String.valueOf(timeEntry.get(Calendar.HOUR)))
						: timeEntry.get(Calendar.HOUR));
		String min = String.valueOf(
				timeEntry.get(Calendar.MINUTE) <= 9 ? "0".concat(String.valueOf(timeEntry.get(Calendar.MINUTE)))
						: timeEntry.get(Calendar.MINUTE));
		String ampm = timeEntry.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

		result = hour + ":" + min + " " + ampm;

		return result;
	}

	public void showPrintEmployeeScheduleUploading() {
		try {
			FxmlUtil<PrintEmployeeScheduleUploadingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/PrintEmployeeScheduleUploading.fxml"));
			AnchorPane printLayout = fxmlUtil.getFxmlPane();

			Stage dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.primaryStage);
			dialogStage.setTitle("Generate Report - Employee Schedule Uploading - Biometrics");
			dialogStage.setResizable(false);

			PrintEmployeeScheduleUploadingController controller = fxmlUtil.getController();
			dialogStage.setScene(new Scene(printLayout));

			controller.setMainApplication(this);
			controller.setStage(dialogStage);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showUploadEmployeeScheduleUploading() {
		try {
			FxmlUtil<UploadEmployeeScheduleUploadingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/UploadEmployeeScheduleUploading.fxml"));
			AnchorPane printLayout = fxmlUtil.getFxmlPane();

			Stage dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.primaryStage);
			dialogStage.setTitle("Upload");
			dialogStage.setResizable(false);

			UploadEmployeeScheduleUploadingController controller = fxmlUtil.getController();
			dialogStage.setScene(new Scene(printLayout));

			controller.setMainApplication(this);
			controller.setStage(dialogStage);

			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean showEditEmployeeScheduleUploading(EmployeeScheduleUploading employeeScheduleUploading,
			ModificationType modificationType) {
		try {
			FxmlUtil<EditEmployeeScheduleUploadingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditEmployeeScheduleUploading.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			EditEmployeeScheduleUploadingController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			return controller.showEditDialogStage(modificationType + " - Employee Schedule Uploading",
					this.primaryStage, editLayout, employeeScheduleUploading);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean showSetEmployeeScheduleUploadingOvertime(EmployeeScheduleUploading employeeScheduleUploading,
			ModificationType modificationType, Stage stage) {

		try {
			FxmlUtil<SetEmployeeScheduleUploadingOvertimeController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/SetEmployeeScheduleUploadingOvertime.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			SetEmployeeScheduleUploadingOvertimeController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			return controller.showEditDialogStage(modificationType + " - Employee Schedule Uploading Overtime", stage,
					editLayout, employeeScheduleUploading);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void getEmployeeSchedule(Integer empIdFromList, List<EmployeeScheduleUploading> biometricsList) {

		List<ScheduleEncoding> scheduleEncodingImportList = new ArrayList<>();
		scheduleEncodingImportList
				.addAll(this.getScheduleEncodingMain().getAllScheduleUploadedByEmployeeId(empIdFromList));
		if (scheduleEncodingImportList != null && !scheduleEncodingImportList.isEmpty()) {
			String schedule = new SimpleDateFormat("hh:mm a").format(scheduleEncodingImportList.get(0).getTimeIn())
					+ " to " + new SimpleDateFormat("hh:mm a").format(scheduleEncodingImportList.get(0).getTimeOut());

			employeeScheduleUploadingList.forEach(entry -> {
				entry.setSchedule(schedule);
			});
		} else {
			EmployeeScheduleEncoding employeeScheduleEncoding = this.getEmployeeScheduleEncodingMain()
					.getEmployeeScheduleByEmployeeID(empIdFromList);

			List<EmployeeScheduleEncodingIrregular> employeeScheduleEncodingIrregularList = this
					.getEmployeeScheduleEncodingIrregularMain()
					.getAllEmployeeIrregularScheduleByEmployeeID(empIdFromList);

			if (employeeScheduleEncoding == null && employeeScheduleEncodingIrregularList.size() == 0) {
				this.listEmpIdNoSched.add(empIdFromList);
				this.getEntryToBeRemoveList().addAll(biometricsList.stream()
						.filter(entry -> entry.getEmployeeId().equals(empIdFromList)).collect(Collectors.toList()));

			} else if (employeeScheduleEncoding != null) {

				List<EmployeeScheduleUploading> employeeScheduleUploadingList = biometricsList.stream()
						.filter(p -> p.getEmployeeId().equals(empIdFromList)).collect(Collectors.toList());

				List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();

				scheduleEncodingList = this.getScheduleEncodingMain().getAllScheduleByPrikeyReferenceClient(
						employeeScheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference());

				if (scheduleEncodingList != null && !scheduleEncodingList.isEmpty()) {

					String schedule = new SimpleDateFormat("hh:mm a").format(scheduleEncodingList.get(0).getTimeIn())
							+ " to " + new SimpleDateFormat("hh:mm a").format(scheduleEncodingList.get(0).getTimeOut());

					employeeScheduleUploadingList.forEach(entry -> {
						entry.setSchedule(schedule);
					});
				}
			}
		}
	}

	public String getDateFromTimestamp(Timestamp timeInEntry) {

		String[] result = this.getDateAndTimeEntryFromTimestamp(timeInEntry);
		String dateEntryToBeSave = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.parseDate(result[0]));

		return dateEntryToBeSave;
	}

	public Time getTimeFromTimestamp(Timestamp entry) {

		String[] result = this.getDateAndTimeEntryFromTimestamp(entry);
		String timeEntryToBeSave = result[1];
		Time timeResult = Time.valueOf(timeEntryToBeSave);

		return timeResult;
	}

	public String[] getDateAndTimeEntryFromTimestamp(Timestamp entry) {
		if (entry != null) {
			String[] result = String.format("%1$TF %1$TT", entry).split("\\s");
			return result;
		}
		return null;
	}

	public ObservableList<Map.Entry<Employee, Boolean>> populateHashMapEmployees(
			ObservableList<Employee> observableListEmployee) {
		ObservableList<Map.Entry<Employee, Boolean>> resultObservableList = FXCollections.observableArrayList();
		LinkedHashMap<Employee, Boolean> employeeMap = new LinkedHashMap<>();

		if (observableListEmployee != null) {
			for (Employee employee : observableListEmployee) {
				employeeMap.put(employee, false);
			}
		}

		resultObservableList.setAll(employeeMap.entrySet());
		ObservableListUtil.sort(resultObservableList, p -> p.getKey().getEmployeeFullName());

		return resultObservableList;
	}

	public EmploymentHistoryMain getEmploymentHistoryMain() {
		return employmentHistoryMain;
	}

	public void setEmploymentHistoryMain(EmploymentHistoryMain employmentHistoryMain) {
		this.employmentHistoryMain = employmentHistoryMain;
	}

	public List<EmployeeScheduleUploading> getEmployeeScheduleUploadingList() {
		return employeeScheduleUploadingList;
	}

	public void setEmployeeScheduleUploadingList(List<EmployeeScheduleUploading> employeeScheduleUploadingList) {
		this.employeeScheduleUploadingList = employeeScheduleUploadingList;
	}

	public ObservableList<Employee> getEmployeeObservableList() {
		return employeeObservableList;
	}

	public void setEmployeeObservableList(ObservableList<Employee> employeeObservableList) {
		this.employeeObservableList = employeeObservableList;
	}

	public ClientMain getClientMain() {
		return clientMain;
	}

	public void setClientMain(ClientMain clientMain) {
		this.clientMain = clientMain;
	}

	public EmployeeMain getEmployeeMain() {
		return employeeMain;
	}

	public void setEmployeeMain(EmployeeMain employeeMain) {
		this.employeeMain = employeeMain;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public EmployeeScheduleEncodingMain getEmployeeScheduleEncodingMain() {
		return employeeScheduleEncodingMain;
	}

	public void setEmployeeScheduleEncodingMain(EmployeeScheduleEncodingMain employeeScheduleEncodingMain) {
		this.employeeScheduleEncodingMain = employeeScheduleEncodingMain;
	}

	public ScheduleEncodingMain getScheduleEncodingMain() {
		return scheduleEncodingMain;
	}

	public void setScheduleEncodingMain(ScheduleEncodingMain scheduleEncodingMain) {
		this.scheduleEncodingMain = scheduleEncodingMain;
	}

	public EmployeeScheduleEncodingIrregularMain getEmployeeScheduleEncodingIrregularMain() {
		return employeeScheduleEncodingIrregularMain;
	}

	public void setEmployeeScheduleEncodingIrregularMain(
			EmployeeScheduleEncodingIrregularMain employeeScheduleEncodingIrregularMain) {
		this.employeeScheduleEncodingIrregularMain = employeeScheduleEncodingIrregularMain;
	}

	public List<Integer> getListEmpIdNoSched() {
		return listEmpIdNoSched;
	}

	public void setListEmpIdNoSched(List<Integer> listEmpIdNoSched) {
		this.listEmpIdNoSched = listEmpIdNoSched;
	}

	public DepartmentMain getDepartmentMain() {
		return departmentMain;
	}

	public void setDepartmentMain(DepartmentMain departmentMain) {
		this.departmentMain = departmentMain;
	}

	public EmployeeScheduleEncodingOvertimeMain getEmployeeScheduleEncodingOvertimeMain() {
		return employeeScheduleEncodingOvertimeMain;
	}

	public void setEmployeeScheduleEncodingOvertimeMain(
			EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain) {
		this.employeeScheduleEncodingOvertimeMain = employeeScheduleEncodingOvertimeMain;
	}

	public OvertimeFilingMain getOvertimeFilingMain() {
		return overtimeFilingMain;
	}

	public void setOvertimeFilingMain(OvertimeFilingMain overtimeFilingMain) {
		this.overtimeFilingMain = overtimeFilingMain;
	}

	public UserMain getUserMain() {
		return userMain;
	}

	public void setUserMain(UserMain userMain) {
		this.userMain = userMain;
	}

	public Date getPayPeriodDateFrom() {
		return payPeriodDateFrom;
	}

	public void setPayPeriodDateFrom(Date payPeriodDateFrom) {
		this.payPeriodDateFrom = payPeriodDateFrom;
	}

	public Date getPayPeriodDateTo() {
		return payPeriodDateTo;
	}

	public void setPayPeriodDateTo(Date payPeriodDateTo) {
		this.payPeriodDateTo = payPeriodDateTo;
	}

	public Date getCutoffDateFrom() {
		return cutoffDateFrom;
	}

	public void setCutoffDateFrom(Date cutoffDateFrom) {
		this.cutoffDateFrom = cutoffDateFrom;
	}

	public Date getCutoffDateTo() {
		return cutoffDateTo;
	}

	public void setCutoffDateTo(Date cutoffDateTo) {
		this.cutoffDateTo = cutoffDateTo;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public ObservableList<Client> getClientObservableList() {
		return clientObservableList;
	}

	public void setClientObservableList(ObservableList<Client> clientObservableList) {
		this.clientObservableList = clientObservableList;
	}

	public List<EmployeeScheduleUploading> getEntryToBeRemoveList() {
		return entryToBeRemoveList;
	}

	public void setEntryToBeRemoveList(List<EmployeeScheduleUploading> entryToBeRemoveList) {
		this.entryToBeRemoveList = entryToBeRemoveList;
	}

	public ObservableList<Employee> getRawEmployeeObservableList() {
		return rawEmployeeObservableList;
	}

	public void setRawEmployeeObservableList(ObservableList<Employee> rawEmployeeObservableList) {
		this.rawEmployeeObservableList = rawEmployeeObservableList;
	}

	public List<EmployeeScheduleUploading> getListFinalizeBiometricsComplete() {
		return listFinalizeBiometricsComplete;
	}

	public void setListFinalizeBiometricsComplete(List<EmployeeScheduleUploading> listFinalizeBiometricsComplete) {
		this.listFinalizeBiometricsComplete = listFinalizeBiometricsComplete;
	}

	public List<EmployeeScheduleUploading> getListFinalizeBiometricsOnlyBio() {
		return listFinalizeBiometricsOnlyBio;
	}

	public void setListFinalizeBiometricsOnlyBio(List<EmployeeScheduleUploading> listFinalizeBiometricsOnlyBio) {
		this.listFinalizeBiometricsOnlyBio = listFinalizeBiometricsOnlyBio;
	}

	public HolidayEncodingMain getHolidayEncodingMain() {
		return holidayEncodingMain;
	}

	public void setHolidayEncodingMain(HolidayEncodingMain holidayEncodingMain) {
		this.holidayEncodingMain = holidayEncodingMain;
	}

	public List<OvertimeType> getOvertimeHeaderList() {
		return overtimeHeaderList;
	}

	public void setOvertimeHeaderList(List<OvertimeType> overtimeHeaderList) {
		this.overtimeHeaderList = overtimeHeaderList;
	}

	public String getPayPeriod(Date payFrom, Date payTo) {
		payPeriod = new SimpleDateFormat("MM/dd/yy").format(payFrom) + " to "
				+ new SimpleDateFormat("MM/dd/yy").format(payTo);
		return payPeriod;
	}

	public void setPayPeriod(String payPeriod) {
		this.payPeriod = payPeriod;
	}

	public OvertimeTypeMain getOvertimeTypeMain() {
		return overtimeTypeMain;
	}

	public void setOvertimeTypeMain(OvertimeTypeMain overtimeTypeMain) {
		this.overtimeTypeMain = overtimeTypeMain;
	}

	public LeaveMonitoringMain getLeaveMonitoringMain() {
		return leaveMonitoringMain;
	}

	public void setLeaveMonitoringMain(LeaveMonitoringMain leaveMonitoringMain) {
		this.leaveMonitoringMain = leaveMonitoringMain;
	}

	public EmployeeScheduleUploadingOvertimeBreakdownMain getEmployeeScheduleUploadingOvertimeBreakdownMain() {
		return employeeScheduleUploadingOvertimeBreakdownMain;
	}

	public void setEmployeeScheduleUploadingOvertimeBreakdownMain(
			EmployeeScheduleUploadingOvertimeBreakdownMain employeeScheduleUploadingOvertimeBreakdownMain) {
		this.employeeScheduleUploadingOvertimeBreakdownMain = employeeScheduleUploadingOvertimeBreakdownMain;
	}

	public RemarksReferenceMain getRemarksReferenceMain() {
		return remarksReferenceMain;
	}

	public void setRemarksReferenceMain(RemarksReferenceMain remarksReferenceMain) {
		this.remarksReferenceMain = remarksReferenceMain;
	}

	public HashMap<Integer, HashSet<String>> getEmployeesWithoutOvertimeConfigHashMap() {
		return employeesWithoutOvertimeConfigHashMap;
	}

	public void setEmployeesWithoutOvertimeConfigHashMap(
			HashMap<Integer, HashSet<String>> employeesWithoutOvertimeConfigHashMap) {
		this.employeesWithoutOvertimeConfigHashMap = employeesWithoutOvertimeConfigHashMap;
	}

	public ObservableList<EmployeeScheduleUploadingOvertimeBreakdown> getObsListOTBreakdown() {
		return obsListOTBreakdown;
	}

	public void setObsListOTBreakdown(ObservableList<EmployeeScheduleUploadingOvertimeBreakdown> obsListOTBreakdown) {
		this.obsListOTBreakdown = obsListOTBreakdown;
	}

	public ObservableList<OvertimeType> getOvertimeTypeByClientObsList() {
		return overtimeTypeByClientObsList;
	}

	public void setOvertimeTypeByClientObsList(ObservableList<OvertimeType> overtimeTypeByClientObsList) {
		this.overtimeTypeByClientObsList = overtimeTypeByClientObsList;
	}

	public Integer getTotalMin() {
		return totalMin;
	}

	public void setTotalMin(Integer totalMin) {
		this.totalMin = totalMin;
	}

	public EmployeeScheduleUploadingOvertimeBreakdown getSelectedOvertimeBreakdown() {
		return selectedOvertimeBreakdown;
	}

	public void setSelectedOvertimeBreakdown(EmployeeScheduleUploadingOvertimeBreakdown selectedOvertimeBreakdown) {
		this.selectedOvertimeBreakdown = selectedOvertimeBreakdown;
	}

	public Client getSelectedClient() {
		return selectedClient;
	}

	public void setSelectedClient(Client selectedClient) {
		this.selectedClient = selectedClient;
	}

	public Department getSelectedDepartment() {
		return selectedDepartment;
	}

	public void setSelectedDepartment(Department selectedDepartment) {
		this.selectedDepartment = selectedDepartment;
	}

	public boolean getIsIntegralEmployee() {
		return isIntegralEmployee;
	}

	public void setIsIntegralEmployee(boolean isIntegralEmployee) {
		this.isIntegralEmployee = isIntegralEmployee;
	}

	public OvertimeFilingClientMain getOvertimeFilingClientMain() {
		return overtimeFilingClientMain;
	}

	public void setOvertimeFilingClientMain(OvertimeFilingClientMain overtimeFilingClientMain) {
		this.overtimeFilingClientMain = overtimeFilingClientMain;
	}

	public ObservableList<Client> getObsListClient() {
		return obsListClient;
	}

	public void setObsListClient(ObservableList<Client> obsListClient) {
		this.obsListClient = obsListClient;
	}

	public ObservableList<Department> getObsListDepartment() {
		return obsListDepartment;
	}

	public void setObsListDepartment(ObservableList<Department> obsListDepartment) {
		this.obsListDepartment = obsListDepartment;
	}

	public ObservableList<Integer> getObsListEmpID() {
		return obsListEmpID;
	}

	public void setObsListEmpID(ObservableList<Integer> obsListEmpID) {
		this.obsListEmpID = obsListEmpID;
	}

	public ObservableList<Employee> getObsListEmployee() {
		return obsListEmployee;
	}

	public void setObsListEmployee(ObservableList<Employee> obsListEmployee) {
		this.obsListEmployee = obsListEmployee;
	}

	public ObservableList<Map.Entry<Employee, Boolean>> getObservableListHashMapEmployees() {
		return observableListHashMapEmployees;
	}

	public void setObservableListHashMapEmployees(
			ObservableList<Map.Entry<Employee, Boolean>> observableListHashMapEmployees) {
		this.observableListHashMapEmployees = observableListHashMapEmployees;
	}

	public PayrollEncodeMain getPayrollEncodeMain() {
		return payrollEncodeMain;
	}

	public void setPayrollEncodeMain(PayrollEncodeMain payrollEncodeMain) {
		this.payrollEncodeMain = payrollEncodeMain;
	}

	public PayrollMain getPayrollMain() {
		return payrollMain;
	}

	public void setPayrollMain(PayrollMain payrollMain) {
		this.payrollMain = payrollMain;
	}

	public PayrollStatusDetailsMain getPayrollStatusDetailsMain() {
		return payrollStatusDetailsMain;
	}

	public void setPayrollStatusDetailsMain(PayrollStatusDetailsMain payrollStatusDetailsMain) {
		this.payrollStatusDetailsMain = payrollStatusDetailsMain;
	}

	public MiscellaneousAdjustmentDetailsMain getMiscellaneousAdjustmentDetailsMain() {
		return miscellaneousAdjustmentDetailsMain;
	}

	public void setMiscellaneousAdjustmentDetailsMain(
			MiscellaneousAdjustmentDetailsMain miscellaneousAdjustmentDetailsMain) {
		this.miscellaneousAdjustmentDetailsMain = miscellaneousAdjustmentDetailsMain;
	}

	public PositionMain getPositionMain() {
		return positionMain;
	}

	public void setPositionMain(PositionMain positionMain) {
		this.positionMain = positionMain;
	}

	public List<Integer> getClientPrikeyCustomLateUndertimeList() {
		return clientPrikeyCustomLateUndertimeList;
	}

	public void setClientPrikeyCustomLateUndertimeList(List<Integer> clientPrikeyCustomLateUndertimeList) {
		this.clientPrikeyCustomLateUndertimeList = clientPrikeyCustomLateUndertimeList;
	}

	public HashMap<EmploymentHistory, String> getEmployeeErrorSavingPayrollHashMap() {
		return employeeErrorSavingPayrollHashMap;
	}

	public void setEmployeeErrorSavingPayrollHashMap(
			HashMap<EmploymentHistory, String> employeeErrorSavingPayrollHashMap) {
		this.employeeErrorSavingPayrollHashMap = employeeErrorSavingPayrollHashMap;
	}

	public List<Integer> getClientPrikeyLunchEntrySameConfigList() {
		return clientPrikeyLunchEntrySameConfigList;
	}

	public void setClientPrikeyLunchEntrySameConfigList(List<Integer> clientPrikeyLunchEntrySameConfigList) {
		this.clientPrikeyLunchEntrySameConfigList = clientPrikeyLunchEntrySameConfigList;
	}

}