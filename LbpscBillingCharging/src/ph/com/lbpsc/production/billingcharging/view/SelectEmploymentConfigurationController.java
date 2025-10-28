package ph.com.lbpsc.production.billingcharging.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ph.com.lbpsc.production.billingcharging.BillingChargingMain;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.billingrate.model.BillingRate;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.clientgroupdetails.model.ClientGroupDetails;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employmentconfiguration.model.EmploymentConfiguration;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.payroll.model.Payroll;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.ErrorLog;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ObservableListUtil;

public class SelectEmploymentConfigurationController
		extends MasterEditController<BillingCharging, BillingChargingMain> {

	private ObservableList<EmploymentConfiguration> observableListEmploymentConfiguration = FXCollections
			.observableArrayList();

	@Override
	public boolean isValid() {
		List<DatePicker> datePickerList = new ArrayList<>();
		datePickerList.add(formattedDatePickerDateRendered);
		if (this.tableViewEmploymentConfiguration.getSelectionModel().getSelectedItem() == null) {
			AlertUtil.showErrorAlert("No employee configuration selected.", this.mainApplication.getPrimaryStage());
			return false;
		}

		if (!FieldValidator.datePickerValidate(datePickerList)) {
			AlertUtil.showIncompleteDataAlert(this.dialogStage);
			return false;
		}

		BillingRate billingRate = new BillingRate();
		billingRate = this.mainApplication.getBillingRate(this.objectToModify.getPayroll(), false,
				this.mainApplication.getSelectedEmploymentConfiguration());

		if (billingRate == null) {
			AlertUtil.showInformationAlert("No billing rate found.", this.getDialogStage());
			return false;
		} else {
			this.objectToModify.setBillingRate(billingRate);
		}

		BillingRate billingRateOvertime = new BillingRate();
		billingRateOvertime = this.mainApplication.getBillingRate(this.objectToModify.getPayroll(), true,
				this.mainApplication.getSelectedEmploymentConfiguration());

		if (billingRateOvertime == null) {
			// AlertUtil.showInformationAlert("No overtime billing rate found.",
			// this.getDialogStage());
			// return false;
			this.objectToModify.setBillingRateOvertime(billingRate);
		} else {
			this.objectToModify.setBillingRateOvertime(billingRateOvertime);
		}

		if (!this.mainApplication.checkClientConfiguration(this.objectToModify.getPayroll(),
				this.autoFillComboBoxClientGroup.getValueObject())) {
			return false;
		}

		return true;
	}

	public EmploymentConfiguration getActualEmploymentConfiguration(Payroll payroll) {
		EmploymentConfiguration employmentConfiguration = new EmploymentConfiguration();
		// billingChargingList.addAll(this.getAllBillingChargingByPayrollPrikey(payroll.getPrimaryKey()));

		employmentConfiguration = this.tableViewEmploymentConfiguration.getSelectionModel().getSelectedItem() != null
				? this.tableViewEmploymentConfiguration.getSelectionModel().getSelectedItem()
				: this.objectToModify.getEmploymentConfiguration();

		if (employmentConfiguration != null) {
			return employmentConfiguration;
		} else {
			if (payroll.getPayrollType().getPayrollTypeKey().equals(5)) {
				// prev ot
				List<Payroll> payrollList = new ArrayList<>();

				payrollList = this.mainApplication.getPayrollMain().getPayrollByEmployeeAndReferenceDate(
						payroll.getEmploymentHistory().getEmployee(), payroll.getOvertimePayPeriodFrom());
				// employmentConfiguration =
				// employmentHistoryMain.getEmploymentHistoryByEmployeeIdAndEffectivityDate(
				// payroll.getEmploymentHistory().getEmployee().getEmployeeCode(),
				// payroll.getOvertimePayPeriodFrom()).getEmploymentConfiguration();
				return payrollList.isEmpty() ? payroll.getActualEmploymentConfiguration()
						: payrollList.get(0).getActualEmploymentConfiguration();
			} else {
				return payroll.getActualEmploymentConfiguration();
			}
		}
	}

	@Override
	public void onSave() {

		this.objectToModify.setEmploymentConfiguration(
				this.tableViewEmploymentConfiguration.getSelectionModel().getSelectedItem());
		this.objectToModify.setClient(this.autoFillComboBoxClient.getValueObject());
		this.objectToModify.setDepartment(this.autoFillComboBoxDepartment.getValueObject());
		this.objectToModify.setIsTransfer(this.checkBoxIsTransfer.isSelected());
		this.objectToModify.setDateRendered(DateFormatter.toDate(this.formattedDatePickerDateRendered.getValue()));
		this.objectToModify.setDaily(this.objectToModify.getEmploymentConfiguration().getIsDaily());

		this.objectToModify.setUser(this.mainApplication.getUser());
		this.objectToModify.setChangedInComputer(this.mainApplication.getComputerName());
		this.objectToModify.setChangedOnDate(new Date());

		this.objectToModify.setClientGroup(this.autoFillComboBoxClientGroup.getValueObject());
	}

	@Override
	public void onShowEditDialogStage() {

		System.out.println("billRate Null = 1st form");
		System.out.println(this.objectToModify.getBillingRate() == null);

		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy");
		this.labelPayFrom.setText(dateTimeFormat.format(this.objectToModify.getPayFrom()));
		this.labelPayTo.setText(dateTimeFormat.format(this.objectToModify.getPayTo()));

		this.populateClient();
		this.populateClientGroup();

		this.showDetails(objectToModify);
		System.out.println(observableListEmploymentConfiguration.size());
	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		this.setListeners();

		this.tableColumnClient.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (cellData.getValue().getClient() != null) {
				property.setValue(cellData.getValue().getClient().getClientName());
			}
			return property;
		});

		this.tableColumnDepartment.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (cellData.getValue().getDepartment() != null) {
				property.setValue(cellData.getValue().getDepartment().getDepartmentName());
			}
			return property;
		});

		this.tableColumnPosition.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (cellData.getValue().getPosition() != null) {
				property.setValue(cellData.getValue().getPosition().getPositionName());
			}
			return property;
		});

		this.tableColumnPayRate.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			NumberFormat formatter = new DecimalFormat("###,###,###.00");
			if (cellData.getValue().getAmountOfPayRate() != null) {
				property.setValue(formatter.format(cellData.getValue().getAmountOfPayRate()));
				this.tableColumnPayRate.setStyle("-fx-alignment: BASELINE_RIGHT;");
			}
			return property;
		});

		this.tableColumnDaily.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (cellData.getValue().getIsDaily() != null) {
				property.setValue(cellData.getValue().getIsDaily().toString());
				this.tableColumnDaily.setStyle("-fx-alignment: BASELINE_CENTER;");
			}
			return property;
		});

		this.tableColumnWorkingDays.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (cellData.getValue().getNumberOfWorkingDays() != null) {
				property.setValue(cellData.getValue().getNumberOfWorkingDays().toString());
			}
			return property;
		});

		this.tableColumnEmergencyCostOfLivingAllowance.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (cellData.getValue().getNumberOfWorkingDays() != null) {
				property.setValue(cellData.getValue().getAmountOfEcola().toString());
			}
			return property;
		});

		// this.observableListCompany.addAll(this.mainApplication.getCompanyMain()
		// .getAllActiveCompanyByUser(this.mainApplication.getUser().getUserName()));

		this.tableViewEmploymentConfiguration.getSelectionModel().selectFirst();

		this.tableViewEmploymentConfiguration.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> {
					if (newValue != null) {
						this.mainApplication.setSelectedEmploymentConfiguration(newValue);
					}
				});
	}

	public void setListeners() {
		this.autoFillComboBoxClient.valueObjectProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.autoFillComboBoxClientGroup.setValue(null);
				this.autoFillComboBoxDepartment.setValue(null);
				this.populateDepartment();
				this.observableListEmploymentConfiguration.clear();
				this.tableViewEmploymentConfiguration.setItems(this.observableListEmploymentConfiguration);
			}
		});

		this.autoFillComboBoxDepartment.valueObjectProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.handleRetrieveData();
			}
		});

		this.autoFillComboBoxClientGroup.valueObjectProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				this.autoFillComboBoxClient.setValue(null);
				this.autoFillComboBoxDepartment.setValue(null);
				this.populateDepartment();
				this.observableListEmploymentConfiguration.clear();
				this.tableViewEmploymentConfiguration.setItems(this.observableListEmploymentConfiguration);
			}
		});
	}

	public void populateDepartment() {
		System.out.println("populateDepartment");

		this.mainApplication.getObservableListDepartment().clear();

		if (this.autoFillComboBoxClient.getValueObject() != null) {
			this.mainApplication.getObservableListDepartment().addAll(this.mainApplication.getDepartmentMain()
					.getDepartmentByClientCode(this.autoFillComboBoxClient.getValueObject()));
		} else {
			List<ClientGroupDetails> listClientGroupDetails = new ArrayList<>();
			listClientGroupDetails.addAll(this.mainApplication.getClientGroupDetailsMain()
					.getAllClientGroupDetailsByClientGroup(this.autoFillComboBoxClientGroup.getValueObject()));

			System.out.println("listClientGroupDetails SIZE: " + listClientGroupDetails.size());

			listClientGroupDetails.forEach(clientGroupDetails -> {
				if (clientGroupDetails.getDepartment() != null) {
					this.mainApplication.getObservableListDepartment().add(clientGroupDetails.getDepartment());
				}
			});
		}

		System.out.println("getObservableListDepartment: " + this.mainApplication.getObservableListDepartment().size());

		this.autoFillComboBoxDepartment.setItems(this.mainApplication.getObservableListDepartment(),
				department -> department.getDepartmentName());

	}

	public void populateClient() {
		// System.out.println("populateClient");
		// this.mainApplication.getObservableListClient().clear();
		// this.mainApplication.getUserClientConfigurationMain().getAllUserClientConfigurationByUserAndPayrollHeader(
		// this.mainApplication.getUser(), this.objectToModify.getBatchDate()).forEach(C
		// -> {
		// this.mainApplication.getObservableListClient().add(C.getClient());
		// });
		this.autoFillComboBoxClient.setItems(this.mainApplication.getObservableListClient(),
				client -> client.getClientName());
	}

	public void populateClientGroup() {
		this.autoFillComboBoxClientGroup.setItems(this.mainApplication.getObservableListClientGroup(),
				clientGroup -> clientGroup.getGroupName());
	}

	@FXML
	private void handleRetrieveData() {
		System.out.println("handleRetrieveData");
		try {
			EmploymentConfiguration employmentConfiguration = new EmploymentConfiguration();

			employmentConfiguration.setClient(this.autoFillComboBoxClient.getValueObject());
			employmentConfiguration.setDepartment(this.autoFillComboBoxDepartment.getValueObject());

			List<EmploymentConfiguration> employmentConfigurationList = this.mainApplication
					.getEmploymentConfigurationMain().getFilteredEmploymentConfiguration(employmentConfiguration);
			System.out.println("employmentConfigurationList: " + employmentConfigurationList.size());

			if (employmentConfigurationList != null && !employmentConfigurationList.isEmpty()) {
				observableListEmploymentConfiguration.clear();
				observableListEmploymentConfiguration.addAll(employmentConfigurationList);
				ObservableListUtil.sort(this.observableListEmploymentConfiguration,
						p -> p.getPosition().getPositionName());
			} else {
				AlertUtil.showErrorAlert("No record found.", this.mainApplication.getPrimaryStage());
			}
		} catch (Exception e) {
			ErrorLog.showErrorLog(e, this.mainApplication.getPrimaryStage());
			e.printStackTrace();
		}
		this.tableViewEmploymentConfiguration.setItems(this.observableListEmploymentConfiguration);
	}

	@Override
	public void showDetails(BillingCharging billingCharging) {
		System.out.println("showDetails");
		// TODO Auto-generated method stub
		// this.labelPayFrom.setText(objectToModify.getPayFrom().toString());
		// this.labelPayTo.setText(objectToModify.getPayTo().toString());
		this.labelEmployeeName.setText(objectToModify.getPayroll().getEmploymentHistory().getEmployee().getFullName());
		// this.autoFillComboBoxCompany.setValue(objectToModify.getClient().getCompany().getCompanyName());
		this.autoFillComboBoxClient
				.setValue(objectToModify.getClient() == null ? "" : objectToModify.getClient().getClientName());
		this.autoFillComboBoxDepartment.setValue(
				objectToModify.getDepartment() == null ? "" : objectToModify.getDepartment().getDepartmentName());
		this.autoFillComboBoxClientGroup.setValue(
				objectToModify.getClientGroup() == null ? "" : objectToModify.getClientGroup().getGroupName());
		this.formattedDatePickerDateRendered
				.setValue(objectToModify.getDateRendered().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		// this.handleRetrieveData();
		// this.tableViewEmploymentConfiguration.setItems(observableListEmploymentConfiguration);

	}

	@FXML
	private AutoFillComboBox<Client> autoFillComboBoxClient;
	@FXML
	private AutoFillComboBox<ClientGroup> autoFillComboBoxClientGroup;
	@FXML
	private AutoFillComboBox<Department> autoFillComboBoxDepartment;
	@FXML
	private TableView<EmploymentConfiguration> tableViewEmploymentConfiguration;
	@FXML
	private TableColumn<EmploymentConfiguration, String> tableColumnClient;
	@FXML
	private TableColumn<EmploymentConfiguration, String> tableColumnDepartment;
	@FXML
	private TableColumn<EmploymentConfiguration, String> tableColumnPosition;
	@FXML
	private TableColumn<EmploymentConfiguration, String> tableColumnPayRate;
	@FXML
	private TableColumn<EmploymentConfiguration, String> tableColumnDaily;
	@FXML
	private TableColumn<EmploymentConfiguration, String> tableColumnWorkingDays;
	@FXML
	private TableColumn<EmploymentConfiguration, String> tableColumnEmergencyCostOfLivingAllowance;
	@FXML
	private Button buttonRetrieveData;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private Label labelEmployeeName;
	@FXML
	private Label labelPayFrom;
	@FXML
	private Label labelPayTo;
	@FXML
	private CheckBox checkBoxIsTransfer;
	@FXML
	private FormattedDatePicker formattedDatePickerDateRendered;
}