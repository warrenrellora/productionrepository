package ph.com.lbpsc.production.annualizationimportdetails.view;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import ph.com.lbpsc.production.annualizationimportdetails.AnnualizationImportDetailsMain;
import ph.com.lbpsc.production.annualizationimportdetails.model.AnnualizationImportDetails;
import ph.com.lbpsc.production.annualizationitems.model.AnnualizationItems;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterclass.MasterBrowseController;
import ph.com.lbpsc.production.masterinterface.InterfaceBrowseControllerCollect;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.FieldValidator;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.TableViewUtil;

public class BrowseAnnualizationImportDetailsController
		extends MasterBrowseController<AnnualizationImportDetails, AnnualizationImportDetailsMain>
		implements InterfaceBrowseControllerCollect {
	List<FormattedDatePicker> formattedDatePicker = new ArrayList<FormattedDatePicker>();

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdd() {
		if (this.isValid()) {
			AnnualizationImportDetails annualizationImportDetails = new AnnualizationImportDetails();
			annualizationImportDetails.setPayFrom(DateFormatter.toDate(this.formattedDatePickerPayFrom.getValue()));
			annualizationImportDetails.setPayTo(DateFormatter.toDate(this.formattedDatePickerPayTo.getValue()));
			annualizationImportDetails.setAnnualizationItems(this.autoFillComboBoxAnnualizationItems.getValueObject());
			this.modifyDetails(annualizationImportDetails, ModificationType.ADD);
		}
	}

	@Override
	public void onEdit() {
		this.mainApplication.getSelectedAnnualizationImportDetails();
		if (this.mainApplication.getAnnualizationImportDetailsList().size() == 1) {
			this.mainApplication.getAnnualizationImportDetailsList().forEach(annualizationImportDetails -> {
				if (annualizationImportDetails.getAnnualization().getPrimaryKey() == null) {
					this.modifyDetails(annualizationImportDetails, ModificationType.EDIT);
				} else {
					AlertUtil.showErrorAlert("Cannot edit details, record already used for Annualization Process.",
							this.mainApplication.getPrimaryStage());
				}
			});
		} else if (this.mainApplication.getAnnualizationImportDetailsList().size() > 1) {
			AlertUtil.showErrorAlert("Cannot edit multiple records at the same time.",
					this.mainApplication.getPrimaryStage());
		} else if (this.mainApplication.getAnnualizationImportDetailsList().isEmpty()) {
			AlertUtil.showErrorAlert("Please select record to edit.", this.mainApplication.getPrimaryStage());
		}
	}

	@Override
	public void onDelete() {
		this.mainApplication.getSelectedAnnualizationImportDetails();
		if (this.mainApplication.getAnnualizationImportDetailsList() != null
				&& !this.mainApplication.getAnnualizationImportDetailsList().isEmpty()) {
			boolean isWithProcessed = this.mainApplication.isAlreadyProcessed();

			if (!isWithProcessed) {
				if (this.mainApplication.deleteMultipleAnnualizationImportDetails(
						this.mainApplication.getAnnualizationImportDetailsList())) {
					AlertUtil.showSuccessDeleteAlert(this.mainApplication.getPrimaryStage());
				} else {
					AlertUtil.showErrorAlert("Record not saved.", this.mainApplication.getPrimaryStage());
				}
			} else {
				AlertUtil.showErrorAlert("Cannot delete details, record/s already used for Annualization Process.",
						this.mainApplication.getPrimaryStage());
			}
		} else {
			AlertUtil.showErrorAlert("Please select record/s to delete.", this.mainApplication.getPrimaryStage());
		}
	}

	@Override
	public void onSearch() {
		if (this.mainApplication.getObservableListAnnualizationImportDetails() != null
				&& !this.mainApplication.getObservableListAnnualizationImportDetails().isEmpty()) {
			ObservableList<Employee> observableListEmployee = FXCollections.observableArrayList();
			this.mainApplication.getObservableListAnnualizationImportDetails().forEach(C -> {
				observableListEmployee.add(C.getKey().getEmployee());
			});

			Employee employee = this.mainApplication.showSearchEmployee(observableListEmployee,
					this.mainApplication.getPrimaryStage());
			if (employee != null) {
				Entry<AnnualizationImportDetails, Boolean> filteredEmployee = ObservableListUtil.getObject(
						this.mainApplication.getObservableListAnnualizationImportDetails(),
						P -> P.getKey().getEmployee().getEmployeeCode().equals(employee.getEmployeeCode()));
				this.tableViewAnnualizationImportDetails.requestFocus();
				this.tableViewAnnualizationImportDetails.getSelectionModel().select(filteredEmployee);
				this.tableViewAnnualizationImportDetails.scrollTo(filteredEmployee);
			}
		} else {
			AlertUtil.showErrorAlert("No data to search.", this.mainApplication.getPrimaryStage());
		}
	}

	@Override
	public void modifyDetails(AnnualizationImportDetails annualizationImportDetails,
			ModificationType modificationType) {
		if (this.mainApplication.showEditAnnualizationImportDetails(annualizationImportDetails, modificationType)) {
			boolean successfulSave = false;
			if (modificationType == ModificationType.ADD) {
				successfulSave = this.mainApplication.createAnnualizationImportDetails(annualizationImportDetails);
				if (successfulSave) {
					LinkedHashMap<AnnualizationImportDetails, Boolean> hashMap = new LinkedHashMap<>();
					hashMap.put(annualizationImportDetails, false);
					this.mainApplication.getObservableListAnnualizationImportDetails().addAll(hashMap.entrySet());
				}
			} else {
				successfulSave = this.mainApplication.updateAnnualizationImportDetails(annualizationImportDetails);
				TableViewUtil.refreshTableView(this.tableViewAnnualizationImportDetails);
				this.showDetails(annualizationImportDetails);
			}

			if (successfulSave) {
				AlertUtil.showSuccessSaveAlert(this.mainApplication.getPrimaryStage());
				Entry<AnnualizationImportDetails, Boolean> filteredAnnualizationImportDetails = ObservableListUtil
						.getObject(this.mainApplication.getObservableListAnnualizationImportDetails(),
								P -> P.getKey().getPrimaryKey().equals(annualizationImportDetails.getPrimaryKey()));

				this.tableViewAnnualizationImportDetails.requestFocus();
				this.tableViewAnnualizationImportDetails.getSelectionModel().select(filteredAnnualizationImportDetails);
				this.tableViewAnnualizationImportDetails.scrollTo(filteredAnnualizationImportDetails);
			} else {
				AlertUtil.showRecordNotSave("Record not Saved", this.mainApplication.getPrimaryStage());
			}
		}
	}

	@Override
	public void showDetails(AnnualizationImportDetails annualizationImportDetails) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		this.disableButtons(true);
		this.initializeTableViewAnnualizationImportDetails();
	}

	public void initializeTableViewAnnualizationImportDetails() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");

		this.tableViewAnnualizationImportDetails
				.setItems(this.mainApplication.getObservableListAnnualizationImportDetails());

		this.tableColumnCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(this.tableColumnCheckBox));

		this.tableColumnCheckBox.setCellValueFactory(c -> {
			SimpleBooleanProperty property = new SimpleBooleanProperty();
			property.setValue(c.getValue().getValue());

			final CheckBoxTableCell<Client, Boolean> checkBoxTableCell = new CheckBoxTableCell<Client, Boolean>();
			checkBoxTableCell.setSelectedStateCallback(new Callback<Integer, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(Integer index) {
					return property;
				}
			});

			if (c.getValue().getValue()) {
				this.buttonCollect.setDisable(false);

			} else {
				this.buttonCollect.setDisable(true);
				this.mainApplication.getObservableListAnnualizationImportDetails().forEach(F -> {
					if (F.getValue()) {
						this.buttonCollect.setDisable(false);
					}
				});
			}

			property.addListener((observableValue, oldValue, newValue) -> {
				c.getValue().setValue(newValue);
				this.tableViewAnnualizationImportDetails.getSelectionModel().select(c.getValue());
				TableViewUtil.refreshTableView(this.tableViewAnnualizationImportDetails);
			});
			return property;
		});

		this.checkBoxAnnualizationImportDetails.selectedProperty().addListener((observale, oldValue, newValue) -> {
			this.mainApplication.getObservableListAnnualizationImportDetails().forEach(object -> {
				object.setValue(newValue);
			});
			TableViewUtil.refreshTableView(this.tableViewAnnualizationImportDetails);
		});

		this.tableViewAnnualizationImportDetails.setEditable(true);
		this.tableColumnCheckBox.setGraphic(this.checkBoxAnnualizationImportDetails);
		this.checkBoxAnnualizationImportDetails.setTooltip(new Tooltip("Select All\r\nUnselect All"));

		this.tableColumnEmployee.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey() != null && film.getValue().getKey().getEmployee() != null) {
				property.setValue(film.getValue().getKey().getEmployee().getEmployeeCode().toString());
			}
			return property;
		});

		this.tableColumnEmployeeName.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey() != null && film.getValue().getKey().getEmployee() != null) {
				property.setValue(film.getValue().getKey().getEmployee().getFullName());
			}
			return property;
		});

		this.tableColumnAmount.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey() != null && film.getValue().getKey().getAmount() != null) {
				property.setValue(decimalFormat.format(film.getValue().getKey().getAmount()));
			}
			return property;
		});

		this.tableColumnDays.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			DecimalFormat decimalDaysFormatter = new DecimalFormat("###,###,##0.0000");
			if (film.getValue().getKey() != null && film.getValue().getKey().getDays() != null) {
				property.setValue(decimalDaysFormatter.format(film.getValue().getKey().getDays()));
			}
			return property;
		});

		this.tableColumnPayFrom.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey() != null && film.getValue().getKey().getPayFrom() != null) {
				property.setValue(dateFormat.format(film.getValue().getKey().getPayFrom()));
			}
			return property;
		});

		this.tableColumnPayTo.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey() != null && film.getValue().getKey().getPayTo() != null) {
				property.setValue(dateFormat.format(film.getValue().getKey().getPayTo()));
			}
			return property;
		});

		this.tableColumnProcessed.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey() != null && film.getValue().getKey().getAnnualization() != null
					&& film.getValue().getKey().getAnnualization().getPrimaryKey() != null) {
				property.setValue(film.getValue().getKey().getAnnualization().getPrimaryKey() != null ? "Yes" : "No");
			}
			return property;
		});

		this.formattedDatePickerPayFrom.valueProperty().addListener((observable, oldValue, newValue) -> {
			this.disableButtons(true);
			this.mainApplication.getObservableListAnnualizationImportDetails().clear();
			if (newValue != null) {
				this.formattedDatePickerPayFrom.setValue(DateFormatter.toLocalDate(
						DateUtil.getCutOffDate(DateFormatter.toDate(this.formattedDatePickerPayFrom), 0, false)));

				this.formattedDatePickerPayTo.setValue(DateFormatter.toLocalDate(
						DateUtil.getCutOffDate(DateFormatter.toDate(this.formattedDatePickerPayFrom), 23, true)));
			}
		});

		this.formattedDatePickerPayTo.valueProperty().addListener((observable, oldValue, newValue) -> {
			this.disableButtons(true);
			this.mainApplication.getObservableListAnnualizationImportDetails().clear();
			if (newValue != null) {
				this.formattedDatePickerPayTo.setValue(DateFormatter.toLocalDate(
						DateUtil.getCutOffDate(DateFormatter.toDate(this.formattedDatePickerPayTo), 0, true)));
			}
		});

		this.autoFillComboBoxAnnualizationItems.setItems(this.mainApplication.getObservableListAnnualizationItems(),
				P -> P.getItemsName());

		this.autoFillComboBoxAnnualizationItems.valueObjectProperty().addListener((observable, oldValue, newValue) -> {
			this.disableButtons(true);
			this.mainApplication.getObservableListAnnualizationImportDetails().clear();
		});

		this.buttonImport.setMnemonicParsing(true);
		this.buttonImport.setText("_Import");
	}

	public void disableButtons(boolean isDisabled) {
		this.buttonAdd.setDisable(isDisabled);
		this.buttonEdit.setDisable(isDisabled);
		this.buttonDelete.setDisable(isDisabled);
	}

	@Override
	public void onCollect() {
		if (this.isValid()) {
			AnnualizationImportDetails annualizationImportDetails = new AnnualizationImportDetails();
			annualizationImportDetails.setAnnualizationItems(this.autoFillComboBoxAnnualizationItems.getValueObject());
			annualizationImportDetails.setPayFrom(DateFormatter.toDate(this.formattedDatePickerPayFrom.getValue()));
			annualizationImportDetails.setPayTo(DateFormatter.toDate(this.formattedDatePickerPayTo.getValue()));

			this.mainApplication.populateAnnualizationImportDetails(annualizationImportDetails);
			this.disableButtons(false);

			if (this.mainApplication.getObservableListAnnualizationImportDetails() == null
					|| this.mainApplication.getObservableListAnnualizationImportDetails().isEmpty()) {
				AlertUtil.showAlert("", "", "No data found.", AlertType.INFORMATION,
						this.mainApplication.getPrimaryStage());
			}
		}
	}

	@Override
	public void handleCollect() {
		InterfaceBrowseControllerCollect.super.handleCollect();
	}

	public void importAnnualization() {

	}

	private boolean isValid() {
		this.formattedDatePicker.add(this.formattedDatePickerPayFrom);
		this.formattedDatePicker.add(this.formattedDatePickerPayTo);

		if (!FieldValidator.formattedDatePickerValidate(this.formattedDatePicker)
				|| !FieldValidator.validateNodeContent(this.autoFillComboBoxAnnualizationItems)) {
			AlertUtil.showIncompleteDataAlert(this.mainApplication.getPrimaryStage());
			return false;
		}
		return true;
	}

	@FXML
	private FormattedDatePicker formattedDatePickerPayFrom;

	@FXML
	private FormattedDatePicker formattedDatePickerPayTo;

	@FXML
	private AutoFillComboBox<AnnualizationItems> autoFillComboBoxAnnualizationItems;

	@FXML
	private CheckBox checkBoxAnnualizationImportDetails = new CheckBox();

	@FXML
	private TableView<Map.Entry<AnnualizationImportDetails, Boolean>> tableViewAnnualizationImportDetails;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, Boolean> tableColumnCheckBox;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, String> tableColumnEmployee;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, String> tableColumnEmployeeName;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, String> tableColumnAmount;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, String> tableColumnDays;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, String> tableColumnPayFrom;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, String> tableColumnPayTo;

	@FXML
	private TableColumn<Map.Entry<AnnualizationImportDetails, Boolean>, String> tableColumnProcessed;

	@FXML
	private Button buttonImport;

	@FXML
	private void handleImport() {
		this.mainApplication.showUploadAnnualizationImportDetails();
	}
}
