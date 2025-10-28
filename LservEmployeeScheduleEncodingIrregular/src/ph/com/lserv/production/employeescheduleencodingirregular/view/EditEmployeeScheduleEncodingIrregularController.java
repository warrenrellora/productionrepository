package ph.com.lserv.production.employeescheduleencodingirregular.view;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ProgressUtil;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lbpsc.production.util.TableViewUtil;
import ph.com.lserv.production.employeescheduleencodingirregular.EmployeeScheduleEncodingIrregularMain;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;

public class EditEmployeeScheduleEncodingIrregularController
		extends MasterEditController<EmployeeScheduleEncodingIrregular, EmployeeScheduleEncodingIrregularMain> {
	List<ValidatedTextField> validatedTextFieldTimeFormattedList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldList = new ArrayList<>();
	List<AutoFillComboBox<?>> autoFillComboBoxList = new ArrayList<>();

	List<EmployeeScheduleEncodingIrregular> copyCreatedObjectToBeSavedList = new ArrayList<>();
	List<EmployeeScheduleEncodingIrregular> objectListToBeAddedList = new ArrayList<>();
	List<EmployeeScheduleEncodingIrregular> objectListToBeUpdatedList = new ArrayList<>();
	List<EmployeeScheduleEncodingIrregular> allIrregularSchedByEmployeeList = new ArrayList<>();

	Boolean isBreakExempted = false;
	String lunchInOldValue = "";
	String lunchOutOldValue = "";
	String selectedMonth = "";
	String selectedYear = "";

	@Override
	public boolean isValid() {
		if (!FieldValidator.textFieldValidate(this.validatedTextFieldTimeFormattedList)
				|| !FieldValidator.textFieldValidate(this.textFieldOffset)
				|| !FieldValidator.autoFillComboBoxValidate(this.autoFillComboBoxList)) {
			AlertUtil.showIncompleteDataAlert(this.getDialogStage());
			return false;
		}

		if (modificationType.equals(ModificationType.ADD)) {
			this.mainApplication.getSelectedDatesList().clear();
			System.out
					.println("getSelectedDatesList size before:" + this.mainApplication.getSelectedDatesList().size());

			this.mainApplication.getObservableListDates().forEach(c -> {
				if (c.getValue().equals(true)) {
					this.mainApplication.getSelectedDatesList().add(c.getKey());

				}
			});

			if (this.mainApplication.getSelectedDatesList().size() == 0) {
				AlertUtil.showInformationAlert("Select date/s for irregular schedule.", this.getDialogStage());
				return false;
			}
		}

		for (ValidatedTextField textField : this.validatedTextFieldTimeFormattedList) {
			if (!textField.getText().isEmpty()) {
				String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
				this.mainApplication.timeConfig(textField);

				if (!textField.getText().matches(regex)) {
					textField.requestFocus();
					AlertUtil.showErrorAlert("Invalid Time Range.", this.getDialogStage());
					return false;
				}
			}
		}

		this.mainApplication.getScheduleEncodingMain().setPrimaryStage(this.getDialogStage());
		if (this.isInvalidRangeForEachTextField()) {
			return false;
		}

		if (this.modificationType.equals(ModificationType.EDIT)) {
			if (!this.isWithDataChanges()) {
				AlertUtil.showNoChangesAlert(this.getDialogStage());
				// this.mainApplication.getSelectedDatesList().clear();
				return false;
			}
		}

		return true;
	}

	public boolean isWithDataChanges() {
		boolean withChanges = true;

		EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = this.mainApplication
				.getEmployeeIrregularScheduleByPrikey(this.objectToModify.getPrimaryKey());

		if (!this.isBreakExempted) {
			if (this.textFieldTimeIn.getText().concat(":00")
					.compareTo(employeeScheduleEncodingIrregular.getTimeIn().toString()) == 0
					&& this.textFieldTimeOut.getText().concat(":00")
							.compareTo(employeeScheduleEncodingIrregular.getTimeOut().toString()) == 0
					&& this.textFieldLunchIn.getText().concat(":00")
							.compareTo(employeeScheduleEncodingIrregular.getLunchIn() == null ? ""
									: employeeScheduleEncodingIrregular.getLunchIn().toString()) == 0
					&& this.textFieldLunchOut.getText().concat(":00")
							.compareTo(employeeScheduleEncodingIrregular.getLunchOut() == null ? ""
									: employeeScheduleEncodingIrregular.getLunchOut().toString()) == 0
					&& this.textFieldOffset.getText()
							.compareTo(employeeScheduleEncodingIrregular.getOffsetAllowed().toString()) == 0
					&& this.textFieldTotalMin.getText()
							.compareTo(employeeScheduleEncodingIrregular.getTotalMinPerDay().toString()) == 0) {
				return withChanges = false;
			}
		} else {
			if (this.textFieldTimeIn.getText().concat(":00")
					.compareTo(employeeScheduleEncodingIrregular.getTimeIn().toString()) == 0
					&& this.textFieldTimeOut.getText().concat(":00")
							.compareTo(employeeScheduleEncodingIrregular.getTimeOut().toString()) == 0
					&& this.textFieldOffset.getText()
							.compareTo(employeeScheduleEncodingIrregular.getOffsetAllowed().toString()) == 0
					&& this.textFieldTotalMin.getText()
							.compareTo(employeeScheduleEncodingIrregular.getTotalMinPerDay().toString()) == 0) {
				return withChanges = false;
			}
		}

		return withChanges;
	}

	public boolean isInvalidRangeForEachTextField() {
		boolean isInvalidRangeResult = false;

		Integer[] result = this.mainApplication.getScheduleEncodingMain()
				.formatStringToIntTime(validatedTextFieldTimeFormattedList, this.isBreakExempted);
		boolean isInvalidRange = this.mainApplication.getScheduleEncodingMain().checkInvalidRangeField(result,
				this.validatedTextFieldTimeFormattedList, this.isBreakExempted);

		if (isInvalidRange) {
			isInvalidRangeResult = isInvalidRange;
		}

		return isInvalidRangeResult;
	}

	public void onSaveObjectToBeAdded(String offsetAllowed, String totalMinPerDay, String timeIn, String timeOut,
			String lunchIn, String lunchOut, Employee employee, String dateSelected, Boolean isBreakExempted) {
		this.mainApplication.getCreatedObjectsToBeSavedList()
				.add(this.mainApplication.createObjectToModify(dateSelected, offsetAllowed, totalMinPerDay, timeIn,
						timeOut, lunchIn, lunchOut, employee, isBreakExempted));
	}

	public void onSaveObjectToBeUpdated(String dateSelected, Employee employee) {
		EmploymentHistory employmentHistory = this.mainApplication.getEmploymentHistoryMain()
				.getEmploymentHistoryMaxByEmployeeCode(employee.getEmployeeCode());

		this.objectToModify.setIsBreakExempted(this.checkBoxBreakExempted.isSelected() ? 1 : 0);
		this.objectToModify.setEmploymentHistory(employmentHistory);

		this.objectToModify.setOffsetAllowed(Integer.valueOf(this.textFieldOffset.getText()));
		this.objectToModify.setTotalMinPerDay(Integer.valueOf(this.textFieldTotalMin.getText()));

		this.objectToModify.setChangedByUser(
				this.mainApplication.getUser() == null ? null : this.mainApplication.getUser().getUserName());
		this.objectToModify.setChangedOnDate(new Date());
		this.objectToModify.setChangedInComputer(this.mainApplication.getComputerName());
		this.objectToModify.setUser(this.mainApplication.getUser());

		DateFormat formatter = new SimpleDateFormat("HH:mm");
		try {
			LocalDate localDateSchedule = DateFormatter
					.toLocalDate(new SimpleDateFormat("MM-dd-yyyy").parse(dateSelected));
			Date dateSchedule = DateFormatter.toDate(localDateSchedule);
			this.objectToModify.setDateSchedule(dateSchedule);
			this.objectToModify.setTimeIn(new Time(formatter.parse(this.textFieldTimeIn.getText()).getTime()));
			this.objectToModify.setTimeOut(new Time(formatter.parse(this.textFieldTimeOut.getText()).getTime()));

			if (!this.isBreakExempted) {
				this.objectToModify.setLunchIn(new Time(formatter.parse(this.textFieldLunchIn.getText()).getTime()));
				this.objectToModify.setLunchOut(new Time(formatter.parse(this.textFieldLunchOut.getText()).getTime()));
				return;
			}
			this.objectToModify.setLunchIn(null);
			this.objectToModify.setLunchOut(null);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onSave() {
		this.saveIrregularSchedule();
	}

	public void saveIrregularSchedule() {
		Task<Void> taskSaveIrregularSchedule = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					String offsetAllowed = textFieldOffset.getText();
					String totalMinPerDay = textFieldTotalMin.getText();
					String timeIn = textFieldTimeIn.getText();
					String timeOut = textFieldTimeOut.getText();
					String lunchIn = textFieldLunchIn.getText();
					String lunchOut = textFieldLunchOut.getText();
					isBreakExempted = checkBoxBreakExempted.isSelected();

					if (isBreakExempted) {
						lunchIn = null;
						lunchOut = null;
					}

					int counter = 0;

					for (Map.Entry<Employee, Boolean> employee : mainApplication.getSelectedEmployeesObservableList()) {

						for (String dateSelected : mainApplication.getSelectedDatesList()) {
							onSaveObjectToBeAdded(offsetAllowed, totalMinPerDay, timeIn, timeOut, lunchIn, lunchOut,
									employee.getKey(), dateSelected, isBreakExempted);
							onSaveObjectToBeUpdated(dateSelected, employee.getKey());
						}

						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter,
								mainApplication.getSelectedEmployeesObservableList().size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("Please wait.\nReading", counter,
								mainApplication.getSelectedEmployeesObservableList().size()));
					}

					counter = 0;
					for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular : mainApplication
							.getCreatedObjectsToBeSavedList()) {
						if (employeeScheduleEncodingIrregular == null) {
							counter += 1;
							continue;
						}

						if (employeeScheduleEncodingIrregular.getEmploymentHistory() == null
								|| employeeScheduleEncodingIrregular.getEmploymentHistory().getEmployee() == null) {
							continue;
						}

						modifyDetailsIrregular(employeeScheduleEncodingIrregular);
						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter,
								mainApplication.getCreatedObjectsToBeSavedList().size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("Please wait.\nModifying", counter,
								mainApplication.getCreatedObjectsToBeSavedList().size()));
					}

					counter = 0;
					for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular : objectListToBeAddedList) {
						if (employeeScheduleEncodingIrregular.getEmploymentHistory() == null
								|| employeeScheduleEncodingIrregular.getEmploymentHistory().getEmployee() == null) {
							continue;
						}

						mainApplication.createMainObject(employeeScheduleEncodingIrregular);
						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter, objectListToBeAddedList.size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("New schedule.\nSaving", counter,
								objectListToBeAddedList.size()));
					}

					counter = 0;
					for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular : objectListToBeUpdatedList) {
						if (employeeScheduleEncodingIrregular.getEmploymentHistory() == null
								|| employeeScheduleEncodingIrregular.getEmploymentHistory().getEmployee() == null) {
							continue;
						}

						mainApplication.updateMainObject(employeeScheduleEncodingIrregular);
						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter, objectListToBeUpdatedList.size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("Updated schedule.\nSaving", counter,
								objectListToBeUpdatedList.size()));
					}
					counter = 0;
					Platform.runLater(() -> {
						AlertUtil.showSuccessSaveAlert(mainApplication.getPrimaryStage());
						// AlertUtil.showSuccessfullAlert("Saving Complete", getDialogStage());
						String errorMessage = "";
						if (!mainApplication.getModifyDetailsErrorAddList().isEmpty()) {
							errorMessage = "Error Saving Schedule: \r\n";
							for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular : mainApplication
									.getModifyDetailsErrorAddList()) {
								String fullname = employeeScheduleEncodingIrregular.getEmploymentHistory().getEmployee()
										.getEmployeeFullName();
								errorMessage = errorMessage + "• " + fullname + ".\r\n";
							}
							AlertUtil.showErrorAlert(errorMessage, mainApplication.getPrimaryStage());
						}
					});
					mainApplication.getModifyDetailsErrorAddList().clear();
					objectListToBeAddedList.clear();
					objectListToBeUpdatedList.clear();
				} catch (Exception e) {
					// TODO: handle exception
					throw e;
				}
				return null;
			}
		};
		ProgressUtil.showProcessInterface(mainApplication.getPrimaryStage(), taskSaveIrregularSchedule, false);
	}

	public void modifyDetailsIrregular(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		boolean successSave = true;

		if (modificationType.equals(ModificationType.ADD)) {
			for (Map.Entry<Employee, Boolean> employee : this.mainApplication.getSelectedEmployeesObservableList()) {
				this.allIrregularSchedByEmployeeList = this.mainApplication
						.getAllEmployeeIrregularScheduleByEmployeeID(employee.getKey().getEmployeeCode());

				if (employee.getKey().getEmployeeCode().compareTo(employeeScheduleEncodingIrregular
						.getEmploymentHistory().getEmployee().getEmployeeCode()) == 0) {

					boolean isSameSched = false;
					for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregularExisting : this.allIrregularSchedByEmployeeList) {

						if (employeeScheduleEncodingIrregular.getDateSchedule()
								.compareTo(employeeScheduleEncodingIrregularExisting.getDateSchedule()) == 0) {
							employeeScheduleEncodingIrregular
									.setPrimaryKey(employeeScheduleEncodingIrregularExisting.getPrimaryKey());
							isSameSched = true;
							successSave = this.objectListToBeUpdatedList.add(employeeScheduleEncodingIrregular);
						}
					}

					if (!isSameSched) {
						successSave = this.objectListToBeAddedList.add(employeeScheduleEncodingIrregular);
					}
				}
			}
		} else {
			successSave = this.mainApplication.updateMainObject(this.objectToModify);
		}

		if (!successSave) {
			this.mainApplication.getModifyDetailsErrorAddList().add(employeeScheduleEncodingIrregular);
		}

	}

	public void modifyDetails() {
		boolean successSave = false;

		this.copyCreatedObjectToBeSavedList.addAll(this.mainApplication.getCreatedObjectsToBeSavedList());

		if (modificationType.equals(ModificationType.ADD)) {
			for (Map.Entry<Employee, Boolean> employee : this.mainApplication.getSelectedEmployeesObservableList()) {
				this.allIrregularSchedByEmployeeList = this.mainApplication
						.getAllEmployeeIrregularScheduleByEmployeeID(employee.getKey().getEmployeeCode());

				// EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregularFromMain =
				// new EmployeeScheduleEncodingIrregular();

				for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregularFromMainCopy : this.copyCreatedObjectToBeSavedList) {

					if (employeeScheduleEncodingIrregularFromMainCopy.getEmploymentHistory() == null
							|| employeeScheduleEncodingIrregularFromMainCopy.getEmploymentHistory()
									.getEmployee() == null) {
						continue;
					}

					if (employee.getKey().getEmployeeCode().compareTo(employeeScheduleEncodingIrregularFromMainCopy
							.getEmploymentHistory().getEmployee().getEmployeeCode()) == 0) {
						// employeeScheduleEncodingIrregularFromMain =
						// employeeScheduleEncodingIrregularFromMainCopy;

						boolean isSameSched = false;
						for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregularExisting : this.allIrregularSchedByEmployeeList) {

							if (employeeScheduleEncodingIrregularFromMainCopy.getDateSchedule()
									.compareTo(employeeScheduleEncodingIrregularExisting.getDateSchedule()) == 0) {
								employeeScheduleEncodingIrregularFromMainCopy
										.setPrimaryKey(employeeScheduleEncodingIrregularExisting.getPrimaryKey());
								isSameSched = true;
								this.objectListToBeUpdatedList.add(employeeScheduleEncodingIrregularFromMainCopy);
							}
						}

						if (!isSameSched) {
							this.objectListToBeAddedList.add(employeeScheduleEncodingIrregularFromMainCopy);
						}

					}
				}

			}

			if (!this.objectListToBeAddedList.isEmpty()) {
				successSave = this.mainApplication.createMultipleData(this.objectListToBeAddedList);
			}

			if (!objectListToBeUpdatedList.isEmpty()) {
				for (EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular : this.objectListToBeUpdatedList) {
					// update irreg by batch
					successSave = this.mainApplication.updateMainObject(employeeScheduleEncodingIrregular);
				}
			}

		} else {
			// update only one irreg
			successSave = this.mainApplication.updateMainObject(this.objectToModify);
		}

		if (successSave) {
			AlertUtil.showSuccessSaveAlert(this.mainApplication.getPrimaryStage());
			this.objectListToBeAddedList.clear();
			this.objectListToBeUpdatedList.clear();
		} else {
			AlertUtil.showErrorAlert("Data not saved", this.mainApplication.getPrimaryStage());
		}
	}

	@Override
	public void onShowEditDialogStage() {
		if (modificationType.equals(ModificationType.EDIT)
				&& this.mainApplication.getSelectedEmployeesObservableList().size() == 1) {
			this.mainApplication.getSelectedDatesList()
					.add(new SimpleDateFormat("MM-dd-yyyy").format(this.objectToModify.getDateSchedule()));
		}
		this.showDetails(this.objectToModify);
	}

	@Override
	public void configureAccess() {

	}

	@Override
	public void showDetails(EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular) {
		this.tableViewDates.setDisable(false);
		this.comboBoxMonth.setDisable(false);

		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		this.tableViewEmployee.setItems(this.mainApplication.getSelectedEmployeesObservableList());
		TableColumnUtil.setColumn(this.tableColumnEmployeeId, p -> p.getKey().getEmployeeCode());
		TableColumnUtil.setColumn(this.tableColumnEmployeeName, p -> p.getKey().getEmployeeFullName());

		if (employeeScheduleEncodingIrregular != null && employeeScheduleEncodingIrregular.getPrimaryKey() != null) {
			this.tableViewDates.setDisable(true);
			this.comboBoxMonth.setDisable(true);
			this.comboBoxYear.setDisable(true);

			this.checkBoxBreakExempted
					.setSelected(employeeScheduleEncodingIrregular.getIsBreakExempted() == null ? false
							: employeeScheduleEncodingIrregular.getIsBreakExempted() == 1 ? true : false);
			this.textFieldTimeIn.setText(timeFormat.format(employeeScheduleEncodingIrregular.getTimeIn().getTime()));

			this.textFieldTimeOut.setText(timeFormat.format(employeeScheduleEncodingIrregular.getTimeOut().getTime()));
			this.textFieldLunchIn.setText(employeeScheduleEncodingIrregular.getLunchIn() == null ? ""
					: timeFormat.format(employeeScheduleEncodingIrregular.getLunchIn().getTime()));
			this.textFieldLunchOut.setText(employeeScheduleEncodingIrregular.getLunchOut() == null ? ""
					: timeFormat.format(employeeScheduleEncodingIrregular.getLunchOut().getTime()));

			this.textFieldOffset.setText(String.valueOf(employeeScheduleEncodingIrregular.getOffsetAllowed()));
			this.textFieldTotalMin.setText(String.valueOf(employeeScheduleEncodingIrregular.getTotalMinPerDay()));

			String getMonth = DateUtil.getMonth(employeeScheduleEncodingIrregular.getDateSchedule());
			Integer getYear = DateUtil.getYear(employeeScheduleEncodingIrregular.getDateSchedule());
			this.comboBoxMonth.setValue(getMonth);
			this.comboBoxYear.setValue(String.valueOf(getYear));

			this.mainApplication.getObservableListDates().forEach(dateList -> {
				if (dateList.getKey()
						.compareTo(dateFormat.format(employeeScheduleEncodingIrregular.getDateSchedule())) == 0) {
					dateList.setValue(true);
					this.tableViewDates.scrollTo(dateList);
				}
			});
		}

	}

	public Calendar getMonth(String newValue) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);

		if (modificationType.equals(ModificationType.EDIT)) {
			cal.set(Calendar.YEAR, DateUtil.getYear(this.objectToModify.getDateSchedule()));
		}

		switch (newValue) {
		case "January":
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			break;
		case "February":
			cal.set(Calendar.MONTH, Calendar.FEBRUARY);
			break;
		case "March":
			cal.set(Calendar.MONTH, Calendar.MARCH);
			break;
		case "April":
			cal.set(Calendar.MONTH, Calendar.APRIL);
			break;
		case "May":
			cal.set(Calendar.MONTH, Calendar.MAY);
			break;
		case "June":
			cal.set(Calendar.MONTH, Calendar.JUNE);
			break;
		case "July":
			cal.set(Calendar.MONTH, Calendar.JULY);
			break;
		case "August":
			cal.set(Calendar.MONTH, Calendar.AUGUST);
			break;
		case "September":
			cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
			break;
		case "October":
			cal.set(Calendar.MONTH, Calendar.OCTOBER);
			break;
		case "November":
			cal.set(Calendar.MONTH, Calendar.NOVEMBER);
			break;
		case "December":
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			break;
		default:
			break;
		}
		return cal;
	}

	public void setDatesOfTheMonth(String month, String year) {

		if (month == "" || year == "") {
			return;
		}

		Calendar cal = this.getMonth(month);
		cal.set(Calendar.YEAR, Integer.valueOf(year));
		int myMonth = cal.get(Calendar.MONTH);
		int myYear = cal.get(Calendar.YEAR);

		if (!this.mainApplication.getAllDateOfTheMonthObservableList().isEmpty()) {
			this.mainApplication.getAllDateOfTheMonthObservableList().clear();
		}

		while (myMonth == cal.get(Calendar.MONTH) && myYear == cal.get(Calendar.YEAR)) {
			this.mainApplication.getAllDateOfTheMonthObservableList().add(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}

		if (!this.mainApplication.getAllDateOfTheMonthFormattedObservableList().isEmpty()) {
			this.mainApplication.getAllDateOfTheMonthFormattedObservableList().clear();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		for (Date date : this.mainApplication.getAllDateOfTheMonthObservableList()) {
			String dates = dateFormat.format(date);
			this.mainApplication.getAllDateOfTheMonthFormattedObservableList().add(dates);
		}

		this.removeAlreadyAddedIrregularSched();

	}

	public void removeAlreadyAddedIrregularSched() {

		if (this.mainApplication.getSelectedEmployeesObservableList().size() > 1) {
			return;
			// return because there's more than 1 selected employee and should not run the
			// next line of codes specifically for 1 selected employee
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		if (modificationType.equals(ModificationType.ADD)) {
			List<String> schedToBeRemove = new ArrayList<>();

			this.mainApplication.getSelectedEmployeesObservableList().forEach(employee -> {
				List<EmployeeScheduleEncodingIrregular> employeeIrregularSched = this.mainApplication
						.getAllEmployeeIrregularScheduleByEmployeeID(employee.getKey().getEmployeeCode());

				for (EmployeeScheduleEncodingIrregular irregularSched : employeeIrregularSched) {
					for (String date : this.mainApplication.getAllDateOfTheMonthFormattedObservableList()) {
						if (dateFormat.format(irregularSched.getDateSchedule()).compareTo(date) == 0) {
							schedToBeRemove.add(date);
						}
					}
				}
			});

			this.mainApplication.getAllDateOfTheMonthFormattedObservableList().removeAll(schedToBeRemove);
		}
	}

	public void setFieldListener() {
		this.validatedTextFieldTimeFormattedList.forEach(textField -> {
			textField.textProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue != null && newValue != "") {
					Long totalMinPerDay = this.mainApplication.getScheduleEncodingMain()
							.computeTotalMinPerDay(this.validatedTextFieldTimeFormattedList, this.isBreakExempted);
					this.textFieldTotalMin.setText(
							totalMinPerDay == null ? "" : totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));
				}
			});
		});

		this.comboBoxMonth.valueProperty().addListener((obs, oldValue, newValue) -> {
			this.selectedMonth = newValue;
			
			if (newValue != null && !newValue.isEmpty()) {
				if (selectedYear != null) {
					this.setDatesOfTheMonth(newValue, selectedYear);
					this.setTableViewItems();
				}

			}
		});

		this.comboBoxYear.valueProperty().addListener((obs, oldValue, newValue) -> {
			
			this.selectedYear = newValue;

			if (newValue != null && !newValue.isEmpty()) {
				if (selectedMonth != null) {
					this.setDatesOfTheMonth(selectedMonth, newValue);
					this.setTableViewItems();
				}

			}
		});

		this.tableColumnDatesCheckbox.setGraphic(this.checkBoxDates);
		this.checkBoxDates.setTooltip(new Tooltip("Select All\r\nUnselect All"));
		this.tableViewDates.setEditable(true);

		this.checkBoxDates.selectedProperty().addListener((obs, oldValue, newValue) -> {
			this.mainApplication.getObservableListDates().forEach(object -> {
				object.setValue(newValue);
			});

			TableViewUtil.refreshTableView(this.tableViewDates);
		});

		this.tableColumnDatesCheckbox.setCellValueFactory(c -> {
			SimpleBooleanProperty property = new SimpleBooleanProperty();
			property.setValue(c.getValue().getValue());

			final CheckBoxTableCell<String, Boolean> checkBoxTableCell = new CheckBoxTableCell<String, Boolean>();

			checkBoxTableCell.setSelectedStateCallback(new Callback<Integer, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(Integer index) {
					return property;
				}
			});

			property.addListener((obs, oldValue, newValue) -> {
				c.getValue().setValue(newValue);
				this.tableViewDates.getSelectionModel().select(c.getValue());
				TableViewUtil.refreshTableView(this.tableViewDates);
			});

			return property;
		});

		this.tableColumnDatesCheckbox.setCellFactory(CheckBoxTableCell.forTableColumn(this.tableColumnDatesCheckbox));

		this.checkBoxBreakExempted.selectedProperty().addListener((obs, oldValue, newValue) -> {
			this.isBreakExempted = newValue;

			if (newValue.equals(true)) {
				this.lunchInOldValue = this.textFieldLunchIn.getText();
				this.lunchOutOldValue = this.textFieldLunchOut.getText();
				this.textFieldLunchIn.setDisable(true);
				this.textFieldLunchOut.setDisable(true);
				this.validatedTextFieldTimeFormattedList.remove(this.textFieldLunchOut);
				this.validatedTextFieldTimeFormattedList.remove(this.textFieldLunchIn);
				this.textFieldLunchIn.clear();
				this.textFieldLunchOut.clear();
			} else {
				this.textFieldLunchIn.setDisable(false);
				this.textFieldLunchOut.setDisable(false);
				this.validatedTextFieldTimeFormattedList.add(this.textFieldLunchOut);
				this.validatedTextFieldTimeFormattedList.add(this.textFieldLunchIn);
				this.textFieldLunchIn.setText(this.lunchInOldValue);
				this.textFieldLunchOut.setText(this.lunchOutOldValue);
			}

			Long totalMinPerDay = this.mainApplication.getScheduleEncodingMain()
					.computeTotalMinPerDay(this.validatedTextFieldTimeFormattedList, this.isBreakExempted);
			this.textFieldTotalMin
					.setText(totalMinPerDay == null ? "" : totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));

		});
	}

	@Override
	public void onSetMainApplication() {
		this.setFieldConfig();

		this.mainApplication.getAllMonthsNameObservableList().setAll(DateUtil.getAllMonthName());
		this.comboBoxMonth.setItems(this.mainApplication.getAllMonthsNameObservableList(), p -> p, true);

		ObservableList<String> yearsObservableList = FXCollections.observableArrayList();

		for (int years = Calendar.getInstance().get(Calendar.YEAR); years != 1970 ; years--) {
			yearsObservableList.add(years + "");
		}
		
		this.comboBoxYear.setItems(yearsObservableList, p -> p, true);

	}

	public void setTableViewItems() {
		this.mainApplication.populateObservableListDates();

		this.tableViewDates.setItems(this.mainApplication.getObservableListDates());
		// TableColumnUtil.setColumn(this.tableColumnDates, p -> p.getKey());

		this.tableColumnDates.setCellValueFactory(cellData -> {
			SimpleStringProperty property = new SimpleStringProperty();
			// DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

			if (cellData.getValue().getKey() != null) {
				property.setValue(cellData.getValue().getKey());
				this.tableColumnDates.setStyle("-fx-alignment: BASELINE_CENTER;");
			}
			return property;
		});

	}

	public void setFieldConfig() {
		this.setFieldList();
		this.setFieldListener();
		this.setTextFieldFormat(this.validatedTextFieldTimeFormattedList);

		this.validatedTextFieldTimeFormattedList.forEach(textField -> {
			this.mainApplication.setTimeConfigTextField(textField);
		});

		this.textFieldTotalMin.setEditable(false);
		this.textFieldTotalMin.setStyle("-fx-text-box-border: black;");
	}

	public void setFieldList() {
		this.validatedTextFieldTimeFormattedList.add(textFieldTimeIn);
		this.validatedTextFieldTimeFormattedList.add(textFieldTimeOut);
		this.validatedTextFieldTimeFormattedList.add(textFieldLunchOut);
		this.validatedTextFieldTimeFormattedList.add(textFieldLunchIn);

		this.validatedTextFieldList.addAll(this.validatedTextFieldTimeFormattedList);
		this.validatedTextFieldList.add(this.textFieldOffset);
		// this.validatedTextFieldList.add(this.textFieldTotalMin);

		this.autoFillComboBoxList.add(this.comboBoxMonth);
	}

	public void setTextFieldFormat(List<ValidatedTextField> textFieldList) {
		textFieldList.forEach(textField -> {
			textField.setFormat("##:##");
			textField.setPromptText("HH:mm");
		});

		this.textFieldOffset.setFormat("######");
		this.textFieldTotalMin.setFormat("######");
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private AutoFillComboBox<String> comboBoxMonth;
	@FXML
	private TableView<Map.Entry<String, Boolean>> tableViewDates;
	@FXML
	private TableColumn<Map.Entry<String, Boolean>, String> tableColumnDates;
	@FXML
	private ValidatedTextField textFieldTimeIn;
	@FXML
	private ValidatedTextField textFieldTimeOut;
	@FXML
	private TableColumn<Map.Entry<String, Boolean>, Boolean> tableColumnDatesCheckbox;
	@FXML
	private CheckBox checkBoxDates = new CheckBox();
	@FXML
	private ValidatedTextField textFieldLunchIn;
	@FXML
	private ValidatedTextField textFieldLunchOut;
	@FXML
	private ValidatedTextField textFieldOffset;
	@FXML
	private ValidatedTextField textFieldTotalMin;
	@FXML
	private TableView<Map.Entry<Employee, Boolean>> tableViewEmployee;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeId;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeName;
	@FXML
	private CheckBox checkBoxBreakExempted;
	@FXML
	private AutoFillComboBox<String> comboBoxYear;

}
