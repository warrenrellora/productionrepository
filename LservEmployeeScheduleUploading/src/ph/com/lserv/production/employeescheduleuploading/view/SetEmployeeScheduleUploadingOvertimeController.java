package ph.com.lserv.production.employeescheduleuploading.view;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;

public class SetEmployeeScheduleUploadingOvertimeController
		extends MasterEditController<EmployeeScheduleUploading, EmployeeScheduleUploadingMain> {

	Integer initialTotalMins = 0;

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		if (this.textFieldTotalMins.getText().isEmpty() || this.comboBoxOvertimeType.getValueObject() == null) {
			AlertUtil.showIncompleteDataAlert(this.getDialogStage());
			this.textFieldTotalMins.requestFocus();
			return false;
		}

		if (this.initialTotalMins.equals(Integer.valueOf(this.textFieldTotalMins.getText()))) {
			AlertUtil.showNoChangesAlert(this.getDialogStage());
			return false;
		}

		return true;
	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub
		if (this.isValid()) {
			EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown = new EmployeeScheduleUploadingOvertimeBreakdown();

			if (modificationType.equals(ModificationType.ADD)) {
				employeeScheduleUploadingOvertimeBreakdown.setOvertimeType(this.comboBoxOvertimeType.getValueObject());
				employeeScheduleUploadingOvertimeBreakdown.setTotalMin(Integer.valueOf(this.textFieldTotalMins.getText()));
				this.mainApplication.getObsListOTBreakdown().add(employeeScheduleUploadingOvertimeBreakdown);
			} else {
				employeeScheduleUploadingOvertimeBreakdown = this.mainApplication.getSelectedOvertimeBreakdown();
				this.mainApplication.getObsListOTBreakdown()
						.remove(this.mainApplication.getSelectedOvertimeBreakdown());
				
				employeeScheduleUploadingOvertimeBreakdown.setTotalMin(Integer.valueOf(this.textFieldTotalMins.getText()));
//				employeeScheduleUploadingOvertimeBreakdown.setEmployeeScheduleUploading(this.objectToModify);
				
				this.mainApplication.getObsListOTBreakdown().add(employeeScheduleUploadingOvertimeBreakdown);
			}

		}
	}

	@Override
	public void onShowEditDialogStage() {
		// TODO Auto-generated method stub
		this.textFieldTotalMins.setFormat("#####");
		this.textFieldTotalMins.setPromptText("total minutes");

		List<OvertimeType> listOvertimeTypeByClient = new ArrayList<>();
		listOvertimeTypeByClient = this.mainApplication.getOvertimeTypeMain()
				.getOvertimeTypeByClientCode(this.objectToModify.getClient().getClientCode());

		List<OvertimeType> listToIterate = new ArrayList<>();
		listToIterate.addAll(listOvertimeTypeByClient);

		for (EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown : this.mainApplication
				.getObsListOTBreakdown()) {
			for (OvertimeType overtimeType : listToIterate) {
				if (overtimeType.getPrimaryKey()
						.equals(employeeScheduleUploadingOvertimeBreakdown.getOvertimeType().getPrimaryKey())
						&& modificationType.equals(ModificationType.ADD)) {
					listOvertimeTypeByClient.remove(overtimeType);
					this.comboBoxOvertimeType.setDisable(false);
				}
			}
		}

		this.mainApplication.getOvertimeTypeByClientObsList().setAll(listOvertimeTypeByClient);
		this.comboBoxOvertimeType.setItems(this.mainApplication.getOvertimeTypeByClientObsList(),
				p -> p.getOvertimeName(), true);

		if (modificationType.equals(ModificationType.EDIT)) {
			this.comboBoxOvertimeType
					.setValue(this.mainApplication.getSelectedOvertimeBreakdown().getOvertimeType().getOvertimeName());
			this.textFieldTotalMins.setText(String.valueOf(this.mainApplication.getTotalMin()));

			this.comboBoxOvertimeType.setDisable(true);
		}

		this.initialTotalMins = this.mainApplication.getTotalMin();

	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDetails(EmployeeScheduleUploading arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		// TODO Auto-generated method stub

	}

	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	@FXML
	private AutoFillComboBox<OvertimeType> comboBoxOvertimeType;
	@FXML
	private ValidatedTextField textFieldTotalMins;

}
