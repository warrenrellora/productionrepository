package ph.com.lbpsc.production.billingcharging.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ph.com.lbpsc.production.billingcharging.BillingChargingMain;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.clientgroup.model.ClientGroup;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.custom.controls.objectselector.ObjectSelector;
import ph.com.lbpsc.production.statementofaccount.model.StatementOfAccount;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ProcessingMessage;

public class FilterBillingController {
	private BillingChargingMain mainApplication;
	private Stage dialogStage;
	private boolean isOkClicked = false;
	private ObservableList<Client> observableListClient = FXCollections.observableArrayList();
	private ObservableList<ClientGroup> observableListClientGroup = FXCollections.observableArrayList();
	private ObservableList<StatementOfAccount> observableListStatementOfAccount = FXCollections.observableArrayList();

	public void setMainApplication(BillingChargingMain mainApplication, Stage dialogStage) {
		this.mainApplication = mainApplication;
		this.dialogStage = dialogStage;

		FieldValidator.validateNodeContent(this.formattedDatePickerBatchDate);

		this.buttonOk.setDisable(true);
		this.formattedDatePickerBatchDate.isFocused();
	}

	public boolean isValid() {
		if (!FieldValidator.validateNodeContent(this.formattedDatePickerBatchDate)) {
			return false;
		}
		return true;
	}

	public void closeDialogStage() {
		this.dialogStage.close();
	}

	@FXML
	public void handleCollect() {
		if (this.isValid()) {
			ProcessingMessage.showProcessingMessage(this.dialogStage, "Collecting Data");
			this.observableListStatementOfAccount.clear();
			this.objectSelectorClient.getObservableListSelectedObject().clear();
			this.objectSelectorClient.getObservableListNotSelectedObject().clear();
			this.objectSelectorClientGroup.getObservableListSelectedObject().clear();
			this.objectSelectorClientGroup.getObservableListNotSelectedObject().clear();
			this.observableListClient.clear();
			this.observableListClientGroup.clear();

			this.observableListStatementOfAccount.addAll(this.mainApplication.getStatementOfAccountMain()
					.getAllStatementOfAccountByUserAndBatchDate(this.mainApplication.getUser(),
							DateFormatter.toDate(this.formattedDatePickerBatchDate.getValue())));

			this.observableListStatementOfAccount.forEach(C -> {
				if (C.getClientGroup() != null) {
					if (!this.observableListClientGroup.contains(C.getClientGroup())) {
						this.observableListClientGroup.add(C.getClientGroup());
					}
				}
				if (C.getClient() != null) {
					if (!this.observableListClient.contains(C.getClient())) {
						this.observableListClient.add(C.getClient());
					}
				}
			});

			this.objectSelectorClient.getObservableListNotSelectedObject().addAll(observableListClient);
			this.objectSelectorClientGroup.getObservableListNotSelectedObject().addAll(observableListClientGroup);
			this.objectSelectorClient.setPropertyName(P -> P.getClientName());
			this.objectSelectorClientGroup.setPropertyName(P -> P.getGroupName());

			ProcessingMessage.closeProcessingMessage();

			if (this.objectSelectorClient.getObservableListSelectedObject() != null
					|| this.objectSelectorClientGroup.getObservableListSelectedObject() != null) {
				this.buttonOk.setDisable(false);
			} else {
				this.buttonOk.setDisable(true);
			}
		}
	}

	@FXML
	public void handleOk() {
		this.mainApplication.getObservableListStatementOfAccount().clear();

		if (this.objectSelectorClient.getObservableListSelectedObject() != null
				&& !this.objectSelectorClient.getObservableListSelectedObject().isEmpty()) {
			this.mainApplication.getObservableListStatementOfAccount().addAll(
					this.mainApplication.getStatementOfAccountMain().getStatementOfAccountByClientListAndBatchDate(
							this.objectSelectorClient.getObservableListSelectedObject(),
							DateFormatter.toDate(this.formattedDatePickerBatchDate.getValue())));
		}

		if (this.objectSelectorClientGroup.getObservableListSelectedObject() != null
				&& !this.objectSelectorClientGroup.getObservableListSelectedObject().isEmpty()) {
			this.mainApplication.getObservableListStatementOfAccount()
					.addAll(this.mainApplication.getStatementOfAccountMain()
							.getMaxStatementOfAccountByClientGroupListAndBatchDate(
									this.objectSelectorClientGroup.getObservableListSelectedObject(),
									DateFormatter.toDate(this.formattedDatePickerBatchDate.getValue())));
		}

		this.setOkClicked(true);
		this.closeDialogStage();
	}

	@FXML
	public void handleCancel() {
		this.setOkClicked(false);
		this.closeDialogStage();
	}

	public boolean isOkClicked() {
		return isOkClicked;
	}

	public void setOkClicked(boolean isOkClicked) {
		this.isOkClicked = isOkClicked;
	}

	@FXML
	private FormattedDatePicker formattedDatePickerBatchDate;
	@FXML
	private ObjectSelector<Client> objectSelectorClient;
	@FXML
	private ObjectSelector<ClientGroup> objectSelectorClientGroup;
	@FXML
	private Button buttonOk;
}
