package ph.com.lbpsc.production.annualizationimportdetails.view;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ph.com.lbpsc.production.annualizationimportdetails.AnnualizationImportDetailsMain;
import ph.com.lbpsc.production.annualizationimportdetails.model.AnnualizationImportDetails;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField.ValidationType;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DecimalFormatter;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;

public class EditAnnualizationImportDetailsController
		extends MasterEditController<AnnualizationImportDetails, AnnualizationImportDetailsMain> {

	@Override
	public boolean isValid() {
		if (!FieldValidator.validateNodeContent(this.validatedTextFieldAmount)
				|| !FieldValidator.validateNodeContent(this.validatedTextFieldDays)
				|| !FieldValidator.validateNodeContent(this.autoFillComboBoxEmployee)) {
			AlertUtil.showIncompleteDataAlert(this.dialogStage);
			return false;
		}

		AnnualizationImportDetails annualizationImportDetails = new AnnualizationImportDetails();
		this.setProperties(annualizationImportDetails);
		if (this.modificationType == ModificationType.EDIT) {
			this.setEditProperties(annualizationImportDetails);
		}

		if (!this.mainApplication.withChangesEmployee(this.objectToModify, annualizationImportDetails)) {
			AlertUtil.showNoChangesAlert(this.dialogStage);
			return false;
		}

		if (this.mainApplication.isDuplicate(annualizationImportDetails)) {
			AlertUtil.showDuplicateDataAlert(this.dialogStage);
			return false;
		}
		return true;
	}

	public void setProperties(AnnualizationImportDetails annualizationImportDetails) {
		if (annualizationImportDetails != null) {
			annualizationImportDetails.setEmployee(this.autoFillComboBoxEmployee.getValueObject());
			annualizationImportDetails.setAmount(DecimalFormatter.toDecimal(this.validatedTextFieldAmount.getText()));
			annualizationImportDetails.setDays(this.validatedTextFieldDays.getText() != null
					? DecimalFormatter.toDecimal(this.validatedTextFieldDays.getText()) : BigDecimal.ZERO);
			annualizationImportDetails.setUser(this.mainApplication.getUser());
			annualizationImportDetails.setChangedInComputer(this.mainApplication.getComputerName());
			annualizationImportDetails.setChangedOnDate(this.mainApplication.getDateNow());
		}
	}

	public void setEditProperties(AnnualizationImportDetails annualizationImportDetails) {
		if (annualizationImportDetails != null) {
			annualizationImportDetails.setPayFrom(this.objectToModify.getPayFrom());
			annualizationImportDetails.setPayTo(this.objectToModify.getPayTo());
			annualizationImportDetails.setAnnualizationItems(this.objectToModify.getAnnualizationItems());
		}
	}

	@Override
	public void onSave() {
		this.setProperties(this.objectToModify);
	}

	@Override
	public void onShowEditDialogStage() {
		this.mainApplication.populateEmployee(this.objectToModify.getPayFrom(), this.objectToModify.getPayTo());
		this.autoFillComboBoxEmployee.setItems(this.mainApplication.getObservableListEmployee(), P -> P.getFullName());

		this.showDetails(this.objectToModify);

		if (this.modificationType == ModificationType.EDIT) {
			this.autoFillComboBoxEmployee.setDisable(true);
		}
	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDetails(AnnualizationImportDetails annualizationImportDetails) {
		DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");
		DecimalFormat decimalDaysFormat = new DecimalFormat("###,###,##0.0000");
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		this.labelReference.setText(annualizationImportDetails.getAnnualizationItems() != null
				? annualizationImportDetails.getAnnualizationItems().getItemsName() : "");
		this.labelPayFrom.setText(annualizationImportDetails.getPayFrom() != null
				? dateFormat.format(annualizationImportDetails.getPayFrom()) : "");
		this.labelPayTo.setText(annualizationImportDetails.getPayTo() != null
				? dateFormat.format(annualizationImportDetails.getPayTo()) : "");
		this.autoFillComboBoxEmployee.setValue(annualizationImportDetails.getEmployee() != null
				? annualizationImportDetails.getEmployee().getFullName() : "");
		this.validatedTextFieldAmount.setText(annualizationImportDetails.getAmount() != null
				? decimalFormat.format(annualizationImportDetails.getAmount()) : "0.00");
		this.validatedTextFieldDays.setText(annualizationImportDetails.getDays() != null
				? decimalDaysFormat.format(annualizationImportDetails.getDays()) : "0.00");
	}

	@Override
	public void onSetMainApplication() {
		this.validatedTextFieldAmount.setValidationType(ValidationType.NUMERIC);
		this.validatedTextFieldDays.setValidationType(ValidationType.NUMERIC);

		this.validatedTextFieldAmount.setFormat("#,###,##0.00");
		this.validatedTextFieldDays.setFormat("#0.0000");

		this.validatedTextFieldAmount.setNumericNegative(true);
		this.validatedTextFieldDays.setNumericNegative(true);

		this.validatedTextFieldAmount.focusedProperty().addListener((observable, newValue, oldValue) -> {
			if (newValue) {
				if (FieldValidator.isFieldEmpty(this.validatedTextFieldAmount)) {
					this.validatedTextFieldAmount.setText("0");
				}
			}
		});

		this.validatedTextFieldDays.focusedProperty().addListener((observable, newValue, oldValue) -> {
			if (newValue) {
				if (FieldValidator.isFieldEmpty(this.validatedTextFieldDays)) {
					this.validatedTextFieldDays.setText("0");
				}
			}
		});
	}

	@FXML
	private Label labelReference;

	@FXML
	private Label labelPayFrom;

	@FXML
	private Label labelPayTo;

	@FXML
	private ValidatedTextField validatedTextFieldAmount;

	@FXML
	private ValidatedTextField validatedTextFieldDays;

	@FXML
	private AutoFillComboBox<Employee> autoFillComboBoxEmployee;

}
