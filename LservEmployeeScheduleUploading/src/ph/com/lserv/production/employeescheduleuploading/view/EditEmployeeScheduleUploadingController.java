package ph.com.lserv.production.employeescheduleuploading.view;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lbpsc.production.util.TableViewUtil;
import ph.com.lserv.production.employeescheduleencodingovertime.model.EmployeeScheduleEncodingOvertime;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;
import ph.com.lserv.production.remarksreference.model.RemarksReference;

public class EditEmployeeScheduleUploadingController
		extends MasterEditController<EmployeeScheduleUploading, EmployeeScheduleUploadingMain> {
	ObservableList<RemarksReference> remarksReferenceObsList = FXCollections.observableArrayList();
	Employee selectedEmployee = new Employee();
	Integer selectedDetailsIndex = 0;
	List<EmployeeScheduleUploading> bioDetailsByEmployeeList = new ArrayList<>();
	List<ValidatedTextField> timeTextFieldsList = new ArrayList<>();
	List<DatePicker> datePickerList = new ArrayList<>();
	ObservableList<OvertimeFiling> obsListOvertimeFiling = FXCollections.observableArrayList();
	ObservableList<OvertimeFilingClient> obsListOvertimeFilingClient = FXCollections.observableArrayList();
	OvertimeFilingClient selectedOvertimeFilingClient;
	boolean isSuccessSave = false;
	String timeFormat = "^(2[0-3]|[01]?[0-9]):([0-5]?[0-9]):([0-5]?[0-9])$";

	@Override
	public boolean isValid() {
		this.checkEntriesIfNoLogs();
		this.checkEntriesDateWithDateEntry();

		// validation for invalid time
		for (ValidatedTextField textField : this.timeTextFieldsList) {
			if (textField.getText().length() != 0) {
				if (!textField.getText().matches(timeFormat)) {
					AlertUtil.showInformationAlert("Invalid time entry.", this.getDialogStage());
					textField.requestFocus();
					return false;
				}
			}
		}

		// validation for lunch entry regardless if break exempted or not
		if ((this.datePickerLunchIn.getValue() != null && this.textFieldLunchIn.getText().length() == 8)
				&& (this.datePickerLunchOut.getValue() != null && this.textFieldLunchOut.getText().length() == 8)) {
			Timestamp lunchOutEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerLunchOut.getValue()),
					Time.valueOf(this.textFieldLunchOut.getText()));
			Timestamp lunchInEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerLunchIn.getValue()),
					Time.valueOf(this.textFieldLunchIn.getText()));
			if (lunchOutEntry.after(lunchInEntry)) {
				AlertUtil.showInformationAlert("Invalid Lunch in/out entry.", this.getDialogStage());
				return false;
			}
		}

		// validation for time in greater than time out
		if ((this.datePickerTimeIn.getValue() != null && this.textFieldTimeIn.getText().length() == 8)
				&& (this.datePickerTimeOut.getValue() != null && this.textFieldTimeOut.getText().length() == 8)) {
			Timestamp timeInEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeIn.getValue()),
					Time.valueOf(this.textFieldTimeIn.getText()));
			Timestamp timeOutEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeOut.getValue()),
					Time.valueOf(this.textFieldTimeOut.getText()));
			if (timeInEntry.after(timeOutEntry) || timeInEntry.equals(timeOutEntry)) {
				AlertUtil.showInformationAlert("Invalid Time in/out entry.", this.getDialogStage());
				return false;
			}
		}

		return true;
	}

	public void checkEntriesDateWithDateEntry() {
		// dateEntry and dateTimeIn & dateTimeOut not the same
		for (DatePicker datePicker : datePickerList) {
			if (datePicker.getValue() != null) {
				if (!DateFormatter.toDate(datePicker).equals(this.objectToModify.getDateEntry())) {
					this.comboBoxRemarks.setValue("ERROR");
					if (AlertUtil.showQuestionAlertBoolean(
							"Error log time detected.\nDo you want to remove error mark for this record?",
							this.getDialogStage())) {
						this.comboBoxRemarks.setValue("");
						return;
					}
				}
			}
		}
	}

	public void checkEntriesIfNoLogs() {
		if (this.objectToModify.getIsRegularSchedule() == 1) {

			if (this.objectToModify.getScheduleEncoding().getIsBreakExempted() == 1) {
				this.setNoLogNoBreakEntry();
			} else {
				this.setNoLogWithBreakEntry();
			}
		} else {
			if (this.objectToModify.getEmployeeScheduleEncodingIrregular() != null) {
				if (this.objectToModify.getEmployeeScheduleEncodingIrregular().getIsBreakExempted() == 1) {
					this.setNoLogNoBreakEntry();
				} else {
					this.setNoLogWithBreakEntry();
				}
			}
		}

	}

	public void setNoLogWithBreakEntry() {
		boolean isNoTimeIn = false;
		boolean isNoLunchOut = false;
		boolean isNoLunchIn = false;
		boolean isNoTimeOut = false;

		if (this.datePickerTimeIn.getValue() == null && this.textFieldTimeIn.getText().isEmpty()) {
			isNoTimeIn = true;
		}
		if (this.datePickerLunchOut.getValue() == null && this.textFieldLunchOut.getText().isEmpty()) {
			isNoLunchOut = true;
		}
		if (this.datePickerLunchIn.getValue() == null && this.textFieldLunchIn.getText().isEmpty()) {
			isNoLunchIn = true;
		}
		if (this.datePickerTimeOut.getValue() == null && this.textFieldTimeOut.getText().isEmpty()) {
			isNoTimeOut = true;
		}

		if (isNoTimeIn && isNoLunchOut && isNoLunchIn && isNoTimeOut) {
			if (this.comboBoxRemarks.getValue().isEmpty() || this.comboBoxRemarks.getValueObject() == null
					|| this.comboBoxRemarks.getValue().compareTo("NO LOG") == 0) {
				this.comboBoxRemarks.setValue("ABSENT");
			}
			return;
		}

		if (isNoTimeIn || isNoLunchOut || isNoLunchIn || isNoTimeOut) {
			// if (this.comboBoxRemarks.getValue().isEmpty() ||
			// this.comboBoxRemarks.getValueObject() == null
			// || this.comboBoxRemarks.getValue().compareTo("ABSENT") == 0) {
			// this.comboBoxRemarks.setValue("NO LOG");
			// }
			// return;
		} else {
			if (!isNoTimeIn || !isNoLunchOut || !isNoLunchIn || !isNoTimeOut) {
				if (this.comboBoxRemarks.getValue().compareTo("ABSENT") == 0
						|| this.comboBoxRemarks.getValue().compareTo("NO LOG") == 0) {
					this.comboBoxRemarks.setValue("");
				}
			}
			return;
		}
	}

	public void setNoLogNoBreakEntry() {
		boolean noTimeIn = false;
		boolean noTimeOut = false;
		if (datePickerTimeIn.getValue() == null || textFieldTimeIn.getText().isEmpty()) {
			noTimeIn = true;
		}

		if (datePickerTimeOut.getValue() == null || textFieldTimeOut.getText().isEmpty()) {
			noTimeOut = true;
		}

		if (noTimeIn && noTimeOut) {
			if (this.comboBoxRemarks.getValue().isEmpty() || this.comboBoxRemarks.getValueObject() == null
					|| this.comboBoxRemarks.getValue().compareTo("NO LOG") == 0) {
				this.comboBoxRemarks.setValue("ABSENT");
			}
			return;
		}

		if ((noTimeIn && !noTimeOut) || (!noTimeIn && noTimeOut)) {
			if (this.comboBoxRemarks.getValue().isEmpty() || this.comboBoxRemarks.getValueObject() == null
					|| this.comboBoxRemarks.getValue().compareTo("ABSENT") == 0) {
				this.comboBoxRemarks.setValue("NO LOG");
			}
			return;
		} else {
			if (!noTimeIn && !noTimeOut) {
				if (this.comboBoxRemarks.getValue().compareTo("ABSENT") == 0
						|| this.comboBoxRemarks.getValue().compareTo("NO LOG") == 0) {
					this.comboBoxRemarks.setValue("");
				}
			}
			return;
		}
	}

	@Override
	public void onSave() {
		if (this.saveBiometrics()) {
			if (this.saveOvertimeFilingClient()) {
				if (!this.saveOTBreakdown()) {
					AlertUtil.showRecordNotSave("Record not saved", this.getDialogStage());
				}
			}
		}
	}

	public boolean saveBiometrics() {
		Timestamp timeInEntry = null;
		Timestamp timeOutEntry = null;

		this.objectToModify.setChangedByUser(
				this.mainApplication.getUser() == null ? null : this.mainApplication.getUser().getUserName());
		this.objectToModify.setChangedOnDate(new Date());
		this.objectToModify.setChangedInComputer(this.mainApplication.getComputerName());
		this.objectToModify.setUser(this.mainApplication.getUser());

		if (this.datePickerTimeIn.getValue() != null && this.textFieldTimeIn.getText().length() == 8) {
			timeInEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeIn.getValue()),
					Time.valueOf(this.textFieldTimeIn.getText()));
		}
		this.objectToModify.setTimeInEntry(timeInEntry);

		if (this.datePickerTimeOut.getValue() != null && this.textFieldTimeOut.getText().length() == 8) {
			timeOutEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeOut.getValue()),
					Time.valueOf(this.textFieldTimeOut.getText()));
		}
		this.objectToModify.setTimeOutEntry(timeOutEntry);

		if ((this.datePickerLunchIn.getValue() != null && this.textFieldLunchIn.getText().length() == 8)
				&& (this.datePickerLunchOut.getValue() != null && this.textFieldLunchOut.getText().length() == 8)) {
			Timestamp lunchOutEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerLunchOut.getValue()),
					Time.valueOf(this.textFieldLunchOut.getText()));
			Timestamp lunchInEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerLunchIn.getValue()),
					Time.valueOf(this.textFieldLunchIn.getText()));
			this.objectToModify.setLunchInEntry(lunchInEntry);
			this.objectToModify.setLunchOutEntry(lunchOutEntry);
		}

		if (this.textFieldUndertime.getText().length() != 0) {
			this.objectToModify.setUndertime(Integer.valueOf(this.textFieldUndertime.getText()));
		}
		this.objectToModify.setRemarksReference(this.comboBoxRemarks.getValueObject());

		if (this.obsListOvertimeFilingClient != null && !this.obsListOvertimeFilingClient.isEmpty()) {
			this.objectToModify.setAllowedOvertime(this.mainApplication.getTimeFromTimestamp(
					this.obsListOvertimeFilingClient.get(this.obsListOvertimeFilingClient.size() - 1).getDateTimeTo()));
		}

		return this.mainApplication.updateMainObject(this.objectToModify);
	}

	public boolean saveOTBreakdown() {
		boolean isSuccessSave = true;
		List<EmployeeScheduleUploadingOvertimeBreakdown> createOTBreakdownList = new ArrayList<>();
		List<EmployeeScheduleUploadingOvertimeBreakdown> updateOTBreakdownList = new ArrayList<>();
		List<EmployeeScheduleUploadingOvertimeBreakdown> deleteOTBreakdownList = new ArrayList<>();

		List<EmployeeScheduleUploadingOvertimeBreakdown> existingOTBreakdownList = new ArrayList<>();
		existingOTBreakdownList = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
				.getDataByPrikeyEmployeeScheduleUploading(this.objectToModify.getPrimaryKey());

		for (EmployeeScheduleUploadingOvertimeBreakdown newOTBreakdown : this.mainApplication.getObsListOTBreakdown()) {
			boolean isExisting = existingOTBreakdownList.stream().filter(
					p -> p.getOvertimeType().getPrimaryKey().equals(newOTBreakdown.getOvertimeType().getPrimaryKey()))
					.findFirst().isPresent();

			if (isExisting) {
				EmployeeScheduleUploadingOvertimeBreakdown existingOTBreakdown = new EmployeeScheduleUploadingOvertimeBreakdown();
				existingOTBreakdown = existingOTBreakdownList.stream().filter(p -> p.getOvertimeType().getPrimaryKey()
						.equals(newOTBreakdown.getOvertimeType().getPrimaryKey())).findFirst().get();

				if (newOTBreakdown.getEmployeeScheduleUploading() == null) {
					existingOTBreakdown.setTotalMin(newOTBreakdown.getTotalMin());
					updateOTBreakdownList.add(existingOTBreakdown);
				} else {
					existingOTBreakdown.setOvertimeFiling(newOTBreakdown.getOvertimeFiling());
					existingOTBreakdown.setEarlyOvertimeFiling(newOTBreakdown.getEarlyOvertimeFiling());
					existingOTBreakdown
							.setEmployeeScheduleEncodingOvertime(newOTBreakdown.getEmployeeScheduleEncodingOvertime());
					existingOTBreakdown.setOvertimeFilingClient(newOTBreakdown.getOvertimeFilingClient());
					existingOTBreakdown.setEarlyOvertimeFilingClient(newOTBreakdown.getEarlyOvertimeFilingClient());
					existingOTBreakdown.setTotalMin(newOTBreakdown.getTotalMin());
					updateOTBreakdownList.add(existingOTBreakdown);
				}

			} else {
				if (newOTBreakdown.getEmployeeScheduleUploading() == null) {
					// added manual OT Breakdown by audit -- may not be connected to other ot filing
					// this object doesn't go thru automated process of breaking down OT
					// hence this object will only have prikey reference overtime(if it is encoded
					// to the employee) and total min value,
					// other paramaters will be NULL,
					// if so, create the object to fill the NULL values
					EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime = new EmployeeScheduleEncodingOvertime();

					if (this.objectToModify.getIsRegularSchedule() == 1) {
						employeeScheduleEncodingOvertime = this.mainApplication
								.getEmployeeScheduleEncodingOvertimeMain().getDataByEmpIdAndOvertimeTypeId(
										this.objectToModify.getEmploymentHistory().getEmployee().getEmployeeCode(),
										newOTBreakdown.getOvertimeType().getPrimaryKey());
					} else {
						employeeScheduleEncodingOvertime = this.mainApplication
								.getEmployeeScheduleEncodingOvertimeMain().getDataIrregularByPrikeyIrregularSchedule(
										this.objectToModify.getEmployeeScheduleEncodingIrregular().getPrimaryKey());

						if (employeeScheduleEncodingOvertime == null) {
							employeeScheduleEncodingOvertime = this.mainApplication
									.getEmployeeScheduleEncodingOvertimeMain().getDataByEmpIdAndOvertimeTypeId(
											this.objectToModify.getEmploymentHistory().getEmployee().getEmployeeCode(),
											newOTBreakdown.getOvertimeType().getPrimaryKey());
						}
					}

					EmployeeScheduleUploadingOvertimeBreakdown createdOTBreakdown = new EmployeeScheduleUploadingOvertimeBreakdown();

					createdOTBreakdown = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
							.createObject(this.objectToModify, employeeScheduleEncodingOvertime, null, null,
									newOTBreakdown.getTotalMin(), newOTBreakdown.getOvertimeType(), true);

					createOTBreakdownList.add(createdOTBreakdown);
				} else {
					// manual OT Breakdown from EDIT form but goes thru automated process of
					// breaking down OT
					newOTBreakdown.setEmployeeScheduleUploading(this.objectToModify);
					createOTBreakdownList.add(newOTBreakdown);
				}
			}
		}

		deleteOTBreakdownList.addAll(existingOTBreakdownList);

		existingOTBreakdownList.forEach(p -> {
			this.mainApplication.getObsListOTBreakdown().forEach(q -> {
				if (p.getOvertimeType().getOvertimeName().equals(q.getOvertimeType().getOvertimeName())) {
					deleteOTBreakdownList.remove(p);
				}
			});

			createOTBreakdownList.forEach(r -> {
				if (p.getOvertimeType().getOvertimeName().equals(r.getOvertimeType().getOvertimeName())) {
					deleteOTBreakdownList.remove(p);
				}
			});

			updateOTBreakdownList.forEach(s -> {
				if (p.getOvertimeType().getOvertimeName().equals(s.getOvertimeType().getOvertimeName())) {
					deleteOTBreakdownList.remove(p);
				}
			});
		});

		for (EmployeeScheduleUploadingOvertimeBreakdown existingOTBreakdown : updateOTBreakdownList) {
			isSuccessSave = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
					.updateMainObject(existingOTBreakdown);
		}

		for (EmployeeScheduleUploadingOvertimeBreakdown newOTBreakdown : createOTBreakdownList) {
			isSuccessSave = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
					.createMainObject(newOTBreakdown);
		}

		for (EmployeeScheduleUploadingOvertimeBreakdown removedOTBreakdown : deleteOTBreakdownList) {
			isSuccessSave = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
					.deleteMainObject(removedOTBreakdown);
		}

		return isSuccessSave;
	}

	public boolean saveOvertimeFilingClient() {
		boolean isSuccessSave = true;
		List<OvertimeFilingClient> listExistingOvertimeFilingClient = new ArrayList<>();

		listExistingOvertimeFilingClient
				.addAll(this.mainApplication.getOvertimeFilingClientMain().getDataByEmpIdAndOvertimeDateFrom(
						this.objectToModify.getEmploymentHistory().getEmployee().getEmployeeCode(),
						this.objectToModify.getDateEntry()));

		List<OvertimeFilingClient> createOTClientList = new ArrayList<>();
		List<OvertimeFilingClient> updateOTClientList = new ArrayList<>();
		List<OvertimeFilingClient> deleteOTClientList = new ArrayList<>();

		// obsList is the current list of unsaved(newly added) & existing ot filing
		// client from this edit form
		createOTClientList.addAll(this.obsListOvertimeFilingClient.stream().filter(p -> p.getPrimaryKey() == null)
				.collect(Collectors.toList()));
		updateOTClientList.addAll(this.obsListOvertimeFilingClient.stream().filter(p -> p.getPrimaryKey() != null)
				.collect(Collectors.toList()));

		deleteOTClientList.addAll(listExistingOvertimeFilingClient);

		listExistingOvertimeFilingClient.forEach(p -> {
			createOTClientList.forEach(r -> {
				if (p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(
						r.getEmploymentHistory().getEmployee().getEmployeeCode()) && p.getPrimaryKey() == null) {
					deleteOTClientList.remove(p);
				}
			});

			updateOTClientList.forEach(s -> {
				if (p.getPrimaryKey().equals(s.getPrimaryKey())) {
					deleteOTClientList.remove(p);
				}
			});
		});

		for (OvertimeFilingClient newOTFilingClient : createOTClientList) {
			isSuccessSave = this.mainApplication.getOvertimeFilingClientMain().createMainObject(newOTFilingClient);
		}

		for (OvertimeFilingClient existingOTFilingClient : updateOTClientList) {
			isSuccessSave = this.mainApplication.getOvertimeFilingClientMain().updateMainObject(existingOTFilingClient);
		}

		for (OvertimeFilingClient removedOTFilingClient : deleteOTClientList) {
			isSuccessSave = this.mainApplication.getOvertimeFilingClientMain().deleteMainObject(removedOTFilingClient);
		}

		return isSuccessSave;
	}

	@Override
	public void onShowEditDialogStage() {

		this.mainApplication.getObsListOTBreakdown().setAll(this.objectToModify.getOvertimeBreakdownList());

		EmployeeScheduleUploading employeeScheduleUploading = this.mainApplication.getDataByEmployeeIdAndDateEntry(
				this.objectToModify.getEmploymentHistory().getEmployee().getEmployeeCode(),
				this.objectToModify.getDateEntry());

		this.mainApplication
				.setIsIntegralEmployee(this.objectToModify.getClient().getClientCode().equals("LBPSC") ? true : false);

		this.setFieldFormat();
		
		RemarksReference blankRemark = new RemarksReference();
		blankRemark.setRemarks("");
		this.remarksReferenceObsList.add(blankRemark);
		this.remarksReferenceObsList.addAll(this.mainApplication.getRemarksReferenceMain().getAllData());
		this.comboBoxRemarks.setItems(this.remarksReferenceObsList, p -> p.getRemarks(), false);

		this.showDetails(employeeScheduleUploading);
	}

	public void disableLunchField(boolean isDisable) {
		this.datePickerLunchOut.setDisable(isDisable);
		this.datePickerLunchIn.setDisable(isDisable);
		this.textFieldLunchOut.setDisable(isDisable);
		this.textFieldLunchIn.setDisable(isDisable);
	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	public void setOvertimeButtons() {
		if (this.mainApplication.getIsIntegralEmployee()) {
			this.buttonAddOvertime.setDisable(true);
			this.buttonEditOvertime.setDisable(true);
			this.buttonDeleteOvertime.setDisable(true);
		} else {
			this.buttonAddOvertime.setDisable(false);
			this.buttonEditOvertime.setDisable(false);
			this.buttonDeleteOvertime.setDisable(false);
		}
	}

	public void setTimeTextFieldList() {
		this.timeTextFieldsList.add(textFieldTimeIn);
		this.timeTextFieldsList.add(textFieldTimeOut);
		this.timeTextFieldsList.add(textFieldLunchIn);
		this.timeTextFieldsList.add(textFieldLunchOut);
	}

	public void setDatePickerList() {
		this.datePickerList.add(datePickerTimeIn);
		this.datePickerList.add(datePickerTimeOut);
		this.datePickerList.add(datePickerLunchIn);
		this.datePickerList.add(datePickerLunchOut);
	}

	@Override
	public void showDetails(EmployeeScheduleUploading employeeScheduleUploading) {
		Employee employee = employeeScheduleUploading.getEmploymentHistory().getEmployee();
		String dateEntry = new SimpleDateFormat("MM/dd/yyyy").format(employeeScheduleUploading.getDateEntry());
		Timestamp timeInEntry = employeeScheduleUploading.getTimeInEntry();
		Timestamp timeOutEntry = employeeScheduleUploading.getTimeOutEntry();
		Timestamp lunchInEntry = employeeScheduleUploading.getLunchInEntry();
		Timestamp lunchOutEntry = employeeScheduleUploading.getLunchOutEntry();
		Integer employeeCode = employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode();

		String scheduleTime = "";
		String dayOff = "";
		String workDay = "";

		boolean isRegularSched = employeeScheduleUploading.getIsRegularSchedule() == 1 ? true : false;

		if (isRegularSched) {
			String[] result = this.mainApplication.getWorkDayAndDayOff(employeeCode);
			scheduleTime = result[0];
			dayOff = result[1];
			workDay = result[2];
		} else {
			if (employeeScheduleUploading.getEmployeeScheduleEncodingIrregular() != null) {
				String timeFormat = "HH:mm";
				scheduleTime = new SimpleDateFormat(timeFormat)
						.format(employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeIn()) + "-"
						+ new SimpleDateFormat(timeFormat)
								.format(employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getTimeOut());
				workDay = "Irregular Schedule: " + new SimpleDateFormat("EEEE")
						.format(employeeScheduleUploading.getEmployeeScheduleEncodingIrregular().getDateSchedule());
			}
		}

		String timeInEntryTime = timeInEntry == null ? ""
				: this.mainApplication.getTimeFromTimestamp(timeInEntry).toString();
		String timeOutEntryTime = timeOutEntry == null ? ""
				: this.mainApplication.getTimeFromTimestamp(timeOutEntry).toString();
		String lunchInEntryTime = lunchInEntry == null ? ""
				: this.mainApplication.getTimeFromTimestamp(lunchInEntry).toString();
		String lunchOutEntryTime = lunchOutEntry == null ? ""
				: this.mainApplication.getTimeFromTimestamp(lunchOutEntry).toString();
		String dayOfDate = employeeScheduleUploading.getDayOfDate();

		LocalDate timeInEntryDate = timeInEntry == null ? null : timeInEntry.toLocalDateTime().toLocalDate();
		LocalDate timeOutEntryDate = timeOutEntry == null ? null : timeOutEntry.toLocalDateTime().toLocalDate();
		LocalDate lunchInEntryDate = lunchInEntry == null ? null : lunchInEntry.toLocalDateTime().toLocalDate();
		LocalDate lunchOutEntryDate = lunchOutEntry == null ? null : lunchOutEntry.toLocalDateTime().toLocalDate();

		String undertime = employeeScheduleUploading.getUndertime() == null ? ""
				: employeeScheduleUploading.getUndertime().toString();
		String remarks = employeeScheduleUploading.getRemarksReference() != null
				? employeeScheduleUploading.getRemarksReference().getRemarks()
				: "";

		this.clearFields();
		this.textFieldDay.setText(dayOfDate);
		this.textFieldEmployeeName.setText(employee.getEmployeeFullName().toUpperCase());
		this.textFieldDepartment.setText(employeeScheduleUploading.getDepartment().getDepartmentName());
		this.textFieldEmployeeId.setText(employee.getEmployeeCode().toString());
		this.textFieldBioId.setText(employeeScheduleUploading.getBioId());

		this.textFieldDate.setText(dateEntry);
		this.datePickerTimeIn.setValue(timeInEntryDate);
		this.textFieldTimeIn.setText(timeInEntryTime);
		this.datePickerTimeOut.setValue(timeOutEntryDate);
		this.textFieldTimeOut.setText(timeOutEntryTime);
		this.datePickerLunchOut.setValue(lunchOutEntryDate);
		this.textFieldLunchOut.setText(lunchOutEntryTime);
		this.datePickerLunchIn.setValue(lunchInEntryDate);
		this.textFieldLunchIn.setText(lunchInEntryTime);
		this.textFieldUndertime.setText(undertime);
		this.textFieldSchedule.setText(workDay + " : " + scheduleTime);
		this.textFieldRestDay.setText(dayOff);

		this.comboBoxRemarks.setValue(remarks);

		this.setTableViews(employeeScheduleUploading);

		this.setOvertimeButtons();
		this.setOvertimeTypeButtons();
	}

	public void clearFields() {
		// for some reason, tf needs to be cleared
		// to insert new text if the tf has format and promp text
		this.textFieldTimeIn.clear();
		this.textFieldTimeOut.clear();
		this.textFieldLunchIn.clear();
		this.textFieldLunchOut.clear();
		this.textFieldDay.clear();
		this.datePickerLunchIn.setValue(null);
		this.datePickerLunchOut.setValue(null);
	}

	public void setFieldFormat() {
		String timeFormat = "##:##:##";
		String prompTextTime = "HH:mm:ss";

		this.textFieldTimeIn.setFormat(timeFormat);
		this.textFieldLunchIn.setFormat(timeFormat);
		this.textFieldLunchOut.setFormat(timeFormat);
		this.textFieldTimeOut.setFormat(timeFormat);
		this.textFieldUndertime.setFormat("#####");

		this.textFieldTimeIn.setPromptText(prompTextTime);
		this.textFieldLunchIn.setPromptText(prompTextTime);
		this.textFieldLunchOut.setPromptText(prompTextTime);
		this.textFieldTimeOut.setPromptText(prompTextTime);

	}

	public void setTableViewOTFiling() {
		TableViewUtil.refreshTableView(this.tableViewOvertimeFiling);
		if (this.obsListOvertimeFiling != null && !this.obsListOvertimeFiling.isEmpty()) {
			this.tableViewOvertimeFiling.setVisible(true);
			this.tableViewOvertimeFilingClient.setVisible(false);
			this.tableViewOvertimeFiling.setItems(this.obsListOvertimeFiling);
			TableColumnUtil.setColumn(this.tableColumnOTStart, p -> p.getDateTimeFrom());
			TableColumnUtil.setColumn(this.tableColumnOTEnd, p -> p.getDateTimeTo());
			TableColumnUtil.setColumn(this.tableColumnTotalHrs, p -> p.getTotalOtHours());
			this.buttonAdd.setDisable(false);
			this.buttonEdit.setDisable(false);
			this.buttonRemove.setDisable(false);
		} else {
			this.buttonAdd.setDisable(true);
			this.buttonEdit.setDisable(true);
			this.buttonRemove.setDisable(true);
		}
	}

	public void setTableViewOTFilingClient() {
		TableViewUtil.refreshTableView(this.tableViewOvertimeFilingClient);
		if (this.obsListOvertimeFilingClient != null && !this.obsListOvertimeFilingClient.isEmpty()) {
			this.tableViewOvertimeFilingClient.setVisible(true);
			this.tableViewOvertimeFiling.setVisible(false);
			this.tableViewOvertimeFilingClient.setItems(this.obsListOvertimeFilingClient);
			TableColumnUtil.setColumn(this.tableColumnOTStartClient, p -> p.getDateTimeFrom());
			TableColumnUtil.setColumn(this.tableColumnOTEndClient, p -> p.getDateTimeTo());
			TableColumnUtil.setColumn(this.tableColumnTotalHrsClient, p -> p.getTotalOtHours());
			this.buttonAdd.setDisable(false);
			this.buttonEdit.setDisable(false);
			this.buttonRemove.setDisable(false);
		} else {
			this.buttonAdd.setDisable(true);
			this.buttonEdit.setDisable(true);
			this.buttonRemove.setDisable(true);
		}
	}

	public void setTableViews(EmployeeScheduleUploading employeeScheduleUploading) {

		this.mainApplication.getObsListOTBreakdown().setAll(employeeScheduleUploading.getOvertimeBreakdownList());

		this.tableViewOTbreakdown.setItems(this.mainApplication.getObsListOTBreakdown());
		TableColumnUtil.setColumn(this.tableColumnOvertimeName, p -> p.getOvertimeType().getOvertimeName());
		TableColumnUtil.setColumn(this.tableColumnTotalMin, p -> p.getTotalMin());

		if (this.mainApplication.getIsIntegralEmployee()) {

			User user = new User();
			user = this.mainApplication.getUserMain().getUserByEmployeeId(
					employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode());
			if (user != null) {
				this.obsListOvertimeFiling.setAll(this.mainApplication.getOvertimeFilingMain()
						.getAllOvertimeByOvertimeFromAndPrikeyUser(new SimpleDateFormat("yyyy-MM-dd")
								.format(employeeScheduleUploading.getDateEntry()).concat("%"), user.getPrimaryKey()));
				Collections.sort(this.obsListOvertimeFiling,
						(a, b) -> a.getDateTimeFrom().compareTo(b.getDateTimeFrom()));

				this.setTableViewOTFiling();
			}
		} else {
			this.obsListOvertimeFilingClient
					.setAll(this.mainApplication.getOvertimeFilingClientMain().getDataByEmpIdAndOvertimeDateFrom(
							employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode(),
							employeeScheduleUploading.getDateEntry()));
			Collections.sort(this.obsListOvertimeFilingClient,
					(a, b) -> a.getDateTimeFrom().compareTo(b.getDateTimeFrom()));

			this.setTableViewOTFilingClient();
		}

	}

	public void handleAdd() {
		this.mainApplication.showSetEmployeeScheduleUploadingOvertime(this.objectToModify, ModificationType.ADD,
				this.getDialogStage());
	}

	public void handleEdit() {
		boolean isInvalid = false;
		if (this.mainApplication.getSelectedOvertimeBreakdown() != null) {
			if (this.mainApplication.getSelectedOvertimeBreakdown().getOvertimeType() != null) {
				if (this.mainApplication.showSetEmployeeScheduleUploadingOvertime(this.objectToModify,
						ModificationType.EDIT, this.getDialogStage())) {
					TableViewUtil.refreshTableView(this.tableViewOTbreakdown);
					return;
				}
			} else {
				isInvalid = true;
			}
		} else {
			isInvalid = true;
		}

		if (isInvalid) {
			AlertUtil.showInformationAlert("Select overtime breakdown to edit.", this.getDialogStage());
		}
	}

	public void handleRemove() {
		boolean isInvalid = false;
		if (this.mainApplication.getSelectedOvertimeBreakdown() != null) {
			if (this.mainApplication.getSelectedOvertimeBreakdown().getOvertimeType() != null) {
				if (AlertUtil.showQuestionAlertBoolean("Remove "
						+ this.mainApplication.getSelectedOvertimeBreakdown().getOvertimeType().getOvertimeName()
						+ " from the list?", this.getDialogStage())) {
					this.mainApplication.getObsListOTBreakdown()
							.remove(this.mainApplication.getSelectedOvertimeBreakdown());
					return;
				}
			} else {
				isInvalid = true;
			}
		} else {
			isInvalid = true;
		}

		if (isInvalid) {
			AlertUtil.showInformationAlert("Select overtime breakdown to remove.", this.getDialogStage());
		}
		this.computeFields();
		this.mainApplication.setSelectedOvertimeBreakdown(null);
	}

	public boolean isValidBioEntryWithOvertime() {

		Timestamp timeInEntry = new Timestamp(new Date().getTime());

		if ((this.datePickerTimeIn.getValue() != null && this.datePickerTimeOut.getValue() != null)
				&& (this.textFieldTimeIn.getText().matches(this.timeFormat)
						&& this.textFieldTimeOut.getText().matches(this.timeFormat))) {

			timeInEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeIn.getValue()),
					Time.valueOf(this.textFieldTimeIn.getText().substring(0, 6).concat("00")));

			if (this.obsListOvertimeFiling != null && this.obsListOvertimeFiling.size() != 0) {

				if (timeInEntry.after(obsListOvertimeFiling.get(0).getDateTimeFrom())) {
					return false;
				}
			} else if (this.obsListOvertimeFilingClient != null && this.obsListOvertimeFilingClient.size() != 0) {

				if (timeInEntry.after(obsListOvertimeFilingClient.get(0).getDateTimeFrom())) {
					return false;
				}
			}
		} else {
			return false;
		}

		return true;
	}

	public void setFieldListener() {

		this.tableViewOTbreakdown.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.getOvertimeType() != null) {
				this.mainApplication.setSelectedOvertimeBreakdown(newValue);
				this.mainApplication.setTotalMin(newValue.getTotalMin());
			}
		});

		this.tableViewOvertimeFilingClient.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> {
					if (newValue != null) {
						this.selectedOvertimeFilingClient = newValue;
					}
				});

		this.timeTextFieldsList.forEach(textField -> {
			this.setTextFieldListener(textField);
		});

		this.datePickerList.forEach(datePicker -> {
			this.setDatePickerListener(datePicker);
		});

	}

	public void setDatePickerListener(DatePicker datePicker) {
		datePicker.setOnShown(e -> {
			if (datePicker.getValue() == null) {
				datePicker.setValue(
						this.objectToModify.getDateEntry().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				setOvertimeButtons();
			}
		});

		datePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
			setOvertimeButtons();
		});

	}

	public void setTextFieldListener(ValidatedTextField textField) {
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					computeFields();
					setOvertimeButtons();
				}
			}
		});

		textField.textProperty().addListener((obs, oldValue, newValue) -> {
			setOvertimeButtons();
		});
	}

	public void computeFields() {

		this.objectToModify.setTimeInEntry(null);
		this.objectToModify.setTimeOutEntry(null);
		this.objectToModify.setLunchOutEntry(null);
		this.objectToModify.setLunchInEntry(null);

		if (this.datePickerTimeIn.getValue() != null && !this.textFieldTimeIn.getText().isEmpty()) {
			this.objectToModify.setTimeInEntry(this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeIn.getValue()),
					Time.valueOf(this.textFieldTimeIn.getText().substring(0, 6).concat("00"))));
		}

		if (this.datePickerTimeOut.getValue() != null && !this.textFieldTimeOut.getText().isEmpty()) {
			this.objectToModify.setTimeOutEntry(this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeOut.getValue()),
					Time.valueOf(this.textFieldTimeOut.getText().substring(0, 6).concat("00"))));
		}

		if (this.datePickerLunchOut.getValue() != null && !this.textFieldLunchOut.getText().isEmpty()) {
			this.objectToModify.setLunchOutEntry(this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerLunchOut.getValue()),
					Time.valueOf(this.textFieldLunchOut.getText().substring(0, 6).concat("00"))));
		}

		if (this.datePickerLunchIn.getValue() != null && !this.textFieldLunchIn.getText().isEmpty()) {
			this.objectToModify.setLunchInEntry(this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerLunchIn.getValue()),
					Time.valueOf(this.textFieldLunchIn.getText().substring(0, 6).concat("00"))));
		}

		this.mainApplication.computeUndertime(this.objectToModify);
		if (this.objectToModify.getUndertime() != null && this.objectToModify.getUndertime() != 0) {
			this.textFieldUndertime.setText(this.objectToModify.getUndertime().toString());
		} else {
			this.textFieldUndertime.clear();
		}

		if (!this.obsListOvertimeFiling.isEmpty() || !this.obsListOvertimeFilingClient.isEmpty()) {
			if (this.isValidBioEntryWithOvertime()) {
				this.computeOvertime(this.objectToModify);
			} else {
				AlertUtil.showInformationAlert(
						"Invalid Biometrics or Overtime Filing Entries.\nPlease check the data carefully.",
						this.getDialogStage());
				this.mainApplication.getObsListOTBreakdown().clear();
			}
		}

		if (this.mainApplication.getIsIntegralEmployee()) {
			this.setTableViewOTFiling();
		} else {
			this.setTableViewOTFilingClient();
		}
	}

	public void checkIfValidDateEntryAndOvertimeFiling(EmployeeScheduleUploading employeeScheduleUploading) {
		String timeInEntryTime = new SimpleDateFormat("HH:mm:00")
				.format(this.mainApplication.getTimeFromTimestamp(employeeScheduleUploading.getTimeInEntry()));

		Timestamp timeFrom = this.mainApplication.entryToTimestampConverter(
				DateFormatter.toDate(employeeScheduleUploading.getTimeInEntry().toLocalDateTime().toLocalDate()),
				Time.valueOf(timeInEntryTime));
		RemarksReference remarksError = new RemarksReference();

		if (this.obsListOvertimeFiling != null && !this.obsListOvertimeFiling.isEmpty()) {
			if (timeFrom.after(this.mainApplication.entryToTimestampConverter(employeeScheduleUploading.getDateEntry(),
					new Time(this.obsListOvertimeFiling.get(0).getDateTimeFrom().getTime())))) {
				remarksError.setPrimaryKey(3);
				employeeScheduleUploading.setRemarksReference(remarksError);
			} else {
				employeeScheduleUploading.setRemarksReference(null);
			}
		} else if (this.obsListOvertimeFilingClient != null && !this.obsListOvertimeFilingClient.isEmpty()) {
			if (timeFrom.after(this.mainApplication.entryToTimestampConverter(employeeScheduleUploading.getDateEntry(),
					new Time(this.obsListOvertimeFilingClient.get(0).getDateTimeFrom().getTime())))) {
				remarksError.setPrimaryKey(3);
				employeeScheduleUploading.setRemarksReference(remarksError);
			} else {
				employeeScheduleUploading.setRemarksReference(null);
			}
		}

		if (remarksError.getPrimaryKey() != null) {
			this.comboBoxRemarks.setValue(remarksError.getRemarks());
		} else {
			this.comboBoxRemarks.setValue("");
		}
	}

	public void computeOvertime(EmployeeScheduleUploading employeeScheduleUploading) {
		List<EmployeeScheduleUploadingOvertimeBreakdown> resultList = new ArrayList<>();

		Collections.sort(this.obsListOvertimeFiling, (a, b) -> a.getDateTimeFrom().compareTo(b.getDateTimeFrom()));
		Collections.sort(this.obsListOvertimeFilingClient,
				(a, b) -> a.getDateTimeFrom().compareTo(b.getDateTimeFrom()));

		if (this.mainApplication.getIsIntegralEmployee()) {
			resultList = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
					.breakdownOvertimeFromEdit(employeeScheduleUploading, this.obsListOvertimeFiling, null,
							this.getDialogStage(), this.mainApplication.getUser());
		} else {
			resultList = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
					.breakdownOvertimeFromEdit(employeeScheduleUploading, null, this.obsListOvertimeFilingClient,
							this.getDialogStage(), this.mainApplication.getUser());
		}

		// if there's no ot breakdown, clear ot filing / ot filing client list
		// possible cause if no breakdown, there's no ot type encoded to user
		// or invalid biometrics / ot entry
		if (resultList.isEmpty() || resultList.size() == 0) {
			this.obsListOvertimeFiling.clear();
			this.obsListOvertimeFilingClient.clear();
		}

		HashSet<EmployeeScheduleUploadingOvertimeBreakdown> resultDistinctHashSet = new HashSet<>();
		resultDistinctHashSet.addAll(resultList);
		this.mainApplication.getObsListOTBreakdown().setAll(resultDistinctHashSet);

	}

	public void setOvertimeTypeButtons() {
		if (this.isWithOvertimeFiling()) {
			this.buttonAdd.setDisable(false);
			this.buttonEdit.setDisable(false);
			this.buttonRemove.setDisable(false);
		} else {
			this.buttonAdd.setDisable(true);
			this.buttonEdit.setDisable(true);
			this.buttonRemove.setDisable(true);
		}
	}

	public boolean isWithOvertimeFiling() {

		if (this.obsListOvertimeFiling.isEmpty() && this.obsListOvertimeFilingClient.isEmpty()) {
			return false;
		}

		return true;
	}

	@Override
	public void onSetMainApplication() {
		this.setTimeTextFieldList();
		this.setDatePickerList();
		this.setFieldListener();

	}

	// public void handlePrevBio() {
	// this.mainApplication.setSelectedOvertimeBreakdown(null);
	// if (this.selectedDetailsIndex > 0) {
	// this.selectedDetailsIndex = this.selectedDetailsIndex - 1;
	//
	// this.bioDetailsByEmployeeList =
	// this.mainApplication.getDataByEmployeePayFromPayTo(
	// this.selectedEmployee.getEmployeeCode(),
	// this.mainApplication.getPayPeriodDateFrom(),
	// this.mainApplication.getPayPeriodDateTo());
	//
	// this.selectedBiometric =
	// this.bioDetailsByEmployeeList.get(this.selectedDetailsIndex);
	// this.showDetails(this.selectedBiometric);
	//
	// this.selectedOvertimeFilingClient = null;
	// this.setOvertimeTypeButtons();
	// }
	// }
	//
	// public void handleNextBio() {
	// this.mainApplication.setSelectedOvertimeBreakdown(null);
	// if (this.selectedDetailsIndex >= 0 && this.selectedDetailsIndex <
	// this.bioDetailsByEmployeeList.size() - 1) {
	// this.selectedDetailsIndex = this.selectedDetailsIndex + 1;
	//
	// this.bioDetailsByEmployeeList =
	// this.mainApplication.getDataByEmployeePayFromPayTo(
	// this.selectedEmployee.getEmployeeCode(),
	// this.mainApplication.getPayPeriodDateFrom(),
	// this.mainApplication.getPayPeriodDateTo());
	//
	// this.selectedBiometric =
	// this.bioDetailsByEmployeeList.get(this.selectedDetailsIndex);
	// this.showDetails(this.selectedBiometric);
	//
	// this.selectedOvertimeFilingClient = null;
	// this.setOvertimeTypeButtons();
	// }
	//
	// // this.showDetails(employeeScheduleUploading);
	// }
	//
	// public void handlePrevEmployee() {
	// this.mainApplication.setSelectedOvertimeBreakdown(null);
	//
	// int index = 0;
	// ObservableList<Employee> employeesObservableList =
	// FXCollections.observableArrayList();
	// employeesObservableList.setAll(this.mainApplication.populateEmployee(this.mainApplication.getSelectedClient(),
	// this.mainApplication.getSelectedDepartment()));
	//
	// ObservableListUtil.sort(employeesObservableList, p -> p.getSurname());
	//
	// for (Employee employee : employeesObservableList) {
	// if
	// (employee.getEmployeeCode().equals(this.selectedEmployee.getEmployeeCode()))
	// {
	// index = employeesObservableList.indexOf(employee);
	// }
	// }
	//
	// if (index > 0) {
	// index -= 1;
	// Employee employee = employeesObservableList.get(index);
	//
	// this.bioDetailsByEmployeeList =
	// this.mainApplication.getDataByEmployeePayFromPayTo(
	// employee.getEmployeeCode(), this.mainApplication.getPayPeriodDateFrom(),
	// this.mainApplication.getPayPeriodDateTo());
	//
	// this.selectedBiometric =
	// this.bioDetailsByEmployeeList.get(this.selectedDetailsIndex);
	//
	// this.selectedEmployee = employee;
	// this.mainApplication.setIsIntegralEmployee(employee.getClient().equals("LBPSC")
	// ? true : false);
	// this.selectedOvertimeFilingClient = null;
	//
	// this.setOvertimeTypeButtons();
	// this.showDetails(this.selectedBiometric);
	// }
	// }
	//
	// public void handleNextEmployee() {
	// this.mainApplication.setSelectedOvertimeBreakdown(null);
	//
	// int index = 0;
	//
	// ObservableList<Employee> employeesObservableList =
	// FXCollections.observableArrayList();
	// employeesObservableList.setAll(this.mainApplication.populateEmployee(this.mainApplication.getSelectedClient(),
	// this.mainApplication.getSelectedDepartment()));
	//
	// ObservableListUtil.sort(employeesObservableList, p -> p.getSurname());
	//
	// for (Employee employee : employeesObservableList) {
	// if (employee.getEmployeeCode().equals(selectedEmployee.getEmployeeCode())) {
	// index = employeesObservableList.indexOf(employee);
	// }
	// }
	//
	// if (index >= 0 && index < employeesObservableList.size() - 1) {
	// index += 1;
	// Employee employee = employeesObservableList.get(index);
	//
	// this.bioDetailsByEmployeeList =
	// this.mainApplication.getDataByEmployeePayFromPayTo(
	// employee.getEmployeeCode(), this.mainApplication.getPayPeriodDateFrom(),
	// this.mainApplication.getPayPeriodDateTo());
	//
	// this.selectedBiometric =
	// this.bioDetailsByEmployeeList.get(this.selectedDetailsIndex);
	//
	// this.selectedEmployee = employee;
	// this.mainApplication.setIsIntegralEmployee(employee.getClient().equals("LBPSC")
	// ? true : false);
	// this.selectedOvertimeFilingClient = null;
	//
	// this.setOvertimeTypeButtons();
	// this.showDetails(this.selectedBiometric);
	// }
	// }

	public void disableFieldsEdit(boolean isDisabled) {
		this.gridPaneBio.setDisable(isDisabled);
		this.borderPaneBreakdown.setDisable(isDisabled);
		this.buttonSave.setDisable(isDisabled);
	}

	public void handleEditBio() {
		this.disableFieldsEdit(true);
	}

	public void handleAddOvertime() {
		if (this.isValidBioEntryWithOvertime()) {
			Timestamp timeInEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeIn.getValue()),
					Time.valueOf(this.textFieldTimeIn.getText().substring(0, 5).concat(":00")));

			Timestamp timeOutEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeOut.getValue()),
					Time.valueOf(this.textFieldTimeOut.getText().substring(0, 5).concat(":00")));

			OvertimeFilingClient overtimeFilingClient = new OvertimeFilingClient();
			overtimeFilingClient = this.mainApplication.getOvertimeFilingClientMain().showEditOvertimeFilingClient(
					new OvertimeFilingClient(), ModificationType.ADD, this.objectToModify, this.getDialogStage(),
					timeInEntry, timeOutEntry, this.obsListOvertimeFilingClient);

			if (overtimeFilingClient != null) {
				if (overtimeFilingClient.getTotalOtHours() != null) {
					this.obsListOvertimeFilingClient.add(overtimeFilingClient);
				}
			}

			if (!this.obsListOvertimeFilingClient.isEmpty()) {
				this.tableViewOvertimeFilingClient.setVisible(true);
				this.tableViewOvertimeFiling.setVisible(false);
				this.tableViewOvertimeFilingClient.setItems(this.obsListOvertimeFilingClient);
				TableColumnUtil.setColumn(this.tableColumnOTStartClient, p -> p.getDateTimeFrom());
				TableColumnUtil.setColumn(this.tableColumnOTEndClient, p -> p.getDateTimeTo());
			}
			this.computeFields();
		}
		else {
			AlertUtil.showInformationAlert("No TIME IN or TIME OUT.", this.getDialogStage());
		}
	}

	public void handleEditOvertime() {
		if (this.selectedOvertimeFilingClient != null) {
			Timestamp timeInEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeIn.getValue()),
					Time.valueOf(this.textFieldTimeIn.getText().substring(0, 5).concat(":00")));

			Timestamp timeOutEntry = this.mainApplication.entryToTimestampConverter(
					DateFormatter.toDate(this.datePickerTimeOut.getValue()),
					Time.valueOf(this.textFieldTimeOut.getText().substring(0, 5).concat(":00")));

			OvertimeFilingClient overtimeFilingClient = new OvertimeFilingClient();
			overtimeFilingClient = this.mainApplication.getOvertimeFilingClientMain().showEditOvertimeFilingClient(
					this.selectedOvertimeFilingClient, ModificationType.EDIT, this.objectToModify,
					this.getDialogStage(), timeInEntry, timeOutEntry, this.obsListOvertimeFilingClient);

			if (overtimeFilingClient != null) {
				this.obsListOvertimeFilingClient.remove(this.selectedOvertimeFilingClient);
				this.obsListOvertimeFilingClient.add(overtimeFilingClient);
			}

			this.computeFields();
		} else {
			AlertUtil.showInformationAlert("No selected overtime to edit.", this.getDialogStage());
		}

	}

	public void handleDeleteOvertime() {
		if (this.selectedOvertimeFilingClient != null) {
			// delete existing data and current unsaved data if naremove
			if (AlertUtil.showDeleteQuestionAlertBoolean(this.getDialogStage())) {
				this.obsListOvertimeFilingClient.remove(this.selectedOvertimeFilingClient);
				this.setTableViewOTFilingClient();
				this.computeFields();
			}
		} else {
			AlertUtil.showInformationAlert("No selected overtime to delete", this.getDialogStage());
		}
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private TextField textFieldDate;
	@FXML
	private Button buttonAdd;
	@FXML
	private Button buttonEdit;
	@FXML
	private Button buttonRemove;
	@FXML
	private ValidatedTextField textFieldTimeIn;
	@FXML
	private ValidatedTextField textFieldLunchOut;
	@FXML
	private ValidatedTextField textFieldLunchIn;
	@FXML
	private ValidatedTextField textFieldTimeOut;
	@FXML
	private ValidatedTextField textFieldUndertime;
	@FXML
	private TableView<EmployeeScheduleUploadingOvertimeBreakdown> tableViewOTbreakdown;
	@FXML
	private AutoFillComboBox<RemarksReference> comboBoxRemarks;
	@FXML
	private TextField textFieldEmployeeName;
	@FXML
	private TextField textFieldDepartment;
	@FXML
	private TextField textFieldEmployeeId;
	@FXML
	private TextField textFieldBioId;
	@FXML
	private TextField textFieldSchedule;
	@FXML
	private TextField textFieldRestDay;
	@FXML
	private DatePicker datePickerTimeIn;
	@FXML
	private DatePicker datePickerTimeOut;
	@FXML
	private DatePicker datePickerLunchIn;
	@FXML
	private DatePicker datePickerLunchOut;
	@FXML
	private TableColumn<EmployeeScheduleUploadingOvertimeBreakdown, String> tableColumnOvertimeName;
	@FXML
	private TableColumn<EmployeeScheduleUploadingOvertimeBreakdown, String> tableColumnTotalMin;
	@FXML
	private BorderPane borderPaneBreakdown;
	@FXML
	private GridPane gridPaneBio;
	@FXML
	private TextField textFieldDay;
	@FXML
	private Button buttonAddOvertime;
	@FXML
	private Button buttonEditOvertime;
	@FXML
	private Button buttonDeleteOvertime;
	@FXML
	private TableView<OvertimeFiling> tableViewOvertimeFiling;
	@FXML
	private TableColumn<OvertimeFiling, String> tableColumnOTStart;
	@FXML
	private TableColumn<OvertimeFiling, String> tableColumnOTEnd;
	@FXML
	private TableColumn<OvertimeFiling, String> tableColumnTotalHrs;
	@FXML
	private TableView<OvertimeFilingClient> tableViewOvertimeFilingClient;
	@FXML
	private TableColumn<OvertimeFilingClient, String> tableColumnOTStartClient;
	@FXML
	private TableColumn<OvertimeFilingClient, String> tableColumnOTEndClient;
	@FXML
	private TableColumn<OvertimeFilingClient, String> tableColumnTotalHrsClient;
}
