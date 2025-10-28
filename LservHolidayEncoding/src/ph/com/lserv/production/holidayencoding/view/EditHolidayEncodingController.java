package ph.com.lserv.production.holidayencoding.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lserv.production.holidayencoding.HolidayEncodingMain;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;
import ph.com.lserv.production.holidaytypereference.model.HolidayTypeReference;

public class EditHolidayEncodingController extends MasterEditController<HolidayEncoding, HolidayEncodingMain> {
	ObservableList<HolidayTypeReference> observableListHolidayTypeName = FXCollections.observableArrayList();
	ObservableList<Client> observableListClientName = FXCollections.observableArrayList();
	List<AutoFillComboBox<?>> autoFillComboBoxList = new ArrayList<>();
	List<ValidatedTextField> validatedTextFieldList = new ArrayList<>();
	List<FormattedDatePicker> formatedDatePickerList = new ArrayList<>();

	@Override
	public boolean isValid() {
		if (!FieldValidator.textFieldValidate(this.validatedTextFieldList)
				| !FieldValidator.formattedDatePickerValidate(this.formatedDatePickerList)
				| !FieldValidator.autoFillComboBoxValidate(this.autoFillComboBoxList)) {
			AlertUtil.showIncompleteDataAlert(this.mainApplication.getPrimaryStage());
			return false;
		}

		if (this.mainApplication.getFormAccessList().isEmpty()) {
			if (this.comboBoxClient.getValue() == null || this.comboBoxClient.getValue().compareTo("") == 0) {
				AlertUtil.showIncompleteDataAlert(this.getDialogStage());
				this.comboBoxClient.setStyle("-fx-border-color: red");
				return false;
			}
		}

		if (modificationType.equals(ModificationType.EDIT)) {
			HolidayEncoding holidayEncoding = this.mainApplication
					.getHolidayByPrikey(this.objectToModify.getPrimaryKey());

			if (!this.isWithChanges(holidayEncoding)) {
				AlertUtil.showNoChangesAlert(this.getDialogStage());
				return false;
			}
		} else if (modificationType.equals(ModificationType.ADD)) {
			if (this.comboBoxClient.getValueObject() != null) {
				if (this.isWithDuplicate(
						this.mainApplication.getAllHolidayByClientName(this.comboBoxClient.getValue()))) {
					AlertUtil.showDuplicateDataAlert(this.getDialogStage());
					return false;
				}
			} else {
				Boolean isExistingHoliday = this.mainApplication.getAllHolidayRegular().stream()
						.filter(p -> new SimpleDateFormat("MM-dd").format(p.getDate()).equals(
								new SimpleDateFormat("MM-dd").format(DateFormatter.toDate(this.datePickerDate.getValue()))))
						.findAny().isPresent();

				if (isExistingHoliday) {
					AlertUtil.showDuplicateDataAlert(this.getDialogStage());
					return false;
				}
			}
		}

		return true;
	}

	public boolean isWithDuplicate(List<HolidayEncoding> holidayEncodingList) {

		boolean checkBoxSelected = this.checkBoxFixed.isSelected();
		boolean checkDateDuplicate = false;

		for (HolidayEncoding holidayEncoding : holidayEncodingList) {
			boolean fixedDate = holidayEncoding.getFixed() == 0 ? false : true;

			if (holidayEncoding.getOverrideDate() == null) {
				checkDateDuplicate = new SimpleDateFormat("MM-dd").format(DateFormatter.toDate(this.datePickerDate.getValue()))
						.equals(new SimpleDateFormat("MM-dd").format(holidayEncoding.getDate()));
			} else {
				checkDateDuplicate = new SimpleDateFormat("MM-dd").format(DateFormatter.toDate(this.datePickerDate.getValue()))
						.equals(new SimpleDateFormat("MM-dd").format(holidayEncoding.getOverrideDate()));
			}

			if (checkBoxSelected == fixedDate
					&& this.comboBoxClient.getValueObject().getClientCode().equals(holidayEncoding.getClientCode())
					&& checkDateDuplicate
					&& this.textFieldDescription.getText().equals(holidayEncoding.getDescription()) && this.comboBoxType
							.getValue().equals(holidayEncoding.getHolidayTypeReference().getHolidayType())) {
				return true;
			}
		}

		return false;
	}

	public boolean isWithChanges(HolidayEncoding holidayEncoding) {

		boolean fixedDate = holidayEncoding.getFixed() == 0 ? false : true;
		boolean checkBoxSelected = this.checkBoxFixed.isSelected();
		boolean checkDateDuplicate = false;

		if (holidayEncoding.getOverrideDate() == null) {
			checkDateDuplicate = DateFormatter.toDate(this.datePickerDate.getValue()).equals(holidayEncoding.getDate());
		} else {
			checkDateDuplicate = DateFormatter.toDate(this.datePickerDate.getValue())
					.equals(holidayEncoding.getOverrideDate());
		}

		if (this.mainApplication.getFormAccessList().isEmpty()) {
			//user
			if (checkBoxSelected == fixedDate && checkDateDuplicate
					&& this.textFieldDescription.getText().equals(holidayEncoding.getDescription())) {
				return false;
			}
		} else {
			//admin
			if (checkBoxSelected == fixedDate && checkDateDuplicate
					&& this.textFieldDescription.getText().equals(holidayEncoding.getDescription()) && this.comboBoxType
							.getValue().equals(holidayEncoding.getHolidayTypeReference().getHolidayType())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void onSave() {
		this.objectToModify.setFixed(this.checkBoxFixed.isSelected() == false ? 0 : 1);
		this.objectToModify.setHolidayTypeReference(this.comboBoxType.getValueObject());

		this.objectToModify.setDescription(this.textFieldDescription.getText());

		this.objectToModify.setChangedByUser(
				this.mainApplication.getUser() == null ? null : this.mainApplication.getUser().getUserName());
		this.objectToModify.setChangedOnDate(new Date());
		this.objectToModify.setChangedInComputer(this.mainApplication.getComputerName());
		this.objectToModify.setUser(this.mainApplication.getUser());

		if (this.comboBoxClient.getValueObject() != null) {
			this.objectToModify.setClientCode(this.comboBoxClient.getValueObject().getClientCode());
		} else {
			this.objectToModify.setClientCode(null);
		}

		if (this.objectToModify.getDate() != null) {
			this.objectToModify.setOverrideDate(DateFormatter.toDate(this.datePickerDate.getValue()));
		} else {
			this.objectToModify.setDate(DateFormatter.toDate(this.datePickerDate.getValue()));
		}

	}

	@Override
	public void onShowEditDialogStage() {
		this.showDetails(this.objectToModify);
	}

	@Override
	public void configureAccess() {
		if (this.mainApplication.getFormAccessList().isEmpty()) {
			this.comboBoxType.setDisable(true);
		}

		this.mainApplication.getFormAccessList().forEach(p -> {
			if (p.getFormAccessReference().getPrimaryKey() != 6) {
				this.buttonAdd.setDisable(false);
				this.buttonEdit.setDisable(false);
				this.buttonDelete.setDisable(false);
			}
		});
	}

	@Override
	public void showDetails(HolidayEncoding holidayEncoding) {
		if (holidayEncoding != null && holidayEncoding.getPrimaryKey() != null) {
			this.checkBoxFixed.setSelected(holidayEncoding.getFixed() == 0 ? false : true);
			this.textFieldDescription.setText(holidayEncoding.getDescription());
			this.comboBoxType.setValue(holidayEncoding.getHolidayTypeReference().getHolidayType());
			this.datePickerDate.setValue(DateFormatter.toLocalDate(holidayEncoding.getDate()));

			if (holidayEncoding.getOverrideDate() != null) {
				this.datePickerDate.setValue(DateFormatter.toLocalDate(holidayEncoding.getOverrideDate()));
			}

		}

		if (modificationType.equals(ModificationType.EDIT)) {
			this.comboBoxClient.setDisable(true);

			if (holidayEncoding.getClient() != null) {
				if (!holidayEncoding.getClient().getClientName().isEmpty()) {
					this.comboBoxClient.setValue(holidayEncoding.getClient().getClientName());
				}
			} else {
				this.comboBoxClient.setValue("");
			}

		} else {
			this.comboBoxClient.setDisable(false);
		}

		if (this.mainApplication.getFormAccessList().isEmpty()) {
			HolidayTypeReference holidayTypeReference = this.mainApplication.getHolidayTypeReferenceMain()
					.getHolidayTypeReferenceByPrikey(2);
			this.comboBoxType.setValue(holidayTypeReference.getHolidayType());
		}

	}

	@Override
	public void onSetMainApplication() {
		this.setFieldsData();
		this.setFieldsValidationList();
	}

	public void setFieldsValidationList() {
		this.autoFillComboBoxList.add(this.comboBoxType);
		this.validatedTextFieldList.add(this.textFieldDescription);
		this.formatedDatePickerList.add(this.datePickerDate);
	}

	public void setFieldsData() {
		this.observableListHolidayTypeName.addAll(this.mainApplication.getHolidayTypeReferenceMain().getAllData());
		this.comboBoxType.setItems(this.observableListHolidayTypeName, p -> p.getHolidayType());

		this.observableListClientName
				.addAll(this.mainApplication.getClientMain().getClientByUser(this.mainApplication.getUser()));
		this.comboBoxClient.setItems(this.observableListClientName, p -> p.getClientName());
	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private FormattedDatePicker datePickerDate;
	@FXML
	private AutoFillComboBox<HolidayTypeReference> comboBoxType;
	@FXML
	private AutoFillComboBox<Client> comboBoxClient;
	@FXML
	private ValidatedTextField textFieldDescription;
	@FXML
	private CheckBox checkBoxFixed;

	public ObservableList<HolidayTypeReference> getObservableListHolidayTypeName() {
		return observableListHolidayTypeName;
	}

	public void setObservableListHolidayTypeName(ObservableList<HolidayTypeReference> observableListHolidayTypeName) {
		this.observableListHolidayTypeName = observableListHolidayTypeName;
	}

}
