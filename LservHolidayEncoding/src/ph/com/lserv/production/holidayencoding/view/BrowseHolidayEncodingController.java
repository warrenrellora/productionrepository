package ph.com.lserv.production.holidayencoding.view;

import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.masterclass.MasterBrowseController;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lserv.production.holidayencoding.HolidayEncodingMain;
import ph.com.lserv.production.holidayencoding.model.HolidayEncoding;

public class BrowseHolidayEncodingController extends MasterBrowseController<HolidayEncoding, HolidayEncodingMain> {
	public SimpleDateFormat dateFormat = new SimpleDateFormat("MMM. dd,yyyy");

	@Override
	public void configureAccess() {
		if (this.mainApplication.getFormAccessList().isEmpty()) {
			if (this.comboBoxClient.getValue() == null || this.comboBoxClient.getValue().compareTo("") == 0) {
				this.buttonEdit.setDisable(true);
				this.buttonDelete.setDisable(true);
			}
		}

		this.mainApplication.getFormAccessList().forEach(p -> {
			if (p.getFormAccessReference().getPrimaryKey() != 6) {
				this.setButtonIsDisable(false);

			}
		});
	}

	@Override
	public void onAdd() {
		this.modifyDetails(new HolidayEncoding(), ModificationType.ADD);
	}

	@Override
	public void onEdit() {
		HolidayEncoding holidayEncoding = this.tableViewHoliday.getSelectionModel().getSelectedItem();

		if (holidayEncoding == null) {
			AlertUtil.showNoDataAlert(this.getParentStage());
			return;
		}

		this.modifyDetails(holidayEncoding, ModificationType.EDIT);
	}

	@Override
	public void onDelete() {
		HolidayEncoding holidayEncoding = this.tableViewHoliday.getSelectionModel().getSelectedItem();

		if (holidayEncoding == null) {
			AlertUtil.showNoDataAlert(this.getParentStage());
			return;
		}

		if (AlertUtil.showDeleteQuestionAlertBoolean(this.getParentStage())) {
			boolean successSave = this.mainApplication.deleteMainObject(holidayEncoding);
			if (successSave) {
				AlertUtil.showSuccessDeleteAlert(this.getParentStage());
			} else {
				AlertUtil.showInformationAlert("Data is not deleted", this.getParentStage());
			}
		}
		this.setTableViewItems();

	}

	@Override
	public void onSearch() {

	}

	@Override
	public void modifyDetails(HolidayEncoding holidayEncoding, ModificationType modificationType) {
		if (this.comboBoxClient.getValueObject() != null) {
			this.mainApplication.setClient(this.comboBoxClient.getValueObject());
		} else {
			this.mainApplication.setClient(null);
		}

		if (this.mainApplication.showEditHolidayEncoding(holidayEncoding, modificationType)) {
			boolean successSave = modificationType.equals(ModificationType.ADD)
					? this.mainApplication.createMainObject(holidayEncoding)
					: this.mainApplication.updateMainObject(holidayEncoding);

			if (successSave) {
				AlertUtil.showSuccessSaveAlert(this.getParentStage());
				this.setTableViewItems();
			} else {
				AlertUtil.showErrorAlert("Data not saved.", this.getParentStage());
			}
		}
	}

	@Override
	public void showDetails(HolidayEncoding holidayEncoding) {

		if (holidayEncoding != null) {

			this.textFieldDescription.setText(holidayEncoding.getDescription());
			this.textFieldDate
					.setText(holidayEncoding.getOverrideDate() == null ? dateFormat.format(holidayEncoding.getDate())
							: dateFormat.format(holidayEncoding.getOverrideDate()));
			this.textFieldFixed.setText(holidayEncoding.getFixed() == 0 ? "No" : "Yes");
			this.textFieldType.setText(holidayEncoding.getHolidayTypeReference().getHolidayType());

		}
	}

	public void setButtonIsDisable(boolean set) {
		this.buttonAdd.setDisable(set);
		this.buttonEdit.setDisable(set);
		this.buttonDelete.setDisable(set);
	}

	@Override
	public void onSetMainApplication() {
		this.setTableViewItems();
		this.setClientComboBox();

		this.comboBoxClient.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			this.setTableViewItems();

			if (newValue != null) {
				this.mainApplication.setClientName(newValue.getClientName());
			}
		});

		this.tableViewHoliday.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue != null) {
				this.showDetails(newValue);

				// if without admin access
				if (this.mainApplication.getFormAccessList().isEmpty()) {
					// if no client
					if (newValue.getClientCode() == null) {
						this.buttonEdit.setDisable(true);
						this.buttonDelete.setDisable(true);
					} else {
						// if with client
						if (newValue.getFixed() == 1) {
							this.buttonEdit.setDisable(true);
							this.buttonDelete.setDisable(true);
						} else {
							this.buttonEdit.setDisable(false);
							this.buttonDelete.setDisable(false);
						}
					}
				} else {
					// if with admin access
					if (newValue.getFixed() == 1) {
						this.buttonEdit.setDisable(true);
						this.buttonDelete.setDisable(true);
					} else {
						this.buttonEdit.setDisable(false);
						this.buttonDelete.setDisable(false);
					}
				}
			}

		});

	}

	public void setClientComboBox() {
		this.mainApplication.populateClient();
		this.comboBoxClient.setItems(this.mainApplication.getObservableListClient(), p -> p.getClientName());
	}

	public void setTableViewItems() {

		if (this.comboBoxClient.getValueObject() == null) {
			this.mainApplication.getHolidays(true, null);
		} else {
			this.mainApplication.getHolidays(false, this.comboBoxClient.getValueObject());
		}

		this.tableViewHoliday.setItems(this.mainApplication.getObservableListHoliday());
		TableColumnUtil.setColumn(this.tableColumnDate,
				o -> dateFormat.format(o.getOverrideDate() != null ? o.getOverrideDate() : o.getDate()));
		TableColumnUtil.setColumn(this.tableColumnDescription, o -> o.getDescription());

	}

	public void clearFields() {
		this.textFieldDate.setText("");
		this.textFieldType.setText("");
		this.textFieldDescription.setText("");
		this.textFieldFixed.setText("");
	}

	@FXML
	public void handleCollect() {
		ProcessingMessage.showProcessingMessage(this.getParentStage());
		this.setTableViewItems();
		this.clearFields();

		if (this.comboBoxClient.getValue() != null) {
			if (this.comboBoxClient.getValue().compareTo("") == 0) {
				if (this.mainApplication.getFormAccessList().isEmpty()) {
					this.buttonEdit.setDisable(true);
					this.buttonDelete.setDisable(true);
				} else {
					this.buttonEdit.setDisable(false);
					this.buttonDelete.setDisable(false);
				}
			}
		}
		ProcessingMessage.closeProcessingMessage();

	}

	@FXML
	public void handleDelete() {
		this.onDelete();
	}

	@FXML
	private Button buttonCollect;
	@FXML
	private Button buttonAdd;
	@FXML
	private Button buttonEdit;
	@FXML
	private Button buttonDelete;
	@FXML
	private GridPane gridPaneOvertime;
	@FXML
	private TableView<HolidayEncoding> tableViewHoliday;
	@FXML
	private TableColumn<HolidayEncoding, String> tableColumnDate;
	@FXML
	private TableColumn<HolidayEncoding, String> tableColumnDescription;
	@FXML
	private AutoFillComboBox<Client> comboBoxClient;
	@FXML
	private ValidatedTextField textFieldDate;
	@FXML
	private ValidatedTextField textFieldDescription;
	@FXML
	private ValidatedTextField textFieldFixed;
	@FXML
	private ValidatedTextField textFieldType;
}
