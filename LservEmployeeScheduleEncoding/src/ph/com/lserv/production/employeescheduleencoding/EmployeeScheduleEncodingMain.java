package ph.com.lserv.production.employeescheduleencoding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.client.ClientMain;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.department.DepartmentMain;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.EmployeeMain;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.EmploymentHistoryMain;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lserv.production.employeescheduleencoding.data.EmployeeScheduleEncodingDao;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;
import ph.com.lserv.production.employeescheduleencoding.view.BrowseEmployeeScheduleEncodingController;
import ph.com.lserv.production.employeescheduleencoding.view.EditRegularEmployeeScheduleEncodingController;
import ph.com.lserv.production.employeescheduleencodingirregular.EmployeeScheduleEncodingIrregularMain;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;
import ph.com.lserv.production.employeescheduleencodingovertime.EmployeeScheduleEncodingOvertimeMain;
import ph.com.lserv.production.employeescheduleencodingovertime.model.EmployeeScheduleEncodingOvertime;
import ph.com.lserv.production.scheduleencoding.ScheduleEncodingMain;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;
import ph.com.lserv.production.scheduleencodingreference.ScheduleEncodingReferenceMain;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class EmployeeScheduleEncodingMain extends MasterMain<EmployeeScheduleEncoding> {
	private ObservableList<Date> allDateOfTheMonthObservableList;
	private ObservableList<String> allMonthsNameObservableList;
	private ObservableList<String> allDateOfTheMonthFormattedObservableList;
	private ObservableList<Map.Entry<Employee, Boolean>> observableListEmployeesByClientDepartment;

	private ObservableList<Client> clientObservableList;
	private ClientMain clientMain;

	private List<EmploymentHistory> employmentHistoryList;
	private ObservableList<EmploymentHistory> employmentHistoryObservableList;
	private ObservableList<Employee> employeeObservableList;
	private EmploymentHistoryMain employmentHistoryMain;
	private EmploymentHistory employmentHistory;

	private ScheduleEncodingMain scheduleEncodingMain;
	private List<ValidatedTextField> enabledValidatedTextFieldList;
	private ObservableList<ScheduleEncodingReference> schedulesByClientObservableList;

	private ScheduleEncodingReferenceMain scheduleEncodingReferenceMain;
	private Employee selectedEmployee;
	private Client selectedClient;
	private EmploymentHistory selectedEmploymentHistory;

	private ObservableList<ScheduleEncoding> scheduleEncodingList;

	private EmployeeScheduleEncodingIrregularMain employeeScheduleEncodingIrregularMain;
	private EmployeeScheduleEncodingIrregular selectedEmployeeScheduleEncodingIrregular;

	private BrowseEmployeeScheduleEncodingController browseEmployeeScheduleEncodingController;
	private ObservableList<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> allIrregularScheduleObservableList;
	private ObservableList<ScheduleEncoding> allRegularScheduleObservableList;
	private List<EmployeeScheduleEncodingIrregular> allIrregularScheduleList;
	private List<ScheduleEncoding> allRegularScheduleList;

	private EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain;
	private ObservableList<EmployeeScheduleEncodingOvertime> allOvertimeScheduleObservableList;
	private ObservableList<EmployeeScheduleEncodingOvertime> allOvertimeScheduleIrregularObservableList;

	private DepartmentMain departmentMain;
	private ObservableList<Department> departmentByClientObservableList;
	private Department selectedDepartment;

	private EmployeeMain employeeMain;
	private ObservableList<Map.Entry<Employee, Boolean>> selectedEmployeesObservableList;
	private List<EmployeeScheduleEncoding> employeeScheduleEncodingToBeSaveList;

	private List<EmployeeScheduleEncoding> modifyDetailsErrorAddList;
	private List<EmployeeScheduleEncoding> modifyDetailsErrorUpdateList;

	private List<ScheduleEncoding> objectToModifySelectedObjectList;
	private Boolean isAddingRegularOvertime;
	private ObservableList<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> selectedIrregularScheduleObservableList;

	public EmployeeScheduleEncodingMain()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(EmployeeScheduleEncoding.class);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public boolean createMainObject(EmployeeScheduleEncoding employeeScheduleEncoding) {
		return new EmployeeScheduleEncodingDao(sqlSessionFactory).createData(employeeScheduleEncoding) > 0;
	}

	public boolean createMultipleData(List<EmployeeScheduleEncoding> selectedEmployeesToBeSaveList) {
		return new EmployeeScheduleEncodingDao(sqlSessionFactory).createMultipleData(selectedEmployeesToBeSaveList) > 0;
	}

	@Override
	public boolean updateMainObject(EmployeeScheduleEncoding employeeScheduleEncoding) {
		return new EmployeeScheduleEncodingDao(sqlSessionFactory).updateData(employeeScheduleEncoding) > 0;
	}

	@Override
	public boolean deleteMainObject(EmployeeScheduleEncoding employeeScheduleEncoding) {
		return new EmployeeScheduleEncodingDao(sqlSessionFactory).deleteData(employeeScheduleEncoding) > 0;
	}

	public EmployeeScheduleEncoding getEmployeeScheduleByEmployeeID(Integer employeeID) {
		return new EmployeeScheduleEncodingDao(sqlSessionFactory).getEmployeeScheduleByEmployeeID(employeeID);
	}

	public List<EmployeeScheduleEncoding> getAllEmployeeScheduleByPrikeyReferenceClient(Integer prikeyReferenceClient) {
		return new EmployeeScheduleEncodingDao(sqlSessionFactory)
				.getAllEmployeeScheduleByPrikeyReferenceClient(prikeyReferenceClient);
	}

	public List<EmployeeScheduleEncoding> getAllEmployeeScheduleByEmpId(Integer employeeID) {
		return new EmployeeScheduleEncodingDao(sqlSessionFactory).getAllEmployeeScheduleByEmpId(employeeID);
	}

	public AnchorPane showBrowseEmployeeScheduleEncoding() throws IOException {
		this.initializeObjects();
		FxmlUtil<BrowseEmployeeScheduleEncodingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseEmployeeScheduleEncoding.fxml"));
		AnchorPane browseLayout = fxmlUtil.getFxmlPane();
		BrowseEmployeeScheduleEncodingController controller = fxmlUtil.getController();
		controller.setMainApplication(this);

		this.primaryStage.setTitle("Employee Schedule Encoding");
		this.primaryStage.setMaximized(true);

		return browseLayout;
	}

	public boolean showEditRegularEmployeeScheduleEncoding(EmployeeScheduleEncoding employeeScheduleEncoding,
			ModificationType modificationType) {
		try {
			FxmlUtil<EditRegularEmployeeScheduleEncodingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditRegularEmployeeScheduleEncoding.fxml"));
			AnchorPane editLayout = fxmlUtil.getFxmlPane();
			EditRegularEmployeeScheduleEncodingController controller = fxmlUtil.getController();
			controller.setMainApplication(this, modificationType);
			return controller.showEditDialogStage(modificationType + " - Regular Employee Schedule Encoding",
					this.primaryStage, editLayout, employeeScheduleEncoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean showEditIrregularEmployeeScheduleEncoding(
			EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular, ModificationType modificationType) {
		this.getEmployeeScheduleEncodingIrregularMain().setPrimaryStage(this.primaryStage);
		this.getEmployeeScheduleEncodingIrregularMain().setUser(this.getUser());

		if (modificationType.equals(ModificationType.ADD)) {
			return this.getEmployeeScheduleEncodingIrregularMain().showEditEmployeeScheduleEncodingIrregular(
					employeeScheduleEncodingIrregular, ModificationType.ADD, this.getSelectedEmployeesObservableList());
		} else {
			return this.getEmployeeScheduleEncodingIrregularMain().showEditEmployeeScheduleEncodingIrregular(
					employeeScheduleEncodingIrregular, ModificationType.EDIT,
					this.getSelectedEmployeesObservableList());
		}
	}

	public boolean showEditOvertimeEmployeeScheduleEncoding(
			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime, ModificationType modificationType) {
		Boolean isShowSetEmployeeScheduleEncodingOvertimeWindowFromMain = true;
		this.getEmployeeScheduleEncodingOvertimeMain().setPrimaryStage(this.primaryStage);
		this.getEmployeeScheduleEncodingOvertimeMain().setUser(this.getUser());

		if (modificationType.equals(ModificationType.ADD)) {
			return this.getEmployeeScheduleEncodingOvertimeMain().showEditEmployeeScheduleEncodingOvertime(
					employeeScheduleEncodingOvertime, ModificationType.ADD, this.getSelectedEmployee(),
					this.getSelectedClient(), this.getSelectedEmployeesObservableList(),
					this.getSelectedIrregularScheduleObservableList(), this.getIsAddingRegularOvertime());
		} else {
			return this.getEmployeeScheduleEncodingOvertimeMain().showSetEmployeeScheduleEncodingOvertime(
					employeeScheduleEncodingOvertime, ModificationType.EDIT, this.getSelectedEmployee(),
					this.getSelectedClient(), this.getSelectedEmployeesObservableList(),
					this.getSelectedIrregularScheduleObservableList(), this.getIsAddingRegularOvertime(),
					this.primaryStage, isShowSetEmployeeScheduleEncodingOvertimeWindowFromMain);
		}
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.showBrowseEmployeeScheduleEncoding();
	}

	@Override
	public void initializeObjects() {
		// TODO Auto-generated method stub
		this.allDateOfTheMonthObservableList = FXCollections.observableArrayList();
		this.allMonthsNameObservableList = FXCollections.observableArrayList();
		this.allDateOfTheMonthFormattedObservableList = FXCollections.observableArrayList();
		this.clientObservableList = FXCollections.observableArrayList();
		this.employmentHistoryList = new ArrayList<>();
		this.employeeObservableList = FXCollections.observableArrayList();
		this.employmentHistoryObservableList = FXCollections.observableArrayList();
		this.selectedEmployee = new Employee();
		this.employmentHistory = new EmploymentHistory();
		this.selectedClient = new Client();
		this.selectedEmploymentHistory = new EmploymentHistory();
		this.scheduleEncodingList = FXCollections.observableArrayList();
		this.selectedEmployeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();
		this.browseEmployeeScheduleEncodingController = new BrowseEmployeeScheduleEncodingController();
		this.allIrregularScheduleObservableList = FXCollections.observableArrayList();
		this.allRegularScheduleObservableList = FXCollections.observableArrayList();
		this.allIrregularScheduleList = new ArrayList<>();
		this.allRegularScheduleList = new ArrayList<>();
		this.allOvertimeScheduleObservableList = FXCollections.observableArrayList();
		this.observableListEmployeesByClientDepartment = FXCollections.observableArrayList();
		this.selectedEmployeesObservableList = FXCollections.observableArrayList();
		this.employeeScheduleEncodingToBeSaveList = new ArrayList<>();
		this.modifyDetailsErrorAddList = new ArrayList<>();
		this.modifyDetailsErrorUpdateList = new ArrayList<>();
		this.enabledValidatedTextFieldList = new ArrayList<>();
		this.schedulesByClientObservableList = FXCollections.observableArrayList();
		this.objectToModifySelectedObjectList = new ArrayList<>();
		this.isAddingRegularOvertime = false;
		this.selectedIrregularScheduleObservableList = FXCollections.observableArrayList();
		this.allOvertimeScheduleIrregularObservableList = FXCollections.observableArrayList();
		try {
			this.employeeScheduleEncodingIrregularMain = new EmployeeScheduleEncodingIrregularMain();
			this.employeeScheduleEncodingOvertimeMain = new EmployeeScheduleEncodingOvertimeMain();
			this.clientMain = new ClientMain();
			this.scheduleEncodingMain = new ScheduleEncodingMain();
			this.employmentHistoryMain = new EmploymentHistoryMain();
			this.scheduleEncodingReferenceMain = new ScheduleEncodingReferenceMain();
			this.scheduleEncodingMain.initializeObjects();
			this.scheduleEncodingReferenceMain.initializeObjects();
			this.departmentMain = new DepartmentMain();
			this.selectedDepartment = new Department();
			this.employeeMain = new EmployeeMain();
			this.departmentByClientObservableList = FXCollections.observableArrayList();
			this.observableListEmployeesByClientDepartment = FXCollections.observableArrayList();
			// this.setSelectedEmployee(null);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public void populateClient() {
		if (this.getClientMain().getClientByUser(this.getUser()) != null) {
			this.clientObservableList.setAll(this.getClientMain().getClientByUser(this.getUser()));
		}
	}

	public void populateTableViewRegularObservableList() {
		List<ScheduleEncoding> listScheduleEncodingImport = new ArrayList<>();
		listScheduleEncodingImport.addAll(this.getScheduleEncodingMain()
				.getAllScheduleUploadedByEmployeeId(this.getSelectedEmployee().getEmployeeCode()));

		if (!listScheduleEncodingImport.isEmpty()) {
			this.allRegularScheduleObservableList.setAll(listScheduleEncodingImport);
		} else {
			EmployeeScheduleEncoding employeeScheduleEncoding = new EmployeeScheduleEncoding();

			employeeScheduleEncoding = this
					.getEmployeeScheduleByEmployeeID(this.getSelectedEmployee().getEmployeeCode());

			if (employeeScheduleEncoding != null) {
				this.allRegularScheduleObservableList
						.setAll(this.getScheduleEncodingMain().getAllScheduleByPrikeyReferenceClient(
								employeeScheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference()));
			}
		}
	}

	public void populateTableViewIrregularObservableList() {
		if (this.getSelectedEmployee() != null) {
			if (this.getAllIrregularScheduleList() != null && !this.getAllIrregularScheduleList().isEmpty()) {
				this.getAllIrregularScheduleList().clear();
			}
		}

		this.getAllIrregularScheduleList().addAll(this.getEmployeeScheduleEncodingIrregularMain()
				.getAllEmployeeIrregularScheduleByEmployeeID(this.getSelectedEmployee().getEmployeeCode()));

		LinkedHashMap<EmployeeScheduleEncodingIrregular, Boolean> employeeIrregularScheduleMap = new LinkedHashMap<>();

		if (this.getAllIrregularScheduleList() != null) {
			for (EmployeeScheduleEncodingIrregular employeeIrregularSchedule : this.getAllIrregularScheduleList()) {
				employeeIrregularScheduleMap.put(employeeIrregularSchedule, false);
			}

			this.getAllIrregularScheduleObservableList().setAll(employeeIrregularScheduleMap.entrySet());
			ObservableListUtil.sort(this.getAllIrregularScheduleObservableList(),
					p -> new SimpleDateFormat("MMM. dd, yyyy").format(p.getKey().getDateSchedule()));
		}

	}

	public void populateDepartment() {

		this.departmentByClientObservableList
				.setAll(this.getDepartmentMain().getDepartmentByClientCode(this.getSelectedClient()));

	}

	public void populateEmployee() {
		if (this.getSelectedClient() != null) {

			if (this.getSelectedDepartment() != null) {
				// this.employeeObservableList.setAll(this.getEmployeeMain().getAllActiveEmployeeByDepartmentNo(
				// this.getSelectedDepartment().getDepartmentCode().toString()));

				this.employeeObservableList.setAll(this.getEmployeeMain().getAllEmployeeByClientCdDepartmentNo(
						this.getSelectedClient().getClientCode(), this.getSelectedDepartment().getDepartmentCode()));

				return;
			}
			this.employeeObservableList.setAll(this.getEmployeeMain()
					.getAllEmployeeByClientCdDepartmentNo(this.getSelectedClient().getClientCode(), null));

		}
	}

	public void populateRegularOvertimeSchedule() {

		this.allOvertimeScheduleObservableList.setAll(this.getEmployeeScheduleEncodingOvertimeMain()
				.getAllOvertimeRegularByEmployeeId(this.getSelectedEmployee().getEmployeeCode()));

	}

	public void populateIrregularOvertimeSchedule(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {

		this.allOvertimeScheduleIrregularObservableList.setAll(this.getEmployeeScheduleEncodingOvertimeMain()
				.getAllOvertimeIrregularByEmployeeIdAndPrikeyIrregularSchedule(employeeScheduleEncodingIrregular,
						this.getSelectedEmployee().getEmployeeCode()));

	}

	public void populateObservableListEmployees() {
		LinkedHashMap<Employee, Boolean> employeeMap = new LinkedHashMap<>();

		if (this.employeeObservableList != null) {
			for (Employee employee : this.employeeObservableList) {
				employeeMap.put(employee, false);
			}

			this.getObservableListEmployeesByClientDepartment().setAll(employeeMap.entrySet());
			ObservableListUtil.sort(this.getObservableListEmployeesByClientDepartment(),
					p -> p.getKey().getEmployeeFullName());
		}
	}

	public void getSelectedEmployeesFromTableView() {
		this.getSelectedEmployeesObservableList().clear();

		this.getObservableListEmployeesByClientDepartment().forEach(p -> {
			if (p.getValue().equals(true)) {
				this.getSelectedEmployeesObservableList().add(p);
			}
		});
	}

	public void getSelectedIrregularSchedule() {
		this.getSelectedIrregularScheduleObservableList().clear();

		this.getAllIrregularScheduleObservableList().forEach(p -> {
			if (p.getValue().equals(true)) {
				this.getSelectedIrregularScheduleObservableList().add(p);
			}
		});

	}

	public void populateSchedulePreset() {
		this.schedulesByClientObservableList
				.setAll(this.getScheduleEncodingReferenceMain().getAllScheduleByClientCode(this.getSelectedClient()));
	}

	public ScheduleEncodingReference setScheduleReferenceObject(String schedName) {
		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();

		scheduleEncodingReference.setScheduleName(schedName);
		scheduleEncodingReference.setClient(this.getSelectedClient());
		scheduleEncodingReference.setUser(this.getUser());
		scheduleEncodingReference.setChangedOnDate(this.getDateNow());
		scheduleEncodingReference.setChangedInComputer(this.getComputerName());

		return scheduleEncodingReference;
	}

	public ObservableList<Map.Entry<Employee, Boolean>> getObservableListEmployeesByClientDepartment() {
		return observableListEmployeesByClientDepartment;
	}

	public void setObservableListEmployeesByClientDepartment(
			ObservableList<Map.Entry<Employee, Boolean>> observableListEmployeesByClientDepartment) {
		this.observableListEmployeesByClientDepartment = observableListEmployeesByClientDepartment;
	}

	public ObservableList<Client> getClientObservableList() {
		return clientObservableList;
	}

	public void setClientObservableList(ObservableList<Client> clientObservableList) {
		this.clientObservableList = clientObservableList;
	}

	public ClientMain getClientMain() {
		return clientMain;
	}

	public void setClientMain(ClientMain clientMain) {
		this.clientMain = clientMain;
	}

	public ScheduleEncodingMain getScheduleEncodingMain() {
		return scheduleEncodingMain;
	}

	public void setScheduleEncodingMain(ScheduleEncodingMain scheduleEncodingMain) {
		this.scheduleEncodingMain = scheduleEncodingMain;
	}

	public ObservableList<Date> getAllDateOfTheMonthObservableList() {
		return allDateOfTheMonthObservableList;
	}

	public void setAllDateOfTheMonthObservableList(ObservableList<Date> allDateOfTheMonthObservableList) {
		this.allDateOfTheMonthObservableList = allDateOfTheMonthObservableList;
	}

	public ObservableList<String> getAllMonthsNameObservableList() {
		return allMonthsNameObservableList;
	}

	public void setAllMonthsNameObservableList(ObservableList<String> allMonthsNameObservableList) {
		this.allMonthsNameObservableList = allMonthsNameObservableList;
	}

	public ObservableList<String> getAllDateOfTheMonthFormattedObservableList() {
		return allDateOfTheMonthFormattedObservableList;
	}

	public void setAllDateOfTheMonthFormattedObservableList(
			ObservableList<String> allDateOfTheMonthFormattedObservableList) {
		this.allDateOfTheMonthFormattedObservableList = allDateOfTheMonthFormattedObservableList;
	}

	public EmploymentHistoryMain getEmploymentHistoryMain() {
		return employmentHistoryMain;
	}

	public void setEmploymentHistoryMain(EmploymentHistoryMain employmentHistoryMain) {
		this.employmentHistoryMain = employmentHistoryMain;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public List<EmploymentHistory> getEmploymentHistoryList() {
		return employmentHistoryList;
	}

	public void setEmploymentHistoryList(List<EmploymentHistory> employmentHistoryList) {
		this.employmentHistoryList = employmentHistoryList;
	}

	public ObservableList<Employee> getEmployeeObservableList() {
		return employeeObservableList;
	}

	public void setEmployeeObservableList(ObservableList<Employee> employeeObservableList) {
		this.employeeObservableList = employeeObservableList;
	}

	public ObservableList<EmploymentHistory> getEmploymentHistoryObservableList() {
		return employmentHistoryObservableList;
	}

	public void setEmploymentHistoryObservableList(ObservableList<EmploymentHistory> employmentHistoryObservableList) {
		this.employmentHistoryObservableList = employmentHistoryObservableList;
	}

	public EmploymentHistory getEmploymentHistory() {
		return employmentHistory;
	}

	public void setEmploymentHistory(EmploymentHistory employmentHistory) {
		this.employmentHistory = employmentHistory;
	}

	public Client getSelectedClient() {
		return selectedClient;
	}

	public void setSelectedClient(Client selectedClient) {
		this.selectedClient = selectedClient;
	}

	public EmploymentHistory getSelectedEmploymentHistory() {
		return selectedEmploymentHistory;
	}

	public void setSelectedEmploymentHistory(EmploymentHistory selectedEmploymentHistory) {
		this.selectedEmploymentHistory = selectedEmploymentHistory;
	}

	public ScheduleEncodingReferenceMain getScheduleEncodingReferenceMain() {
		return scheduleEncodingReferenceMain;
	}

	public void setScheduleEncodingReferenceMain(ScheduleEncodingReferenceMain scheduleEncodingReferenceMain) {
		this.scheduleEncodingReferenceMain = scheduleEncodingReferenceMain;
	}

	public ObservableList<ScheduleEncoding> getScheduleEncodingList() {
		return scheduleEncodingList;
	}

	public void setScheduleEncodingList(ObservableList<ScheduleEncoding> scheduleEncodingList) {
		this.scheduleEncodingList = scheduleEncodingList;
	}

	public EmployeeScheduleEncodingIrregularMain getEmployeeScheduleEncodingIrregularMain() {
		return employeeScheduleEncodingIrregularMain;
	}

	public void setEmployeeScheduleEncodingIrregularMain(
			EmployeeScheduleEncodingIrregularMain employeeScheduleEncodingIrregularMain) {
		this.employeeScheduleEncodingIrregularMain = employeeScheduleEncodingIrregularMain;
	}

	public EmployeeScheduleEncodingIrregular getSelectedEmployeeScheduleEncodingIrregular() {
		return selectedEmployeeScheduleEncodingIrregular;
	}

	public void setSelectedEmployeeScheduleEncodingIrregular(
			EmployeeScheduleEncodingIrregular selectedEmployeeScheduleEncodingIrregular) {
		this.selectedEmployeeScheduleEncodingIrregular = selectedEmployeeScheduleEncodingIrregular;
	}

	public BrowseEmployeeScheduleEncodingController getBrowseEmployeeScheduleEncodingController() {
		return browseEmployeeScheduleEncodingController;
	}

	public void setBrowseEmployeeScheduleEncodingController(
			BrowseEmployeeScheduleEncodingController browseEmployeeScheduleEncodingController) {
		this.browseEmployeeScheduleEncodingController = browseEmployeeScheduleEncodingController;
	}

	public ObservableList<ScheduleEncoding> getAllRegularScheduleObservableList() {
		return allRegularScheduleObservableList;
	}

	public void setAllRegularScheduleObservableList(ObservableList<ScheduleEncoding> allRegularScheduleObservableList) {
		this.allRegularScheduleObservableList = allRegularScheduleObservableList;
	}

	public List<EmployeeScheduleEncodingIrregular> getAllIrregularScheduleList() {
		return allIrregularScheduleList;
	}

	public void setAllIrregularScheduleList(List<EmployeeScheduleEncodingIrregular> allIrregularScheduleList) {
		this.allIrregularScheduleList = allIrregularScheduleList;
	}

	public List<ScheduleEncoding> getAllRegularScheduleList() {
		return allRegularScheduleList;
	}

	public void setAllRegularScheduleList(List<ScheduleEncoding> allRegularScheduleList) {
		this.allRegularScheduleList = allRegularScheduleList;
	}

	public EmployeeScheduleEncodingOvertimeMain getEmployeeScheduleEncodingOvertimeMain() {
		return employeeScheduleEncodingOvertimeMain;
	}

	public void setEmployeeScheduleEncodingOvertimeMain(
			EmployeeScheduleEncodingOvertimeMain employeeScheduleEncodingOvertimeMain) {
		this.employeeScheduleEncodingOvertimeMain = employeeScheduleEncodingOvertimeMain;
	}

	public ObservableList<EmployeeScheduleEncodingOvertime> getAllOvertimeScheduleObservableList() {
		return allOvertimeScheduleObservableList;
	}

	public void setAllOvertimeScheduleObservableList(
			ObservableList<EmployeeScheduleEncodingOvertime> allOvertimeScheduleObservableList) {
		this.allOvertimeScheduleObservableList = allOvertimeScheduleObservableList;
	}

	public DepartmentMain getDepartmentMain() {
		return departmentMain;
	}

	public void setDepartmentMain(DepartmentMain departmentMain) {
		this.departmentMain = departmentMain;
	}

	public ObservableList<Department> getDepartmentByClientObservableList() {
		return departmentByClientObservableList;
	}

	public void setDepartmentByClientObservableList(ObservableList<Department> departmentByClientObservableList) {
		this.departmentByClientObservableList = departmentByClientObservableList;
	}

	public Department getSelectedDepartment() {
		return selectedDepartment;
	}

	public void setSelectedDepartment(Department selectedDepartment) {
		this.selectedDepartment = selectedDepartment;
	}

	public EmployeeMain getEmployeeMain() {
		return employeeMain;
	}

	public void setEmployeeMain(EmployeeMain employeeMain) {
		this.employeeMain = employeeMain;
	}

	public ObservableList<Map.Entry<Employee, Boolean>> getSelectedEmployeesObservableList() {
		return selectedEmployeesObservableList;
	}

	public void setSelectedEmployeesObservableList(
			ObservableList<Map.Entry<Employee, Boolean>> selectedEmployeesObservableList) {
		this.selectedEmployeesObservableList = selectedEmployeesObservableList;
	}

	public List<EmployeeScheduleEncoding> getEmployeeScheduleEncodingToBeSaveList() {
		return employeeScheduleEncodingToBeSaveList;
	}

	public void setEmployeeScheduleEncodingToBeSaveList(
			List<EmployeeScheduleEncoding> employeeScheduleEncodingToBeSaveList) {
		this.employeeScheduleEncodingToBeSaveList = employeeScheduleEncodingToBeSaveList;
	}

	public List<EmployeeScheduleEncoding> getModifyDetailsErrorAddList() {
		return modifyDetailsErrorAddList;
	}

	public void setModifyDetailsErrorAddList(List<EmployeeScheduleEncoding> modifyDetailsErrorAddList) {
		this.modifyDetailsErrorAddList = modifyDetailsErrorAddList;
	}

	public List<EmployeeScheduleEncoding> getModifyDetailsErrorUpdateList() {
		return modifyDetailsErrorUpdateList;
	}

	public void setModifyDetailsErrorUpdateList(List<EmployeeScheduleEncoding> modifyDetailsErrorUpdateList) {
		this.modifyDetailsErrorUpdateList = modifyDetailsErrorUpdateList;
	}

	public List<ValidatedTextField> getEnabledValidatedTextFieldList() {
		return enabledValidatedTextFieldList;
	}

	public void setEnabledValidatedTextFieldList(List<ValidatedTextField> enabledValidatedTextFieldList) {
		this.enabledValidatedTextFieldList = enabledValidatedTextFieldList;
	}

	public ObservableList<ScheduleEncodingReference> getSchedulesByClientObservableList() {
		return schedulesByClientObservableList;
	}

	public void setSchedulesByClientObservableList(
			ObservableList<ScheduleEncodingReference> schedulesByClientObservableList) {
		this.schedulesByClientObservableList = schedulesByClientObservableList;
	}

	public List<ScheduleEncoding> getObjectToModifySelectedObjectList() {
		return objectToModifySelectedObjectList;
	}

	public void setObjectToModifySelectedObjectList(List<ScheduleEncoding> objectToModifySelectedObjectList) {
		this.objectToModifySelectedObjectList = objectToModifySelectedObjectList;
	}

	public ObservableList<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> getAllIrregularScheduleObservableList() {
		return allIrregularScheduleObservableList;
	}

	public void setAllIrregularScheduleObservableList(
			ObservableList<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> allIrregularScheduleObservableList) {
		this.allIrregularScheduleObservableList = allIrregularScheduleObservableList;
	}

	public Boolean getIsAddingRegularOvertime() {
		return isAddingRegularOvertime;
	}

	public void setIsAddingRegularOvertime(Boolean isAddingRegularOvertime) {
		this.isAddingRegularOvertime = isAddingRegularOvertime;
	}

	public ObservableList<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> getSelectedIrregularScheduleObservableList() {
		return selectedIrregularScheduleObservableList;
	}

	public void setSelectedIrregularScheduleObservableList(
			ObservableList<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> selectedIrregularScheduleObservableList) {
		this.selectedIrregularScheduleObservableList = selectedIrregularScheduleObservableList;
	}

	public ObservableList<EmployeeScheduleEncodingOvertime> getAllOvertimeScheduleIrregularObservableList() {
		return allOvertimeScheduleIrregularObservableList;
	}

	public void setAllOvertimeScheduleIrregularObservableList(
			ObservableList<EmployeeScheduleEncodingOvertime> allOvertimeScheduleIrregularObservableList) {
		this.allOvertimeScheduleIrregularObservableList = allOvertimeScheduleIrregularObservableList;
	}

}
