package ph.com.lserv.production.employeescheduleencoding.view;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lserv.production.employeescheduleencoding.EmployeeScheduleEncodingMain;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class EditRegularEmployeeScheduleEncodingController
		extends MasterEditController<EmployeeScheduleEncoding, EmployeeScheduleEncodingMain> {
	List<ValidatedTextField> textFieldHeaderList = new ArrayList<>();
	List<ValidatedTextField> textFieldList = new ArrayList<>();
	ObservableList<ScheduleEncoding> schedulePresetObservableList = FXCollections.observableArrayList();
	List<CheckBox> checkBoxList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldTimeList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldTotalMinList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldMonList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldTuesList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldWedList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldThursList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldFriList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldSatList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldSunList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldLunchList = new ArrayList<>();
	List<ValidatedTextField> removedValidatedTextFieldLunchList = new ArrayList<>();
	List<String> preSelectedCheckboxList = new ArrayList<>();

	Boolean isSavingOverride = false;
	Boolean isBreakExempted = false;

	@Override
	public boolean isValid() {
		this.mainApplication.getEnabledValidatedTextFieldList().removeAll(this.validatedTextFieldTotalMinList);

		if (this.comboBoxSchedule.getValueObject() == null) {
			this.comboBoxSchedule.requestFocus();
			this.setFieldDisable(false);
			AlertUtil.showErrorAlert("Select a schedule.", this.getDialogStage());
			return false;
		}

		if (!FieldValidator.textFieldValidate(this.mainApplication.getEnabledValidatedTextFieldList())
				|| !FieldValidator.textFieldValidate(this.textFieldHeaderList)) {

			this.validatedTextFieldTotalMinList.forEach(textField -> {
				textField.setStyle("-fx-text-box-border: black;");
			});

			AlertUtil.showIncompleteDataAlert(this.getDialogStage());
			return false;
		}

		// validation employee already have selected schedule
		if (!this.isSavingOverride) {
			this.mainApplication.getEnabledValidatedTextFieldList().forEach(textField -> {
				textField.setStyle("-fx-text-box-border: black;");
			});
			this.textFieldOffsetAllowed.setStyle("-fx-text-box-border: black;");
			this.textFieldScheduleName.setStyle("-fx-text-box-border: black;");

			if (this.mainApplication.getSelectedEmployeesObservableList().size() == 1) {
				for (Map.Entry<Employee, Boolean> employee : this.mainApplication
						.getSelectedEmployeesObservableList()) {
					EmployeeScheduleEncoding employeeSched = this.mainApplication
							.getEmployeeScheduleByEmployeeID(employee.getKey().getEmployeeCode());
					if (employeeSched != null) {
						if (this.comboBoxSchedule.getValueObject() != null) {

							Boolean isSameSched = false;

							isSameSched = employeeSched.getScheduleEncodingReference().getPrimaryKeyReference()
									.compareTo(this.comboBoxSchedule.getValueObject().getPrimaryKeyReference()) == 0;

							String alertMessage = "Schedule: "
									+ employeeSched.getScheduleEncodingReference().getScheduleName()
									+ "\nis already encoded to \nEmployee: "
									+ employeeSched.getEmploymentHistory().getEmployee().getEmployeeFullName();

							if (isSameSched) {
								List<ScheduleEncoding> listRegularScheduleImport = new ArrayList<>();
								listRegularScheduleImport.addAll(this.mainApplication.getScheduleEncodingMain()
										.getAllScheduleUploadedByEmployeeId(employee.getKey().getEmployeeCode()));
								if (listRegularScheduleImport.isEmpty()) {
									AlertUtil.showInformationAlert(alertMessage, this.getDialogStage());
									this.setFieldDisable(false);
									this.checkBoxOverride.setSelected(false);
									return false;
								}
							}
						}
					}
				}
			}

		}

		if (this.isSavingOverride) {
			this.validatedTextFieldTotalMinList.forEach(textField -> {
				textField.setStyle("-fx-text-box-border: black;");
			});
			for (ValidatedTextField textFieldEnabled : this.mainApplication.getEnabledValidatedTextFieldList()) {
				for (ValidatedTextField textFieldTime : this.validatedTextFieldTimeList) {
					if (textFieldTime != null && !textFieldTime.getText().isEmpty()) {
						if (textFieldEnabled.equals(textFieldTime)) {

							this.mainApplication.getScheduleEncodingMain().timeConfig(textFieldTime);
							String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

							if (!textFieldTime.getText().matches(regex)) {
								textFieldTime.requestFocus();
								AlertUtil.showErrorAlert("Invalid Time Range",
										this.getMainApplication().getPrimaryStage());
								this.validatedTextFieldTotalMinList.forEach(textField -> {
									textField.setStyle("-fx-text-box-border: black;");
								});
								return false;
							}
						}
					}
				}
			}

			this.mainApplication.getScheduleEncodingMain().setPrimaryStage(this.getDialogStage());
			if (this.mainApplication.getScheduleEncodingMain().isInvalidRangeForEachTextField(validatedTextFieldMonList,
					validatedTextFieldTuesList, validatedTextFieldWedList, validatedTextFieldThursList,
					validatedTextFieldFriList, validatedTextFieldSatList, validatedTextFieldSunList,
					this.isBreakExempted)) {
				this.validatedTextFieldTotalMinList.forEach(textField -> {
					textField.setStyle("-fx-text-box-border: black;");
				});
				// AlertUtil.showErrorAlert("Invalid Time Range", this.getDialogStage());
				return false;
			}

			if (this.isWithSchedNameNoDays()) {
				AlertUtil.showInformationAlert("Invalid schedule, select any day.",
						this.getMainApplication().getPrimaryStage());
				return false;
			}

			if (this.comboBoxSchedule.getValueObject() != null) {
				ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
				if (this.isSavingOverride) {

					scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
							.getScheduleByScheduleNameAndClientCode(this.textFieldScheduleName.getText(),
									this.mainApplication.getSelectedClient());

					if (scheduleEncodingReference != null) {
						this.validatedTextFieldTotalMinList.forEach(textField -> {
							textField.setStyle("-fx-text-box-border: black;");
						});
						AlertUtil.showInformationAlert("Override Schedule:\nPlease change schedule name.",
								this.getDialogStage());
						return false;
					}
					this.validatedTextFieldTotalMinList.forEach(textField -> {
						textField.setStyle("-fx-text-box-border: black;");
					});
				}
			}

			ObservableList<ScheduleEncoding> scheduleEncodingObservableList = FXCollections.observableArrayList();
			scheduleEncodingObservableList
					.setAll(this.mainApplication.getScheduleEncodingMain().getAllScheduleByPrikeyReferenceClient(
							this.comboBoxSchedule.getValueObject().getPrimaryKeyReference()));

			if (scheduleEncodingObservableList != null) {
				if (!scheduleEncodingObservableList.isEmpty()) {
					try {
						if (this.isSameSchedule(scheduleEncodingObservableList)) {
							if (this.isWithNewSelectedDays()) {
								this.validatedTextFieldTotalMinList.forEach(textField -> {
									textField.setStyle("-fx-text-box-border: black;");
								});
								return true;
							}
							this.validatedTextFieldTotalMinList.forEach(textField -> {
								textField.setStyle("-fx-text-box-border: black;");
							});
							AlertUtil.showInformationAlert("Please make some changes to schedule.",
									this.getDialogStage());
							return false;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}

		this.validatedTextFieldTotalMinList.forEach(textField -> {
			textField.setStyle("-fx-text-box-border: black;");
		});
		return true;
	}

	public boolean isWithSchedNameNoDays() {
		if (!this.textFieldScheduleName.getText().isEmpty() && this.textFieldScheduleName.getText() != null) {
			for (CheckBox checkBox : checkBoxList) {
				if (checkBox.isSelected()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isWithNewSelectedDays() {
		List<String> newSelectedCheckBox = new ArrayList<>();

		for (CheckBox checkBox : this.checkBoxList) {
			if (checkBox.isSelected()) {
				newSelectedCheckBox.add(checkBox.getText());
			}
		}

		if (newSelectedCheckBox.size() > preSelectedCheckboxList.size()
				| newSelectedCheckBox.size() < preSelectedCheckboxList.size()) {
			return true;
		}

		if (newSelectedCheckBox.size() == preSelectedCheckboxList.size()) {
			if (!preSelectedCheckboxList.containsAll(newSelectedCheckBox)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSameScheduleForEach(List<ValidatedTextField> textFieldList, ScheduleEncoding scheduleEncoding,
			String schedDay) throws ParseException {

		DateFormat formatter = new SimpleDateFormat("HH:mm");

		Boolean isBreakExempted = scheduleEncoding.getIsBreakExempted() == null ? false
				: scheduleEncoding.getIsBreakExempted() == 1 ? true : false;

		if (!isBreakExempted) {
			if (new Time(formatter.parse(textFieldList.get(0).getText()).getTime())
					.compareTo(scheduleEncoding.getTimeIn()) == 0
					&& new Time(formatter.parse(textFieldList.get(1).getText()).getTime())
							.compareTo(scheduleEncoding.getTimeOut()) == 0
					&& new Time(formatter.parse(textFieldList.get(2).getText()).getTime())
							.compareTo(scheduleEncoding.getLunchOut()) == 0
					&& new Time(formatter.parse(textFieldList.get(3).getText()).getTime())
							.compareTo(scheduleEncoding.getLunchIn()) == 0
					&& scheduleEncoding.getOffsetAllowed().toString()
							.compareTo(this.textFieldOffsetAllowed.getText()) == 0) {
				return true;
			}
		} else {
			if (new Time(formatter.parse(textFieldList.get(0).getText()).getTime())
					.compareTo(scheduleEncoding.getTimeIn()) == 0
					&& new Time(formatter.parse(textFieldList.get(1).getText()).getTime())
							.compareTo(scheduleEncoding.getTimeOut()) == 0
					&& scheduleEncoding.getOffsetAllowed().toString()
							.compareTo(this.textFieldOffsetAllowed.getText()) == 0) {
				return true;
			}
		}

		return false;
	}

	public boolean isSameSchedule(List<ScheduleEncoding> scheduleEncodingObservableList) throws ParseException {
		Boolean isSameSched = false;
		Boolean schedMon = true;
		Boolean schedTues = true;
		Boolean schedWed = true;
		Boolean schedThurs = true;
		Boolean schedFri = true;
		Boolean schedSat = true;
		Boolean schedSun = true;

		if (scheduleEncodingObservableList != null) {
			if (!scheduleEncodingObservableList.isEmpty()) {
				for (ScheduleEncoding scheduleEncoding : scheduleEncodingObservableList) {
					for (CheckBox checkBox : checkBoxList) {
						if (checkBox.isSelected()) {
							String schedDay = checkBox.getText();

							switch (schedDay) {
							case "Monday":
								if (FieldValidator.textFieldValidate(this.validatedTextFieldMonList)) {
									schedMon = this.isSameScheduleForEach(this.validatedTextFieldMonList,
											scheduleEncoding, schedDay);
								}
								break;
							case "Tuesday":
								if (FieldValidator.textFieldValidate(this.validatedTextFieldTuesList)) {
									schedTues = this.isSameScheduleForEach(this.validatedTextFieldTuesList,
											scheduleEncoding, schedDay);
								}
								break;
							case "Wednesday":
								if (FieldValidator.textFieldValidate(this.validatedTextFieldWedList)) {
									schedWed = this.isSameScheduleForEach(this.validatedTextFieldWedList,
											scheduleEncoding, schedDay);
								}
								break;
							case "Thursday":
								if (FieldValidator.textFieldValidate(this.validatedTextFieldThursList)) {
									schedThurs = this.isSameScheduleForEach(this.validatedTextFieldThursList,
											scheduleEncoding, schedDay);
								}
								break;
							case "Friday":
								if (FieldValidator.textFieldValidate(this.validatedTextFieldFriList)) {
									schedFri = this.isSameScheduleForEach(this.validatedTextFieldFriList,
											scheduleEncoding, schedDay);
								}
								break;
							case "Saturday":
								if (FieldValidator.textFieldValidate(this.validatedTextFieldSatList)) {
									schedSat = this.isSameScheduleForEach(this.validatedTextFieldSatList,
											scheduleEncoding, schedDay);
								}
								break;
							case "Sunday":
								if (FieldValidator.textFieldValidate(this.validatedTextFieldSunList)) {
									schedSun = this.isSameScheduleForEach(this.validatedTextFieldSunList,
											scheduleEncoding, schedDay);
								}
								break;
							default:
								break;
							}
						}
					}

					if (schedMon && schedTues && schedWed && schedThurs && schedFri && schedSat && schedSun) {
						isSameSched = true;
					}
				}
			}
		}

		return isSameSched;
	}

	@Override
	public void onSave() {
		if (this.isSavingOverride) {
			String schedName = this.textFieldScheduleName.getText();
			ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
			scheduleEncodingReference = this.mainApplication.setScheduleReferenceObject(schedName);

			if (scheduleEncodingReference != null) {
				if (this.mainApplication.getScheduleEncodingReferenceMain()
						.createMainObject(scheduleEncodingReference)) {
					if (this.setObjectToModify()) {
						this.createEmployeeScheduleEncodingObject();
					}
				}
			}
		} else {
			this.createEmployeeScheduleEncodingObject();
		}
	}

	public void createEmployeeScheduleEncodingObject() {
		this.mainApplication.getSelectedEmployeesObservableList().forEach(employee -> {
			EmployeeScheduleEncoding employeeScheduleEncoding = new EmployeeScheduleEncoding();

			EmploymentHistory employmentHistory = this.mainApplication.getEmploymentHistoryMain()
					.getEmploymentHistoryMaxByEmployeeCode(employee.getKey().getEmployeeCode());

			List<ScheduleEncoding> listRegularScheduleImport = new ArrayList<>();
			listRegularScheduleImport.addAll(this.mainApplication.getScheduleEncodingMain()
					.getAllScheduleUploadedByEmployeeId(employmentHistory.getEmployee().getEmployeeCode()));

			if (!listRegularScheduleImport.isEmpty()) {
				this.mainApplication.getScheduleEncodingMain().deleteMultipleMainObject(listRegularScheduleImport);
			}

			employeeScheduleEncoding.setEmploymentHistory(employmentHistory);

			employeeScheduleEncoding.setChangedByUser(
					this.mainApplication.getUser() == null ? null : this.mainApplication.getUser().getUserName());
			employeeScheduleEncoding.setChangedOnDate(this.mainApplication.getDateNow());
			employeeScheduleEncoding.setChangedInComputer(this.mainApplication.getComputerName());
			employeeScheduleEncoding.setUser(this.mainApplication.getUser());

			ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
			if (!this.isSavingOverride) {
				scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
						.getScheduleByScheduleNameAndClientCode(this.comboBoxSchedule.getValue(),
								this.mainApplication.getSelectedClient());
			} else {
				scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
						.getScheduleByScheduleNameAndClientCode(this.textFieldScheduleName.getText(),
								this.mainApplication.getSelectedClient());
			}

			employeeScheduleEncoding.setScheduleEncodingReference(scheduleEncodingReference);

			this.mainApplication.getEmployeeScheduleEncodingToBeSaveList().add(employeeScheduleEncoding);
		});
	}

	public void objectToModifyConfig(List<ValidatedTextField> validatedTextFieldList, String schedDay,
			ScheduleEncodingReference scheduleEncodingReference) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("HH:mm");

		Time timeIn = new Time(System.currentTimeMillis());
		Time timeOut = new Time(System.currentTimeMillis());
		Time lunchIn = new Time(System.currentTimeMillis());
		Time lunchOut = new Time(System.currentTimeMillis());
		Integer totalMinPerDay;
		Integer isBreakExempted;

		Integer offsetAllowed = Integer.valueOf(this.textFieldOffsetAllowed.getText());

		timeIn = new Time(formatter.parse(validatedTextFieldList.get(0).getText()).getTime());
		timeOut = new Time(formatter.parse(validatedTextFieldList.get(1).getText()).getTime());
		lunchIn = null;
		lunchOut = null;

		if (!this.isBreakExempted) {
			lunchOut = new Time(formatter.parse(validatedTextFieldList.get(2).getText()).getTime());
			lunchIn = new Time(formatter.parse(validatedTextFieldList.get(3).getText()).getTime());
			totalMinPerDay = Integer.valueOf(validatedTextFieldList.get(4).getText());
			isBreakExempted = this.checkBoxBreakExempted.isSelected() == true ? 1 : 0;

			this.mainApplication.getScheduleEncodingMain().setUser(this.mainApplication.getUser());

			this.mainApplication.getObjectToModifySelectedObjectList()
					.add(this.mainApplication.getScheduleEncodingMain().createScheduleEncodingObject(schedDay, timeIn,
							timeOut, lunchIn, lunchOut, offsetAllowed, scheduleEncodingReference, totalMinPerDay,
							isBreakExempted));
			return;
		}

		totalMinPerDay = Integer.valueOf(validatedTextFieldList.get(2).getText());
		isBreakExempted = this.checkBoxBreakExempted.isSelected() == true ? 1 : 0;

		this.mainApplication.getScheduleEncodingMain().setUser(this.mainApplication.getUser());

		this.mainApplication.getObjectToModifySelectedObjectList()
				.add(this.mainApplication.getScheduleEncodingMain().createScheduleEncodingObject(schedDay, timeIn,
						timeOut, lunchIn, lunchOut, offsetAllowed, scheduleEncodingReference, totalMinPerDay,
						isBreakExempted));

	}

	public boolean setObjectToModify() {
		ScheduleEncodingReference scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.textFieldScheduleName.getText(),
						this.mainApplication.getSelectedClient());

		try {
			for (CheckBox checkBox : checkBoxList) {
				if (checkBox.isSelected()) {
					String schedDay = checkBox.getText();
					switch (schedDay) {
					case "Monday":
						this.objectToModifyConfig(this.validatedTextFieldMonList, schedDay, scheduleEncodingReference);
						break;
					case "Tuesday":
						this.objectToModifyConfig(this.validatedTextFieldTuesList, schedDay, scheduleEncodingReference);
						break;
					case "Wednesday":
						this.objectToModifyConfig(this.validatedTextFieldWedList, schedDay, scheduleEncodingReference);
						break;
					case "Thursday":
						this.objectToModifyConfig(this.validatedTextFieldThursList, schedDay,
								scheduleEncodingReference);
						break;
					case "Friday":
						this.objectToModifyConfig(this.validatedTextFieldFriList, schedDay, scheduleEncodingReference);
						break;
					case "Saturday":
						this.objectToModifyConfig(this.validatedTextFieldSatList, schedDay, scheduleEncodingReference);
						break;
					case "Sunday":
						this.objectToModifyConfig(this.validatedTextFieldSunList, schedDay, scheduleEncodingReference);
						break;
					default:
						break;
					}
				}
			}
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;

	}

	public void setTableView() {
		this.mainApplication.getSelectedEmployeesFromTableView();

		this.tableViewEmployee.setItems(this.mainApplication.getSelectedEmployeesObservableList());
		TableColumnUtil.setColumn(this.tableColumnEmployeeId, p -> p.getKey().getEmployeeCode());
		TableColumnUtil.setColumn(this.tableColumnEmployeeName, p -> p.getKey().getEmployeeFullName());

	}

	@Override
	public void onShowEditDialogStage() {
		// if (this.removedValidatedTextFieldLunchList != null &&
		// !this.removedValidatedTextFieldLunchList.isEmpty()) {
		// this.removedValidatedTextFieldLunchList.clear();
		// }
	}

	public void setInitialSelectedTextFieldCount(List<ValidatedTextField> textFieldList, Boolean isBreakExempted) {
		if (isBreakExempted) {
			for (ValidatedTextField textField : textFieldList) {
				if (this.validatedTextFieldLunchList.contains(textField)) {
					if (!this.removedValidatedTextFieldLunchList.contains(textField)) {
						this.removedValidatedTextFieldLunchList.add(textField);
					}
					// textField.setDisable(true);
					textField.clear();
				}
			}

			textFieldList.removeAll(this.removedValidatedTextFieldLunchList);

		} else {
			this.clearListItems();
			this.setFieldList();

			if (this.removedValidatedTextFieldLunchList != null && !this.removedValidatedTextFieldLunchList.isEmpty()) {
				this.removedValidatedTextFieldLunchList.forEach(textField -> {
					if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textField)) {
						this.mainApplication.getEnabledValidatedTextFieldList().add(textField);
						// textField.setDisable(false);
						textField.clear();
					}
				});
			}
		}
	}

	public void clearListItems() {
		this.validatedTextFieldMonList.clear();
		this.validatedTextFieldTuesList.clear();
		this.validatedTextFieldWedList.clear();
		this.validatedTextFieldThursList.clear();
		this.validatedTextFieldFriList.clear();
		this.validatedTextFieldSatList.clear();
		this.validatedTextFieldSunList.clear();
	}

	@Override
	public void configureAccess() {

	}

	@Override
	public void showDetails(EmployeeScheduleEncoding employeeScheduleEncoding) {

	}

	public void clearFields() {
		this.textFieldList.forEach(textField -> {
			textField.clear();
		});

		this.checkBoxList.forEach(checkbox -> {
			checkbox.setSelected(false);
		});

		this.textFieldHeaderList.forEach(textField -> {
			textField.clear();
		});

	}

	public void setFieldList() {

		this.textFieldHeaderList.add(textFieldOffsetAllowed);
		this.textFieldHeaderList.add(textFieldScheduleName);

		this.validatedTextFieldTotalMinList.add(textFieldTotalMinMon);
		this.validatedTextFieldTotalMinList.add(textFieldTotalMinTues);
		this.validatedTextFieldTotalMinList.add(textFieldTotalMinWed);
		this.validatedTextFieldTotalMinList.add(textFieldTotalMinThurs);
		this.validatedTextFieldTotalMinList.add(textFieldTotalMinFri);
		this.validatedTextFieldTotalMinList.add(textFieldTotalMinSat);
		this.validatedTextFieldTotalMinList.add(textFieldTotalMinSun);

		this.validatedTextFieldMonList.add(textFieldMonTimeIn);
		this.validatedTextFieldMonList.add(textFieldMonTimeOut);
		this.validatedTextFieldMonList.add(textFieldMonLunchOut);
		this.validatedTextFieldMonList.add(textFieldMonLunchIn);
		this.validatedTextFieldMonList.add(textFieldTotalMinMon);

		this.validatedTextFieldTuesList.add(textFieldTuesTimeIn);
		this.validatedTextFieldTuesList.add(textFieldTuesTimeOut);
		this.validatedTextFieldTuesList.add(textFieldTuesLunchOut);
		this.validatedTextFieldTuesList.add(textFieldTuesLunchIn);
		this.validatedTextFieldTuesList.add(textFieldTotalMinTues);

		this.validatedTextFieldWedList.add(textFieldWedTimeIn);
		this.validatedTextFieldWedList.add(textFieldWedTimeOut);
		this.validatedTextFieldWedList.add(textFieldWedLunchOut);
		this.validatedTextFieldWedList.add(textFieldWedLunchIn);
		this.validatedTextFieldWedList.add(textFieldTotalMinWed);

		this.validatedTextFieldThursList.add(textFieldThursTimeIn);
		this.validatedTextFieldThursList.add(textFieldThursTimeOut);
		this.validatedTextFieldThursList.add(textFieldThursLunchOut);
		this.validatedTextFieldThursList.add(textFieldThursLunchIn);
		this.validatedTextFieldThursList.add(textFieldTotalMinThurs);

		this.validatedTextFieldFriList.add(textFieldFriTimeIn);
		this.validatedTextFieldFriList.add(textFieldFriTimeOut);
		this.validatedTextFieldFriList.add(textFieldFriLunchOut);
		this.validatedTextFieldFriList.add(textFieldFriLunchIn);
		this.validatedTextFieldFriList.add(textFieldTotalMinFri);

		this.validatedTextFieldSatList.add(textFieldSatTimeIn);
		this.validatedTextFieldSatList.add(textFieldSatTimeOut);
		this.validatedTextFieldSatList.add(textFieldSatLunchOut);
		this.validatedTextFieldSatList.add(textFieldSatLunchIn);
		this.validatedTextFieldSatList.add(textFieldTotalMinSat);

		this.validatedTextFieldSunList.add(textFieldSunTimeIn);
		this.validatedTextFieldSunList.add(textFieldSunTimeOut);
		this.validatedTextFieldSunList.add(textFieldSunLunchOut);
		this.validatedTextFieldSunList.add(textFieldSunLunchIn);
		this.validatedTextFieldSunList.add(textFieldTotalMinSun);

		this.validatedTextFieldTimeList.addAll(validatedTextFieldMonList);
		this.validatedTextFieldTimeList.addAll(validatedTextFieldTuesList);
		this.validatedTextFieldTimeList.addAll(validatedTextFieldWedList);
		this.validatedTextFieldTimeList.addAll(validatedTextFieldThursList);
		this.validatedTextFieldTimeList.addAll(validatedTextFieldFriList);
		this.validatedTextFieldTimeList.addAll(validatedTextFieldSatList);
		this.validatedTextFieldTimeList.addAll(validatedTextFieldSunList);

		this.validatedTextFieldTimeList.removeAll(this.validatedTextFieldTotalMinList);

		this.textFieldList.addAll(this.textFieldHeaderList);
		this.textFieldList.addAll(this.validatedTextFieldTimeList);

		if (this.checkBoxList.isEmpty()) {
			this.checkBoxList.add(checkBoxMonday);
			this.checkBoxList.add(checkBoxTuesday);
			this.checkBoxList.add(checkBoxWednesday);
			this.checkBoxList.add(checkBoxThursday);
			this.checkBoxList.add(checkBoxFriday);
			this.checkBoxList.add(checkBoxSaturday);
			this.checkBoxList.add(checkBoxSunday);
		}

		this.validatedTextFieldLunchList.add(this.textFieldMonLunchIn);
		this.validatedTextFieldLunchList.add(this.textFieldMonLunchOut);
		this.validatedTextFieldLunchList.add(this.textFieldTuesLunchIn);
		this.validatedTextFieldLunchList.add(this.textFieldTuesLunchOut);
		this.validatedTextFieldLunchList.add(this.textFieldWedLunchIn);
		this.validatedTextFieldLunchList.add(this.textFieldWedLunchOut);
		this.validatedTextFieldLunchList.add(this.textFieldThursLunchIn);
		this.validatedTextFieldLunchList.add(this.textFieldThursLunchOut);
		this.validatedTextFieldLunchList.add(this.textFieldFriLunchIn);
		this.validatedTextFieldLunchList.add(this.textFieldFriLunchOut);
		this.validatedTextFieldLunchList.add(this.textFieldSatLunchIn);
		this.validatedTextFieldLunchList.add(this.textFieldSatLunchOut);
		this.validatedTextFieldLunchList.add(this.textFieldSunLunchIn);
		this.validatedTextFieldLunchList.add(this.textFieldSunLunchOut);

	}

	public void setFieldDisable(boolean isOverride) {

		this.validatedTextFieldTotalMinList.forEach(textField -> {
			textField.setEditable(false);
			textField.setStyle("-fx-text-box-border: black;");
		});

		if (isOverride) {
			this.textFieldHeaderList.forEach(textField -> {
				textField.setEditable(true);
				textField.setStyle("");
			});

			this.checkBoxList.forEach(checkBox -> {
				checkBox.setDisable(false);
			});

		} else {

			this.showSchedulePresetDetails();

			this.textFieldHeaderList.forEach(textField -> {
				textField.setEditable(false);
				textField.setStyle("-fx-text-box-border: black;");
			});

			this.validatedTextFieldTimeList.forEach(textField -> {
				textField.setEditable(false);
				textField.setStyle("-fx-text-box-border: black;");
			});

			this.checkBoxList.forEach(checkbox -> {
				checkbox.setDisable(true);
			});

		}

	}

	public void showDetailsSelectedFromEdit(String schedDay) {
		List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();

		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
		scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.comboBoxSchedule.getValueObject().getScheduleName(),
						this.mainApplication.getSelectedClient());

		if (scheduleEncodingReference != null) {
			scheduleEncodingList = this.mainApplication.getScheduleEncodingMain()
					.getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference());
		}

		for (ScheduleEncoding scheduleEncoding : scheduleEncodingList) {
			if (scheduleEncoding != null && scheduleEncoding.getPrimaryKey() != null) {
				switch (schedDay) {
				case "Monday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.getScheduleEncodingMain()
								.showDetailsScheduleConfig(this.validatedTextFieldMonList, scheduleEncoding);
					}
					break;
				case "Tuesday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.getScheduleEncodingMain()
								.showDetailsScheduleConfig(this.validatedTextFieldTuesList, scheduleEncoding);
					}
					break;
				case "Wednesday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.getScheduleEncodingMain()
								.showDetailsScheduleConfig(this.validatedTextFieldWedList, scheduleEncoding);
					}
					break;
				case "Thursday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.getScheduleEncodingMain()
								.showDetailsScheduleConfig(this.validatedTextFieldThursList, scheduleEncoding);
					}
					break;
				case "Friday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.getScheduleEncodingMain()
								.showDetailsScheduleConfig(this.validatedTextFieldFriList, scheduleEncoding);
					}
					break;
				case "Saturday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.getScheduleEncodingMain()
								.showDetailsScheduleConfig(this.validatedTextFieldSatList, scheduleEncoding);
					}
					break;
				case "Sunday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.getScheduleEncodingMain()
								.showDetailsScheduleConfig(this.validatedTextFieldSunList, scheduleEncoding);
					}
					break;
				default:
					break;
				}
			}
		}
	}

	public void startSetEditableFieldListener(boolean isOverride) {
		this.setEditableFieldListener(checkBoxMonday, this.validatedTextFieldMonList, isOverride);
		this.setEditableFieldListener(checkBoxTuesday, this.validatedTextFieldTuesList, isOverride);
		this.setEditableFieldListener(checkBoxWednesday, this.validatedTextFieldWedList, isOverride);
		this.setEditableFieldListener(checkBoxThursday, this.validatedTextFieldThursList, isOverride);
		this.setEditableFieldListener(checkBoxFriday, this.validatedTextFieldFriList, isOverride);
		this.setEditableFieldListener(checkBoxSaturday, this.validatedTextFieldSatList, isOverride);
		this.setEditableFieldListener(checkBoxSunday, this.validatedTextFieldSunList, isOverride);
	}

	public void showDetailsSchedule(List<ScheduleEncoding> schedulePresetByClientList) {
		for (ValidatedTextField textField : this.textFieldList) {
			textField.clear();
		}

		for (ScheduleEncoding scheduleEncoding : schedulePresetByClientList) {

			if (scheduleEncoding != null && scheduleEncoding.getPrimaryKey() != null) {

				this.textFieldScheduleName.setText(scheduleEncoding.getScheduleEncodingReference() != null
						? scheduleEncoding.getScheduleEncodingReference().getScheduleName()
						: "");
				this.textFieldOffsetAllowed.setText(String.valueOf(scheduleEncoding.getOffsetAllowed()));

				this.isBreakExempted = scheduleEncoding.getIsBreakExempted() == null ? false
						: scheduleEncoding.getIsBreakExempted() == 1 ? true : false;

				this.checkBoxBreakExempted.setSelected(this.isBreakExempted);

				switch (scheduleEncoding.getScheduleDay()) {
				case "Monday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldMonList, this.isBreakExempted);
					this.mainApplication.getScheduleEncodingMain()
							.showDetailsScheduleConfig(this.validatedTextFieldMonList, scheduleEncoding);
					this.checkBoxMonday.setSelected(true);
					break;
				case "Tuesday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldTuesList, this.isBreakExempted);
					this.mainApplication.getScheduleEncodingMain()
							.showDetailsScheduleConfig(this.validatedTextFieldTuesList, scheduleEncoding);
					this.checkBoxTuesday.setSelected(true);
					break;
				case "Wednesday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldWedList, this.isBreakExempted);
					this.mainApplication.getScheduleEncodingMain()
							.showDetailsScheduleConfig(this.validatedTextFieldWedList, scheduleEncoding);
					this.checkBoxWednesday.setSelected(true);
					break;
				case "Thursday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldThursList, this.isBreakExempted);
					this.mainApplication.getScheduleEncodingMain()
							.showDetailsScheduleConfig(this.validatedTextFieldThursList, scheduleEncoding);
					this.checkBoxThursday.setSelected(true);
					break;
				case "Friday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldFriList, this.isBreakExempted);
					this.mainApplication.getScheduleEncodingMain()
							.showDetailsScheduleConfig(this.validatedTextFieldFriList, scheduleEncoding);
					this.checkBoxFriday.setSelected(true);
					break;
				case "Saturday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldSatList, this.isBreakExempted);
					this.mainApplication.getScheduleEncodingMain()
							.showDetailsScheduleConfig(this.validatedTextFieldSatList, scheduleEncoding);
					this.checkBoxSaturday.setSelected(true);
					break;
				case "Sunday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldSunList, this.isBreakExempted);
					this.mainApplication.getScheduleEncodingMain()
							.showDetailsScheduleConfig(this.validatedTextFieldSunList, scheduleEncoding);
					this.checkBoxSunday.setSelected(true);
					break;
				default:
					break;
				}
			}
		}

	}

	public void setEditableFieldListener(CheckBox checkbox, List<ValidatedTextField> textFieldList,
			Boolean isOverride) {

		if (checkbox != null) {
			if (!isOverride) {
				if (checkbox.isSelected()) {
					textFieldList.forEach(textField -> {
						textField.setEditable(false);
						textField.setStyle("-fx-text-box-border: black;");
					});
				}
			} else {

				if (checkbox.isSelected()) {
					textFieldList.forEach(textField -> {
						textField.setEditable(true);
						textField.setStyle("");
					});
				}
			}

			checkbox.selectedProperty().addListener((obs, oldValue, newValue) -> {

				this.setInitialSelectedTextFieldCount(textFieldList, this.isBreakExempted);

				textFieldList.forEach(textField -> {
					if (isOverride) {
						if (newValue) {
							textField.setEditable(true);
							textField.setStyle("");
							this.showDetailsSelectedFromEdit(checkbox.getText());

							this.validatedTextFieldTotalMinList.forEach(textFieldTotalMin -> {
								textFieldTotalMin.setEditable(false);
								textFieldTotalMin.setStyle("-fx-text-box-border: black;");
							});
						} else {
							textField.setEditable(false);
							textField.setStyle("-fx-text-box-border: black;");
							textField.clear();
						}
					} else {
						textField.setEditable(false);
						textField.setStyle("-fx-text-box-border: black;");
					}

				});
			});

			this.validatedTextFieldTotalMinList.forEach(textFieldTotalMin -> {
				textFieldTotalMin.setEditable(false);
				textFieldTotalMin.setStyle("-fx-text-box-border: black;");
			});
		}

		this.checkBoxBreakExempted.selectedProperty().addListener((obs, oldValue, newValue) -> {
			this.isBreakExempted = newValue;

			if (newValue) {
				if (checkbox.isSelected()) {
					this.setInitialSelectedTextFieldCount(textFieldList, this.isBreakExempted);
				}
			} else {
				if (checkbox.isSelected()) {
					for (ValidatedTextField textField : this.mainApplication.getEnabledValidatedTextFieldList()) {
						if (this.removedValidatedTextFieldLunchList.contains(textField)) {
							this.removedValidatedTextFieldLunchList.add(textField);
							textField.setDisable(false);
						}
					}
					this.setInitialSelectedTextFieldCount(textFieldList, this.isBreakExempted);
				}
			}
		});

		if (textFieldList != null) {
			if (textFieldList.size() != 0) {
				for (ValidatedTextField validatedTextField : textFieldList) {

					validatedTextField.textProperty().addListener((obs, oldValue, newValue) -> {
						if (newValue != null) {
							if (newValue != "") {
								// this.mainApplication.getScheduleEncodingMain().showTotalMin(textFieldList,
								// this.isBreakExempted, checkbox.isSelected());
								Long totalMinPerDay = this.mainApplication.getScheduleEncodingMain()
										.computeTotalMinPerDay(textFieldList, this.isBreakExempted);
								if (this.isBreakExempted) {
									if (checkbox.isSelected()) {
										textFieldList.get(2).setText(totalMinPerDay == null ? ""
												: totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));
									}
								} else {
									if (checkbox.isSelected()) {
										textFieldList.get(4).setText(totalMinPerDay == null ? ""
												: totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));
									}
								}
							}
						}
					});
				}
			}
		}

	}

	public void setFieldListener() {
		this.comboBoxSchedule.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue != null && !newValue.isEmpty()) {

				if (this.mainApplication.getEnabledValidatedTextFieldList() != null
						&& !this.mainApplication.getEnabledValidatedTextFieldList().isEmpty()) {
					this.mainApplication.getEnabledValidatedTextFieldList().clear();
				}

				if (this.removedValidatedTextFieldLunchList != null
						&& !this.removedValidatedTextFieldLunchList.isEmpty()) {
					this.removedValidatedTextFieldLunchList.clear();
				}

				this.checkBoxOverride.setDisable(false);

				this.showSchedulePresetDetails();
				this.getCheckBoxSelected();
				// this.mainApplication.getScheduleEncodingMain().setIsBreakExempted(this.isBreakExempted);
				// this.checkBoxList.forEach(checkBox -> {
				// if (checkBox.isSelected()) {
				// this.showDetailsSelectedFromEdit(checkBox.getText());
				// }
				// });

			} else {
				this.clearFields();
				this.checkBoxOverride.setDisable(true);
				this.checkBoxOverride.setSelected(false);
				this.textFieldList.forEach(textField -> {
					textField.setDisable(false);
				});
			}
		});

		// this.titledPaneScheduleDetails.setDisable(true);

		this.checkBoxOverride.selectedProperty().addListener((obs, oldValue, newValue) -> {
			this.isSavingOverride = newValue;
			this.setFieldDisable(newValue);
			this.startSetEditableFieldListener(newValue);

		});

	}

	public void setFields() {
		this.checkBoxOverride.setDisable(true);
		this.checkBoxBreakExempted.setDisable(true);
		this.setFieldList();

		this.validatedTextFieldTimeList.forEach(textField -> {
			this.mainApplication.getScheduleEncodingMain().setTextFieldFormat(textField);
			this.mainApplication.getScheduleEncodingMain().setTimeConfigTextField(textField);
		});

		this.setFieldDisable(false);
		this.startSetEditableFieldListener(false);
		this.checkBoxListener();

		this.mainApplication.populateClient();
		this.mainApplication.populateSchedulePreset();
		this.comboBoxSchedule.setItems(this.mainApplication.getSchedulesByClientObservableList(),
				p -> p.getScheduleName());

		this.textFieldOffsetAllowed.setPromptText("in minutes");
		this.textFieldOffsetAllowed.setFormat("#####");

		this.validatedTextFieldTotalMinList.forEach(textField -> {
			textField.setPromptText("in minutes");
		});

		this.textFieldClient.setEditable(false);
		this.textFieldClient.setStyle("-fx-text-box-border: black;");
		this.textFieldClient.setText(this.mainApplication.getSelectedClient() == null ? ""
				: this.mainApplication.getSelectedClient().getClientName());

		this.labelScheduleInfo.setTooltip(new Tooltip(
				"If there is no available schedule, \nOpen \"Schedule Encoding\" to encode schedule for this client."));
	}

	public void showSchedulePresetDetails() {
		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
		scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.comboBoxSchedule.getValue(),
						this.mainApplication.getSelectedClient());

		if (scheduleEncodingReference != null) {
			this.clearFields();
			this.showDetailsSchedule(this.mainApplication.getScheduleEncodingMain()
					.getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference()));
		}
	}

	public void addEnabledValidatedTextFields(String checkBoxDayName) {
		switch (checkBoxDayName) {
		case "Monday":
			this.mainApplication.getEnabledValidatedTextFieldList().addAll(this.validatedTextFieldMonList);
			break;
		case "Tuesday":
			this.mainApplication.getEnabledValidatedTextFieldList().addAll(this.validatedTextFieldTuesList);
			break;
		case "Wednesday":
			this.mainApplication.getEnabledValidatedTextFieldList().addAll(this.validatedTextFieldWedList);
			break;
		case "Thursday":
			this.mainApplication.getEnabledValidatedTextFieldList().addAll(this.validatedTextFieldThursList);
			break;
		case "Friday":
			this.mainApplication.getEnabledValidatedTextFieldList().addAll(this.validatedTextFieldFriList);
			break;
		case "Saturday":
			this.mainApplication.getEnabledValidatedTextFieldList().addAll(this.validatedTextFieldSatList);
			break;
		case "Sunday":
			this.mainApplication.getEnabledValidatedTextFieldList().addAll(this.validatedTextFieldSunList);
			break;
		default:
			break;
		}
	}

	public void getCheckBoxSelected() {

		if (!this.mainApplication.getEnabledValidatedTextFieldList().isEmpty()) {
			this.mainApplication.getEnabledValidatedTextFieldList().clear();
		}

		if (!this.preSelectedCheckboxList.isEmpty()) {
			this.preSelectedCheckboxList.clear();
		}

		this.checkBoxList.forEach(checkBox -> {
			if (checkBox.isSelected()) {
				this.addEnabledValidatedTextFields(checkBox.getText());
				this.preSelectedCheckboxList.add(checkBox.getText());
			}
		});

	}

	public void checkBoxListener() {

		for (CheckBox checkBox : checkBoxList) {
			checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue) {
					this.addEnabledValidatedTextFields(checkBox.getText());
				} else {
					switch (checkBox.getText()) {
					case "Monday":
						this.mainApplication.getEnabledValidatedTextFieldList()
								.removeAll(this.validatedTextFieldMonList);
						break;
					case "Tuesday":
						this.mainApplication.getEnabledValidatedTextFieldList()
								.removeAll(this.validatedTextFieldTuesList);
						break;
					case "Wednesday":
						this.mainApplication.getEnabledValidatedTextFieldList()
								.removeAll(this.validatedTextFieldWedList);
						break;
					case "Thursday":
						this.mainApplication.getEnabledValidatedTextFieldList()
								.removeAll(this.validatedTextFieldThursList);
						break;
					case "Friday":
						this.mainApplication.getEnabledValidatedTextFieldList()
								.removeAll(this.validatedTextFieldFriList);
						break;
					case "Saturday":
						this.mainApplication.getEnabledValidatedTextFieldList()
								.removeAll(this.validatedTextFieldSatList);
						break;
					case "Sunday":
						this.mainApplication.getEnabledValidatedTextFieldList()
								.removeAll(this.validatedTextFieldSunList);
						break;
					default:
						break;
					}
				}
			});
		}

	}

	@Override
	public void onSetMainApplication() {
		this.setFields();
		this.setFieldListener();
		this.setTableView();
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private AutoFillComboBox<ScheduleEncodingReference> comboBoxSchedule;
	@FXML
	private ValidatedTextField textFieldClient;
	@FXML
	private ValidatedTextField textFieldOffsetAllowed;
	@FXML
	private CheckBox checkBoxMonday;
	@FXML
	private ValidatedTextField textFieldMonTimeIn;
	@FXML
	private ValidatedTextField textFieldMonTimeOut;
	@FXML
	private ValidatedTextField textFieldMonLunchIn;
	@FXML
	private ValidatedTextField textFieldMonLunchOut;
	@FXML
	private CheckBox checkBoxTuesday;
	@FXML
	private ValidatedTextField textFieldTuesTimeIn;
	@FXML
	private ValidatedTextField textFieldTuesTimeOut;
	@FXML
	private ValidatedTextField textFieldTuesLunchIn;
	@FXML
	private ValidatedTextField textFieldTuesLunchOut;
	@FXML
	private CheckBox checkBoxWednesday;
	@FXML
	private ValidatedTextField textFieldWedTimeIn;
	@FXML
	private ValidatedTextField textFieldWedTimeOut;
	@FXML
	private ValidatedTextField textFieldWedLunchIn;
	@FXML
	private ValidatedTextField textFieldWedLunchOut;
	@FXML
	private CheckBox checkBoxThursday;
	@FXML
	private ValidatedTextField textFieldThursTimeIn;
	@FXML
	private ValidatedTextField textFieldThursTimeOut;
	@FXML
	private ValidatedTextField textFieldThursLunchIn;
	@FXML
	private ValidatedTextField textFieldThursLunchOut;
	@FXML
	private CheckBox checkBoxFriday;
	@FXML
	private ValidatedTextField textFieldFriTimeIn;
	@FXML
	private ValidatedTextField textFieldFriTimeOut;
	@FXML
	private ValidatedTextField textFieldFriLunchIn;
	@FXML
	private ValidatedTextField textFieldFriLunchOut;
	@FXML
	private CheckBox checkBoxSaturday;
	@FXML
	private ValidatedTextField textFieldSatTimeIn;
	@FXML
	private ValidatedTextField textFieldSatTimeOut;
	@FXML
	private ValidatedTextField textFieldSatLunchIn;
	@FXML
	private ValidatedTextField textFieldSatLunchOut;
	@FXML
	private CheckBox checkBoxSunday;
	@FXML
	private ValidatedTextField textFieldSunTimeIn;
	@FXML
	private ValidatedTextField textFieldSunTimeOut;
	@FXML
	private ValidatedTextField textFieldSunLunchIn;
	@FXML
	private ValidatedTextField textFieldSunLunchOut;
	@FXML
	private ValidatedTextField textFieldTotalMinMon;
	@FXML
	private ValidatedTextField textFieldTotalMinTues;
	@FXML
	private ValidatedTextField textFieldTotalMinWed;
	@FXML
	private ValidatedTextField textFieldTotalMinThurs;
	@FXML
	private ValidatedTextField textFieldTotalMinFri;
	@FXML
	private ValidatedTextField textFieldTotalMinSat;
	@FXML
	private ValidatedTextField textFieldTotalMinSun;
	@FXML
	private TableView<Map.Entry<Employee, Boolean>> tableViewEmployee;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeId;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeName;
	@FXML
	private CheckBox checkBoxOverride;
	@FXML
	private ValidatedTextField textFieldScheduleName;
	@FXML
	private TitledPane titledPaneScheduleDetails;
	@FXML
	private CheckBox checkBoxBreakExempted;
	@FXML
	private Label labelScheduleInfo;
}
