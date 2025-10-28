package ph.com.lbpsc.production.billingcharging.view;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.billingcharging.BillingChargingMain;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.billingclientconfiguration.model.BillingClientConfiguration;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.clientgroupdetails.model.ClientGroupDetails;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.employee.model.EmployeePayroll;
import ph.com.lbpsc.production.masterclass.MasterBrowseController;
import ph.com.lbpsc.production.overtime.model.Overtime;
import ph.com.lbpsc.production.payroll.model.Payroll;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObjectCopyUtil;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lbpsc.production.util.TableViewUtil;

public class BrowseBillingChargingController extends MasterBrowseController<BillingCharging, BillingChargingMain> {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private ObservableList<EmployeePayroll> observableListEmployeePayroll = FXCollections.observableArrayList();
	private ObservableList<BillingCharging> observableListCharging = FXCollections.observableArrayList();
	private Payroll payroll;
	private EmployeePayroll employeePayroll;
	private Date batchDate;
	private Date payFrom, payTo;

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdd() {
		// TODO Auto-generated method stub
		this.handleSelectEmploymentConfiguration();
	}

	@Override
	public void onEdit() {
		BillingClientConfiguration billingClientConfiguration = this.mainApplication.getBillingClientConfigurationMain()
				.getBillingClientConfigurationByClientCode(this.tableViewBillingCharging.getSelectionModel()
						.getSelectedItem().getPayroll().getActualEmploymentConfiguration().getClient());
		if (billingClientConfiguration == null) {
			AlertUtil.showInformationAlert("No client configuration found.", this.mainApplication.getPrimaryStage());
			return;
		}

		BillingCharging billingCharging = this.tableViewBillingCharging.getSelectionModel().getSelectedItem();

		if (billingCharging != null) {
			this.modifyDetails(billingCharging, ModificationType.EDIT);
		} else {
			AlertUtil.showInformationAlert("Select billing charging.", this.getParentStage());
			return;
		}

	}

	@Override
	public void onDelete() {
		BillingCharging billingCharging = this.tableViewBillingCharging.getSelectionModel().getSelectedItem();
		if (billingCharging != null) {

			if (billingCharging.getOvertimeList() != null) {
				this.mainApplication.getOvertimeMain()
						.deleteBillingChargingOvertimeByPrikeyBillingCharging(billingCharging.getPrimaryKey());
				// if (!successDeleteOvertimeCharging) {
				// AlertUtil.showErrorAlert("Error deleting record overtime.",
				// this.mainApplication.getPrimaryStage());
				// return;
				// }
			}

			if (this.mainApplication.deleteBillingCharging(billingCharging)) {
				this.mainApplication.getObservableListBillingCharging().remove(billingCharging);
				AlertUtil.showSuccessDeleteAlert(this.mainApplication.getPrimaryStage());
				this.mainApplication.getBillingCharging().setChangedOnDate(this.mainApplication.getDateNow());
			} else {
				AlertUtil.showErrorAlert("Error deleting record", this.mainApplication.getPrimaryStage());
			}

		} else {
			AlertUtil.showNoDataAlert(this.mainApplication.getPrimaryStage());
		}

		this.showDetails(null);
		this.setTableViewOvertimeCharging(null);
	}

	@Override
	public void onSearch() {
		// TODO Auto-generated method stub

	}

	@Override
	public void modifyDetails(BillingCharging billingCharging, ModificationType modificationType) {
		if (billingCharging != null) {
			System.out.println("\nmodifyDetails : BROWSEBILLINGCHARGING");
			// System.out.println(billingCharging.getOvertimeList().size());
			System.out.println("prikeyCharging: " + billingCharging.getPrimaryKey());
			System.out.println("prikeyPayroll: " + billingCharging.getPayroll().getPrimaryKey());
			System.out.println("prikeyEmpConfig: " + billingCharging.getEmploymentConfiguration().getPrimaryKey());

			if (this.mainApplication.showEditBillingCharging(billingCharging, modificationType,
					this.getParentStage())) {
				boolean successfulSave = false;
				successfulSave = modificationType.equals(ModificationType.ADD)
						? this.mainApplication.createBillingCharging(billingCharging)
						: this.mainApplication.updateBillingCharging(billingCharging);

				this.mainApplication.saveBillingChargingOvertime(billingCharging);

				if (successfulSave) {
					AlertUtil.showSuccessSaveAlert(this.mainApplication.getPrimaryStage());
					TableViewUtil.refreshTableView(this.tableViewBillingCharging);
					this.showDetails(billingCharging);
					this.tableViewBillingCharging.requestFocus();
					this.tableViewBillingCharging.getSelectionModel().select(billingCharging);
					this.tableViewBillingCharging.scrollTo(billingCharging);
					// this.mainApplication.getBillingCharging().setPrimaryKey(billingCharging.getPrimaryKey());
					ObjectCopyUtil.copyProperties(billingCharging, this.mainApplication.getBillingCharging(),
							BillingCharging.class);
				} else {
					AlertUtil.showRecordNotSave("Error in saving record", this.mainApplication.getPrimaryStage());
				}
			}
			this.mainApplication.getObservableListBillingCharging()
					.setAll(this.mainApplication.getAllBillingChargingByPayroll(this.mainApplication.getPayroll()));
			this.tableViewBillingCharging.setItems(this.mainApplication.getObservableListBillingCharging());
			TableViewUtil.refreshTableView(this.tableViewBillingCharging);
			this.tableViewBillingCharging.getSelectionModel().selectFirst();
			this.setTableViewOvertimeCharging(billingCharging);
		}
	}

	@Override
	public void showDetails(BillingCharging billingCharging) {
		System.out.println("showDetails - billingcharging");
		if (billingCharging != null) {

			System.out.println(billingCharging.getPayroll() == null);

			this.labelClientCharging
					.setText(billingCharging.getClient() != null ? billingCharging.getClient().getClientName() : "");
			this.labelDepartmentCharging.setText(
					billingCharging.getDepartment() != null ? billingCharging.getDepartment().getDepartmentName() : "");
			this.labelClientGroupCharging.setText(
					billingCharging.getClientGroup() != null ? billingCharging.getClientGroup().getGroupName() : "");

			this.labelDailyCharging.setText(billingCharging.getDaily() == true ? "Yes" : "No");
			this.labelRegularDaysCharging.setText(
					billingCharging.getRegularDays() == null ? "0" : billingCharging.getRegularDays().toString());
			this.labelHolidayCharging
					.setText(billingCharging.getHolidays() == null ? "0" : billingCharging.getHolidays().toString());
			this.labelBillRate.setText(billingCharging.getBillingRate().getAmountOfDailyBillingRate().toString());
			if (billingCharging.getOvertimeList() != null) {
				this.labelBillRateOvertime.setText(!billingCharging.getOvertimeList().isEmpty()
						? billingCharging.getOvertimeList().get(0).getBillingRate().getAmountOfDailyBillingRate().toString()
								: billingCharging.getBillingRate().getAmountOfDailyBillingRate().toString());
			}
			// this.labelUndertimeHoursCharging.setText(
			// billingCharging.getUndertimeHours() == null ? "0" :
			// billingCharging.getUndertimeHours().toString());
			// this.labelUndertimeMinsCharging.setText(billingCharging.getUndertimeMinutes()
			// == null ? "0"
			// : billingCharging.getUndertimeMinutes().toString());
			// this.labelOvertimeType.setText(billingCharging.getOvertimeType() == null ? ""
			// : billingCharging.getOvertimeType().getOvertimeName());
			// this.labelOvertimeHrs.setText(billingCharging.getOvert
			// imeHours().toString());
			// this.labelOvertimeMins.setText(billingCharging.getOvertimeMinutes().toString());
			this.labelPayFromCharging.setText(this.dateFormat.format(billingCharging.getPayFrom()));
			this.labelPayToCharging.setText(this.dateFormat.format(billingCharging.getPayTo()));
			this.labelBatchDateCharging.setText(this.dateFormat.format(billingCharging.getBatchDate()));

			this.labelPositionCharging.setText(billingCharging.getEmploymentConfiguration() == null ? ""
					: billingCharging.getEmploymentConfiguration().getPosition().getPositionName());
			// this.labelRateCharging.setText(billingCharging.getBillingRate() == null ? ""
			// :
			// String.valueOf(billingCharging.getBillingRate().getAmountOfDailyBillingRate()));
			// this.labelWorkHrsCharging.setText(
			// billingCharging.getWorkHours() == null ? "" :
			// String.valueOf(billingCharging.getWorkHours()));

		} else {
			this.labelClientCharging.setText("");
			this.labelDepartmentCharging.setText("");
			this.labelClientGroupCharging.setText("");
			this.labelDailyCharging.setText("");
			this.labelRegularDaysCharging.setText("");
			this.labelHolidayCharging.setText("");
			// this.labelUndertimeHoursCharging.setText("");
			// this.labelUndertimeMinsCharging.setText("");
			// this.labelOvertimeType.setText("");
			// this.labelOvertimeHrs.setText("");
			// this.labelOvertimeMins.setText("");
			this.labelPayFromCharging.setText("");
			this.labelPayToCharging.setText("");
			this.labelBatchDateCharging.setText("");

			this.labelPositionCharging.setText("");
			this.labelBillRate.setText("");
			this.labelBillRateOvertime.setText("");
			// this.labelRateCharging.setText("");
			// this.labelWorkHrsCharging.setText("");
		}
	}

	public Payroll updatePayrollData(Payroll payroll) {
		List<Overtime> listOvertime = new ArrayList<Overtime>();
		Overtime overtime = new Overtime();

		this.observableListCharging
				.setAll(this.mainApplication.getAllBillingChargingByPayrollPrikey(payroll.getPrimaryKey()));

		for (BillingCharging billingCharging : this.observableListCharging) {

			if (billingCharging != null
					&& billingCharging.getPayroll().getPrimaryKey().compareTo(payroll.getPrimaryKey()) == 0) {
				payroll.setNumberOfRegularDays(Integer.valueOf(
						payroll.getNumberOfRegularDays().intValue() - (billingCharging.getRegularDays() == null ? 0
								: billingCharging.getRegularDays().intValue())));
				payroll.setNumberOfHolidays(Integer.valueOf(payroll.getNumberOfHolidays().intValue()
						- (billingCharging.getHolidays() == null ? 0 : billingCharging.getHolidays().intValue())));
				payroll.setHoursOfUndertime(Integer.valueOf(
						payroll.getHoursOfUndertime().intValue() - (billingCharging.getUndertimeHours() == null ? 0
								: billingCharging.getUndertimeHours().intValue())));
				payroll.setMinutesOfUndertime(Integer.valueOf(
						payroll.getMinutesOfUndertime().intValue() - (billingCharging.getUndertimeMinutes() == null ? 0
								: billingCharging.getUndertimeMinutes().intValue())));
				for (Overtime overtimeBillingCharging : billingCharging.getOvertimeList()) {
					for (Overtime overtimePayroll : payroll.getOvertimeList()) {
						overtime.setHoursOfOvertime(Integer
								.valueOf(overtimePayroll != null ? overtimePayroll.getHoursOfOvertime().intValue()
										: 0 - overtimeBillingCharging.getHoursOfOvertime().intValue()));
						overtime.setMinutesOfOvertime(Integer
								.valueOf(overtimePayroll != null ? overtimePayroll.getMinutesOfOvertime().intValue()
										: 0 - overtimeBillingCharging.getMinutesOfOvertime().intValue()));
						overtime.setOvertimeType(overtimePayroll != null ? overtimePayroll.getOvertimeType() : null);
					}
				}
				listOvertime.add(overtime);
				payroll.setOvertimeList(listOvertime);
			}
		}
		return payroll;
	}

	public void clearFields() {
		this.labelClientPayroll.setText("");
		this.labelDepartmentPayroll.setText("");
		this.labelPositionPayroll.setText("");
		this.labelDailyPayroll.setText("");
		this.labelRegularDaysPayroll.setText("");
		this.labelHolidayPayroll.setText("");
		this.labelRegularPayPayroll.setText("");
		this.labelPayRatePayroll.setText("");
		this.labelAbsentPayroll.setText("");
		this.labelUndertimePayroll.setText("");
		this.labelPayFromPayroll.setText("");
		this.labelPayToPayroll.setText("");
		this.labelBatchDatePayroll.setText("");
	}

	public void showDetailsPayroll(Payroll payroll) {
		System.out.println("pass setPayroll");
		if (payroll != null) {
			System.out.println("showDetails: PayrollChargingEncoding");
			this.mainApplication.setPayroll(this.updatePayrollData(payroll));

			this.labelClientPayroll.setText(payroll.getActualEmploymentConfiguration().getClient() == null ? ""
					: payroll.getActualEmploymentConfiguration().getClient().getClientName());
			this.labelDepartmentPayroll
					.setText((payroll.getActualEmploymentConfiguration().getDepartment() == null) ? ""
							: payroll.getActualEmploymentConfiguration().getDepartment().getDepartmentName());
			this.labelPositionPayroll.setText((payroll.getActualEmploymentConfiguration().getPosition() == null) ? ""
					: payroll.getActualEmploymentConfiguration().getPosition().getPositionName());
			this.labelDailyPayroll.setText(payroll.getActualIsDaily().booleanValue() ? "Yes" : "No");

			this.labelRegularDaysPayroll.setText(payroll.getNumberOfRegularDays().toString());
			this.labelHolidayPayroll.setText(payroll.getNumberOfHolidays().toString());
			this.labelRegularPayPayroll.setText(payroll.getAmountOfRegularPay().toString());
			this.labelPayRatePayroll
					.setText(payroll.getActualEmploymentConfiguration().getAmountOfPayRate().toString());
			this.labelAbsentPayroll.setText(payroll.getNumberOfDaysAbsent().toString());
			this.labelUndertimePayroll.setText((payroll.getHoursOfUndertime() != null)
					? (payroll.getHoursOfUndertime()
							+ " hr/s and "
							+ ((payroll.getMinutesOfUndertime() != null) ? (payroll.getMinutesOfUndertime() + " min/s ")
									: "0 mins"))
					: ("0 hr/s and "
							+ ((payroll.getMinutesOfUndertime() != null) ? (payroll.getMinutesOfUndertime() + " min/s ")
									: "0 mins")));
			this.labelPayFromPayroll.setText(this.dateFormat.format(payroll.getPayPeriodFrom()));
			this.labelPayToPayroll.setText(this.dateFormat.format(payroll.getPayPeriodTo()));
			this.labelBatchDatePayroll.setText(this.dateFormat.format(payroll.getPayrollHeader().getPayPeriodFrom()));
		} else {
			clearFields();
		}

	}

	public void populateBillingCharging(Payroll payroll) {
		System.out.println("populateBillingCharging");
		this.mainApplication.getObservableListBillingCharging()
				.setAll(this.mainApplication.getAllBillingChargingByPayroll(payroll));
		this.setTableViewBillingCharging();
		// this.showDetails(this.tableViewBillingCharging.getSelectionModel().getSelectedItem());
	}

	public void setTableViewBillingCharging() {
		this.tableViewBillingCharging.setItems(this.mainApplication.getObservableListBillingCharging());

		TableColumnUtil.setColumn(this.tableColumnEmployeeIDCharging, p -> p == null ? ""
				: String.valueOf(p.getPayroll().getEmploymentHistory().getEmployee().getEmployeeCode()));
		TableColumnUtil.setColumn(this.tableColumnEmployeeNameCharging,
				p -> p == null ? "" : p.getPayroll().getEmploymentHistory().getEmployee().getEmployeeName());

		this.tableViewBillingCharging.getSelectionModel().selectFirst();
	}

	public void setTableViewPayrolll() {
		this.observableListEmployeePayroll.clear();

		List<ClientGroupDetails> listClientGroupDetails = new ArrayList<>();
		listClientGroupDetails.addAll(this.mainApplication.getClientGroupDetailsMain()
				.getAllClientGroupDetailsByClientGroup(this.mainApplication.getSelectedClientGroup()));

		if (this.mainApplication.getSelectedClient() != null) {
			ProcessingMessage.showProcessingMessage(this.getParentStage());
			this.observableListEmployeePayroll.addAll(this.mainApplication.getEmployeeMain().getAllPayrollTableData(
					null, this.mainApplication.getSelectedClient().getClientCode(), null, null, null, null,
					this.payFrom, this.payTo, this.batchDate, null, null));

			if (this.observableListEmployeePayroll.isEmpty() || this.observableListEmployeePayroll == null) {
				AlertUtil.showInformationAlert("No data found.", this.getParentStage());
				ProcessingMessage.closeProcessingMessage();
				return;
			}

			ObservableListUtil.sort(this.observableListEmployeePayroll, P -> P.getEmployeeName());
			this.setTableViewBillingCharging();
			ProcessingMessage.closeProcessingMessage();

		} else if (this.mainApplication.getSelectedClientGroup() != null) {

			listClientGroupDetails.forEach(p -> {
				System.out.println(p.getDepartment().getDepartmentCode());
			});

			ProcessingMessage.showProcessingMessage(this.getParentStage());

			listClientGroupDetails.forEach(clientGroupDetails -> {
				this.observableListEmployeePayroll.addAll(this.mainApplication.getEmployeeMain().getAllPayrollTableData(
						null, null, clientGroupDetails.getDepartment().getDepartmentCode().toString(), null, null, null,
						this.payFrom, this.payTo, this.batchDate, null, null));
			});

			if (this.observableListEmployeePayroll.isEmpty() || this.observableListEmployeePayroll == null) {
				AlertUtil.showInformationAlert("No data found.", this.getParentStage());
				ProcessingMessage.closeProcessingMessage();
				return;
			}

			ObservableListUtil.sort(this.observableListEmployeePayroll, P -> P.getEmployeeName());
			this.setTableViewBillingCharging();
			ProcessingMessage.closeProcessingMessage();

		} else {
			AlertUtil.showInformationAlert("Select client or client group.", this.getParentStage());
			return;
		}

		this.tableViewPayroll.setItems(this.observableListEmployeePayroll);

		TableColumnUtil.setColumn(this.tableColumnEmployeeIDPayroll, p -> String.valueOf(p.getEmployeeCode()));
		TableColumnUtil.setColumn(this.tableColumnEmployeeNamePayroll, p -> p.getEmployeeName());

		this.tableViewPayroll.getSelectionModel().selectFirst();
		if (this.employeePayroll != null) {
			this.tableViewPayroll.getSelectionModel().select(this.employeePayroll);
		}

	}

	public void setTableViewOvertimeCharging(BillingCharging billingCharging) {
		ObservableList<Overtime> overtimeChargingObsList = FXCollections.observableArrayList();
		if (billingCharging != null && billingCharging.getOvertimeList() != null) {
			overtimeChargingObsList.setAll(billingCharging.getOvertimeList());
		}

		this.tableViewChargingOvertime.setItems(overtimeChargingObsList);
		TableColumnUtil.setColumn(this.tableColumnChargingOldRate,
				p -> p.getOldOvertimeType() != null ? p.getOldOvertimeType().getOvertimeName() : "");
		TableColumnUtil.setColumn(this.tableColumnChargingNewRate,
				p -> p.getOvertimeType().getOvertimeName() != null ? p.getOvertimeType().getOvertimeName() : "");
		TableColumnUtil.setColumn(this.tableColumnChargingHours,
				p -> p.getHoursOfOvertime() != null ? p.getHoursOfOvertime() : "");
		TableColumnUtil.setColumn(this.tableColumnChargingMins,
				p -> p.getMinutesOfOvertime() != null ? p.getMinutesOfOvertime() : "");
		TableColumnUtil.setColumn(this.tableColumnChargingAmount,
				p -> p.getAmountOfOvertime() != null ? p.getAmountOfOvertime() : "");
		TableColumnUtil.setColumn(this.tableColumnChargingIsCoverUp,
				p -> p.getIsRDCoverUp() ? "Rest day" : p.getIsRHCoverUp() ? "Holiday" : "");
		TableColumnUtil.setColumn(this.tableColumnChargingIsExtendedDuty,
				p -> p.getIsExtendedDuty() != null ? p.getIsExtendedDuty() ? "Yes" : "No" : "");
	}

	public void setTableViewOvertimePayroll(Payroll payroll) {
		ObservableList<Overtime> overtimePayrollObsList = FXCollections.observableArrayList();
		if (payroll != null) {
			overtimePayrollObsList.setAll(payroll.getOvertimeList());
		}

		this.tableViewPayrollOvertime.setItems(overtimePayrollObsList);
		TableColumnUtil.setColumn(this.tableColumnPayrollOTType, p -> p.getOvertimeType().getOvertimeName());
		TableColumnUtil.setColumn(this.tableColumnPayrollOTHours, p -> p.getHoursOfOvertime());
		TableColumnUtil.setColumn(this.tableColumnPayrollOTMins, p -> p.getMinutesOfOvertime());
		TableColumnUtil.setColumn(this.tableColumnPayrollOTAmount, p -> p.getAmountOfOvertime());
	}

	public void setListeners() {
		this.tableViewBillingCharging.getSelectionModel().selectedItemProperty()
				.addListener((oldValue, observable, newValue) -> {
					if (newValue != null) {
						System.out.println("tableViewBillingCharging listener");
						this.showDetails(newValue);

						this.setTableViewOvertimeCharging(newValue);
					}
				});

		this.mainApplication.getObservableListBillingCharging().addListener((ListChangeListener<BillingCharging>) C -> {
			if (this.mainApplication.getObservableListBillingCharging().isEmpty()) {
				this.setDisableButtons(true);
			} else {
				this.setDisableButtons(false);
			}
		});

		this.tableViewPayroll.getSelectionModel().selectedItemProperty()
				.addListener((oldValue, observable, newValue) -> {
					if (newValue != null) {
						this.employeePayroll = newValue;
						this.payroll = new Payroll();
						TableViewUtil.refreshTableView(this.tableViewPayroll);
						if (newValue != null) {
							Payroll payroll = this.mainApplication.getPayrollMain()
									.getPayrollByID(newValue.getPayroll());
							ObjectCopyUtil.copyProperties(payroll, this.payroll, Payroll.class);
							System.out.println("payrollPrikey: " + payroll.getPrimaryKey());
							this.showDetailsPayroll(this.payroll);
							this.populateBillingCharging(payroll);
							this.mainApplication.setSelectedPayroll(payroll);

							this.setTableViewOvertimePayroll(payroll);
							this.setTableViewOvertimeCharging(
									this.tableViewBillingCharging.getSelectionModel().getSelectedItem());
							this.showDetails(this.tableViewBillingCharging.getSelectionModel().getSelectedItem());
						}
					}
				});

		this.formattedDatePickerBatchDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			// this.mainApplication.getObservableListClient().clear();
			// this.formattedDatePickerBatchDate.setValue(newValue);
			this.batchDate = DateFormatter.toDate(newValue);
			this.formattedDatePickerPayPeriodFrom.setValue(newValue);
			// this.populateClient();
			// this.formattedDatePickerBatchDate.setValue(this.batchDate);
		});

		this.formattedDatePickerPayPeriodFrom.valueProperty().addListener((observable, oldValue, newValue) -> {
			// this.mainApplication.getObservableListClient().clear();
			this.payFrom = DateFormatter.toDate(newValue);
			// this.populateClient();
			// this.formattedDatePickerPayPeriodFrom.setValue(newValue);
			this.formattedDatePickerPayPeriodTo.setValue(
					DateFormatter.toLocalDate(DateUtil.getCutOffDate(DateFormatter.toDate(newValue), 0, true)));
			this.payTo = DateFormatter.toDate(this.formattedDatePickerPayPeriodTo.getValue());
		});

		this.autoFillComboBoxClient.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			this.mainApplication.setSelectedClient(newValue);
			this.mainApplication.setSelectedClientGroup(null);
			this.autoFillComboBoxClientGroup.setValue(null);
			this.resetBrowse();
		});

		this.autoFillComboBoxClientGroup.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			this.mainApplication.setSelectedClientGroup(newValue);
			this.mainApplication.setSelectedClient(null);
			this.autoFillComboBoxClient.setValue(null);
			this.resetBrowse();
		});

		// this.setTableViewBillingCharging();
		// this.setTableViewPayrolll();

		// this.mainApplication.getBillingChargingMain().getBillingCharging().addPropertyChangeListener(C
		// -> {
		// if (C != null && C.getPropertyName().equals("changedOnDate")) {
		// Payroll payroll =
		// this.mainApplication.getPayrollMain().getPayrollByID(this.payroll.getPrimaryKey());
		// ObjectCopyUtil.copyProperties(payroll, this.payroll, Payroll.class);
		// showDetails(this.payroll);
		// }
		// });

	}

	public void resetBrowse() {
		this.observableListEmployeePayroll.clear();
		this.tableViewPayroll.setItems(this.observableListEmployeePayroll);
		this.observableListCharging.clear();
		this.setTableViewBillingCharging();
		this.setTableViewOvertimePayroll(null);
		this.setTableViewOvertimeCharging(null);
		this.showDetails(null);
		this.showDetailsPayroll(null);
	}

	@Override
	public void onSetMainApplication() {
		this.setDisableButtons(true);
		this.setListeners();

		this.mainApplication.getObservableListClient()
				.setAll(this.mainApplication.getClientMain().getClientByUser(this.mainApplication.getUser()));
		this.mainApplication.getObservableListClientGroup()
				.setAll(this.mainApplication.getClientGroupMain().getClientGroupByUser(this.mainApplication.getUser()));
		ObservableListUtil.sort(this.mainApplication.getObservableListClient(), p -> p.getClientName());

		this.autoFillComboBoxClient.setItems(this.mainApplication.getObservableListClient(), p -> p.getClientName());
		this.autoFillComboBoxClientGroup.setItems(this.mainApplication.getObservableListClientGroup(),
				clientGroup -> clientGroup.getGroupName());
	}

	public void handleSelectEmploymentConfiguration() {
		System.out.println("handleSelectEmploymentConfiguration");
		this.mainApplication.setIsBillingChargingByDepartment(false);
		// this.isDepartment = false;
		if (this.tableViewPayroll.getSelectionModel() != null
				&& this.tableViewPayroll.getSelectionModel().getSelectedItem() != null) {
			if (this.mainApplication.checkClientConfiguration(this.payroll,
					this.autoFillComboBoxClientGroup.getValueObject())) {

				BillingCharging billingCharging = new BillingCharging();
				billingCharging.setBatchDate(this.payroll.getPayrollHeader().getPayPeriodFrom());
				billingCharging.setDaily(this.payroll.getActualIsDaily());
				billingCharging.setPayFrom(this.payroll.getPayPeriodFrom());
				billingCharging.setPayTo(this.payroll.getPayPeriodTo());
				billingCharging.setDepartment(this.payroll.getActualEmploymentConfiguration().getDepartment());
				billingCharging.setPayroll(this.payroll);
				billingCharging.setClient(this.payroll.getActualEmploymentConfiguration().getClient());
				billingCharging.setDaily(this.payroll.getActualIsDaily());
				billingCharging.setDateRendered(new Date());

				// add new charging
				if (this.mainApplication.showSelectEmploymentBillingCharging(billingCharging, ModificationType.ADD)) {
					boolean successfulSave = false;

					// successfulSave = this.mainApplication.createBillingCharging(billingCharging);
					// ProcessBillingStandard processBillingStandard = new ProcessBillingStandard();

					// if (successfulSave) {

					this.mainApplication.getObservableListBillingCharging().add(billingCharging);
					this.tableViewBillingCharging.getSelectionModel().select(billingCharging);

					// new bill charge = add mod type
					if (this.mainApplication.showEditBillingCharging(billingCharging, ModificationType.ADD,
							this.getParentStage())) {

						successfulSave = this.mainApplication.createBillingCharging(billingCharging);

						if (successfulSave) {
							AlertUtil.showSuccessSaveAlert(this.getParentStage());

							EmployeePayroll employeePayroll = this.tableViewPayroll.getSelectionModel()
									.getSelectedItem();

							this.setTableViewPayrolll();
							this.setTableViewBillingCharging();
							this.showDetails(billingCharging);

							this.tableViewPayroll.getSelectionModel().select(employeePayroll);
							this.tableViewBillingCharging.getSelectionModel().select(billingCharging);
						} else {
							// AlertUtil.showRecordNotSave("Record not saved", this.getParentStage());
							// this.tableViewBillingCharging.refresh();
							// TableViewUtil.refreshTableView(this.tableViewBillingCharging);
							// this.setTableViewBillingCharging();
							this.populateBillingCharging(payroll);
							this.showDetails(null);
						}

						if (billingCharging.getOvertimeList() != null && !billingCharging.getOvertimeList().isEmpty()) {
							this.mainApplication.saveBillingChargingOvertime(billingCharging);
						}

						Payroll payroll = this.mainApplication.getPayrollMain()
								.getPayrollByID(billingCharging.getPayroll().getPrimaryKey());

						ObjectCopyUtil.copyProperties(payroll, this.payroll, Payroll.class);
						this.setTableViewPayrolll();
						this.showDetailsPayroll(this.payroll);

					} else {
						// AlertUtil.showRecordNotSave("Record not saved", this.getParentStage());
						// this.tableViewBillingCharging.refresh();
						// TableViewUtil.refreshTableView(this.tableViewBillingCharging);
						this.populateBillingCharging(payroll);
						this.showDetails(null);
					}
					// }
					// else {
					// AlertUtil.showRecordNotSave("New billing charging not saved",
					// this.getParentStage());
					// }
				} else {
				}
			} else {
				AlertUtil.showErrorAlert("No client configuration found", getParentStage());
			}
		} else {
			AlertUtil.showErrorAlert("Please select payroll data", getParentStage());
		}
	}

	public void populateClient() {
		// OLD
		System.out.println("populateClient");
		ProcessingMessage.showProcessingMessage(this.getParentStage());

		this.observableListEmployeePayroll.clear();
		this.observableListCharging.clear();
		this.mainApplication.getObservableListBillingCharging().clear();
		clearFields();

		// List<UserClientConfiguration> userClientConfigList = new ArrayList<>();
		//
		// System.out.println(this.mainApplication.getUser());
		// System.out.println(this.formattedDatePickerBatchDate.getValue());
		// System.out.println(DateFormatter.toDate(this.formattedDatePickerBatchDate.getValue()));
		//
		// userClientConfigList.addAll(this.mainApplication.getUserClientConfigurationMain()
		// .getAllUserClientConfigurationByUserAndPayrollHeader(this.mainApplication.getUser(),
		// DateFormatter.toDate(this.batchDate)));
		//
		// System.out.println("userClientConfigList: " + userClientConfigList.size());
		//
		// this.mainApplication.getObservableListClient()
		// .setAll(userClientConfigList.stream().map(p ->
		// p.getClient()).distinct().collect(Collectors.toList()));
		//
		// System.out.println("getObservableListClient: " +
		// this.mainApplication.getObservableListClient().size());
		//
		// this.autoFillComboBoxClient.setItems(this.mainApplication.getObservableListClient(),
		// P -> P.getClientName());

		this.mainApplication.getUserClientConfigurationMain()
				.getAllUserClientConfigurationByUserAndPayrollHeader(this.mainApplication.getUser(),
						DateFormatter.toDate((LocalDate) this.formattedDatePickerBatchDate.getValue()))
				.forEach(C -> this.mainApplication.getObservableListClient().add(C.getClient()));

		this.autoFillComboBoxClient.setItems(this.mainApplication.getObservableListClient(), P -> P.getClientName());

		ProcessingMessage.closeProcessingMessage();
	}

	public void setDisableButtons(Boolean isDisable) {
		this.buttonEdit.setDisable(isDisable);
		this.buttonDelete.setDisable(isDisable);
	}

	@FXML
	public void handlePrint() {
		System.out.println("PRINT");

		if (this.mainApplication.getSelectedClientGroup() == null && this.mainApplication.getSelectedClient() == null) {
			AlertUtil.showInformationAlert("Please select a client or client group.", this.getParentStage());
			return;
		}

		if (this.payFrom == null || this.payTo == null) {
			AlertUtil.showInformationAlert("Please select pay period.", this.getParentStage());
			return;
		}

		// if (this.tableViewBillingCharging.getSelectionModel().getSelectedItem() ==
		// null) {
		// AlertUtil.showInformationAlert("Please select billing charging.",
		// this.getParentStage());
		// return;
		// }

		this.mainApplication.showPrintBillingCharging(this.mainApplication.getObservableListClient(),
				this.mainApplication.getSelectedClient(), this.mainApplication.getSelectedClientGroup(), this.payFrom,
				this.payTo, DateFormatter.toDate(this.formattedDatePickerBatchDate),
				this.tableViewBillingCharging.getSelectionModel().getSelectedItem(), this.getParentStage());
	}

	@FXML
	public void handleCollect() {
		System.out.println("handleCollect");
		this.observableListEmployeePayroll.clear();

		List<FormattedDatePicker> listFormattedDatePicker = new ArrayList<>();
		listFormattedDatePicker.add(this.formattedDatePickerBatchDate);
		listFormattedDatePicker.add(this.formattedDatePickerPayPeriodFrom);
		listFormattedDatePicker.add(this.formattedDatePickerPayPeriodTo);

		List<AutoFillComboBox<?>> listAutoFillComboBox = new ArrayList<>();
		listAutoFillComboBox.add(this.autoFillComboBoxClient);

		this.setTableViewPayrolll();
	}

	@FXML
	public void handleCharge() {
		// this.isDepartment = true;
		this.mainApplication.setIsBillingChargingByDepartment(true);

		if (this.tableViewPayroll.getSelectionModel() != null
				&& this.tableViewPayroll.getSelectionModel().getSelectedItem() != null) {
			if (this.mainApplication.checkClientConfiguration(this.payroll,
					this.autoFillComboBoxClientGroup.getValueObject())) {
				BillingCharging billingCharging = new BillingCharging();
				billingCharging.setBatchDate(this.payroll.getPayrollHeader().getPayPeriodFrom());
				billingCharging.setPayFrom(this.payroll.getPayPeriodFrom());
				billingCharging.setPayTo(this.payroll.getPayPeriodTo());
				billingCharging.setEmploymentConfiguration(this.payroll.getActualEmploymentConfiguration());
				billingCharging.setPayroll(this.payroll);
				billingCharging.setClient(this.payroll.getActualEmploymentConfiguration().getClient());
				billingCharging.setDateRendered(new Date());
				billingCharging.setDaily(this.payroll.getActualIsDaily());
				// gawa ko --
				// billingCharging.setDaily(this.payroll.getActualEmploymentConfiguration().getIsDaily());

				if (this.mainApplication.showEditBillingCharging(billingCharging, ModificationType.ADD,
						this.mainApplication.getPrimaryStage())) {
					boolean successfulSave = false;
					successfulSave = this.mainApplication.createBillingCharging(billingCharging);

					if (successfulSave) {
						this.mainApplication.getObservableListBillingCharging().add(billingCharging);
						Payroll payroll = this.mainApplication.getPayrollMain()
								.getPayrollByID(billingCharging.getPayroll().getPrimaryKey());
						if (billingCharging.getOvertimeList() != null) {
							billingCharging.getOvertimeList().forEach(C -> {
								if (C.getHoursOfOvertime().intValue() != 0
										|| C.getMinutesOfOvertime().intValue() != 0) {
									this.mainApplication.setBillingChargingOvertime(billingCharging, C);
								}
							});
						}

						ObjectCopyUtil.copyProperties(payroll, this.payroll, Payroll.class);
						this.showDetailsPayroll(this.payroll);
						AlertUtil.showSuccessSaveAlert(this.mainApplication.getPrimaryStage());
					} else {
						AlertUtil.showRecordNotSave("Record not Saved", this.getParentStage());
					}
				}
			} else {
				AlertUtil.showErrorAlert("No client configuration found", getParentStage());
			}
		} else {
			AlertUtil.showErrorAlert("Please select payroll data", getParentStage());
		}
	}

	@FXML
	private TableView<BillingCharging> tableViewBillingCharging;
	@FXML
	private TableColumn<BillingCharging, String> tableColumnEmployeeIDCharging;
	@FXML
	private TableColumn<BillingCharging, String> tableColumnEmployeeNameCharging;
	@FXML
	private Label labelClientCharging;
	@FXML
	private Label labelDepartmentCharging;
	@FXML
	private Label labelClientGroupCharging;
	@FXML
	private Label labelDailyCharging;
	@FXML
	private Label labelRegularDaysCharging;
	@FXML
	private Label labelHolidayCharging;
	@FXML
	private Label labelUndertimeHoursCharging;
	@FXML
	private Label labelUndertimeMinsCharging;
	@FXML
	private Label labelPayFromCharging;
	@FXML
	private Label labelPayToCharging;
	@FXML
	private Label labelBatchDateCharging;
	@FXML
	private Button buttonSaveCharging;
	@FXML
	private Button buttonCancelCharging;

	@FXML
	private TableView<EmployeePayroll> tableViewPayroll;
	@FXML
	private TableColumn<EmployeePayroll, String> tableColumnEmployeeIDPayroll;
	@FXML
	private TableColumn<EmployeePayroll, String> tableColumnEmployeeNamePayroll;
	@FXML
	private Label labelClientPayroll;
	@FXML
	private Label labelDepartmentPayroll;
	@FXML
	private Label labelPositionPayroll;
	@FXML
	private Label labelTaxExemptionPayroll;
	@FXML
	private Label labelDailyPayroll;
	@FXML
	private Label labelRegularDaysPayroll;
	@FXML
	private Label labelRegularPayPayroll;
	@FXML
	private Label labelPayRatePayroll;
	@FXML
	private Label labelHolidayPayroll;
	@FXML
	private Label labelAbsentPayroll;
	@FXML
	private Label labelUndertimePayroll;
	@FXML
	private Label labelPayFromPayroll;
	@FXML
	private Label labelPayToPayroll;
	@FXML
	private Label labelBatchDatePayroll;
	@FXML
	private AnchorPane anchorPaneBillingCharging;
	@FXML
	private FormattedDatePicker formattedDatePickerBatchDate;
	@FXML
	private FormattedDatePicker formattedDatePickerPayPeriodFrom;
	@FXML
	private FormattedDatePicker formattedDatePickerPayPeriodTo;
	@FXML
	private AutoFillComboBox<Client> autoFillComboBoxClient;
	@FXML
	private AutoFillComboBox<ClientGroup> autoFillComboBoxClientGroup;
	@FXML
	private Button buttonCollect;
	@FXML
	private Button buttonCharge;
	@FXML
	private MenuButton menuButtonCharge;
	@FXML
	private Label labelPositionCharging;
	@FXML
	private Label labelRateCharging;
	@FXML
	private Label labelWorkHrsCharging;

	@FXML
	private TableView<Overtime> tableViewPayrollOvertime;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTType;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTHours;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTMins;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTAmount;

	@FXML
	private TableView<Overtime> tableViewChargingOvertime;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingOldRate;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingNewRate;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingHours;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingMins;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingAmount;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingIsCoverUp;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingIsExtendedDuty;

	@FXML
	private Label labelBillRate;
	@FXML
	private Label labelBillRateOvertime;

	// @FXML
	// private Label labelOvertimeType;
	//
	// @FXML
	// private Label labelOvertimeHrs;
	//
	// @FXML
	// private Label labelOvertimeMins;

}
