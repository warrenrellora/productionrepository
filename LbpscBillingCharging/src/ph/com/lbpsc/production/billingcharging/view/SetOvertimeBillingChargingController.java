package ph.com.lbpsc.production.billingcharging.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import ph.com.lbpsc.production.billingcharging.BillingChargingMain;
import ph.com.lbpsc.production.billingcharging.model.BillingCharging;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.overtime.model.Overtime;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;

public class SetOvertimeBillingChargingController extends MasterEditController<BillingCharging, BillingChargingMain> {
	Overtime selectedOvertime = new Overtime();

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub

		if (!this.isValidHrsAndMins()) {
			AlertUtil.showIncompleteDataAlert(this.getDialogStage());
			return false;
		}

		if (this.isOvertimeHrMinsExceeds()) {
			AlertUtil.showInformationAlert("Overtime charging exceeds for \nOvertime Type: "
					+ this.autoFillComboBoxOTtypeOld.getValueObject().getOvertimeName(), this.getDialogStage());
			return false;
		}

		return true;
	}

	public boolean isValidHrsAndMins() {
		List<ValidatedTextField> textFieldList = new ArrayList<>();
		textFieldList.add(this.validatedTextFieldHoursOfOvertime);
		textFieldList.add(this.validatedTextFieldMinutesOfOvertime);
		Integer hrs = Integer.parseInt(this.validatedTextFieldHoursOfOvertime.getText());
		Integer mins = Integer.parseInt(this.validatedTextFieldMinutesOfOvertime.getText());

		if (hrs == 0 && mins == 0) {
			return false;
		}

		if (!FieldValidator.textFieldValidate(textFieldList)) {
			return false;
		}

		return true;
	}

	public boolean isOvertimeHrMinsExceeds() {
		BigDecimal hrMinsPayrollOT = BigDecimal.ZERO;
		BigDecimal hrMinsChargingOT = BigDecimal.ZERO;
		BigDecimal currentObjectOT = BigDecimal.ZERO;
		Integer hours = 0;
		Integer mins = 0;
		List<Overtime> overtimeChargingTempList = new ArrayList<>();
		List<Overtime> overtimeChargingSelectedTypeList = new ArrayList<>();
		List<Overtime> overtimePayrollSelectedTypeList = new ArrayList<>();

		if (this.objectToModify.getOvertimeList() != null) {
			// add all overtime charging in this list
			overtimeChargingTempList.addAll(this.objectToModify.getOvertimeList());
			// if edit, remove the existing ot type to validate again
			if (modificationType.equals(ModificationType.EDIT)) {
				overtimeChargingTempList.removeIf(p -> p.getOldOvertimeType().getPrimaryKey()
						.equals(this.autoFillComboBoxOTtypeOld.getValueObject().getPrimaryKey())
						&& p.getIsExtendedDuty().equals(this.radioButtonExtendedDuty.isSelected())
						&& p.getIsRDCoverUp().equals(this.radioButtonRDCoverUp.isSelected())
						&& p.getIsRHCoverUp().equals(this.radioButtonRHCoverUp.isSelected()));
			}
		}
		// save current ot type charging temporarily in a list for validation
		// this.saveOvertimeCharging(overtimeChargingTempList);

		// compute overall hrs & mins of this ot type from existing overtime charging
		OvertimeType selectedOTtype = this.autoFillComboBoxOTtypeOld.getValueObject();
		if (selectedOTtype != null) {
			overtimeChargingSelectedTypeList.addAll(overtimeChargingTempList.stream()
					.filter(p -> p.getOldOvertimeType().getPrimaryKey().equals(selectedOTtype.getPrimaryKey()))
					.map(p -> p).collect(Collectors.toList()));
			for (Overtime overtime : overtimeChargingSelectedTypeList) {
				hours = hours + overtime.getHoursOfOvertime();
				mins = mins + overtime.getMinutesOfOvertime();
			}
			hrMinsChargingOT = this.mainApplication.minutesToHours(hours, mins);
		}

		// compute overall hrs & mins of this ot type from payroll overtime
		hours = 0;
		mins = 0;
		overtimePayrollSelectedTypeList.addAll(this.objectToModify.getPayroll().getOvertimeList().stream()
				.filter(p -> p.getOvertimeType().getPrimaryKey().equals(selectedOTtype.getPrimaryKey())).map(p -> p)
				.collect(Collectors.toList()));
		for (Overtime overtime : overtimePayrollSelectedTypeList) {
			hours = hours + overtime.getHoursOfOvertime();
			mins = mins + overtime.getMinutesOfOvertime();
		}
		hrMinsPayrollOT = this.mainApplication.minutesToHours(hours, mins);

		// subtract overall hrs and mins of both payroll overtime & charging overtime of
		// this ot type
		BigDecimal hrMinsDiff = BigDecimal.ZERO;
		hrMinsDiff = hrMinsPayrollOT.subtract(hrMinsChargingOT);

		System.out.println("\nhrMinsPayrollOT: " + hrMinsPayrollOT);
		System.out.println("hrMinsChargingOT: " + hrMinsChargingOT);
		System.out.println("hrMinsDiff: " + hrMinsDiff);

		// check if the difference is not <= 0
		// and check if the current ot type charging exceeds the hr & mins left for this
		// ot type
		if (hrMinsDiff.compareTo(BigDecimal.ZERO) > 0) {
			currentObjectOT = this.mainApplication.minutesToHours(
					Integer.parseInt(this.validatedTextFieldHoursOfOvertime.getText()),
					Integer.parseInt(this.validatedTextFieldMinutesOfOvertime.getText()));

			System.out.println("currentObjectOT: " + currentObjectOT);

			BigDecimal currentDiff = hrMinsDiff.subtract(currentObjectOT);

			System.out.println("currentDiff: " + currentDiff);

			if (currentDiff.compareTo(BigDecimal.ZERO) < 0) {
				// this new ot type WILL exceed the ot type available hrs and mins
				return true;
			}
		} else {
			// already exceeds
			return true;
		}

		return false;
	}

	public void saveOvertimeCharging() {
		if (modificationType.equals(ModificationType.EDIT)) {
			this.objectToModify.getOvertimeList().forEach(overtime -> {
				if (overtime.getOvertimeType().getPrimaryKey()
						.equals(this.autoFillComboBoxOTtype.getValueObject().getPrimaryKey())) {
					this.setOvertime(overtime);
				}
			});
		} else {
			Overtime overtime = new Overtime();
			this.setOvertime(overtime);

			if (this.objectToModify.getOvertimeList() != null) {
				this.objectToModify.getOvertimeList().add(overtime);
			} else {
				ObservableList<Overtime> overtimeObsList = FXCollections.observableArrayList();
				overtimeObsList.add(overtime);
				this.objectToModify.setOvertimeList(overtimeObsList);
			}
		}
	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub
		this.saveOvertimeCharging();

		this.handleCancel();
	}

	public Overtime setOvertime(Overtime overtime) {
		overtime.setHoursOfOvertime(this.validatedTextFieldHoursOfOvertime.getText().compareTo("") != 0
				? Integer.valueOf(this.validatedTextFieldHoursOfOvertime.getText())
				: 0);
		overtime.setMinutesOfOvertime(this.validatedTextFieldMinutesOfOvertime.getText().compareTo("") != 0
				? Integer.valueOf(this.validatedTextFieldMinutesOfOvertime.getText())
				: 0);
		overtime.setOldOvertimeType(this.autoFillComboBoxOTtypeOld.getValueObject());
		overtime.setOvertimeType(this.autoFillComboBoxOTtype.getValueObject());
		overtime.setIsRDCoverUp(this.radioButtonRDCoverUp.isSelected());
		overtime.setIsRHCoverUp(this.radioButtonRHCoverUp.isSelected());
		overtime.setIsExtendedDuty(this.radioButtonExtendedDuty.isSelected());

		return overtime;
	}

	public void setDisabledFields(boolean isDisabled) {
		this.autoFillComboBoxOTtype.setDisable(isDisabled);
		this.autoFillComboBoxOTtypeOld.setDisable(isDisabled);
		this.radioButtonExtendedDuty.setDisable(isDisabled);
		this.radioButtonRDCoverUp.setDisable(isDisabled);
		this.radioButtonRHCoverUp.setDisable(isDisabled);
	}

	public void setOvertimeTypeComboBox() {
//		ObservableList<OvertimeType> overtimeChargingObsList = FXCollections.observableArrayList();
		ObservableList<OvertimeType> overtimeTypeBillingObsList = FXCollections.observableArrayList();
		ObservableList<OvertimeType> overtimeTypePayrollObsList = FXCollections.observableArrayList();
		ObservableList<OvertimeType> overtimeTypeFinalObsList = FXCollections.observableArrayList();

		overtimeTypePayrollObsList.setAll(this.objectToModify.getPayroll().getOvertimeList().stream()
				.map(p -> p.getOvertimeType()).collect(Collectors.toList()));

		overtimeTypeBillingObsList.setAll(this.mainApplication.getBillingClientConfiguration()
				.getConfigurationOvertimeList().stream().map(p -> p.getOvertimeType()).collect(Collectors.toList()));

		overtimeTypeFinalObsList.setAll(overtimeTypeBillingObsList);
		this.setDisabledFields(true);

		if (modificationType.equals(ModificationType.ADD)) {
			this.setDisabledFields(false);

//			if (this.objectToModify.getOvertimeList() != null) {
//				overtimeChargingObsList.setAll(this.objectToModify.getOvertimeList().stream()
//						.map(p -> p.getOvertimeType()).collect(Collectors.toList()));
//
//				for (OvertimeType overtimeType : overtimeChargingObsList) {
//					OvertimeType overtimeTypeObject = ObservableListUtil.getObject(overtimeTypeFinalObsList,
//							p -> p.getPrimaryKey().compareTo(overtimeType.getPrimaryKey()) == 0);
//
//					overtimeTypeFinalObsList.remove(overtimeTypeObject);
//				}
//			}
		}

		this.autoFillComboBoxOTtypeOld.setItems(overtimeTypePayrollObsList, p -> p.getOvertimeName());
		this.autoFillComboBoxOTtype.setItems(overtimeTypeFinalObsList, p -> p.getOvertimeName());
	}

	@Override
	public void onShowEditDialogStage() {
		// TODO Auto-generated method stub
		if (this.mainApplication.getBillingClientConfiguration() != null) {
			if (this.mainApplication.getBillingClientConfiguration().getConfigurationOvertimeList() != null
					&& !this.mainApplication.getBillingClientConfiguration().getConfigurationOvertimeList().isEmpty()) {
				this.setOvertimeTypeComboBox();
			}
		}

		this.showDetails(this.objectToModify);

	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDetails(BillingCharging billingCharging) {
		System.out.println("showDetails");

		// TODO Auto-generated method stub
		if (this.modificationType.equals(ModificationType.EDIT)) {
			System.out.println(this.selectedOvertime == null);

			this.validatedTextFieldHoursOfOvertime.setText(this.selectedOvertime.getHoursOfOvertime().toString());
			this.validatedTextFieldMinutesOfOvertime.setText(this.selectedOvertime.getMinutesOfOvertime().toString());
			this.radioButtonRDCoverUp.setSelected(
					this.selectedOvertime.getIsRDCoverUp() != null ? this.selectedOvertime.getIsRDCoverUp() : false);
			this.radioButtonRHCoverUp.setSelected(
					this.selectedOvertime.getIsRHCoverUp() != null ? this.selectedOvertime.getIsRHCoverUp() : false);
			this.radioButtonExtendedDuty.setSelected(
					this.selectedOvertime.getIsExtendedDuty() != null ? this.selectedOvertime.getIsExtendedDuty()
							: false);
			this.autoFillComboBoxOTtype.setValue(this.selectedOvertime.getOvertimeType().getOvertimeName());
			this.autoFillComboBoxOTtypeOld.setValue(this.selectedOvertime.getOldOvertimeType() != null
					? this.selectedOvertime.getOldOvertimeType().getOvertimeName()
					: "");
		} else {
			this.validatedTextFieldHoursOfOvertime.setText("0");
			this.validatedTextFieldMinutesOfOvertime.setText("0");
			this.radioButtonRDCoverUp.setSelected(false);
			this.radioButtonRHCoverUp.setSelected(false);
			this.radioButtonExtendedDuty.setSelected(false);
			this.autoFillComboBoxOTtype.setValue(null);
			this.autoFillComboBoxOTtypeOld.setValue(null);
		}
	}

	@Override
	public void onSetMainApplication() {
		// TODO Auto-generated method stub
		ToggleGroup toggleGroup = new ToggleGroup();

		this.radioButtonRDCoverUp.setToggleGroup(toggleGroup);
		this.radioButtonRHCoverUp.setToggleGroup(toggleGroup);
		this.radioButtonExtendedDuty.setToggleGroup(toggleGroup);

		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> obs, Toggle oldValue, Toggle newValue) {

				RadioButton radioButton = (RadioButton) toggleGroup.getSelectedToggle();

				if (radioButton != null) {
					String string = radioButton.getText();
					System.out.println(string + " is selected");
				}
			}
		});

	}

	public Overtime getSelectedOvertime() {
		return selectedOvertime;
	}

	public void setSelectedOvertime(Overtime selectedOvertime) {
		this.selectedOvertime = selectedOvertime;
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private ValidatedTextField validatedTextFieldHoursOfOvertime;
	@FXML
	private ValidatedTextField validatedTextFieldMinutesOfOvertime;
	@FXML
	private AutoFillComboBox<OvertimeType> autoFillComboBoxOTtype;
	@FXML
	private AutoFillComboBox<OvertimeType> autoFillComboBoxOTtypeOld;
	@FXML
	private RadioButton radioButtonRDCoverUp;
	@FXML
	private RadioButton radioButtonRHCoverUp;
	@FXML
	private RadioButton radioButtonExtendedDuty;

}
