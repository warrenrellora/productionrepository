package ph.com.lserv.production.scheduleencoding.view;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lserv.production.scheduleencoding.ScheduleEncodingMain;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;
import ph.com.lserv.production.scheduleencodingreference.model.ScheduleEncodingReference;

public class EditScheduleEncodingController extends MasterEditController<ScheduleEncoding, ScheduleEncodingMain> {
	Map<String, Boolean> selectedDays;
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
	List<String> preSelectedCheckboxList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldHeaderList = new ArrayList<>();
	public List<AutoFillComboBox<?>> comboBoxList = new ArrayList<>();
	List<ValidatedTextField> enabledValidatedTextFieldEditList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldLunchList = new ArrayList<>();
	List<ValidatedTextField> removedValidatedTextFieldLunchList = new ArrayList<>();

	@Override
	public boolean isValid() {
		this.mainApplication.getEnabledValidatedTextFieldList().removeAll(this.validatedTextFieldTotalMinList);

		if (!FieldValidator.textFieldValidate(this.mainApplication.getEnabledValidatedTextFieldList())
				|| !FieldValidator.textFieldValidate(this.validatedTextFieldHeaderList)) {

			for (ValidatedTextField textFieldTime : this.validatedTextFieldTimeList) {
				if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textFieldTime)) {
					textFieldTime.setStyle("");
				}
			}

			for (ValidatedTextField textFieldLunch : this.removedValidatedTextFieldLunchList) {
				if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textFieldLunch)) {
					textFieldLunch.setStyle("");
				}
			}

			this.validatedTextFieldTotalMinList.forEach(textField -> {
				textField.setStyle("-fx-text-box-border: black;");
			});
			AlertUtil.showIncompleteDataAlert(this.getDialogStage());
			return false;
		}

		for (ValidatedTextField textFieldValidated : this.mainApplication.getEnabledValidatedTextFieldList()) {
			for (ValidatedTextField textFieldtime : this.validatedTextFieldTimeList) {
				if (textFieldtime != null && !textFieldtime.getText().isEmpty()) {
					if (textFieldValidated.equals(textFieldtime)) {
						String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
						this.mainApplication.timeConfig(textFieldtime);

						if (!textFieldtime.getText().matches(regex)) {
							this.validatedTextFieldTotalMinList.forEach(textField -> {
								textField.setStyle("-fx-text-box-border: black;");
							});
							textFieldtime.requestFocus();
							AlertUtil.showErrorAlert("Invalid Time Range", this.getDialogStage());
							return false;
						}
					}
				}
			}
		}

		if (this.mainApplication.isInvalidRangeForEachTextField(validatedTextFieldMonList, validatedTextFieldTuesList,
				validatedTextFieldWedList, validatedTextFieldThursList, validatedTextFieldFriList,
				validatedTextFieldSatList, validatedTextFieldSunList, this.mainApplication.getIsBreakExempted())) {
			this.validatedTextFieldTotalMinList.forEach(textField -> {
				textField.setStyle("-fx-text-box-border: black;");
			});
			return false;
		}

		if (this.isWithSchedNameNoDays()) {
			for (ValidatedTextField textFieldTime : this.validatedTextFieldTimeList) {
				if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textFieldTime)) {
					textFieldTime.setStyle("");
				}
			}

			for (ValidatedTextField textFieldLunch : this.removedValidatedTextFieldLunchList) {
				if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textFieldLunch)) {
					textFieldLunch.setStyle("");
				}
			}
			;

			this.validatedTextFieldTotalMinList.forEach(textField -> {
				textField.setStyle("-fx-text-box-border: black;");
			});
			AlertUtil.showInformationAlert("Invalid schedule. Select any day.", this.getDialogStage());
			return false;
		}

		// validation for same schedName
		List<ScheduleEncodingReference> getAllScheduleByClientReference = this.mainApplication
				.getScheduleEncodingReferenceMain().getAllScheduleByClientCode(this.comboBoxClient.getValueObject());

		if (modificationType == ModificationType.ADD) {

			for (ScheduleEncodingReference scheduleEncodingReference : getAllScheduleByClientReference) {
				if (this.textFieldScheduleName.getText().compareTo(scheduleEncodingReference.getScheduleName()) == 0) {
					this.validatedTextFieldTotalMinList.forEach(textField -> {
						textField.setStyle("-fx-text-box-border: black;");
					});
					AlertUtil.showInformationAlert("Schedule Name already exist.", this.getDialogStage());
					return false;
				}
			}
		}

		if (modificationType == ModificationType.EDIT) {
			try {
				if (this.isWithNoDataChanges()) {
					if (this.isWithNewSelectedDays()) {
						return true;
					}
					for (ValidatedTextField textFieldTime : this.validatedTextFieldTimeList) {
						if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textFieldTime)) {
							textFieldTime.setStyle("");
						}
					}

					for (ValidatedTextField textFieldLunch : this.removedValidatedTextFieldLunchList) {
						if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textFieldLunch)) {
							textFieldLunch.setStyle("");
						}
					}

					this.textFieldScheduleName.setStyle("-fx-text-box-border: black;");
					this.validatedTextFieldTotalMinList.forEach(textField -> {
						textField.setStyle("-fx-text-box-border: black;");
					});

					AlertUtil.showNoChangesAlert(this.getDialogStage());
					return false;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		this.textFieldScheduleName.setStyle("-fx-text-box-border: black;");
		this.validatedTextFieldTotalMinList.forEach(textField -> {
			textField.setStyle("-fx-text-box-border: black;");
		});
		this.validatedTextFieldTimeList.forEach(p -> {
			p.setStyle("");
		});
		return true;
	}

	public boolean isWithNewSelectedDays() {
		List<String> newSelectedCheckbox = new ArrayList<>();
		this.checkBoxList.clear();
		this.setCheckboxList();
		
		for (CheckBox checkBox : checkBoxList) {
			if (checkBox.isSelected()) {
				newSelectedCheckbox.add(checkBox.getText());
			}
		}

		if (newSelectedCheckbox.size() > preSelectedCheckboxList.size()) {
			return true;
		}

		if (newSelectedCheckbox.size() == preSelectedCheckboxList.size()) {
			if (!preSelectedCheckboxList.containsAll(newSelectedCheckbox)) {
				return true;
			}
		}

		return false;
	}

	public boolean isWithNoDataChanges() throws ParseException {
		Boolean withNoChanges = true;
		List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();

		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
		scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.objectToModify.getScheduleName(),
						this.mainApplication.getClient());

		if (scheduleEncodingReference != null) {
			scheduleEncodingList = this.mainApplication
					.getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference());
		}

		for (ScheduleEncoding scheduleEncoding : scheduleEncodingList) {

			if (this.textFieldOffset.getText().compareTo(String.valueOf(scheduleEncoding.getOffsetAllowed())) != 0) {
				withNoChanges = false;
			}

			Integer isBreakExempted = this.checkBoxBreakExempted.isSelected() == true ? 1 : 0;
			if (isBreakExempted.compareTo(scheduleEncoding.getIsBreakExempted()) != 0) {
				withNoChanges = false;
			}

			switch (scheduleEncoding.getScheduleDay()) {
			case "Monday":
				if (FieldValidator.textFieldValidate(this.validatedTextFieldMonList)) {
					if (!this.isWithNoChangesField(scheduleEncoding, this.validatedTextFieldMonList)) {
						withNoChanges = false;
					}

				} else {
					this.mainApplication.getScheduleEncodingToDeleteList().add(scheduleEncoding);
					withNoChanges = false;
				}
				break;
			case "Tuesday":
				if (FieldValidator.textFieldValidate(this.validatedTextFieldTuesList)) {
					if (!this.isWithNoChangesField(scheduleEncoding, this.validatedTextFieldTuesList)) {
						withNoChanges = false;
					}
				} else {
					this.mainApplication.getScheduleEncodingToDeleteList().add(scheduleEncoding);
					withNoChanges = false;
				}
				break;
			case "Wednesday":
				if (FieldValidator.textFieldValidate(this.validatedTextFieldWedList)) {
					if (!this.isWithNoChangesField(scheduleEncoding, this.validatedTextFieldWedList)) {
						withNoChanges = false;
					}
				} else {
					this.mainApplication.getScheduleEncodingToDeleteList().add(scheduleEncoding);
					withNoChanges = false;
				}
				break;
			case "Thursday":
				if (FieldValidator.textFieldValidate(this.validatedTextFieldThursList)) {
					if (!this.isWithNoChangesField(scheduleEncoding, this.validatedTextFieldThursList)) {
						withNoChanges = false;
					}
				} else {
					this.mainApplication.getScheduleEncodingToDeleteList().add(scheduleEncoding);
					withNoChanges = false;
				}
				break;
			case "Friday":
				if (FieldValidator.textFieldValidate(this.validatedTextFieldFriList)) {
					if (!this.isWithNoChangesField(scheduleEncoding, this.validatedTextFieldFriList)) {
						withNoChanges = false;
					}
				} else {
					this.mainApplication.getScheduleEncodingToDeleteList().add(scheduleEncoding);
					withNoChanges = false;
				}
				break;
			case "Saturday":
				if (FieldValidator.textFieldValidate(this.validatedTextFieldSatList)) {
					if (!this.isWithNoChangesField(scheduleEncoding, this.validatedTextFieldSatList)) {
						withNoChanges = false;
					}
				} else {
					this.mainApplication.getScheduleEncodingToDeleteList().add(scheduleEncoding);
					withNoChanges = false;
				}
				break;
			case "Sunday":
				if (FieldValidator.textFieldValidate(this.validatedTextFieldSunList)) {
					if (!this.isWithNoChangesField(scheduleEncoding, this.validatedTextFieldSunList)) {
						withNoChanges = false;
					}
				} else {
					this.mainApplication.getScheduleEncodingToDeleteList().add(scheduleEncoding);
					withNoChanges = false;
				}
				break;
			default:
				break;
			}
		}
		return withNoChanges;
	}

	public boolean isWithNoChangesField(ScheduleEncoding scheduleEncoding,
			List<ValidatedTextField> validatedTextFieldList) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		boolean noChangesField = true;
		if (!this.mainApplication.getIsBreakExempted()) {
			if (!new Time(formatter.parse(validatedTextFieldList.get(0).getText()).getTime())
					.equals(scheduleEncoding.getTimeIn())
					|| !new Time(formatter.parse(validatedTextFieldList.get(1).getText()).getTime())
							.equals(scheduleEncoding.getTimeOut())
					|| !new Time(formatter.parse(validatedTextFieldList.get(2).getText()).getTime())
							.equals(scheduleEncoding.getLunchOut())
					|| !new Time(formatter.parse(validatedTextFieldList.get(3).getText()).getTime())
							.equals(scheduleEncoding.getLunchIn())
					|| !(validatedTextFieldList.get(4).getText())
							.equals(String.valueOf(scheduleEncoding.getTotalMinPerDay()))) {
				noChangesField = false;
				return noChangesField;
			}
		} else {
			if (!new Time(formatter.parse(validatedTextFieldList.get(0).getText()).getTime())
					.equals(scheduleEncoding.getTimeIn())
					|| !new Time(formatter.parse(validatedTextFieldList.get(1).getText()).getTime())
							.equals(scheduleEncoding.getTimeOut())
					|| !(validatedTextFieldList.get(2).getText())
							.equals(String.valueOf(scheduleEncoding.getTotalMinPerDay()))) {
				noChangesField = false;
				return noChangesField;
			}
		}

		return noChangesField;
	}

	public boolean isWithSchedNameNoDays() {
		if (!this.textFieldScheduleName.getText().isEmpty() || this.textFieldScheduleName.getText() != null) {
			for (CheckBox checkBox : checkBoxList) {
				if (checkBox.isSelected()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void onSave() {
		this.setHashMapCheckBox();

		for (Map.Entry<String, Boolean> entry : this.selectedDays.entrySet()) {
			if (entry.getValue().equals(true)) {
				this.mainApplication.getObjectToModifySelectedList().add(entry.getKey());
			}
		}

		try {
			this.setObjectToModify();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void checkBoxListener() {
		for (CheckBox checkBox : checkBoxList) {
			checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue) {
					switch (checkBox.getText()) {
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
						this.mainApplication.getEnabledValidatedTextFieldList()
								.addAll(this.validatedTextFieldThursList);
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
	public void onShowEditDialogStage() {
		List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();
		if (this.removedValidatedTextFieldLunchList != null && !this.removedValidatedTextFieldLunchList.isEmpty()) {
			this.removedValidatedTextFieldLunchList.clear();
		}

		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
		scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.objectToModify.getScheduleName(),
						this.mainApplication.getClient());

		if (scheduleEncodingReference != null) {
			scheduleEncodingList = this.mainApplication
					.getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference());
		}

		this.showDetailsSchedule(scheduleEncodingReference, scheduleEncodingList);

	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDetails(ScheduleEncoding scheduleEncoding) {
		// TODO Auto-generated method stub

	}

	public void objectToModifyConfig(List<ValidatedTextField> validatedTextFieldList, String schedDay,
			ScheduleEncodingReference scheduleEncodingReference) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("HH:mm");

		Time timeIn = new Time(System.currentTimeMillis());
		Time timeOut = new Time(System.currentTimeMillis());
		Time lunchIn = new Time(System.currentTimeMillis());
		Time lunchOut = new Time(System.currentTimeMillis());
		Integer totalMinPerDay;

		Integer offsetAllowed = Integer.valueOf(this.textFieldOffset.getText());
		Integer isBreakExempted = this.checkBoxBreakExempted.isSelected() == true ? 1 : 0;

		timeIn = new Time(formatter.parse(validatedTextFieldList.get(0).getText()).getTime());
		timeOut = new Time(formatter.parse(validatedTextFieldList.get(1).getText()).getTime());
		lunchIn = null;
		lunchOut = null;

		if (!this.mainApplication.getIsBreakExempted()) {
			lunchOut = new Time(formatter.parse(validatedTextFieldList.get(2).getText()).getTime());
			lunchIn = new Time(formatter.parse(validatedTextFieldList.get(3).getText()).getTime());
			totalMinPerDay = Integer.valueOf(validatedTextFieldList.get(4).getText());

			this.mainApplication.getObjectToModifySelectedObjectList()
					.add(this.mainApplication.createScheduleEncodingObject(schedDay, timeIn, timeOut, lunchIn, lunchOut,
							offsetAllowed, scheduleEncodingReference, totalMinPerDay, isBreakExempted));

			return;
		}
		totalMinPerDay = Integer.valueOf(validatedTextFieldList.get(2).getText());

		this.mainApplication.getObjectToModifySelectedObjectList()
				.add(this.mainApplication.createScheduleEncodingObject(schedDay, timeIn, timeOut, lunchIn, lunchOut,
						offsetAllowed, scheduleEncodingReference, totalMinPerDay, isBreakExempted));
	}

	public void setObjectToModify() throws ParseException {
		// edit
		this.mainApplication.setClient(this.comboBoxClient.getValueObject());
		this.mainApplication.setSchedName(this.textFieldScheduleName.getText());

		ScheduleEncodingReference scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.textFieldScheduleName.getText(),
						this.comboBoxClient.getValueObject());

		// this.setInitialSelectedCheckboxCount();

		for (String schedDay : this.mainApplication.getObjectToModifySelectedList()) {

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
				this.objectToModifyConfig(this.validatedTextFieldThursList, schedDay, scheduleEncodingReference);
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

	public void showDetailsSchedule(ScheduleEncodingReference scheduleEncodingReference,
			List<ScheduleEncoding> scheduleEncodingList) {

		this.comboBoxClient.setDisable(true);

		if (modificationType.equals(ModificationType.ADD)) {
			this.comboBoxClient
					.setValue(this.comboBoxClient.getValue() == "" ? "" : this.mainApplication.getClientName());
			this.textFieldScheduleName.setEditable(true);
			this.textFieldScheduleName.setStyle("");
			this.comboBoxClient.setStyle("");
			this.checkBoxBreakExempted.setSelected(false);
			this.mainApplication.setIsBreakExempted(false);
		} else {
			this.checkBoxBreakExempted.setDisable(true);
			this.textFieldScheduleName.setEditable(false);
			this.comboBoxClient.setStyle("-fx-text-box-border: black;");
			this.textFieldScheduleName.setStyle("-fx-text-box-border: black;");
		}

		// this.setInitialSelectedCheckboxCount();

		for (ScheduleEncoding scheduleEncoding : scheduleEncodingList) {
			if (scheduleEncoding != null && scheduleEncoding.getPrimaryKey() != null) {

				this.textFieldScheduleName.setText(scheduleEncodingReference.getScheduleName());
				this.textFieldOffset.setText(String.valueOf(scheduleEncoding.getOffsetAllowed()));

				this.checkBoxBreakExempted.setSelected(scheduleEncoding.getIsBreakExempted() == null ? false
						: scheduleEncoding.getIsBreakExempted() == 1 ? true : false);
				this.mainApplication.setIsBreakExempted(scheduleEncoding.getIsBreakExempted() == null ? false
						: scheduleEncoding.getIsBreakExempted() == 1 ? true : false);

				this.comboBoxClient.setValue(this.mainApplication.getClientName());

				switch (scheduleEncoding.getScheduleDay()) {
				case "Monday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldMonList);
					this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldMonList, scheduleEncoding);
					this.checkBoxMonday.setSelected(true);
					break;
				case "Tuesday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldTuesList);
					this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldTuesList, scheduleEncoding);
					this.checkBoxTuesday.setSelected(true);
					break;
				case "Wednesday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldWedList);
					this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldWedList, scheduleEncoding);
					this.checkBoxWednesday.setSelected(true);
					break;
				case "Thursday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldThursList);
					this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldThursList, scheduleEncoding);
					this.checkBoxThursday.setSelected(true);
					break;
				case "Friday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldFriList);
					this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldFriList, scheduleEncoding);
					this.checkBoxFriday.setSelected(true);
					break;
				case "Saturday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldSatList);
					this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldSatList, scheduleEncoding);
					this.checkBoxSaturday.setSelected(true);
					break;
				case "Sunday":
					this.setInitialSelectedTextFieldCount(this.validatedTextFieldSunList);
					this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldSunList, scheduleEncoding);
					this.checkBoxSunday.setSelected(true);
					break;
				default:
					break;
				}
			}
		}

		this.setInitialSelectedCheckboxCount();

	}

	public void setInitialSelectedTextFieldCount(List<ValidatedTextField> textFieldList) {
		if (this.mainApplication.getIsBreakExempted()) {
			for (ValidatedTextField textField : textFieldList) {
				if (this.validatedTextFieldLunchList.contains(textField)) {
					if (!this.removedValidatedTextFieldLunchList.contains(textField)) {
						this.removedValidatedTextFieldLunchList.add(textField);
					}
					textField.setDisable(true);
					textField.clear();
				}
			}

			textFieldList.removeAll(this.removedValidatedTextFieldLunchList);

		} else {
			this.clearListItems();
			this.setFieldList();
			// for (ValidatedTextField textField : textFieldList) {
			// if (this.validatedTextFieldLunchList.contains(textField)) {
			// this.removedValidatedTextFieldLunchList.add(textField);
			// textField.setDisable(false);
			// textField.clear();
			// }
			// }
			if (this.removedValidatedTextFieldLunchList != null && !this.removedValidatedTextFieldLunchList.isEmpty()) {
				this.removedValidatedTextFieldLunchList.forEach(textField -> {
					if (!this.mainApplication.getEnabledValidatedTextFieldList().contains(textField)) {
						this.mainApplication.getEnabledValidatedTextFieldList().add(textField);
						textField.setDisable(false);
						textField.clear();
					}
				});
			}
		}

	}

	public void setInitialSelectedCheckboxCount() {
		if (!this.preSelectedCheckboxList.isEmpty()) {
			this.preSelectedCheckboxList.clear();
		}

		for (CheckBox checkBox : checkBoxList) {
			if (checkBox.isSelected()) {
				this.preSelectedCheckboxList.add(checkBox.getText());
			}
		}

	}

	public void setHashMapCheckBox() {

		this.selectedDays = ImmutableMap.<String, Boolean>builder()
				.put(this.checkBoxMonday.getText(), this.checkBoxMonday.isSelected())
				.put(this.checkBoxTuesday.getText(), this.checkBoxTuesday.isSelected())
				.put(this.checkBoxWednesday.getText(), this.checkBoxWednesday.isSelected())
				.put(this.checkBoxThursday.getText(), this.checkBoxThursday.isSelected())
				.put(this.checkBoxFriday.getText(), this.checkBoxFriday.isSelected())
				.put(this.checkBoxSaturday.getText(), this.checkBoxSaturday.isSelected())
				.put(this.checkBoxSunday.getText(), this.checkBoxSunday.isSelected()).build();

	}

	public void setupFields() {
		this.setFieldList();
		this.setFieldDisable();
		this.startSetFieldListener();
		this.setFieldFormat();

		this.comboBoxClient.setValue(this.mainApplication.getClientName());

		for (ValidatedTextField textField : this.validatedTextFieldTimeList) {
			this.mainApplication.setTimeConfigTextField(textField);
		}

		this.textFieldOffset.setPromptText("in minutes");
		this.textFieldOffset.setFormat("###");

		for (ValidatedTextField textField : this.validatedTextFieldTotalMinList) {
			textField.setPromptText("in minutes");
		}

		// this.mainApplication.setIsBreakExempted(this.checkBoxBreakExempted.isSelected());
	}

	public void setFieldFormat() {
		this.validatedTextFieldTimeList.forEach(textField -> {
			this.mainApplication.setTextFieldFormat(textField);
		});

		this.validatedTextFieldTotalMinList.forEach(textField -> {
			textField.setFormat("####");
			textField.setPromptText("in minutes");
		});
	}

	public void setFieldDisable() {
		this.validatedTextFieldTimeList.forEach(textField -> {
			textField.setDisable(true);
		});

		this.validatedTextFieldTotalMinList.forEach(textField -> {
			textField.setStyle("-fx-text-box-border: black;");
			textField.setDisable(true);
		});

	}

	public void startSetFieldListener() {
		this.setFieldListener(checkBoxMonday, this.validatedTextFieldMonList);
		this.setFieldListener(checkBoxTuesday, this.validatedTextFieldTuesList);
		this.setFieldListener(checkBoxWednesday, this.validatedTextFieldWedList);
		this.setFieldListener(checkBoxThursday, this.validatedTextFieldThursList);
		this.setFieldListener(checkBoxFriday, this.validatedTextFieldFriList);
		this.setFieldListener(checkBoxSaturday, this.validatedTextFieldSatList);
		this.setFieldListener(checkBoxSunday, this.validatedTextFieldSunList);
	}

	public void setFieldListener(CheckBox checkbox, List<ValidatedTextField> textFieldList) {

		checkbox.selectedProperty().addListener((obs, oldValue, newValue) -> {

			// if (this.mainApplication.getIsBreakExempted()) {
			this.setInitialSelectedTextFieldCount(textFieldList);

			// if (this.validatedTextFieldLunchList.contains(textField)) {
			// textField.setDisable(true);
			// textField.clear();
			// }
			// }

			for (ValidatedTextField textField : textFieldList) {
				if (newValue) {
					textField.setDisable(false);
					this.showDetailsSelectedFromEdit(checkbox.getText());

				} else {
					// this.setInitialSelectedTextFieldCount(textFieldList);
					textField.setDisable(true);
					textField.clear();
				}
			}
		});

		this.checkBoxBreakExempted.selectedProperty().addListener((obs, oldValue, newValue) -> {
			this.mainApplication.setIsBreakExempted(newValue);

			// this.setInitialSelectedTextFieldCount(textFieldList);

			if (newValue) {
				if (checkbox.isSelected()) {

					this.setInitialSelectedTextFieldCount(textFieldList);

					this.showDetailsSelectedFromEdit(checkbox.getText());
					this.showTotalMin(textFieldList);

					for (ValidatedTextField textFieldEnabled : this.mainApplication
							.getEnabledValidatedTextFieldList()) {
						// if (this.validatedTextFieldLunchList.contains(textFieldEnabled)) {
						// this.removedValidatedTextFieldLunchList.add(textFieldEnabled);
						// textFieldEnabled.setDisable(true);
						// textFieldEnabled.clear();
						// }

						this.removedValidatedTextFieldLunchList.forEach(p -> {
							if (p.equals(textFieldEnabled)) {
								textFieldEnabled.setDisable(true);
								textFieldEnabled.clear();
							}
						});

					}

					this.mainApplication.getEnabledValidatedTextFieldList()
							.removeAll(this.removedValidatedTextFieldLunchList);

				} else {

				}

			} else {
				// this.setInitialSelectedTextFieldCount(textFieldList);

				if (checkbox.isSelected()) {
					// this.setInitialSelectedTextFieldCount(textFieldList);
					// for (ValidatedTextField textField : textFieldList) {
					// if (this.removedValidatedTextFieldLunchList.contains(textField)) {
					// textField.setDisable(false);
					// }
					// }

					// textFieldList.get(2)
					// .setText(String.valueOf(this.mainApplication.computeTotalMinPerDay(textFieldList)));

					for (ValidatedTextField textFieldEnabled : this.mainApplication
							.getEnabledValidatedTextFieldList()) {
						if (this.validatedTextFieldLunchList.contains(textFieldEnabled)) {
							this.removedValidatedTextFieldLunchList.add(textFieldEnabled);
							textFieldEnabled.setDisable(false);
							// textFieldEnabled.clear();
						}
					}

					this.setInitialSelectedTextFieldCount(textFieldList);
					this.showTotalMin(textFieldList);
				} else {

				}
			}
		});

		// String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
		// if(!validatedTextField.getText().matches(regex)) {
		// return;
		// }

		for (ValidatedTextField validatedTextField : textFieldList) {
			validatedTextField.textProperty().addListener((obs, oldValue, newValue) -> {
				// if (newValue != null && newValue != "") {
				Long totalMinPerDay = this.mainApplication.computeTotalMinPerDay(textFieldList,
						this.mainApplication.getIsBreakExempted());
				if (this.mainApplication.getIsBreakExempted()) {
					textFieldList.get(2).setText(
							totalMinPerDay == null ? "" : totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));

				} else {
					// this.setInitialSelectedTextFieldCount(textFieldList);
					textFieldList.get(4).setText(
							totalMinPerDay == null ? "" : totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));
				}
				// }
			});
		}

	}

	public void showTotalMin(List<ValidatedTextField> textFieldList) {
		// if (newValue != null && newValue != "") {
		Long totalMinPerDay = this.mainApplication.computeTotalMinPerDay(textFieldList,
				this.mainApplication.getIsBreakExempted());
		if (this.mainApplication.getIsBreakExempted()) {
			textFieldList.get(2)
					.setText(totalMinPerDay == null ? "" : totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));

		} else {
			// this.setInitialSelectedTextFieldCount(textFieldList);
			textFieldList.get(4)
					.setText(totalMinPerDay == null ? "" : totalMinPerDay <= 0 ? "0" : String.valueOf(totalMinPerDay));
		}
		// }
	}

	public void showDetailsSelectedFromEdit(String schedDay) {
		List<ScheduleEncoding> scheduleEncodingList = new ArrayList<>();

		ScheduleEncodingReference scheduleEncodingReference = new ScheduleEncodingReference();
		scheduleEncodingReference = this.mainApplication.getScheduleEncodingReferenceMain()
				.getScheduleByScheduleNameAndClientCode(this.objectToModify.getScheduleName(),
						this.mainApplication.getClient());

		if (scheduleEncodingReference != null) {
			scheduleEncodingList = this.mainApplication
					.getAllScheduleByPrikeyReferenceClient(scheduleEncodingReference.getPrimaryKeyReference());
		}

		for (ScheduleEncoding scheduleEncoding : scheduleEncodingList) {
			if (scheduleEncoding != null && scheduleEncoding.getPrimaryKey() != null) {

				switch (schedDay) {
				case "Monday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldMonList,
								scheduleEncoding);
					}
					break;
				case "Tuesday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldTuesList,
								scheduleEncoding);
					}
					break;
				case "Wednesday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldWedList,
								scheduleEncoding);
					}
					break;
				case "Thursday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldThursList,
								scheduleEncoding);
					}
					break;
				case "Friday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldFriList,
								scheduleEncoding);
					}
					break;
				case "Saturday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldSatList,
								scheduleEncoding);
					}
					break;
				case "Sunday":
					if (scheduleEncoding.getScheduleDay().equals(schedDay)) {
						this.mainApplication.showDetailsScheduleConfig(this.validatedTextFieldSunList,
								scheduleEncoding);
					}
					break;
				default:
					break;
				}
			}
		}
	}

	public void setComboBoxClient() {
		this.mainApplication.populateComboBoxClient();
		this.comboBoxClient.setItems(this.mainApplication.getObservableListClient(), p -> p.getClientName());
	}

	@Override
	public void onSetMainApplication() {
		this.setupFields();
		this.checkBoxListener();
		this.setComboBoxClient();

	}

	public void setCheckboxList() {
		this.checkBoxList.add(checkBoxMonday);
		this.checkBoxList.add(checkBoxTuesday);
		this.checkBoxList.add(checkBoxWednesday);
		this.checkBoxList.add(checkBoxThursday);
		this.checkBoxList.add(checkBoxFriday);
		this.checkBoxList.add(checkBoxSaturday);
		this.checkBoxList.add(checkBoxSunday);
	}

	public void setFieldList() {

		this.validatedTextFieldHeaderList.add(textFieldOffset);
		this.validatedTextFieldHeaderList.add(textFieldScheduleName);

		this.comboBoxList.add(comboBoxClient);
		
		this.setCheckboxList();
		
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

		this.validatedTextFieldTimeList.addAll(this.validatedTextFieldMonList);
		this.validatedTextFieldTimeList.addAll(this.validatedTextFieldTuesList);
		this.validatedTextFieldTimeList.addAll(this.validatedTextFieldWedList);
		this.validatedTextFieldTimeList.addAll(this.validatedTextFieldThursList);
		this.validatedTextFieldTimeList.addAll(this.validatedTextFieldFriList);
		this.validatedTextFieldTimeList.addAll(this.validatedTextFieldSatList);
		this.validatedTextFieldTimeList.addAll(this.validatedTextFieldSunList);
		this.validatedTextFieldTimeList.removeAll(this.validatedTextFieldTotalMinList);

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

	public void clearListItems() {
		this.validatedTextFieldMonList.clear();
		this.validatedTextFieldTuesList.clear();
		this.validatedTextFieldWedList.clear();
		this.validatedTextFieldThursList.clear();
		this.validatedTextFieldFriList.clear();
		this.validatedTextFieldSatList.clear();
		this.validatedTextFieldSunList.clear();
	}

	public List<String> getPreSelectedCheckboxList() {
		return preSelectedCheckboxList;
	}

	public void setPreSelectedCheckboxList(List<String> preSelectedCheckboxList) {
		this.preSelectedCheckboxList = preSelectedCheckboxList;
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private CheckBox checkBoxMonday;
	@FXML
	private CheckBox checkBoxTuesday;
	@FXML
	private CheckBox checkBoxWednesday;
	@FXML
	private CheckBox checkBoxThursday;
	@FXML
	private CheckBox checkBoxFriday;
	@FXML
	private CheckBox checkBoxSaturday;
	@FXML
	private CheckBox checkBoxSunday;
	@FXML
	private ValidatedTextField textFieldScheduleName;
	@FXML
	private ValidatedTextField textFieldMonTimeIn;
	@FXML
	private ValidatedTextField textFieldMonTimeOut;
	@FXML
	private ValidatedTextField textFieldTuesTimeIn;
	@FXML
	private ValidatedTextField textFieldTuesTimeOut;
	@FXML
	private ValidatedTextField textFieldWedTimeIn;
	@FXML
	private ValidatedTextField textFieldWedTimeOut;
	@FXML
	private ValidatedTextField textFieldThursTimeIn;
	@FXML
	private ValidatedTextField textFieldThursTimeOut;
	@FXML
	private ValidatedTextField textFieldFriTimeIn;
	@FXML
	private ValidatedTextField textFieldFriTimeOut;
	@FXML
	private ValidatedTextField textFieldSatTimeIn;
	@FXML
	private ValidatedTextField textFieldSatTimeOut;
	@FXML
	private ValidatedTextField textFieldSunTimeIn;
	@FXML
	private ValidatedTextField textFieldSunTimeOut;
	@FXML
	private ValidatedTextField textFieldMonLunchIn;
	@FXML
	private ValidatedTextField textFieldMonLunchOut;
	@FXML
	private ValidatedTextField textFieldTuesLunchIn;
	@FXML
	private ValidatedTextField textFieldTuesLunchOut;
	@FXML
	private ValidatedTextField textFieldWedLunchIn;
	@FXML
	private ValidatedTextField textFieldWedLunchOut;
	@FXML
	private ValidatedTextField textFieldThursLunchIn;
	@FXML
	private ValidatedTextField textFieldThursLunchOut;
	@FXML
	private ValidatedTextField textFieldFriLunchIn;
	@FXML
	private ValidatedTextField textFieldFriLunchOut;
	@FXML
	private ValidatedTextField textFieldSatLunchIn;
	@FXML
	private ValidatedTextField textFieldSatLunchOut;
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
	private AutoFillComboBox<Client> comboBoxClient;
	@FXML
	private ValidatedTextField textFieldOffset;
	@FXML
	private CheckBox checkBoxBreakExempted;
}
