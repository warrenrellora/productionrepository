package ph.com.lbpsc.production.billingcharging.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import ph.com.lbpsc.production.billingcharging.BillingChargingMain;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.billingcomputation.computation.BillingComputation;
import ph.com.lbpsc.production.billingcomputation.computation.BillingComputationStandard;
import ph.com.lbpsc.production.billingrate.model.BillingRate;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField.ValidationType;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.overtime.model.Overtime;
import ph.com.lbpsc.production.payrollcomputation.model.ComputedRates;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObjectCopyUtil;
import ph.com.lbpsc.production.util.TableColumnUtil;

public class EditBillingChargingController extends MasterEditController<BillingCharging, BillingChargingMain> {
	private BillingCharging temporaryBillingCharging = new BillingCharging();
	private BillingCharging originalBillingCharging = new BillingCharging();
	private ObservableList<Overtime> overtimeChargingObsList = FXCollections.observableArrayList();
	// private List<Overtime> tempListOvertime = new ArrayList<>();

	@Override
	public boolean isValid() {
		System.out.println("isValid");

		List<ValidatedTextField> validatedTextFieldList = new ArrayList<>();
		List<AutoFillComboBox<?>> autoFillComboBoxsList = new ArrayList<>();
		List<DatePicker> datePickerList = new ArrayList<>();
		validatedTextFieldList.add(this.validatedTextFieldHoursOfUndertime);
		validatedTextFieldList.add(this.validatedTextFieldMinutesOfUndertime);
		validatedTextFieldList.add(this.validatedTextFieldNumberOfDaysOfHoliday);
		validatedTextFieldList.add(this.validatedTextFieldNumberOfRegularDays);
		// if (this.autoFillComboBoxClientGroup.getValueObject() != null) {
		// autoFillComboBoxsList.add(this.autoFillComboBoxClientGroup);
		// } else {
		// autoFillComboBoxsList.add(this.autoFillComboBoxDepartment);
		// autoFillComboBoxsList.add(this.autoFillComboBoxClient);
		// }

		datePickerList.add(this.formattedDatePickerDateRendered);

		if (!FieldValidator.textFieldValidate(validatedTextFieldList)
				| !FieldValidator.autoFillComboBoxValidate(autoFillComboBoxsList)
				| !FieldValidator.datePickerValidate(datePickerList)) {
			AlertUtil.showIncompleteDataAlert(this.dialogStage);
			return false;
		}

		BillingCharging billingCharging = new BillingCharging();
		this.setProperties(billingCharging);
		// this.setProperties(this.objectToModify);

		// billingCharging.getOvertimeList().forEach(p -> System.out.println("AFTER SET
		// PROPERTIES newBillingChargingOT: "
		// + p.getOvertimeType().getOvertimeName() + " : " + p.getAmountOfOvertime()));
		// this.originalBillingCharging.getOvertimeList()
		// .forEach(p -> System.out.println("AFTER SET PROPERTIES oldBillingChargingOT:
		// "
		// + p.getOvertimeType().getOvertimeName() + " : " + p.getAmountOfOvertime()));

		// if (!this.mainApplication.isWithChanges(billingCharging,
		// this.originalBillingCharging)) {
		// AlertUtil.showErrorAlert("No changes detected.",
		// this.mainApplication.getPrimaryStage());
		// return false;
		// }

		// if (!this.checkBoxTransfer.isSelected() || this.objectToModify.getDaily()) {
		// if (FieldValidator.isFieldEmpty(this.validatedTextFieldNumberOfRegularDays)
		// & FieldValidator.isFieldEmpty(this.validatedTextFieldNumberOfDaysOfHoliday)
		// & FieldValidator.isFieldEmpty(this.validatedTextFieldHoursOfUndertime)
		// & FieldValidator.isFieldEmpty(this.validatedTextFieldMinutesOfUndertime)) {
		// return false;
		// }
		// }

		if (!this.validateChargeInput()) {
			AlertUtil.showErrorAlert("The value entered exceeds the maximum value allowed.", this.dialogStage);
			return false;
		}

		System.out.println("isVALID: check overtime charging input");

		// for (Overtime overtimeEncoded :
		// this.mainApplication.getOvertimeMain().getObservableListOvertime()) {
		//
		// if (overtimeEncoded.getAmountOfOvertime() == null
		// || overtimeEncoded.getAmountOfOvertime().compareTo(BigDecimal.ZERO) == 0) {
		// continue;
		// }
		//
		// Overtime overtime =
		// this.objectToModify.getPayroll().getOvertimeList().stream()
		// .filter(p -> p.getOvertimeType() != null &&
		// p.getOvertimeType().getPrimaryKey()
		// .compareTo(overtimeEncoded.getOvertimeType().getPrimaryKey()) == 0)
		// .findAny().orElse(null);
		//
		// // Overtime overtime =
		// //
		// ObservableListUtil.getObject(this.objectToModify.getPayroll().getOvertimeList(),
		// // predicate -> predicate.getOvertimeType().getPrimaryKey()
		// // .equals(overtimeEncoded.getOvertimeType().getPrimaryKey()));
		//
		// if (overtime != null) {
		// BigDecimal overtimeBilling = this.minutesToHours(new
		// BigDecimal(overtimeEncoded.getHoursOfOvertime()),
		// new BigDecimal(overtimeEncoded.getMinutesOfOvertime()));
		// BigDecimal overtimePayroll = this.minutesToHours(new
		// BigDecimal(overtime.getHoursOfOvertime()),
		// new BigDecimal(overtime.getMinutesOfOvertime()));
		//
		// System.out.println(overtimeEncoded.getOvertimeType().getOvertimeName() + " :
		// " + overtimeBilling + " : "
		// + overtimePayroll);
		//
		// if (overtimeBilling.compareTo(overtimePayroll) > 0) {
		// // AlertUtil.showErrorAlert("The value entered exceeds the maximum value
		// // allowed.", this.dialogStage);
		// // return false;
		//
		// AlertUtil.showErrorAlert(
		// "The value entered for overtime: " +
		// overtimeEncoded.getOvertimeType().getOvertimeName()
		// + " exceeds allowed overtime hours & minutes.",
		// this.dialogStage);
		// return false;
		// }
		// } else {
		// AlertUtil.showErrorAlert(
		// "The value entered for overtime: " +
		// overtimeEncoded.getOvertimeType().getOvertimeName()
		// + " exceeds allowed overtime hours & minutes.",
		// this.dialogStage);
		// return false;
		// }
		// }

		return true;
	}

	public boolean validateChargeInput() {
		this.temporaryBillingCharging = new BillingCharging();

		this.temporaryBillingCharging
				.setRegularDays(Integer.parseInt(this.validatedTextFieldNumberOfRegularDays.getText()));
		// this.temporaryBillingCharging.setWorkHours(new
		// BigDecimal(this.validatedTextFieldHrsRegPay.getText()));
		this.temporaryBillingCharging
				.setHolidays(Integer.parseInt(this.validatedTextFieldNumberOfDaysOfHoliday.getText()));
		this.temporaryBillingCharging
				.setUndertimeHours(Integer.parseInt(this.validatedTextFieldHoursOfUndertime.getText()));
		this.temporaryBillingCharging
				.setUndertimeMinutes(Integer.parseInt(this.validatedTextFieldMinutesOfUndertime.getText()));

		if (
		// this.compareChargeInput(this.temporaryBillingCharging.getRegularDays(),
		// (this.mainApplication.getPayroll().getNumberOfRegularDays()
		// + (this.objectToModify.getRegularDays() == null ? 0 :
		// this.objectToModify.getRegularDays())),
		// this.validatedTextFieldNumberOfRegularDays
		// )
		// |
		this.compareChargeInput(this.temporaryBillingCharging.getHolidays(),
				(this.mainApplication.getPayroll().getNumberOfHolidays()
						+ (this.objectToModify.getHolidays() == null ? 0 : this.objectToModify.getHolidays())),
				this.validatedTextFieldNumberOfDaysOfHoliday)
				| this.compareChargeInput(this.temporaryBillingCharging.getUndertimeHours(),
						(this.mainApplication.getPayroll().getHoursOfUndertime()
								+ (this.objectToModify.getUndertimeHours() == null ? 0
										: this.objectToModify.getUndertimeHours())),
						this.validatedTextFieldHoursOfUndertime)
				| this.compareChargeInput(this.temporaryBillingCharging.getUndertimeMinutes(),
						(this.mainApplication.getPayroll().getMinutesOfUndertime()
								+ (this.objectToModify.getUndertimeMinutes() == null ? 0
										: this.objectToModify.getUndertimeMinutes())),
						this.validatedTextFieldMinutesOfUndertime)) {
			return false;
		}

		return true;
	}

	public boolean compareChargeInput(Integer firstNumber, Integer secondNumber,
			ValidatedTextField validatedTextField) {
		if (firstNumber > secondNumber) {
			FieldValidator.setNodeStyle(validatedTextField);
			return true;
		} else {
			validatedTextField.setStyle("");
		}
		return false;
	}

	public void setProperties(BillingCharging billingCharging) {
		System.out.println("\nsetProperties");
		billingCharging.setClient(this.objectToModify.getClient());
		billingCharging.setDepartment(this.objectToModify.getDepartment());
		billingCharging.setClientGroup(this.objectToModify.getClientGroup());

		// billingCharging.setWorkHours(new
		// BigDecimal(this.validatedTextFieldHrsRegPay.getText()));
		billingCharging.setRegularDays(Integer.parseInt(this.validatedTextFieldNumberOfRegularDays.getText()));
		billingCharging.setUndertimeHours(Integer.parseInt(this.validatedTextFieldHoursOfUndertime.getText()));
		billingCharging.setUndertimeMinutes(Integer.parseInt(this.validatedTextFieldMinutesOfUndertime.getText()));
		billingCharging.setHolidays(Integer.parseInt(this.validatedTextFieldNumberOfDaysOfHoliday.getText()));
		billingCharging.setIsTransfer(checkBoxTransfer.isSelected());
		billingCharging.setRemarks(null);

		billingCharging.setDateRendered(DateFormatter.toDate(this.formattedDatePickerDateRendered.getValue()));

		// List<Overtime> overtimeFinalChangesList = new ArrayList<>();
		// billingCharging.setOvertimeList(this.mainApplication.getOvertimeMain().getObservableListOvertime());

		// billingCharging.getOvertimeList().forEach(C -> {
		// if (C.getBillingCharging() == null) {
		// C.setBillingCharging(billingCharging);
		// }
		//
		// if (C.getAmountOfOvertime() != null &&
		// C.getAmountOfOvertime().compareTo(BigDecimal.ZERO) > 0) {
		// overtimeFinalChangesList.add(C);
		// }
		// });
		//
		// billingCharging.setOvertimeList(overtimeFinalChangesList);

		billingCharging.setEmploymentConfiguration(this.objectToModify.getEmploymentConfiguration());
		billingCharging.setDaily(this.objectToModify.getEmploymentConfiguration().getIsDaily());

		billingCharging.setChangedInComputer(this.mainApplication.getComputerName());
		billingCharging.setChangedOnDate(this.mainApplication.getDateNow());
		billingCharging.setUser(this.mainApplication.getUser());

		BillingRate billingRateOvertime = new BillingRate();

		billingRateOvertime = this.mainApplication.getBillingRate(this.objectToModify.getPayroll(), true,
				this.objectToModify.getEmploymentConfiguration());

		if (billingRateOvertime == null) {
			billingRateOvertime = this.mainApplication.getBillingRate(this.objectToModify.getPayroll(), false,
					this.objectToModify.getEmploymentConfiguration());
		}

		for (Overtime overtime : this.overtimeChargingObsList) {
			overtime.setBillingRate(billingRateOvertime);
		}

		billingCharging.setOvertimeList(this.overtimeChargingObsList);

		billingCharging.setEmployee(this.objectToModify.getPayroll().getEmploymentHistory().getEmployee());
	}

	@Override
	public void onSave() {
		this.setProperties(this.objectToModify);
	}

	@Override
	public void onShowEditDialogStage() {
		System.out.println("onShowEditDialogStage");

		System.out.println("bill rate null : 2nd form");
		System.out.println(this.objectToModify.getBillingRate() == null);
		if (modificationType.equals(ModificationType.ADD)) {
			this.objectToModify.setEmploymentConfiguration(this.mainApplication.getSelectedEmploymentConfiguration());

		}

		ObjectCopyUtil.copyProperties(this.objectToModify, this.originalBillingCharging, BillingCharging.class);

		System.out.println("origbillchargePRIKEY: " + this.originalBillingCharging.getPrimaryKey());

		this.showDetails(this.objectToModify);

		this.setTableViewPayrollOvertime();

		if (this.modificationType.equals(ModificationType.ADD)) {
			this.overtimeChargingObsList.setAll(this.mainApplication.getOvertimeMain()
					.getBillingChargingOvertimeByBillingChargingPrikey(this.objectToModify.getPrimaryKey()));
		} else {
			this.overtimeChargingObsList.setAll(this.objectToModify.getOvertimeList());
		}
		this.setTableViewExtendedDuty();

		this.titledPaneRegular.setVisible(false);
		this.titledPaneRegular.setFocusTraversable(false);
	}

	public void setTableViewPayrollOvertime() {
		ObservableList<Overtime> observableListPayroll = FXCollections.observableArrayList();
		if (this.modificationType.equals(ModificationType.ADD)) {
			if (!this.mainApplication.getSelectedPayroll().getOvertimeList().isEmpty()) {
				observableListPayroll.setAll(this.mainApplication.getSelectedPayroll().getOvertimeList());
			}
		} else {
			if (!this.objectToModify.getPayroll().getOvertimeList().isEmpty()) {
				observableListPayroll.setAll(this.objectToModify.getPayroll().getOvertimeList());
			}
		}
		this.tableViewPayrollOvertime.setItems(observableListPayroll);
		TableColumnUtil.setColumn(this.tableColumnPayrollOTRate, p -> p.getOvertimeType().getOvertimeName());
		TableColumnUtil.setColumn(this.tableColumnPayrollOTHrs, p -> p.getHoursOfOvertime());
		TableColumnUtil.setColumn(this.tableColumnPayrollOTMins, p -> p.getMinutesOfOvertime());
		TableColumnUtil.setColumn(this.tableColumnPayrollOTAmount, p -> p.getAmountOfOvertime());
	}

	public void setTableViewExtendedDuty() {
		this.tableViewExtendedDuty.setItems(this.overtimeChargingObsList);

		TableColumnUtil.setColumn(this.tableColumnChargingHrs, p -> p.getHoursOfOvertime());
		TableColumnUtil.setColumn(this.tableColumnChargingMins, p -> p.getMinutesOfOvertime());
		TableColumnUtil.setColumn(this.tableColumnChargingOldRate,
				p -> p.getOldOvertimeType() != null ? p.getOldOvertimeType().getOvertimeName() : "");
		TableColumnUtil.setColumn(this.tableColumnChargingNewRate, p -> p.getOvertimeType().getOvertimeName());
		TableColumnUtil.setColumn(this.tableColumnChargingAmount, p -> p.getAmountOfOvertime());
		TableColumnUtil.setColumn(this.tableColumnChargingCoverUp,
				p -> p.getIsRDCoverUp() ? "Rest day" : p.getIsRHCoverUp() ? "Holiday" : "");
		TableColumnUtil.setColumn(this.tableColumnChargingExtendedDuty,
				p -> p.getIsExtendedDuty() != null ? p.getIsExtendedDuty() ? "Yes" : "No" : "");
	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showDetails(BillingCharging billingCharging) {
		this.labelEmployeeCode.setText(
				this.objectToModify.getPayroll().getEmploymentHistory().getEmployee().getEmployeeCode().toString());
		this.labelEmployeeName
				.setText(this.objectToModify.getPayroll().getEmploymentHistory().getEmployee().getEmployeeName());
		this.labelChargingPosition
				.setText(this.objectToModify.getEmploymentConfiguration().getPosition().getPositionName());

		this.mainApplication.populateOvertime(this.mainApplication.getPayrollClientConfiguration(),
				this.mainApplication.getBillingClientConfiguration());

		this.labelChargingClient.setText(
				this.objectToModify.getClient() != null ? this.objectToModify.getClient().getClientName() : "");
		this.labelChargingDepartment.setText(
				this.objectToModify.getDepartment() != null ? this.objectToModify.getDepartment().getDepartmentName()
						: "");
		this.labelChargingClientGroup.setText(
				this.objectToModify.getClientGroup() != null ? this.objectToModify.getClientGroup().getGroupName()
						: "");
		this.validatedTextFieldHoursOfUndertime.setText(
				billingCharging.getUndertimeHours() != null ? billingCharging.getUndertimeHours().toString() : "0");
		this.formattedDatePickerDateRendered.setValue(DateFormatter.toLocalDate(billingCharging.getDateRendered()));

		this.validatedTextFieldMinutesOfUndertime.setText(
				billingCharging.getUndertimeMinutes() != null ? billingCharging.getUndertimeMinutes().toString() : "0");
		this.validatedTextFieldNumberOfDaysOfHoliday
				.setText(billingCharging.getHolidays() != null ? billingCharging.getHolidays().toString() : "0");
		this.validatedTextFieldNumberOfRegularDays
				.setText(billingCharging.getRegularDays() != null ? billingCharging.getRegularDays().toString() : "0");

		this.labelChargingBillRate.setText(billingCharging.getBillingRate() != null
				? billingCharging.getBillingRate().getAmountOfDailyBillingRate().toString()
				: "");
		this.labelChargingBillRateOvertime.setText(billingCharging.getBillingRateOvertime() != null
				? billingCharging.getBillingRateOvertime().getAmountOfDailyBillingRate().toString()
				: "");

		this.labelPayrollRegularDays.setText(String.valueOf(billingCharging.getPayroll().getNumberOfRegularDays()));
		this.labelPayrollUndertime.setText("Hours: " + billingCharging.getPayroll().getHoursOfUndertime() + " Mins: "
				+ billingCharging.getPayroll().getMinutesOfUndertime());
		this.labelPayrollHoliday.setText(String.valueOf(billingCharging.getPayroll().getNumberOfHolidays()));

		// this.validatedTextFieldHrsRegPay
		// .setText(billingCharging.getWorkHours() != null ?
		// billingCharging.getWorkHours().toString() : "0");

		// TableViewUtil
		// .refreshTableView(this.mainApplication.getOvertimeMain().getRootController().tableViewPayrollOvertime);
	}

	@Override
	public void onSetMainApplication() {
		// this.anchorPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
		// this.temporaryBillingCharging = new BillingCharging();
		//
		// if (event.getCode().toString().equalsIgnoreCase("ALT")) {
		// this.setAllNodeBorderColor("red");
		// this.temporaryBillingCharging
		// .setRegularDays(Integer.parseInt(this.validatedTextFieldNumberOfRegularDays.getText()));
		// this.temporaryBillingCharging
		// .setHolidays(Integer.parseInt(this.validatedTextFieldNumberOfDaysOfHoliday.getText()));
		// this.temporaryBillingCharging
		// .setUndertimeHours(Integer.parseInt(this.validatedTextFieldHoursOfUndertime.getText()));
		// this.temporaryBillingCharging
		// .setUndertimeMinutes(Integer.parseInt(this.validatedTextFieldMinutesOfUndertime.getText()));
		// this.tempListOvertime.addAll(this.mainApplication.getOvertimeMain().getObservableListOvertime());
		//
		// this.validatedTextFieldNumberOfRegularDays.setText(String.valueOf((this.mainApplication.getPayroll()
		// .getNumberOfRegularDays()
		// + (this.objectToModify.getRegularDays() == null ? 0 :
		// this.objectToModify.getRegularDays()))));
		// this.validatedTextFieldNumberOfDaysOfHoliday.setText(String.valueOf((this.mainApplication.getPayroll()
		// .getNumberOfHolidays()
		// + (this.objectToModify.getHolidays() == null ? 0 :
		// this.objectToModify.getHolidays()))));
		// this.validatedTextFieldHoursOfUndertime
		// .setText(String.valueOf((this.mainApplication.getPayroll().getHoursOfUndertime()
		// + (this.objectToModify.getUndertimeHours() == null ? 0
		// : this.objectToModify.getUndertimeHours()))));
		// this.validatedTextFieldMinutesOfUndertime
		// .setText(String.valueOf((this.mainApplication.getPayroll().getMinutesOfUndertime()
		// + (this.objectToModify.getUndertimeMinutes() == null ? 0
		// : this.objectToModify.getUndertimeMinutes()))));
		//
		// System.out.println("prikeyPayroll: " +
		// this.mainApplication.getPayroll().getPrimaryKey());
		// if (this.mainApplication.getSelectedPayroll() != null
		// && this.mainApplication.getSelectedPayroll().getOvertimeList() != null
		// && !this.mainApplication.getSelectedPayroll().getOvertimeList().isEmpty()) {
		// System.out.println("=======================================");
		// System.out.println(this.mainApplication.getSelectedPayroll().getOvertimeList().size());
		// this.mainApplication.getSelectedPayroll().getOvertimeList().forEach(p -> {
		// if (p.getOvertimeType() != null) {
		// System.out.println(p.getOvertimeType().getOvertimeName() + " : "
		// + p.getOvertimeType().getPrimaryKey());
		// }
		// });
		//
		// this.mainApplication.getOvertimeMain().populateDefaultBillingOvertimeList(
		// this.mainApplication.getSelectedPayroll().getOvertimeList(),
		// this.mainApplication.getBillingClientConfiguration());
		// } else {
		// AlertUtil.showInformationAlert("No overtime found for this payroll",
		// this.getDialogStage());
		// }
		//
		// TableViewUtil.refreshTableView(
		// this.mainApplication.getOvertimeMain().getRootController().tableViewPayrollOvertime);
		//
		// this.titledPaneOvertime.setText("Payroll Overtime");
		// }
		// });
		//
		// this.anchorPane.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
		// if (event.getCode().toString().equalsIgnoreCase("ALT")) {
		// this.setAllNodeBorderColor("default");
		// this.validatedTextFieldNumberOfRegularDays
		// .setText(this.temporaryBillingCharging.getRegularDays().toString());
		// this.validatedTextFieldNumberOfDaysOfHoliday
		// .setText(this.temporaryBillingCharging.getHolidays().toString());
		// this.validatedTextFieldHoursOfUndertime
		// .setText(this.temporaryBillingCharging.getUndertimeHours().toString());
		// this.validatedTextFieldMinutesOfUndertime
		// .setText(this.temporaryBillingCharging.getUndertimeMinutes().toString());
		//
		// if (this.tempListOvertime != null) {
		// this.getMainApplication().getOvertimeMain().populateDefaultBillingOvertimeList(
		// this.tempListOvertime, this.mainApplication.getBillingClientConfiguration());
		// } else {
		// AlertUtil.showInformationAlert("No overtime found for this payroll",
		// this.getDialogStage());
		// }
		//
		// TableViewUtil.refreshTableView(
		// this.mainApplication.getOvertimeMain().getRootController().tableViewPayrollOvertime);
		//
		// this.titledPaneOvertime.setText("Charging Overtime");
		// }
		// });

		this.validatedTextFieldHoursOfUndertime.setValidationType(ValidationType.NUMERIC);
		this.validatedTextFieldMinutesOfUndertime.setValidationType(ValidationType.NUMERIC);
		this.validatedTextFieldNumberOfDaysOfHoliday.setValidationType(ValidationType.NUMERIC);
		this.validatedTextFieldNumberOfRegularDays.setValidationType(ValidationType.NUMERIC);

		this.validatedTextFieldHoursOfUndertime.setFormat("#0");
		this.validatedTextFieldMinutesOfUndertime.setFormat("#0");
		this.validatedTextFieldNumberOfDaysOfHoliday.setFormat("#0");
		this.validatedTextFieldNumberOfRegularDays.setFormat("#0");
		// this.validatedTextFieldHrsRegPay.setFormat("#0");

		// this.checkBoxTransfer.selectedProperty().addListener((observable, oldValue,
		// newValue) -> {
		// this.titledPaneRegular.setVisible(!newValue);
		// this.titledPaneOvertime.setVisible(!newValue);
		// });

		// this.autoFillComboBoxDepartment.valueObjectProperty().addListener((obs,
		// oldValue, newValue) -> {
		// if (newValue != null) {
		// System.out.println("autoFillComboBoxDepartment listener");
		// this.objectToModify.setDepartment(newValue);
		// if (this.mainApplication.getIsBillingChargingByDepartment()) {
		// System.out.println("byDepartment");
		// this.mainApplication.fetchBillingClientConfiguration(this.objectToModify);
		// this.mainApplication.fetchPayrollClientConfiguration(this.objectToModify);
		// this.mainApplication.getOvertimeMain()
		// .setBillingClientConfiguration(this.mainApplication.getBillingClientConfiguration());
		// } else {
		// System.out.println("byEmpConfig");
		// }
		// }
		// });

		/////////////////////////////////////////////////////////////////////////////////////////////////////////

		this.tableViewExtendedDuty.setRowFactory(p -> {
			TableRow<Overtime> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getClickCount() == 2) {
					if (this.mainApplication.showSetOvertimeBillingCharging(this.objectToModify, row.getItem(),
							ModificationType.EDIT)) {
						AlertUtil.showSuccessSaveAlert(this.getDialogStage());
						this.handleComputeOvertime();
						this.tableViewExtendedDuty.refresh();
						this.setTableViewExtendedDuty();
					}
				}
			});
			return row;
		});

	}

	public void handleAddCharging() {
		// TODO
		System.out.println("handleAddCharging");
		System.out.println(this.objectToModify.getOvertimeList() != null);
		if (this.mainApplication.showSetOvertimeBillingCharging(this.objectToModify, null, ModificationType.ADD)) {
			System.out.println("added");
			// TableViewUtil.refreshTableView(this.tableViewExtendedDuty);
			// this.tableViewExtendedDuty.refresh();
			System.out.println("getOvertimeListSize: " + this.objectToModify.getOvertimeList().size());

			this.objectToModify.getOvertimeList().forEach(o -> {
				System.out.println(o.getOldOvertimeType().getOvertimeName() + " : "
						+ o.getOvertimeType().getOvertimeName() + " : " + o.getIsRDCoverUp());
			});

			this.overtimeChargingObsList.setAll(this.objectToModify.getOvertimeList());
			this.handleComputeOvertime();
			this.tableViewExtendedDuty.refresh();
			this.setTableViewExtendedDuty();
		}
	}

	public void handleEditCharging() {
		System.out.println("edit");
		if (this.mainApplication.showSetOvertimeBillingCharging(this.objectToModify,
				this.tableViewExtendedDuty.getSelectionModel().getSelectedItem(), ModificationType.EDIT)) {
			AlertUtil.showSuccessSaveAlert(this.getDialogStage());
			this.handleComputeOvertime();
			this.tableViewExtendedDuty.refresh();
			this.setTableViewExtendedDuty();
		}
	}

	public void handleDeleteCharging() {
		System.out.println("delete");
		if (AlertUtil.showDeleteQuestionAlertBoolean(this.dialogStage)) {
			Overtime overtime = this.tableViewExtendedDuty.getSelectionModel().getSelectedItem();
			if (overtime != null) {
				this.overtimeChargingObsList.remove(overtime);
				this.setTableViewExtendedDuty();
			}
		}
	}

	public void setAllNodeBorderColor(String color) {
		if (color.equals("default")) {
			this.validatedTextFieldNumberOfRegularDays.setStyle("");
			this.validatedTextFieldNumberOfDaysOfHoliday.setStyle("");
			this.validatedTextFieldHoursOfUndertime.setStyle("");
			this.validatedTextFieldMinutesOfUndertime.setStyle("");
			this.mainApplication.getOvertimeMain().getRootController().tableViewPayrollOvertime.setStyle("");
		} else {
			this.validatedTextFieldNumberOfRegularDays.setStyle("-fx-border-color: " + color);
			this.validatedTextFieldNumberOfDaysOfHoliday.setStyle("-fx-border-color: " + color);
			this.validatedTextFieldHoursOfUndertime.setStyle("-fx-border-color: " + color);
			this.validatedTextFieldMinutesOfUndertime.setStyle("-fx-border-color: " + color);
			this.mainApplication.getOvertimeMain().getRootController().tableViewPayrollOvertime
					.setStyle("-fx-border-color: " + color);
		}
	}

	public void setAnchorPaneOvertime(AnchorPane outputpane) {
		this.anchorPaneOvertime.getChildren().add(outputpane);
		AnchorPane.setTopAnchor(outputpane, 0.0);
		AnchorPane.setLeftAnchor(outputpane, 0.0);
		AnchorPane.setRightAnchor(outputpane, 0.0);
		AnchorPane.setBottomAnchor(outputpane, 0.0);
	}

	public void handleComputeOvertime() {
		System.out.println("handleComputeOvertime");
		System.out.println(this.objectToModify.getEmploymentConfiguration().getPrimaryKey());

		BillingRate billingRateOvertime = new BillingRate();

		billingRateOvertime = this.objectToModify.getBillingRateOvertime();

		// billingRateOvertime =
		// this.mainApplication.getBillingRate(this.objectToModify.getPayroll(), true,
		// this.objectToModify.getEmploymentConfiguration());
		//
		// if (billingRateOvertime == null) {
		// billingRateOvertime =
		// this.mainApplication.getBillingRate(this.objectToModify.getPayroll(), false,
		// this.objectToModify.getEmploymentConfiguration());
		// }

		BillingComputation billingComputation = new BillingComputation();
		// BillingRate billingRateOvertime = new BillingRate();

		// if (modificationType == ModificationType.ADD) {
		// billingRateOvertime = this.objectToModify.getBillingRateOvertime() == null
		// ? this.objectToModify.getBillingRate()
		// : this.objectToModify.getBillingRateOvertime();
		// } else {
		// billingRateOvertime =
		// this.objectToModify.getOvertimeList().get(0).getBillingRate();
		// }

		// this.objectToModify.getOvertimeList().get(0).getBillingRate().getAmountOfDailyBillingRate();

		BillingComputationStandard billingComputationStandard = new BillingComputationStandard();
		ComputedRates computedRates = new ComputedRates();

		computedRates = billingComputationStandard.evaluateRates(this.objectToModify.getPayroll().getActualIsDaily(),
				billingRateOvertime, billingRateOvertime.getNumberOfBillingDays());

		// if (overtimeList != null && !overtimeList.isEmpty()) {
		// for (Overtime overtime : overtimeList) {
		// overtime.setAmountOfOvertime(
		// super.evaluateOvertime(overtime.getOvertimeType(),
		// this.computedRates.getAmountOfHourlyRate(),
		// overtime.getHoursOfOvertime(), overtime.getMinutesOfOvertime(), new
		// BigDecimal(60)));
		// }
		// }

		if (computedRates == null) {
			System.out.println("no computed rates");
		}

		BigDecimal totalHrsExtendedDuty = BigDecimal.ZERO;
		BigDecimal totalMinsExtendedDuty = BigDecimal.ZERO;
		for (Overtime overtime : this.overtimeChargingObsList) {
			if (billingRateOvertime != null && overtime.getOvertimeType() != null
					&& (overtime.getHoursOfOvertime() != null || overtime.getMinutesOfOvertime() != null)) {
				overtime.setAmountOfOvertime(billingComputation.evaluateOvertime(overtime.getOvertimeType(),
						computedRates.getAmountOfHourlyRate(), overtime.getHoursOfOvertime(),
						overtime.getMinutesOfOvertime(), new BigDecimal(60)));

				if (overtime.getMinutesOfOvertime() != null) {
					totalMinsExtendedDuty = totalMinsExtendedDuty.add(new BigDecimal(overtime.getMinutesOfOvertime()));
				}

				totalHrsExtendedDuty = totalHrsExtendedDuty.add(new BigDecimal(overtime.getHoursOfOvertime()));
			}
		}

		System.out.println("totalMinsExtendedDuty: " + totalMinsExtendedDuty);
		System.out.println("totalHrsExtendedDuty: " + totalHrsExtendedDuty);

		BigDecimal totalMinsIntoHrs = BigDecimal.ZERO;
		totalMinsIntoHrs = totalMinsExtendedDuty.divide(new BigDecimal("60"), 2, RoundingMode.HALF_UP);
		totalHrsExtendedDuty = totalHrsExtendedDuty.add(totalMinsIntoHrs);

		System.out.println("totalHrsExtendedDutyFINAL: " + totalHrsExtendedDuty);

		this.objectToModify.setWorkHours(totalHrsExtendedDuty);

		this.tableViewExtendedDuty.refresh();

		// TableViewUtil
		// .refreshTableView(this.mainApplication.getOvertimeMain().getRootController().tableViewPayrollOvertime);
	}

	@FXML
	private AnchorPane anchorPaneOvertime;
	@FXML
	private Label labelEmployeeCode;
	@FXML
	private Label labelEmployeeName;
	@FXML
	private Label labelChargingPosition;
	@FXML
	private Label labelChargingClient;
	@FXML
	private Label labelChargingDepartment;
	@FXML
	private Label labelChargingClientGroup;
	@FXML
	private Label labelChargingBillRate;
	@FXML
	private Label labelChargingBillRateOvertime;
	@FXML
	private Label labelPayrollRegularDays;
	@FXML
	private Label labelPayrollHoliday;
	@FXML
	private Label labelPayrollUndertime;
	@FXML
	private ValidatedTextField validatedTextFieldNumberOfRegularDays;
	@FXML
	private ValidatedTextField validatedTextFieldNumberOfDaysOfHoliday;
	@FXML
	private ValidatedTextField validatedTextFieldHoursOfUndertime;
	@FXML
	private ValidatedTextField validatedTextFieldMinutesOfUndertime;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private FormattedDatePicker formattedDatePickerDateRendered;
	@FXML
	private CheckBox checkBoxTransfer;
	@FXML
	private TitledPane titledPaneRegular;
	@FXML
	private TitledPane titledPaneOvertime;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private TableView<Overtime> tableViewPayrollOvertime;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTRate;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTHrs;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTMins;
	@FXML
	private TableColumn<Overtime, String> tableColumnPayrollOTAmount;
	@FXML
	private Button buttonAddCharging;
	@FXML
	private Button buttonEditCharging;
	@FXML
	private Button buttonDeleteCharging;
	@FXML
	private TableView<Overtime> tableViewExtendedDuty;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingOldRate;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingNewRate;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingHrs;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingMins;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingAmount;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingCoverUp;
	@FXML
	private TableColumn<Overtime, String> tableColumnChargingExtendedDuty;
	// @FXML
	// private ValidatedTextField validatedTextFieldHrsRegPay;
	// @FXML
	// private ValidatedTextField validatedTextFieldDaysRegPay;
}
