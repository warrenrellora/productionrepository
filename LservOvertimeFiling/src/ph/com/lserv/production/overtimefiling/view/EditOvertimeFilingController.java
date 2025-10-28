package ph.com.lserv.production.overtimefiling.view;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lserv.production.overtimefiling.OvertimeFilingMain;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefilingtrail.model.OvertimeFilingTrail;

public class EditOvertimeFilingController extends MasterEditController<OvertimeFiling, OvertimeFilingMain> {
	List<AutoFillComboBox<?>> autoFillComboBoxList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldList = new ArrayList<>();
	List<DatePicker> datePickerList = new ArrayList<>();

	@Override
	public boolean isValid() {
		if (!FieldValidator.textFieldValidate(this.validatedTextFieldList)
				| !FieldValidator.datePickerValidate(this.datePickerList)) {
			AlertUtil.showIncompleteDataAlert(this.mainApplication.getPrimaryStage());
			return false;
		}

		// validation for invalid time and date input from filing
		Integer intFrom = this.stringToIntTimeConverter(textFieldTimeFrom);
		Integer intTo = this.stringToIntTimeConverter(textFieldTimeTo);

		if (this.textFieldTimeFrom.getText().length() < 5) {
			AlertUtil.showErrorAlert("Invalid Time Range: Overtime From", this.getDialogStage());
			this.textFieldTimeFrom.requestFocus();
			return false;
		}

		if (this.textFieldTimeTo.getText().length() < 5) {
			AlertUtil.showErrorAlert("Invalid Time Range: Overtime To", this.getDialogStage());
			this.textFieldTimeTo.requestFocus();
			return false;
		}

		if (this.datePickerDateFrom.getValue().equals(this.datePickerDateTo.getValue()) && intFrom > intTo) {
			AlertUtil.showErrorAlert("Invalid Date & Time range.", this.getDialogStage());
			this.textFieldTimeFrom.requestFocus();
			return false;
		}

		Period period = Period.between(this.datePickerDateFrom.getValue(), this.datePickerDateTo.getValue());

		if (period.getDays() < 0) {
			AlertUtil.showErrorAlert("Invalid Date & Time range.", this.getDialogStage());
			this.datePickerDateFrom.requestFocus();
			return false;
		}

		if (this.textFieldTimeFrom.getText().equals(this.textFieldTimeTo.getText()) && period.getDays() == 0
				&& period.getYears() == 0) {
			AlertUtil.showErrorAlert("Invalid Date & Time range.", this.getDialogStage());
			this.datePickerDateTo.requestFocus();
			return false;
		}

		// validation for duplication between current input & approved data from filing
		Timestamp TimeFrom = this.stringToTimestampConverter(datePickerDateFrom, textFieldTimeFrom);
		Timestamp TimeTo = this.stringToTimestampConverter(datePickerDateTo, textFieldTimeTo);

		List<OvertimeFiling> overtimeFilingList = new ArrayList<>();
		Integer overtimeFilingUserPrikey = this.mainApplication.getUser().getPrimaryKey();
		
		overtimeFilingList = this.mainApplication.getUserData(overtimeFilingUserPrikey);

		for (OvertimeFiling overtimeFiling : overtimeFilingList) {

			boolean isDuplicateDateTimeFromTo = overtimeFiling.getDateTimeFrom().equals(TimeFrom)
					&& overtimeFiling.getDateTimeTo().equals(TimeTo);
			// new
			// SimpleDateFormat("HH:mm").format(DateFormatter.toDate(datePickerDateFrom.getValue()));

			if (isDuplicateDateTimeFromTo && overtimeFiling.getWorkDone().equals(this.textFieldWorkDone.getText())
					&& modificationType == ModificationType.EDIT) {
				AlertUtil.showNoChangesAlert(this.getDialogStage());
				return false;
			}

			if (isDuplicateDateTimeFromTo
					&& (modificationType == ModificationType.EDIT || modificationType == ModificationType.ADD)) {
				AlertUtil.showDuplicateDataAlert(this.getDialogStage());
				return false;
			}

			if (this.isOvertimeOverlap(TimeFrom, TimeTo, overtimeFiling, null)) {
				AlertUtil.showInformationAlert("Overtime overlaps existing overtime with the same date.",
						this.getDialogStage());
				return false;
			}
			
		}

		// validation for duplicates between current input and pending data from trail
		List<OvertimeFilingTrail> overtimeFilingTrailList = this.mainApplication.getOvertimeFilingTrailMain()
				.getDataByUserPrikey(overtimeFilingUserPrikey);

		for (OvertimeFilingTrail overtimeFilingTrail : overtimeFilingTrailList) {
			boolean isDuplicateDateTimeFromTo = overtimeFilingTrail.getDateTimeFrom().equals(TimeFrom)
					&& overtimeFilingTrail.getDateTimeTo().equals(TimeTo);
			// pending
			if (overtimeFilingTrail.getRecordStatus().getRecordStatusCode().equals(0)) {
				if (isDuplicateDateTimeFromTo) {
					AlertUtil.showInformationAlert("Similar Data is already pending from Trail", this.getDialogStage());
					return false;
				}
				
				if (this.isOvertimeOverlap(TimeFrom, TimeTo, null, overtimeFilingTrail)) {
					AlertUtil.showInformationAlert("Overtime overlaps another pending overtime with the same date.",
							this.getDialogStage());
					return false;
				}
			}
		}

		// check if current user of system is senior associate thus the parameter is set
		// to null -- based from approval bypass
		if (this.mainApplication.isSeniorAssociateUser(null)) {
			BigDecimal[] results = this.mainApplication.getExceededOtHours(TimeFrom, TimeTo, null);
			if (!this.mainApplication.isWithRemainingOtHours(TimeFrom, TimeTo, null)) {
				if (!AlertUtil.showQuestionAlertBoolean(
						"Overtime Limit reached for this month. Do you still want to save? \nExceeded Time: "
								+ results[0] + " hour/s " + results[1] + " min/s",
						this.dialogStage)) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean isOvertimeOverlap(Timestamp TimeFrom, Timestamp TimeTo, OvertimeFiling overtimeFiling,
			OvertimeFilingTrail overtimeFilingTrail) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Calendar timeFrom = Calendar.getInstance();
		Calendar timeTo = Calendar.getInstance();
		Calendar timeFromReference = Calendar.getInstance();
		Calendar timeToReference = Calendar.getInstance();
		String dateFromReferenceString = "";
		String dateToReferenceString = "";
		
		if (overtimeFiling != null) {
			dateFromReferenceString = dateFormat
					.format(DateUtil.getDate(overtimeFiling.getDateTimeFrom().toLocalDateTime()));
			dateToReferenceString = dateFormat
					.format(DateUtil.getDate(overtimeFiling.getDateTimeTo().toLocalDateTime()));
		}
		else {
			dateFromReferenceString = dateFormat
					.format(DateUtil.getDate(overtimeFilingTrail.getDateTimeFrom().toLocalDateTime()));
			dateToReferenceString = dateFormat
					.format(DateUtil.getDate(overtimeFilingTrail.getDateTimeTo().toLocalDateTime()));
		}

		String dateFromString = dateFormat
				.format(Date.from(this.datePickerDateFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		String dateToString = dateFormat
				.format(Date.from(this.datePickerDateTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		
		if (dateFromReferenceString == "" && dateToReferenceString == "") {
			return true;
		}
		
		if (dateFromString.equals(dateFromReferenceString) && dateToString.equals(dateToReferenceString)) {

			timeFrom.setTime(TimeFrom);
			timeTo.setTime(TimeTo);
			
			if (overtimeFiling != null) {
				timeFromReference.setTime(overtimeFiling.getDateTimeFrom());
				timeToReference.setTime(overtimeFiling.getDateTimeTo());
			}
			else {
				timeFromReference.setTime(overtimeFilingTrail.getDateTimeFrom());
				timeToReference.setTime(overtimeFilingTrail.getDateTimeTo());
			}

			if (timeFrom.getTime().after(timeToReference.getTime())
					&& timeTo.getTime().after(timeToReference.getTime())) {
				return false;
			}

			if (timeFrom.getTime().before(timeFromReference.getTime())
					&& timeTo.getTime().before(timeFromReference.getTime())) {
				return false;
			}

			if (timeFrom.getTime().before(timeToReference.getTime())
//					|| timeFrom.getTime().equals(timeToReference.getTime()))
					&& (timeTo.getTime().before(timeToReference.getTime())
							|| timeTo.getTime().equals(timeToReference.getTime())
							|| timeTo.getTime().after(timeToReference.getTime()))
					&& ((timeFrom.getTime().before(timeFromReference.getTime()))
							|| timeFrom.getTime().equals(timeFromReference.getTime())
							|| timeFrom.getTime().after(timeFromReference.getTime()))) {
				return true;
			}
		}
		
		return false;
		
	}

	@Override
	public void onSave() {

		Timestamp TimeFrom = this.stringToTimestampConverter(datePickerDateFrom, textFieldTimeFrom);
		Timestamp TimeTo = this.stringToTimestampConverter(datePickerDateTo, textFieldTimeTo);

		BigDecimal resultHour = this.mainApplication.getTotalNumberOfHours(TimeFrom, TimeTo);

		this.objectToModify.setDateTimeFrom(TimeFrom);
		this.objectToModify.setDateTimeTo(TimeTo);
		this.objectToModify.setDateFiled(new Date());
		this.objectToModify.setWorkDone(this.textFieldWorkDone.getText());

		this.objectToModify.setChangedByUser(
				this.mainApplication.getUser() == null ? null : this.mainApplication.getUser().getUserName());
		this.objectToModify.setChangedOnDate(new Date());
		this.objectToModify.setChangedInComputer(this.mainApplication.getComputerName());
		this.objectToModify.setUser(this.mainApplication.getUser());
		this.objectToModify.setTotalOtHours(resultHour);

	}

	public Integer stringToIntTimeConverter(ValidatedTextField textField) {
		String getTimeString = textField.getText();
		String[] splitTimeString = getTimeString.split(":");
		return Integer.valueOf(splitTimeString[0] + splitTimeString[1]);
	}

	public Timestamp stringToTimestampConverter(DatePicker datePicker, ValidatedTextField textField) {
		String StringInput = datePicker.getValue().toString().concat(" ".concat(textField.getText()));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime localDateTimeInput = LocalDateTime.from(formatter.parse(StringInput));
		return Timestamp.valueOf(localDateTimeInput);
	}

	@Override
	public void onShowEditDialogStage() {
		this.showDetails(this.objectToModify);
	}

	@Override
	public void configureAccess() {

	}

	@Override
	public void showDetails(OvertimeFiling overtimeFiling) {

		// used on both main table and trail object
		// check if there's object to modify and data from main table or trail
		if (overtimeFiling != null && overtimeFiling.getTotalOtHours() != null) {
			String timePattern = "HH:mm";
			DateFormat timeFormat = new SimpleDateFormat(timePattern);

			this.textFieldWorkDone.setText(overtimeFiling.getWorkDone());
			this.textFieldTimeFrom.setText(timeFormat.format(overtimeFiling.getDateTimeFrom().getTime()));
			this.textFieldTimeTo.setText(timeFormat.format(overtimeFiling.getDateTimeTo().getTime()));
			this.datePickerDateFrom.setValue(overtimeFiling.getDateTimeFrom().toLocalDateTime().toLocalDate());
			this.datePickerDateTo.setValue(overtimeFiling.getDateTimeTo().toLocalDateTime().toLocalDate());
			this.showTotalHrLabel.setText(" " + overtimeFiling.getTotalOtHours().toString());
		} else {
			this.textFieldTimeFrom.setText("");
			this.textFieldTimeTo.setText("");
			this.textFieldWorkDone.setText("");
			this.datePickerDateFrom.setValue(null);
			this.datePickerDateTo.setValue(null);
			this.showTotalHrLabel.setText("");
		}
	}

	@Override
	public void onSetMainApplication() {
		this.validatedTextFieldList.add(textFieldWorkDone);
		this.validatedTextFieldList.add(textFieldTimeFrom);
		this.validatedTextFieldList.add(textFieldTimeTo);
		this.datePickerList.add(datePickerDateFrom);
		this.datePickerList.add(datePickerDateTo);
		this.onKeyPressed();

		try {
			this.timeFormatTextField(textFieldTimeFrom);
			this.timeFormatTextField(textFieldTimeTo);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public void showTotalHours() {
		BigDecimal result = BigDecimal.ZERO;
		String rangeFormat = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

		if (this.datePickerDateFrom.getValue() != null && this.datePickerDateTo.getValue() != null
				&& this.textFieldTimeFrom.getText() != null && this.textFieldTimeTo.getText() != null
				&& !this.textFieldTimeFrom.getText().isEmpty() && !this.textFieldTimeTo.getText().isEmpty()
				&& this.textFieldTimeFrom.getText().matches(rangeFormat)
				&& this.textFieldTimeTo.getText().matches(rangeFormat)) {

			Timestamp TimeFrom = this.stringToTimestampConverter(datePickerDateFrom, textFieldTimeFrom);
			Timestamp TimeTo = this.stringToTimestampConverter(datePickerDateTo, textFieldTimeTo);

			result = this.mainApplication.getTotalNumberOfHours(TimeFrom, TimeTo);

			if (result.compareTo(BigDecimal.ZERO) < 0) {
				this.showTotalHrLabel.setText(" Invalid Date and Time Range.");
				return;
			}
			this.showTotalHrLabel.setText(" " + String.valueOf(result));
		} else {
			this.showTotalHrLabel.setText("");
		}

	}

	public void setTextFieldTimeFormat(ValidatedTextField textField) throws ParseException {
		textField.setFormat("##:##");
		textField.setPromptText("HH:mm");
	}

	public void timeFormatTextField(ValidatedTextField textField) throws ParseException {
		this.setTextFieldTimeFormat(textField);

		textField.setOnMouseMoved(event -> {
			this.timeConfig(textField);
		});

		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
				this.timeConfig(textField);
				if (textField == this.textFieldTimeFrom) {
					this.datePickerDateTo.requestFocus();
					this.showTotalHours();
				}
				if (textField == this.textFieldTimeTo) {
					this.textFieldWorkDone.requestFocus();
					this.showTotalHours();
				}
			}
		});

	}

	public void timeConfig(ValidatedTextField textField) {
		String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
		String input = textField.getText();

		if (!input.isEmpty() && input.length() > 3) {
			Integer result = this.stringToIntTimeConverter(textField);
			Integer hour = result / 100;
			Integer min = result % 100;
			String hourString = String.valueOf(hour);
			String minString = String.valueOf(min);

			if (hour <= 23 && min <= 59) { // 24 hour validation
				String fix = "0".concat(hourString).concat(":".concat(minString));

				if (hour <= 9 && min <= 9) { // 3 input auto format
					String fix2 = "0".concat(hourString).concat(":0".concat(minString));
					textField.replaceSelection("");
					textField.setText(fix2);
					return;

				}
				if (!textField.getSelectedText().matches(regex)) { // 24 hour auto format
					textField.replaceSelection("");
					textField.setText(fix);
					return;
				}

			}
		}
		if (!textField.getSelectedText().matches(regex)) {
			textField.clear();
		}
	}

	public void onKeyPressed() {
		this.datePickerDateFrom.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.textFieldTimeFrom.requestFocus();
			}
		});

		this.datePickerDateTo.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.textFieldTimeTo.requestFocus();
			}
		});

		this.textFieldWorkDone.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.buttonSave.requestFocus();
			}
		});

		this.buttonSave.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.handleSave();
			}
			if (event.getCode() == KeyCode.ADD) {
				this.buttonCancel.requestFocus();
			}
		});

		this.buttonCancel.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.handleCancel();
			}
		});
	}

	@FXML
	public void textFieldOnKeyTyped() throws ParseException {
		this.timeFormatTextField(textFieldTimeFrom);
		this.timeFormatTextField(textFieldTimeTo);
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private ValidatedTextField textFieldWorkDone;
	@FXML
	private ValidatedTextField textFieldTimeFrom;
	@FXML
	private ValidatedTextField textFieldTimeTo;
	@FXML
	private FormattedDatePicker datePickerDateFrom;
	@FXML
	private FormattedDatePicker datePickerDateTo;
	@FXML
	private Label showTotalHrLabel;
}
