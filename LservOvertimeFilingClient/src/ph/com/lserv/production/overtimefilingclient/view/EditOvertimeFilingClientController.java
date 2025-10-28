package ph.com.lserv.production.overtimefilingclient.view;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lserv.production.overtimefilingclient.OvertimeFilingClientMain;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;

public class EditOvertimeFilingClientController
		extends MasterEditController<OvertimeFilingClient, OvertimeFilingClientMain> {
	List<ValidatedTextField> textFieldList = new ArrayList<>();
	List<FormattedDatePicker> datePickerList = new ArrayList<>();

	@Override
	public boolean isValid() {
		if (!FieldValidator.textFieldValidate(textFieldList)
				| !FieldValidator.formattedDatePickerValidate(datePickerList)) {
			AlertUtil.showIncompleteDataAlert(this.getDialogStage());
			this.textFieldOTStart.requestFocus();
			return false;
		}

		if (this.mainApplication.getTimeInEntry().after(this.mainApplication.getOvertimeStart())) {
			AlertUtil.showErrorAlert("Overtime start cannot be before Biometrics Time In", this.getDialogStage());
			this.textFieldOTStart.requestFocus();
			return false;
		}

		if (this.mainApplication.getTimeOutEntry().before(this.mainApplication.getOvertimeEnd())) {
			AlertUtil.showErrorAlert("Overtime end cannot be after Biometrics Time Out", this.getDialogStage());
			this.textFieldOTEnd.requestFocus();
			return false;
		}

		if (this.textFieldTotalHrs.getText().compareTo("0.0000") == 0) {
			AlertUtil.showErrorAlert("Invalid overtime filing entries. Please check the data carefully.",
					this.getDialogStage());
			this.textFieldOTStart.requestFocus();
			return false;
		}

		// validation for duplicate and overlap ot filing
		if ((this.datePickerOTStart.getValue() != null && this.textFieldOTStart.getText().length() == 5)
				&& (this.datePickerOTEnd.getValue() != null && this.textFieldOTEnd.getText().length() == 5)) {

			// Overtime Filing for Managed Employees
			if (this.mainApplication.getOvertimeFilingClientList() != null
					&& !this.mainApplication.getOvertimeFilingClientList().isEmpty()) {

				Timestamp timeFrom = this.stringToTimestampConverter(this.datePickerOTStart, this.textFieldOTStart);
				Timestamp timeTo = this.stringToTimestampConverter(this.datePickerOTEnd, this.textFieldOTEnd);

				for (OvertimeFilingClient overtimeFilingClient : this.mainApplication.getOvertimeFilingClientList()) {

					if (this.objectToModify.equals(overtimeFilingClient)) {
						continue;
					}

					// duplicate
					if (overtimeFilingClient.getDateTimeFrom().equals(timeFrom)
							&& overtimeFilingClient.getDateTimeTo().equals(timeTo)) {
						AlertUtil.showDuplicateDataAlert(this.getDialogStage());
						return false;
					}

					// overlap
					if (this.isOvertimeOverlap(timeFrom, timeTo, overtimeFilingClient)) {
						AlertUtil.showInformationAlert("Overtime data overlaps to an existing overtime data.",
								this.getDialogStage());
						return false;
					}
				}
			}
		}

		// validation for invalid date or time entry
		return true;
	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub

//		 this.mainApplication.createOvertimeFilingClient(overtimeStart, overtimeEnd,
//		 totalHrs);
	}

	@Override
	public void onShowEditDialogStage() {
		// TODO Auto-generated method stub

		this.showDetails(this.objectToModify);

	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	public void setFieldListener() {
		LocalDate dateEntry = this.mainApplication.getSelectedBiometric().getDateEntry().toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate();

		this.datePickerOTStart.setOnShown(e -> {
			this.datePickerOTStart.setValue(dateEntry);
		});

		this.datePickerOTEnd.setOnShown(e -> {
			this.datePickerOTEnd.setValue(dateEntry);
		});
		
		this.datePickerOTStart.focusedProperty().addListener((obs, oldValue, newValue) ->{
			if (this.datePickerOTStart.getValue() == null) {
				this.datePickerOTStart.setValue(dateEntry);
			}
		});
		
		this.datePickerOTEnd.focusedProperty().addListener((obs, oldValue, newValue) ->{
			if (this.datePickerOTEnd.getValue() == null) {
				this.datePickerOTEnd.setValue(dateEntry);
			}
		});

	}

	@Override
	public void showDetails(OvertimeFilingClient overtimeFilingClient) {

		if (modificationType.equals(ModificationType.ADD)) {
			this.textFieldClient.setText(
					this.mainApplication.getSelectedBiometric().getEmploymentHistory().getClient().getClientName());
			this.textFieldEmployee.setText(this.mainApplication.getSelectedBiometric().getEmploymentHistory()
					.getEmployee().getEmployeeFullName());
			this.textFieldDepartment.setText(this.mainApplication.getSelectedBiometric().getEmploymentHistory()
					.getDepartment().getDepartmentName());
		}

		if (modificationType.equals(ModificationType.EDIT)) {
			this.textFieldClient.setText(overtimeFilingClient.getEmploymentHistory().getClient().getClientName());
			this.textFieldEmployee
					.setText(overtimeFilingClient.getEmploymentHistory().getEmployee().getEmployeeFullName());
			this.textFieldDepartment
					.setText(overtimeFilingClient.getEmploymentHistory().getDepartment().getDepartmentName());
			this.datePickerOTStart.setValue(overtimeFilingClient.getDateTimeFrom().toLocalDateTime().toLocalDate());
			this.textFieldOTStart
					.setText(overtimeFilingClient.getDateTimeFrom().toLocalDateTime().toLocalTime().toString());
			this.datePickerOTEnd.setValue(overtimeFilingClient.getDateTimeTo().toLocalDateTime().toLocalDate());
			this.textFieldOTEnd
					.setText(overtimeFilingClient.getDateTimeTo().toLocalDateTime().toLocalTime().toString());
			this.textFieldTotalHrs.setText(overtimeFilingClient.getTotalOtHours().toString());
		}

	}

	public void handleComputeTotalHrs() {

		if ((this.datePickerOTStart.getValue() != null && this.datePickerOTEnd.getValue() != null)
				&& (!this.textFieldOTStart.getText().isEmpty() && !this.textFieldOTEnd.getText().isEmpty())) {

			Timestamp overtimeStart = this.mainApplication.stringToTimestampConverter(this.datePickerOTStart,
					this.textFieldOTStart);
			Timestamp overtimeEnd = this.mainApplication.stringToTimestampConverter(this.datePickerOTEnd,
					this.textFieldOTEnd);

			BigDecimal totalHrs = this.mainApplication.getOvertimeFilingMain().getTotalNumberOfHours(overtimeStart,
					overtimeEnd);

			this.textFieldTotalHrs.setText(String.valueOf(totalHrs));
			this.mainApplication.setTotalHrs(totalHrs);
			this.mainApplication.setOvertimeStart(overtimeStart);
			this.mainApplication.setOvertimeEnd(overtimeEnd);
		}
	}

	@Override
	public void onSetMainApplication() {
		this.setFieldListener();

		this.datePickerList.add(datePickerOTStart);
		this.datePickerList.add(datePickerOTEnd);
		this.textFieldList.add(textFieldOTStart);
		this.textFieldList.add(textFieldOTEnd);

		this.textFieldList.forEach(textField -> {
			this.timeFormatTextField(textField);
		});

	}

	public void timeFormatTextField(ValidatedTextField textField) {
		this.mainApplication.setTextFieldTimeFormat(textField);

		textField.setOnMouseMoved(event -> {
			this.mainApplication.timeConfig(textField);
		});

		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
				this.mainApplication.timeConfig(textField);
				if (textField == this.textFieldOTStart) {
					this.datePickerOTEnd.requestFocus();
					this.handleComputeTotalHrs();
				}
				if (textField == this.textFieldOTEnd) {
					this.textFieldTotalHrs.requestFocus();
					this.handleComputeTotalHrs();
				}
			}
		});
	}

	public boolean isOvertimeOverlap(Timestamp timeFrom, Timestamp timeTo, OvertimeFilingClient overtimeFilingClient) {
		// if (modificationType.equals(ModificationType.ADD)) {
		if (timeFrom.after(overtimeFilingClient.getDateTimeTo())
				&& timeTo.after(overtimeFilingClient.getDateTimeTo())) {
			return false;
		}

		if (timeFrom.before(overtimeFilingClient.getDateTimeFrom())
				&& timeTo.before(overtimeFilingClient.getDateTimeFrom())) {
			return false;
		}

		if (timeFrom.before(overtimeFilingClient.getDateTimeTo())
				&& (timeTo.before(overtimeFilingClient.getDateTimeTo())
						|| timeTo.equals(overtimeFilingClient.getDateTimeTo())
						|| timeTo.after(overtimeFilingClient.getDateTimeTo()))
				&& (timeFrom.before(overtimeFilingClient.getDateTimeFrom())
						|| timeFrom.equals(overtimeFilingClient.getDateTimeFrom())
						|| timeFrom.after(overtimeFilingClient.getDateTimeFrom()))) {
			return true;
		}
		// }

		return false;
	}

	public Timestamp stringToTimestampConverter(DatePicker datePicker, ValidatedTextField textField) {
		String StringInput = datePicker.getValue().toString().concat(" ".concat(textField.getText()));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime localDateTimeInput = LocalDateTime.from(formatter.parse(StringInput));
		return Timestamp.valueOf(localDateTimeInput);
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private TextField textFieldEmployee;
	@FXML
	private TextField textFieldClient;
	@FXML
	private TextField textFieldDepartment;
	@FXML
	private FormattedDatePicker datePickerOTStart;
	@FXML
	private ValidatedTextField textFieldOTStart;
	@FXML
	private FormattedDatePicker datePickerOTEnd;
	@FXML
	private ValidatedTextField textFieldOTEnd;
	@FXML
	private TextField textFieldTotalHrs;

}
