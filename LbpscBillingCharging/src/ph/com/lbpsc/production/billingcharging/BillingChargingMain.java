package ph.com.lbpsc.production.billingcharging;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ph.com.lbpsc.production.billing.BillingMain;
import ph.com.lbpsc.production.billingcharging.data.BillingChargingDao;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.billingcharging.view.BrowseBillingChargingController;
import ph.com.lbpsc.production.billingcharging.view.EditBillingChargingController;
import ph.com.lbpsc.production.billingcharging.view.FilterBillingController;
import ph.com.lbpsc.production.billingcharging.view.PrintBillingChargingController;
import ph.com.lbpsc.production.billingcharging.view.SelectEmploymentConfigurationController;
import ph.com.lbpsc.production.billingcharging.view.SetOvertimeBillingChargingController;
import ph.com.lbpsc.production.billingclientconfiguration.BillingClientConfigurationMain;
import ph.com.lbpsc.production.billingclientconfiguration.model.BillingClientConfiguration;
import ph.com.lbpsc.production.billingrate.BillingRateMain;
import ph.com.lbpsc.production.billingrate.model.BillingRate;
import ph.com.lbpsc.production.billingregisterprocessing.BillingRegisterProcessingMain;
import ph.com.lbpsc.production.billingreportclass.BillingReportClassMain;
import ph.com.lbpsc.production.client.ClientMain;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.ClientGroupMain;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.clientgroupdetails.ClientGroupDetailsMain;
import ph.com.lbpsc.production.company.CompanyMain;
import ph.com.lbpsc.production.configurationovertime.ConfigurationOvertimeMain;
import ph.com.lbpsc.production.configurationovertime.model.ConfigurationOvertime;
import ph.com.lbpsc.production.department.DepartmentMain;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.EmployeeMain;
import ph.com.lbpsc.production.employeeconfiguration.EmployeeConfigurationMain;
import ph.com.lbpsc.production.employeeconfiguration.model.EmployeeConfiguration;
import ph.com.lbpsc.production.employeeconfigurationreference.model.EmployeeConfigurationReference;
import ph.com.lbpsc.production.employmentconfiguration.EmploymentConfigurationMain;
import ph.com.lbpsc.production.employmentconfiguration.model.EmploymentConfiguration;
import ph.com.lbpsc.production.masterclass.MasterMain;
import ph.com.lbpsc.production.overtime.OvertimeMain;
import ph.com.lbpsc.production.overtime.model.Overtime;
import ph.com.lbpsc.production.overtimetype.OvertimeTypeMain;
import ph.com.lbpsc.production.payroll.PayrollMain;
import ph.com.lbpsc.production.payroll.model.Payroll;
import ph.com.lbpsc.production.payrollclientconfiguration.PayrollClientConfigurationMain;
import ph.com.lbpsc.production.payrollclientconfiguration.model.PayrollClientConfiguration;
import ph.com.lbpsc.production.signatory.SignatoryMain;
import ph.com.lbpsc.production.statementofaccount.StatementOfAccountMain;
import ph.com.lbpsc.production.statementofaccount.model.StatementOfAccount;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lbpsc.production.userclientconfiguration.UserClientConfigurationMain;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.CompareDataUtil;
import ph.com.lbpsc.production.util.ErrorLog;
import ph.com.lbpsc.production.util.FxmlUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObjectCopyUtil;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.ProcessingMessage;

public class BillingChargingMain extends MasterMain<BillingCharging> {
	private ObservableList<BillingCharging> observableListBillingCharging = FXCollections.observableArrayList();
	private ObservableList<Department> observableListDepartment = FXCollections.observableArrayList();
	private ObservableList<ConfigurationOvertime> observableListConfigurationOvertime = FXCollections
			.observableArrayList();
	private ObservableList<StatementOfAccount> observableListStatementOfAccount = FXCollections.observableArrayList();
	private Payroll payroll;
	private OvertimeTypeMain overtimeTypeMain;
	private DepartmentMain departmentMain;

	private ConfigurationOvertimeMain configurationOvertimeMain = new ConfigurationOvertimeMain();
	private BillingClientConfigurationMain billingClientConfigurationMain = new BillingClientConfigurationMain();
	private UserClientConfigurationMain userClientConfigurationMain;
	private EmploymentConfigurationMain employmentConfigurationMain;
	private EmployeeConfigurationMain employeeConfigurationMain;
	private ClientMain clientMain;
	private ClientGroupMain clientGroupMain;
	private ClientGroupDetailsMain clientGroupDetailsMain;
	private CompanyMain companyMain;
	private BillingCharging billingCharging;
	private BillingClientConfiguration billingClientConfiguration;
	private PayrollClientConfiguration payrollClientConfiguration;

	private PayrollClientConfigurationMain payrollClientConfigurationMain;
	private BillingRegisterProcessingMain billingRegisterProcessingMain;
	private StatementOfAccountMain statementOfAccountMain;

	private OvertimeMain overtimeMain;
	private SignatoryMain signatoryMain;
	private Boolean isBillingChargingByDepartment;
	private EmployeeMain employeeMain;
	private PayrollMain payrollMain;
	private BillingRateMain billingRateMain;
	private BillingMain billingMain;
	private User user;

	private Map<String, Object> parameterMapPrinting;
	private BillingReportClassMain billingReportClassMain;
	private List<Payroll> listEvaluatedPayroll;
	private Payroll selectedPayroll;
	private EmploymentConfiguration selectedEmploymentConfiguration;
	private Client selectedClient;
	private ClientGroup selectedClientGroup;
	private ObservableList<Client> observableListClient;
	private ObservableList<ClientGroup> observableListClientGroup;

	public BillingChargingMain() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		super(BillingCharging.class);
	}

	@Override
	public boolean createMainObject(BillingCharging billingCharging) {
		return this.createBillingCharging(billingCharging);

	}

	@Override
	public boolean updateMainObject(BillingCharging billingCharging) {
		return this.updateBillingCharging(billingCharging);
	}

	@Override
	public boolean deleteMainObject(BillingCharging billingCharging) {
		return this.deleteBillingCharging(billingCharging);
	}

	@Override
	public AnchorPane getRootLayout() throws IOException {
		return this.getRootLayoutBillingCharging();
	}

	@Override
	public void initializeObjects() {
		try {
			this.isBillingChargingByDepartment = false;
			this.overtimeMain = new OvertimeMain();
			this.departmentMain = new DepartmentMain();
			this.clientGroupMain = new ClientGroupMain();
			this.billingRateMain = new BillingRateMain();
			this.employmentConfigurationMain = new EmploymentConfigurationMain();
			this.employeeConfigurationMain = new EmployeeConfigurationMain();
			this.clientMain = new ClientMain();
			this.companyMain = new CompanyMain();
			this.billingCharging = new BillingCharging();
			this.userClientConfigurationMain = new UserClientConfigurationMain();
			this.overtimeTypeMain = new OvertimeTypeMain();
			this.overtimeMain = new OvertimeMain();
			this.payrollClientConfigurationMain = new PayrollClientConfigurationMain();
			this.signatoryMain = new SignatoryMain();
			this.billingRegisterProcessingMain = new BillingRegisterProcessingMain();
			this.statementOfAccountMain = new StatementOfAccountMain();
			this.employeeMain = new EmployeeMain();
			this.payrollMain = new PayrollMain();
			this.parameterMapPrinting = new HashMap<String, Object>();
			this.observableListStatementOfAccount = FXCollections.observableArrayList();
			this.billingReportClassMain = new BillingReportClassMain();
			this.billingMain = new BillingMain();
			this.listEvaluatedPayroll = new ArrayList<>();
			this.selectedEmploymentConfiguration = new EmploymentConfiguration();
			this.selectedPayroll = new Payroll();
			this.clientGroupDetailsMain = new ClientGroupDetailsMain();
			this.selectedClient = new Client();
			this.selectedClientGroup = new ClientGroup();
			this.observableListClient = FXCollections.observableArrayList();
			this.observableListClientGroup = FXCollections.observableArrayList();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public boolean createBillingCharging(BillingCharging billingCharging) {
		try {
			BillingChargingDao billingChargingDao = new BillingChargingDao(sqlSessionFactory);
			return (billingChargingDao.createData(billingCharging) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, null);
		}
		return false;
	}

	public boolean updateBillingCharging(BillingCharging billingCharging) {
		try {
			BillingChargingDao billingChargingDao = new BillingChargingDao(sqlSessionFactory);
			return (billingChargingDao.updateData(billingCharging) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, null);
		}
		return false;
	}

	public boolean deleteBillingCharging(BillingCharging billingCharging) {
		try {
			BillingChargingDao billingChargingDao = new BillingChargingDao(sqlSessionFactory);
			return (billingChargingDao.deleteData(billingCharging) > 0) ? true : false;
		} catch (PersistenceException e) {
			ErrorLog.showErrorLog(e, null);
		}
		return false;
	}

	public BillingRate getBillingRate(Payroll payroll, boolean isOvertime,
			EmploymentConfiguration employmentConfiguration) {
		System.out.println("getBillingRate selectEmploymentConfigController ===================1");
		System.out.println("payroll prikey: " + payroll.getPrimaryKey());

		BillingRate billingRate = new BillingRate();

		billingRate.setEmploymentConfiguration(
				employmentConfiguration != null ? employmentConfiguration : payroll.getActualEmploymentConfiguration());
		System.out.println("billingRate emp config: " + billingRate.getEmploymentConfiguration().getPrimaryKey());

		billingRate.setEffectiveOnDate(this.getPayrollPeriod(payroll));
		billingRate.setIsActive(true);
		billingRate.setIsOvertime(isOvertime);
		billingRate.setBillingCategory(this.getBillingCategory(payroll, isOvertime));

		System.out.println("PARAMETERS: GET MAX BILL RATE LIST");

		System.out.println(employmentConfiguration.getPrimaryKey());
		System.out.println(employmentConfiguration.getClient().getClientCode());
		System.out.println(employmentConfiguration.getDepartment().getDepartmentCode());
		System.out.println(employmentConfiguration.getPosition().getPositionCode());
		System.out.println(employmentConfiguration.getAmountOfPayRate());
		System.out.println(employmentConfiguration.getIsDaily());
		System.out.println(billingRate.getBillingCategory()); // <-- dapat same billing cat sa bill rate encoding = 1
		System.out.println(billingRate.getEffectiveOnDate());
		System.out.println(billingRate.getIsOvertime());
		System.out.println(billingRate.getIsActive());

		System.out.println("getBillingRate selectEmploymentConfigController ===================2");

		List<BillingRate> resultBillingRateList = this.getBillingRateMain().getMaxBillingRateList(billingRate);

		System.out.println("resultBillingRateList: " + resultBillingRateList.size());

		if (resultBillingRateList != null && !resultBillingRateList.isEmpty()) {
			if (resultBillingRateList.size() == 1) {
				return resultBillingRateList.get(0);
			} else if (resultBillingRateList.size() > 1) {
				// this.notificationList.put(payroll.getEmploymentHistory(), " Multiple bill
				// rates found.");
				AlertUtil.showInformationAlert("Multiple bill rates found.", this.getPrimaryStage());
			}
		} else {
			if (isOvertime == false) {
				if (!this.checkIfPayrollExists(payroll, this.getListEvaluatedPayroll())) {
					if (billingRate.getBillingCategory() == null) {
						return null;
					} else {
						// this.notificationList.put(payroll.getEmploymentHistory(), " No bill rate
						// found.");
						// AlertUtil.showInformationAlert("No bill rate found.", this.getDialogStage());
						return null;
					}
				}
			}
		}
		return null;
	}

	public List<BillingCharging> getAllBillingCharging() {
		return new BillingChargingDao(sqlSessionFactory).getAllData();
	}

	public BillingCharging getBillingChargingByKey(Integer primaryKey) {
		return new BillingChargingDao(sqlSessionFactory).getDataByKey(primaryKey);
	}

	public List<BillingCharging> getAllBillingChargingByDate(Date batchDate) {
		return new BillingChargingDao(sqlSessionFactory).getBillingChargingByBatchDate(batchDate);
	}

	public List<BillingCharging> getBillingChargingByClientAndBatchDate(String clientCode, Date batchDate) {
		return new BillingChargingDao(sqlSessionFactory).getBillingChargingByClientAndBatchDate(clientCode, batchDate);
	}

	public List<BillingCharging> getAllBillingChargingByPayroll(Payroll payroll) {
		return new BillingChargingDao(sqlSessionFactory).getAllBillingChargingByPayroll(payroll);
	}

	public List<BillingCharging> getAllPendingBillingChargingByClient(List<Client> listClient) {
		return new BillingChargingDao(sqlSessionFactory).getAllPendingBillingChargingByClient(listClient);
	}

	public List<BillingCharging> getAllPendingBillingChargingByClientBatchDate(List<Client> listClient,
			Date batchDate) {
		return new BillingChargingDao(sqlSessionFactory).getAllPendingBillingChargingByClientBatchDate(listClient,
				batchDate);
	}

	public List<BillingCharging> getAllBillingChargingByClientGroupAndBatchDate(int prikeyGroup, Date batchDate) {
		return new BillingChargingDao(sqlSessionFactory).getAllBillingChargingByClientGroupAndBatchDate(prikeyGroup,
				batchDate);
	}

	public List<BillingCharging> getBillingChargingByClientCodeAndPayFrom(String clientCode, Date payFromA,
			Date payFromB) {
		return new BillingChargingDao(sqlSessionFactory).getBillingChargingByClientCodeAndPayFrom(clientCode, payFromA,
				payFromB);
	}

	public List<BillingCharging> getAllBillingChargingByClientOrClientGroupAndBatchDate(Client client,
			ClientGroup clientGroup, Date batchDate) {
		return new BillingChargingDao(sqlSessionFactory).getAllBillingChargingByClientOrClientGroupAndBatchDate(client,
				clientGroup, batchDate);
	}

	public List<BillingCharging> getAllBillingChargingByPayrollPrikey(Integer prikeyPayroll) {
		return new BillingChargingDao(sqlSessionFactory).getAllBillingChargingByPayrollPrikey(prikeyPayroll);
	}

	public List<BillingCharging> getAllExtendedDutyBillingChargingByClientCdBatchDate(Date batchDate,
			String clientCode) {
		return new BillingChargingDao(sqlSessionFactory).getAllExtendedDutyBillingChargingByClientCdBatchDate(batchDate,
				clientCode);
	}

	public List<BillingCharging> getAllBillingChargingBatchDateClientOrGroupCoverUpOrExtendedDuty(Client client,
			ClientGroup clientGroup, Date batchDate, Integer isExtendedDuty) {
		return new BillingChargingDao(sqlSessionFactory)
				.getAllBillingChargingBatchDateClientOrGroupCoverUpOrExtendedDuty(client, clientGroup, batchDate,
						isExtendedDuty);
	}

	public AnchorPane getRootLayoutBillingCharging() throws IOException {
		ProcessingMessage.showProcessingMessage(this.getPrimaryStage());
		this.initializeObjects();
		FxmlUtil<BrowseBillingChargingController> fxmlUtil = new FxmlUtil<>(
				this.getClass().getResource("view/BrowseBillingCharging.fxml"));
		AnchorPane rootLayout = fxmlUtil.getFxmlPane();
		BrowseBillingChargingController browseBillingChargingController = fxmlUtil.getController();
		browseBillingChargingController.setMainApplication(this);
		ProcessingMessage.closeProcessingMessage();

		return rootLayout;
	}

	public boolean showEditBillingCharging(BillingCharging billingCharging, ModificationType modificationType) {
		boolean showForm = false;
		try {
			ProcessingMessage.showProcessingMessage(this.getPrimaryStage());

			this.setBillingClientConfiguration(billingCharging.getClient() != null
					? this.getBillingClientConfigurationMain()
							.getBillingClientConfigurationByClientCode(billingCharging.getClient())
					: this.getBillingClientConfigurationMain()
							.getBillingClientConfigurationByClientGroup(billingCharging.getClientGroup()));
			System.out.println(this.getBillingClientConfigurationMain()
					.getBillingClientConfigurationByClientGroup(billingCharging.getClientGroup()));

			FxmlUtil<EditBillingChargingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditBillingCharging.fxml"));
			AnchorPane editBillingChargingLayout = fxmlUtil.getFxmlPane();
			EditBillingChargingController editBillingChargingController = fxmlUtil.getController();

			this.setPayrollClientConfiguration(this.getPayrollClientConfigurationMain()
					.getClientConfigurationByClientCode(billingCharging.getClient()));

			this.overtimeMain.setBillingClientConfiguration(this.billingClientConfiguration);
			this.overtimeMain.setPrimaryStage(this.primaryStage);
			editBillingChargingController.setAnchorPaneOvertime(this.overtimeMain.getRootLayout());
			this.overtimeMain.populateDefaultBillingOvertimeList(billingCharging.getOvertimeList(),
					billingClientConfiguration);

			editBillingChargingController.setMainApplication(this, modificationType);
			ProcessingMessage.closeProcessingMessage();
			showForm = editBillingChargingController.showEditDialogStage("Edit Billing Charging", this.primaryStage,
					editBillingChargingLayout, billingCharging);

			return showForm;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// public BigDecimal handleComputeBillRate(BillingCharging billingCharging) {
	// BigDecimal billRate = BigDecimal.ZERO;
	// if (billingCharging.getEmploymentConfiguration().getIsDaily() != null
	// && billingCharging.getEmploymentConfiguration().getIsDaily()) {
	// billRate = billingCharging.getEmploymentConfiguration().getAmountOfPayRate();
	// } else {
	// billRate =
	// billingCharging.getEmploymentConfiguration().getAmountOfPayRate().multiply(new
	// BigDecimal("12"))
	// .divide(billingCharging.getEmploymentConfiguration().getNumberOfWorkingDays(),
	// 2,
	// RoundingMode.HALF_UP);
	// }
	//
	// System.out.println("billRate: " + billRate);
	// return billRate;
	// }

	public boolean showSelectEmploymentBillingCharging(BillingCharging billingCharging,
			ModificationType modificationType) {
		boolean showForm = false;
		try {
			ProcessingMessage.showProcessingMessage(this.getPrimaryStage());
			this.setBillingClientConfiguration(
					this.getBillingClientConfigurationMain().getBillingClientConfigurationByClientCode(
							billingCharging.getPayroll().getActualEmploymentConfiguration().getClient()));

			FxmlUtil<SelectEmploymentConfigurationController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/SelectEmploymentConfiguration.fxml"));
			AnchorPane editBillingChargingLayout = fxmlUtil.getFxmlPane();
			SelectEmploymentConfigurationController selectEmploymentConfigurationController = fxmlUtil.getController();

			this.setPayrollClientConfiguration(this.getPayrollClientConfigurationMain()
					.getClientConfigurationByClientCode(billingCharging.getClient()));

			selectEmploymentConfigurationController.setMainApplication(this, modificationType);
			ProcessingMessage.closeProcessingMessage();
			showForm = selectEmploymentConfigurationController.showEditDialogStage("Edit Billing Charging",
					this.primaryStage, editBillingChargingLayout, billingCharging);

			return showForm;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void showPrintBillingCharging(ObservableList<Client> observableListClient, Client selectedClient,
			ClientGroup clientGroup, Date payFrom, Date payTo, Date batchDate, BillingCharging billingCharging,
			Stage stage) {
		try {
			FxmlUtil<PrintBillingChargingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/PrintBillingCharging.fxml"));
			AnchorPane printLayout = fxmlUtil.getFxmlPane();

			Stage dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.initOwner(this.getPrimaryStage());
			dialogStage.setTitle("Generate Report - Billing Charging");
			dialogStage.setResizable(false);

			PrintBillingChargingController controller = fxmlUtil.getController();
			dialogStage.setScene(new Scene(printLayout));

			controller.setClient(selectedClient);
			controller.setClientGroup(clientGroup);
			controller.setPayFrom(payFrom);
			controller.setPayTo(payTo);
			controller.setObservableListClient(observableListClient);
			controller.setBillingCharging(billingCharging);
			controller.setBatchDate(batchDate);

			// this.getBillingRegisterProcessingMain().initializeObjects();
			if (this.showFilterBilling(false)) {
				controller.setMainApplication(this);
				controller.setStage(dialogStage);

				dialogStage.showAndWait();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// try {
		// FXMLLoader loader = new FXMLLoader();
		// loader.setLocation(this.getClass().getResource("view/PrintBillingCharging.fxml"));
		// AnchorPane fxmlPane = (AnchorPane) loader.load();
		// PrintBillingChargingController controller = loader.getController();
		// Stage dialogStage = new Stage();
		// dialogStage.initStyle(StageStyle.UTILITY);
		// dialogStage.initModality(Modality.APPLICATION_MODAL);
		// dialogStage.setTitle("Generate Report - Billing Charging");
		// dialogStage.initOwner(this.getPrimaryStage());
		// dialogStage.setResizable(false);
		// Scene scene = new Scene(fxmlPane);
		// dialogStage.setScene(scene);
		//
		// controller.setClient(selectedClient);
		// controller.setPayFrom(payFrom);
		// controller.setPayTo(payTo);
		// controller.setObservableListClient(observableListClient);
		//
		// if (this.showFilterBilling()) {
		// controller.setMainApplication(this);
		// controller.setStage(dialogStage);
		//
		// dialogStage.showAndWait();
		// }
		//
		// return controller.isOkClicked();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return false;

	}

	public boolean showFilterBilling(boolean isOvertime) {
		try {
			String title = !isOvertime ? "Collect Statement of Account" : "Collect Statement of Account Overtime";

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("view/FilterBilling.fxml"));
			AnchorPane fxmlPane = (AnchorPane) loader.load();
			FilterBillingController fxmlController = loader.getController();
			Stage dialogStage = new Stage();
			dialogStage.initStyle(StageStyle.UTILITY);
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setTitle(title);
			dialogStage.initOwner(this.getPrimaryStage());
			dialogStage.setResizable(false);
			Scene scene = new Scene(fxmlPane);
			dialogStage.setScene(scene);
			fxmlController.setMainApplication(this, dialogStage);
			dialogStage.showAndWait();

			return fxmlController.isOkClicked();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void fetchBillingClientConfiguration(BillingCharging billingCharging) {
		this.setBillingClientConfiguration(billingCharging.getClient() != null
				? this.getBillingClientConfigurationMain()
						.getBillingClientConfigurationByClientCode(billingCharging.getClient())
				: this.getBillingClientConfigurationMain()
						.getBillingClientConfigurationByClientGroup(billingCharging.getClientGroup()));
	}

	public void fetchPayrollClientConfiguration(BillingCharging billingCharging) {
		this.setPayrollClientConfiguration(this.getPayrollClientConfigurationMain()
				.getClientConfigurationByClientCode(billingCharging.getClient()));
	}

	public boolean showSetOvertimeBillingCharging(BillingCharging billingCharging, Overtime overtime,
			ModificationType modificationType) {
		try {
			FxmlUtil<SetOvertimeBillingChargingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/SetOvertimeBillingCharging.fxml"));
			AnchorPane overtimeLayout = fxmlUtil.getFxmlPane();
			SetOvertimeBillingChargingController setOvertimeBillingChargingController = fxmlUtil.getController();
			setOvertimeBillingChargingController.setMainApplication(this, modificationType);

			setOvertimeBillingChargingController.setSelectedOvertime(overtime);

			// return editBillingChargingController.showEditDialogStage("Edit Billing
			// Charging", primaryStage,
			// editBillingChargingLayout, billingCharging);
			return setOvertimeBillingChargingController.showEditDialogStage(
					modificationType + "Overtime Billing Charging", this.primaryStage, overtimeLayout, billingCharging);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean showEditBillingCharging(BillingCharging billingCharging, ModificationType modificationType,
			Stage primaryStage) {
		System.out.println("showEditBillingCharging");
		System.out.println(billingCharging.getPrimaryKey());
		System.out.println(billingCharging.getEmploymentConfiguration().getPrimaryKey());
		// this.initializeObjects();
		// querying again to get the unedited existing overtime list,
		// else the unsaved edited overtime previously will show on edit form
		// billingCharging =
		// this.getBillingChargingByKey(billingCharging.getPrimaryKey());
		try {
			ProcessingMessage.showProcessingMessage(this.getPrimaryStage());
			this.fetchBillingClientConfiguration(billingCharging);
			this.fetchPayrollClientConfiguration(billingCharging);

			FxmlUtil<EditBillingChargingController> fxmlUtil = new FxmlUtil<>(
					this.getClass().getResource("view/EditBillingCharging.fxml"));
			AnchorPane editBillingChargingLayout = fxmlUtil.getFxmlPane();
			EditBillingChargingController editBillingChargingController = fxmlUtil.getController();

			// this.overtimeMain.setBillingClientConfiguration(this.billingClientConfiguration);
			// this.overtimeMain.setPrimaryStage(primaryStage);
			// editBillingChargingController.setAnchorPaneOvertime(this.overtimeMain.getRootLayout());
			// this.overtimeMain.populateDefaultBillingOvertimeList(billingCharging.getOvertimeList(),
			// billingClientConfiguration);

			editBillingChargingController.setMainApplication(this, modificationType);

			// return editBillingChargingController.showEditDialogStage("Edit Billing
			// Charging", primaryStage,
			// editBillingChargingLayout, billingCharging);

			ProcessingMessage.closeProcessingMessage();
			return editBillingChargingController.showEditDialogStage(modificationType + "Billing Charging",
					primaryStage, editBillingChargingLayout, billingCharging);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public BigDecimal minutesToHours(Integer hours, Integer minutes) {
		BigDecimal hrs = new BigDecimal(hours);
		BigDecimal mins = new BigDecimal(minutes);

		return hrs.add(mins.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP));
	}

	// public boolean showEditBillingCharging(BillingCharging billingCharging,
	// ModificationType modificationType,
	// Stage primaryStage) {
	// try {
	// this.setBillingClientConfiguration(
	// this.getBillingClientConfigurationMain().getBillingClientConfigurationByClientCode(
	// billingCharging.getPayroll().getActualEmploymentConfiguration().getClient()));
	//
	// FxmlUtil<EditBillingChargingController> fxmlUtil = new FxmlUtil<>(
	// this.getClass().getResource("view/EditBillingCharging.fxml"));
	// AnchorPane editBillingChargingLayout = fxmlUtil.getFxmlPane();
	// EditBillingChargingController editBillingChargingController =
	// fxmlUtil.getController();
	//
	// this.overtimeMain.setBillingClientConfiguration(this.billingClientConfiguration);
	// this.overtimeMain.setPrimaryStage(this.primaryStage);
	// editBillingChargingController.setAnchorPaneOvertime(this.overtimeMain.getRootLayout());
	// this.overtimeMain.populateDefaultBillingOvertimeList(billingCharging.getOvertimeList(),
	// billingClientConfiguration);
	//
	// editBillingChargingController.setMainApplication(this, modificationType);
	//
	// return editBillingChargingController.showEditDialogStage("Edit Billing
	// Charging", primaryStage,
	// editBillingChargingLayout, billingCharging);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return false;
	// }

	public boolean isWithChanges(BillingCharging newBillingCharging, BillingCharging oldBillingCharging) {
		try {

			if (!CompareDataUtil.compare(newBillingCharging, oldBillingCharging, "department.departmentCode")) {
				return true;
			}

			if (!CompareDataUtil.compare(newBillingCharging, oldBillingCharging, "regularDays")) {
				return true;
			}

			if (!CompareDataUtil.compare(newBillingCharging, oldBillingCharging, "undertimeHours")) {
				return true;
			}

			if (!CompareDataUtil.compare(newBillingCharging, oldBillingCharging, "undertimeMinutes")) {
				return true;
			}

			if (!CompareDataUtil.compare(newBillingCharging, oldBillingCharging, "holidays")) {
				return true;
			}

			if (!CompareDataUtil.compare(newBillingCharging, oldBillingCharging, "workHours")) {
				return true;
			}

			// if (isWithChangesOvertimeList(oldBillingCharging, newBillingCharging)) {
			// return true;
			// }

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException
				| IllegalArgumentException | ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isWithChangesOvertimeList(BillingCharging oldBillingCharging, BillingCharging newBillingCharging) {
		System.out.println("isWithChangesOvertimeList2");
		System.out.println("oldBillingCharging.getOvertimeList(): " + oldBillingCharging.getOvertimeList().size());
		System.out.println("newBillingCharging.getOvertimeList(): " + newBillingCharging.getOvertimeList().size());

		List<Overtime> existingBillingChargingOvertimeList = new ArrayList<>();
		existingBillingChargingOvertimeList.addAll(this.getOvertimeMain()
				.getBillingChargingOvertimeByBillingChargingPrikey(oldBillingCharging.getPrimaryKey()));

		if (existingBillingChargingOvertimeList != null && newBillingCharging.getOvertimeList() != null
				&& (existingBillingChargingOvertimeList.size() == newBillingCharging.getOvertimeList().size())) {

			System.out.println("pass");

			for (Overtime firstOvertime : existingBillingChargingOvertimeList) {

				System.out.println("loop: " + firstOvertime.getOvertimeType().getOvertimeName());

				Overtime secondOvertime = ObservableListUtil.getObject(newBillingCharging.getOvertimeList(), P -> P
						.getOvertimeType().getPrimaryKey().equals(firstOvertime.getOvertimeType().getPrimaryKey()));

				// Overtime secondOvertime = newBillingCharging.getOvertimeList().stream()
				// .filter(p -> p.getOvertimeType().getPrimaryKey()
				// .compareTo(firstOvertime.getOvertimeType().getPrimaryKey()) == 0)
				// .map(p -> p).findFirst().orElse(null);

				System.out.println(secondOvertime == null);

				if (secondOvertime != null) {
					try {

						System.out.println(firstOvertime.getOvertimeType().getOvertimeName() + " : "
								+ firstOvertime.getHoursOfOvertime() + " : " + secondOvertime.getHoursOfOvertime());
						System.out.println(firstOvertime.getOvertimeType().getOvertimeName() + " : "
								+ firstOvertime.getMinutesOfOvertime() + " : " + secondOvertime.getMinutesOfOvertime());

						if (!CompareDataUtil.compare(firstOvertime, secondOvertime, "hoursOfOvertime")) {
							System.out.println("withChangesHours1");
							return true;
						}

						if (!CompareDataUtil.compare(firstOvertime, secondOvertime, "minutesOfOvertime")) {
							return true;
						}

						// BillingComputation test = new BillingComputation();
						// for (Overtime overtime : this.getOvertimeMain().getObservableListOvertime())
						// {
						// overtime.setAmountOfOvertime(
						// test.evaluateOvertime(overtime.getOvertimeType(),
						// billingCharging.getBillRate(),
						// overtime.getHoursOfOvertime(), overtime.getMinutesOfOvertime(),
						// new BigDecimal(this.getBillingClientConfiguration().getOvertimeDivisor())));
						// }
						//
						// System.out.println("amountOfOvertime");
						// System.out.println(
						// firstOvertime.getAmountOfOvertime() + " : " +
						// secondOvertime.getAmountOfOvertime());
						// if (!CompareDataUtil.compare(firstOvertime, secondOvertime,
						// "amountOfOvertime")) {
						// System.out.println("amountOfOvertime");
						// return true;
						// }
						//
						// TableViewUtil
						// .refreshTableView(this.getOvertimeMain().getRootController().tableViewPayrollOvertime);
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException | ParseException e) {
						e.printStackTrace();
					}
				} else {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public void populateOvertime(PayrollClientConfiguration payrollClienConfiguration,
			BillingClientConfiguration billingClientConfiguration) {
		System.out.println("populateOvertime : LbpscBillingChargingMain");
		this.observableListConfigurationOvertime.clear();
		// this.observableListConfigurationOvertime.addAll(this.getConfigurationOvertimeMain()
		// .getAllConfigurationOvertimeByPayrollClientConfig(payrollClienConfiguration.getPrimaryKey()));
		if (billingClientConfiguration != null) {
			this.observableListConfigurationOvertime.addAll(this.getConfigurationOvertimeMain()
					.getAllConfigurationOvertimeByBillingClientConfig(billingClientConfiguration.getPrimaryKey()));
		}

		// this.observableListConfigurationOvertime.addAll(this.getConfigurationOvertimeMain()
		// .getAllConfigurationOvertimeByBillingClientConfig(billingClienConfiguration.getPrimaryKey()));
	}

	public void setBillingChargingOvertime(BillingCharging billingCharging, Overtime overtime) {
		overtime.setBillingCharging(billingCharging);
		overtime.setUser(this.getUser());
		overtime.setChangedInComputer(this.getComputerName());
		overtime.setChangedOnDate(this.getDateNow());
	}

	public void saveBillingChargingOvertime(BillingCharging billingCharging) {
		System.out.println("saveBillingChargingOvertime");
		List<Overtime> existingOvertimeList = new ArrayList<>();

		existingOvertimeList.addAll(this.getOvertimeMain()
				.getBillingChargingOvertimeByBillingChargingPrikey(billingCharging.getPrimaryKey()));

		System.out.println("existingOvertimeList: " + existingOvertimeList.size());
		System.out.println(billingCharging.getOvertimeList() == null);

		if (billingCharging.getOvertimeList() == null) {
			System.out.println("if");
			for (Overtime overtime : existingOvertimeList) {
				this.overtimeMain.deleteBillingChargingOvertime(overtime);
			}
			return;
		} else {
			System.out.println("else");
			List<Overtime> overtimeToDeleteList = new ArrayList<>();
			List<Overtime> overtimeUpdatedList = new ArrayList<>();

			billingCharging.getOvertimeList().forEach(C -> {
				if (C.getHoursOfOvertime() != 0 || C.getMinutesOfOvertime() != 0) {
					Overtime overtime = new Overtime();
					this.setBillingChargingOvertime(billingCharging, C);

					if (C.getPrimaryKey() == null) {
						this.getOvertimeMain().createBillingChargingOvertime(C);
					} else {
						overtime = existingOvertimeList.stream()
								.filter(p -> p.getPrimaryKey().compareTo(C.getPrimaryKey()) == 0).findFirst().get();

						if (overtime != null) {
							overtimeUpdatedList.add(overtime);
							ObjectCopyUtil.copyProperties(C, overtime, Overtime.class);
							this.getOvertimeMain().updateBillingChargingOvertime(overtime);
						}
					}
				}
			});

			existingOvertimeList.forEach(overtime -> {
				System.out.println(overtime.getOvertimeType().getOvertimeName());

				boolean isUpdated = overtimeUpdatedList.stream()
						.filter(p -> p.getPrimaryKey().compareTo(overtime.getPrimaryKey()) == 0).findFirst()
						.isPresent();
				if (!isUpdated) {
					overtimeToDeleteList.add(overtime);
				}
			});

			System.out.println("overtimeToDeleteList: " + overtimeToDeleteList.size());

			overtimeToDeleteList.forEach(overtime -> {
				this.overtimeMain.deleteBillingChargingOvertime(overtime);
			});

		}
	}

	public boolean checkClientConfiguration(Payroll payroll, ClientGroup clientGroup) {

		if (clientGroup != null) {
			this.setBillingClientConfiguration(
					this.billingClientConfigurationMain.getBillingClientConfigurationByClientGroup(clientGroup));
			if (this.getBillingClientConfiguration() == null) {
				AlertUtil.showInformationAlert(
						"No billing client configuration found for client group: " + clientGroup.getGroupName(),
						this.primaryStage);
				return false;
			}

			this.setPayrollClientConfiguration(
					this.payrollClientConfigurationMain.getClientConfigurationByClientGroup(clientGroup));

			if (this.getPayrollClientConfiguration() == null) {
				AlertUtil.showInformationAlert(
						"No payroll client configuration found for client group: " + clientGroup.getGroupName(),
						this.primaryStage);
				return false;
			}
		} else {
			this.setBillingClientConfiguration(this.billingClientConfigurationMain
					.getBillingClientConfigurationByClientCode(payroll.getActualEmploymentConfiguration().getClient()));

			if (this.getBillingClientConfiguration() == null) {
				AlertUtil.showInformationAlert(
						"No billing client configuration found for client: "
								+ payroll.getActualEmploymentConfiguration().getClient().getClientName(),
						this.primaryStage);
				return false;
			}

			this.setPayrollClientConfiguration(this.payrollClientConfigurationMain
					.getClientConfigurationByClientCode(payroll.getActualEmploymentConfiguration().getClient()));

			System.out.println("clientConfig: " + this.getPayrollClientConfiguration().getClient());
			if (this.getPayrollClientConfiguration() == null) {
				AlertUtil.showInformationAlert(
						"No payroll client configuration found for client: "
								+ payroll.getActualEmploymentConfiguration().getClient().getClientName(),
						this.primaryStage);
				return false;
			}
		}

		return true;
	}

	public Date getPayrollPeriod(Payroll payroll) {
		if (payroll.getPayrollType().getPayrollTypeKey().toString().equals("5")) {
			return payroll.getOvertimePayPeriodTo();
		} else {
			return payroll.getPayPeriodTo();
		}
	}

	public Integer getBillingCategory(Payroll payroll, boolean isOvertime) {
		EmployeeConfigurationReference employeeConfigurationReference = new EmployeeConfigurationReference();
		if (!isOvertime) {
			employeeConfigurationReference.setPrimaryKey(1);
		} else {
			employeeConfigurationReference.setPrimaryKey(10);
		}

		List<EmployeeConfiguration> employeeConfigurationList = this.getEmployeeConfigurationMain()
				.getAllEmployeeConfigurationByEmployeeConfigurationReference(
						payroll.getEmploymentHistory().getEmployee(), employeeConfigurationReference,
						payroll.getPayPeriodFrom());

		if (employeeConfigurationList != null && !employeeConfigurationList.isEmpty()) {
			if (employeeConfigurationList.size() == 1) {
				EmployeeConfiguration employeeConfiguration = employeeConfigurationList.get(0);
				if (employeeConfiguration.getOtherValue() != null) {
					return Integer.parseInt(employeeConfiguration.getOtherValue());
				}
			} else if (employeeConfigurationList.size() > 1) {
				// this.notificationList.put(payroll.getEmploymentHistory(), " Multiple Employee
				// Configuration found.");
				AlertUtil.showInformationAlert("Multiple Employee Configuration found.", this.getPrimaryStage());
				return null;
			}
		}
		return 0;
	}

	public boolean checkIfPayrollExists(Payroll payroll, List<Payroll> listPayrollToCheck) {
		Payroll payrollMerged = ObservableListUtil.getObject(listPayrollToCheck,
				P -> P.getPrimaryKey().toString().equals(payroll.getPrimaryKey().toString()));

		if (payrollMerged == null) {
			return false;
		} else {
			return true;
		}
	}

	// public void populateOvertime(PayrollClientConfiguration
	// payrollClienConfiguration) {
	// this.observableListConfigurationOvertime.clear();
	// this.observableListConfigurationOvertime.addAll(this.getConfigurationOvertimeMain()
	// .getAllConfigurationOvertimeByBillingClientConfig(payrollClienConfiguration.getPrimaryKey()));
	// }

	public ObservableList<BillingCharging> getObservableListBillingCharging() {
		return observableListBillingCharging;
	}

	public void setObservableListBillingCharging(ObservableList<BillingCharging> observableListBillingCharging) {
		this.observableListBillingCharging = observableListBillingCharging;
	}

	public Payroll getPayroll() {
		return payroll;
	}

	public void setPayroll(Payroll payroll) {
		this.payroll = payroll;
	}

	public ObservableList<Department> getObservableListDepartment() {
		return observableListDepartment;
	}

	public void setObservableListDepartment(ObservableList<Department> observableListDepartment) {
		this.observableListDepartment = observableListDepartment;
	}

	public OvertimeTypeMain getOvertimeTypeMain() {
		return overtimeTypeMain;
	}

	public void setOvertimeTypeMain(OvertimeTypeMain overtimeTypeMain) {
		this.overtimeTypeMain = overtimeTypeMain;
	}

	public DepartmentMain getDepartmentMain() {
		return departmentMain;
	}

	public void setDepartmentMain(DepartmentMain departmentMain) {
		this.departmentMain = departmentMain;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public ConfigurationOvertimeMain getConfigurationOvertimeMain() {
		return configurationOvertimeMain;
	}

	public BillingClientConfigurationMain getBillingClientConfigurationMain() {
		return billingClientConfigurationMain;
	}

	public ObservableList<Client> getObservableListClient() {
		return observableListClient;
	}

	public UserClientConfigurationMain getUserClientConfigurationMain() {
		return userClientConfigurationMain;
	}

	public ObservableList<ConfigurationOvertime> getObservableListConfigurationOvertime() {
		return observableListConfigurationOvertime;
	}

	public BillingCharging getBillingCharging() {
		return billingCharging;
	}

	public void setBillingCharging(BillingCharging billingCharging) {
		this.billingCharging = billingCharging;
	}

	public BillingClientConfiguration getBillingClientConfiguration() {
		return billingClientConfiguration;
	}

	public void setBillingClientConfiguration(BillingClientConfiguration billingClientConfiguration) {
		this.billingClientConfiguration = billingClientConfiguration;
	}

	public PayrollClientConfiguration getPayrollClientConfiguration() {
		return payrollClientConfiguration;
	}

	public void setPayrollClientConfiguration(PayrollClientConfiguration payrollClientConfiguration) {
		this.payrollClientConfiguration = payrollClientConfiguration;
	}

	public OvertimeMain getOvertimeMain() {
		return overtimeMain;
	}

	public void setOvertimeMain(OvertimeMain overtimeMain) {
		this.overtimeMain = overtimeMain;
	}

	public PayrollClientConfigurationMain getPayrollClientConfigurationMain() {
		return payrollClientConfigurationMain;
	}

	public void setPayrollClientConfigurationMain(PayrollClientConfigurationMain payrollClientConfigurationMain) {
		this.payrollClientConfigurationMain = payrollClientConfigurationMain;
	}

	public EmploymentConfigurationMain getEmploymentConfigurationMain() {
		return employmentConfigurationMain;
	}

	public void setEmploymentConfigurationMain(EmploymentConfigurationMain employmentConfigurationMain) {
		this.employmentConfigurationMain = employmentConfigurationMain;
	}

	public ClientMain getClientMain() {
		return clientMain;
	}

	public void setClientMain(ClientMain clientMain) {
		this.clientMain = clientMain;
	}

	public CompanyMain getCompanyMain() {
		return companyMain;
	}

	public void setCompanyMain(CompanyMain companyMain) {
		this.companyMain = companyMain;
	}

	public ClientGroupMain getClientGroupMain() {
		return clientGroupMain;
	}

	public void setClientGroupMain(ClientGroupMain clientGroupMain) {
		this.clientGroupMain = clientGroupMain;
	}

	public SignatoryMain getSignatoryMain() {
		return signatoryMain;
	}

	public void setSignatoryMain(SignatoryMain signatoryMain) {
		this.signatoryMain = signatoryMain;
	}

	public BillingRegisterProcessingMain getBillingRegisterProcessingMain() {
		return billingRegisterProcessingMain;
	}

	public void setBillingRegisterProcessingMain(BillingRegisterProcessingMain billingRegisterProcessingMain) {
		this.billingRegisterProcessingMain = billingRegisterProcessingMain;
	}

	public StatementOfAccountMain getStatementOfAccountMain() {
		return statementOfAccountMain;
	}

	public void setStatementOfAccountMain(StatementOfAccountMain statementOfAccountMain) {
		this.statementOfAccountMain = statementOfAccountMain;
	}

	public ObservableList<StatementOfAccount> getObservableListStatementOfAccount() {
		return observableListStatementOfAccount;
	}

	public void setObservableListStatementOfAccount(
			ObservableList<StatementOfAccount> observableListStatementOfAccount) {
		this.observableListStatementOfAccount = observableListStatementOfAccount;
	}

	public Boolean getIsBillingChargingByDepartment() {
		return isBillingChargingByDepartment;
	}

	public void setIsBillingChargingByDepartment(Boolean isBillingChargingByDepartment) {
		this.isBillingChargingByDepartment = isBillingChargingByDepartment;
	}

	public EmployeeMain getEmployeeMain() {
		return employeeMain;
	}

	public void setEmployeeMain(EmployeeMain employeeMain) {
		this.employeeMain = employeeMain;
	}

	public PayrollMain getPayrollMain() {
		return payrollMain;
	}

	public void setPayrollMain(PayrollMain payrollMain) {
		this.payrollMain = payrollMain;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Map<String, Object> getParameterMapPrinting() {
		return parameterMapPrinting;
	}

	public void setParameterMapPrinting(Map<String, Object> parameterMapPrinting) {
		this.parameterMapPrinting = parameterMapPrinting;
	}

	public BillingReportClassMain getBillingReportClassMain() {
		return billingReportClassMain;
	}

	public void setBillingReportClassMain(BillingReportClassMain billingReportClassMain) {
		this.billingReportClassMain = billingReportClassMain;
	}

	public EmployeeConfigurationMain getEmployeeConfigurationMain() {
		return employeeConfigurationMain;
	}

	public void setEmployeeConfigurationMain(EmployeeConfigurationMain employeeConfigurationMain) {
		this.employeeConfigurationMain = employeeConfigurationMain;
	}

	public BillingRateMain getBillingRateMain() {
		return billingRateMain;
	}

	public void setBillingRateMain(BillingRateMain billingRateMain) {
		this.billingRateMain = billingRateMain;
	}

	public List<Payroll> getListEvaluatedPayroll() {
		return listEvaluatedPayroll;
	}

	public void setListEvaluatedPayroll(List<Payroll> listEvaluatedPayroll) {
		this.listEvaluatedPayroll = listEvaluatedPayroll;
	}

	public BillingMain getBillingMain() {
		return billingMain;
	}

	public void setBillingMain(BillingMain billingMain) {
		this.billingMain = billingMain;
	}

	public EmploymentConfiguration getSelectedEmploymentConfiguration() {
		return selectedEmploymentConfiguration;
	}

	public void setSelectedEmploymentConfiguration(EmploymentConfiguration selectedEmploymentConfiguration) {
		this.selectedEmploymentConfiguration = selectedEmploymentConfiguration;
	}

	public Payroll getSelectedPayroll() {
		return selectedPayroll;
	}

	public void setSelectedPayroll(Payroll selectedPayroll) {
		this.selectedPayroll = selectedPayroll;
	}

	public ClientGroupDetailsMain getClientGroupDetailsMain() {
		return clientGroupDetailsMain;
	}

	public void setClientGroupDetailsMain(ClientGroupDetailsMain clientGroupDetailsMain) {
		this.clientGroupDetailsMain = clientGroupDetailsMain;
	}

	public Client getSelectedClient() {
		return selectedClient;
	}

	public void setSelectedClient(Client selectedClient) {
		this.selectedClient = selectedClient;
	}

	public ClientGroup getSelectedClientGroup() {
		return selectedClientGroup;
	}

	public void setSelectedClientGroup(ClientGroup selectedClientGroup) {
		this.selectedClientGroup = selectedClientGroup;
	}

	public ObservableList<ClientGroup> getObservableListClientGroup() {
		return observableListClientGroup;
	}

	public void setObservableListClientGroup(ObservableList<ClientGroup> observableListClientGroup) {
		this.observableListClientGroup = observableListClientGroup;
	}

	public void setObservableListClient(ObservableList<Client> observableListClient) {
		this.observableListClient = observableListClient;
	}

}
