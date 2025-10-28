package ph.com.lbpsc.production.annualizationimportdetails.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ph.com.lbpsc.production.annualizationimportdetails.AnnualizationImportDetailsMain;
import ph.com.lbpsc.production.annualizationimportdetails.model.AnnualizationImportDetails;
import ph.com.lbpsc.production.annualizationitems.model.AnnualizationItems;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.masterclass.MasterEditController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.FieldValidator;

public class UploadAnnualizationImportDetailsController
		extends MasterEditController<AnnualizationImportDetails, AnnualizationImportDetailsMain> {

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowEditDialogStage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDetails(AnnualizationImportDetails annualizationImportDetails) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		this.autoFillComboBoxAnnualizationItems.setItems(this.mainApplication.getObservableListAnnualizationItems(),
				P -> P.getItemsName());

		this.buttonImportFile.setMnemonicParsing(true);
		this.buttonImportFile.setText("_Import File");

		this.buttonExportTemplate.setMnemonicParsing(true);
		this.buttonExportTemplate.setText("_Export Template");
	}

	@FXML
	private AutoFillComboBox<AnnualizationItems> autoFillComboBoxAnnualizationItems;

	@FXML
	private Button buttonImportFile;

	@FXML
	private Button buttonExportTemplate;

	@FXML
	private void handleImport() {
		if (FieldValidator.validateNodeContent(this.autoFillComboBoxAnnualizationItems)) {
			this.mainApplication.uploadAnnualizationImportDetails(
					this.autoFillComboBoxAnnualizationItems.getValueObject(), this.dialogStage);
		} else {
			AlertUtil.showIncompleteDataAlert(this.dialogStage);
		}
	}

	@FXML
	private void handleExport() {
		this.mainApplication.exportAnnualizationImportDetailsTemplate(this.dialogStage);
	}

}
