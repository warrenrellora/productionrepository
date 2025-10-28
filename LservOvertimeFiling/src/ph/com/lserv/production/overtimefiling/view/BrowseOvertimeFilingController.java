package ph.com.lserv.production.overtimefiling.view;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterclass.MasterBrowseController;
import ph.com.lbpsc.production.masterinterface.InterfaceBrowseControllerApproval;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.CompareDataUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lserv.production.overtimefiling.OvertimeFilingMain;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefilingapproval.OvertimeFilingApprovalMain;
import ph.com.lserv.production.overtimefilingtrail.model.OvertimeFilingTrail;

public class BrowseOvertimeFilingController extends MasterBrowseController<OvertimeFiling, OvertimeFilingMain>
		implements InterfaceBrowseControllerApproval {

	@Override
	public void configureAccess() {

		if (this.mainApplication.getFormAccessList().isEmpty()) {
			this.buttonPrint.setVisible(false);
			this.buttonPrint.setManaged(false);
		}

		this.mainApplication.getFormAccessList().forEach(p -> {
			if (p.getFormAccessReference().getPrimaryKey() != 6) {
				this.buttonPrint.setVisible(false);
				this.buttonPrint.setManaged(false);
			}
		});

	}

	@Override
	public void onAdd() {
		if (this.isWithApprover()) {
			this.modifyDetails(new OvertimeFiling(), ModificationType.ADD);
		}
		else {
			AlertUtil.showInformationAlert("Cannot file overtime.\nUser has no approver.", this.getParentStage());
			return;
		}
	}

	@Override
	public void onEdit() {
		OvertimeFiling overtimeFiling = this.tableViewOvertimeFiling.getSelectionModel().getSelectedItem();
		this.mainApplication.setOrigValue(overtimeFiling);
		List<OvertimeFilingTrail> overtimeFilingTrailList = this.mainApplication.getOvertimeFilingTrailMain()
				.getDataByUserPrikey(this.mainApplication.getUser().getPrimaryKey());

		if (overtimeFiling == null) {
			AlertUtil.showNoDataAlert(this.getParentStage());
			return;
		}

		for (OvertimeFilingTrail overtimeFilingTrail : overtimeFilingTrailList) {
			if (overtimeFilingTrail.getPrimaryKey() == null) {
				continue;
			}
			if (overtimeFilingTrail.getRecordStatus().getRecordStatusCode().equals(0)
					&& overtimeFilingTrail.getPrimaryKey().equals(overtimeFiling.getPrimaryKey())) {
				AlertUtil.showPendingDataAlert(this.getParentStage());

				return;
			}
		}

		this.modifyDetails(overtimeFiling, ModificationType.EDIT);
	}

	@Override
	public void onDelete() {
		OvertimeFiling overtimeFiling = this.tableViewOvertimeFiling.getSelectionModel().getSelectedItem();
		List<OvertimeFilingTrail> overtimeFilingTrailList = this.mainApplication.getOvertimeFilingTrailMain()
				.getDataByUserPrikey(this.mainApplication.getUser().getPrimaryKey());

		if (overtimeFiling == null) {
			AlertUtil.showNoDataAlert(this.getParentStage());
			return;
		}

		for (OvertimeFilingTrail overtimeFilingTrail2 : overtimeFilingTrailList) {
			if (overtimeFilingTrail2.getPrimaryKey() == null) {
				continue;
			}
			if (overtimeFilingTrail2.getRecordStatus().getRecordStatusCode().equals(0)
					&& overtimeFilingTrail2.getPrimaryKey().equals(overtimeFiling.getPrimaryKey())) {
				AlertUtil.showPendingDataAlert(this.getParentStage());

				return;
			}

		}
		if (this.isWithApprover()) {
			if (AlertUtil.showDeleteQuestionAlertBoolean(this.getParentStage())) {
				boolean successSave = this.mainApplication.deleteMainObject(overtimeFiling);
				if (successSave) {
					AlertUtil.showSuccessfullAlert("Data is now pending for deletion.",
							this.mainApplication.getPrimaryStage());
				} else {
					AlertUtil.showInformationAlert("Data is not deleted", this.mainApplication.getPrimaryStage());
				}
			}
		} else {
			AlertUtil.showInformationAlert("Error saving. User has no approver.", this.mainApplication.getPrimaryStage());
		}

	}

	@Override
	public void onSearch() {

	}

	@Override
	public void modifyDetails(OvertimeFiling overtimeFiling, ModificationType modificationType) {

		if (this.mainApplication.showEditOvertimeFiling(overtimeFiling, modificationType)) {
			if (this.isWithApprover()) {
				boolean successSave = modificationType.equals(ModificationType.ADD)
						? this.mainApplication.createMainObject(overtimeFiling)
						: this.mainApplication.updateMainObject(overtimeFiling);

				if (successSave) {
					AlertUtil.showSuccessSaveTrailAlert(this.mainApplication.getPrimaryStage());
					if (modificationType.equals(ModificationType.ADD)) {
						this.mainApplication.handleEmailNotification(overtimeFiling);
					} else if (overtimeFiling.getPrikeyTrail() != null
							&& modificationType.equals(ModificationType.EDIT)) {
						this.mainApplication.handleEmailNotification(overtimeFiling);
					}
				} else {
					AlertUtil.showErrorAlert("Data not saved", this.mainApplication.getPrimaryStage());
				}
			} else {
				AlertUtil.showInformationAlert("Error saving. User has no approver.", this.mainApplication.getPrimaryStage());
			}
		}
	}

	public boolean isWithApprover() {
		if (this.mainApplication.getUserParentList().isEmpty() || this.mainApplication.getUserParentList() == null) {
			return false;
		}
		return true;
	}

	@Override
	public void showDetails(OvertimeFiling overtimeFiling) {
		if (overtimeFiling == null) {
			this.showEmployeeDetails();
			return;
		}
		Employee employee = overtimeFiling.getUser().getEmploymentHistory().getEmployee();
		SimpleDateFormat DateTimeformat = new SimpleDateFormat("MMM. dd,yyyy | HH:mm:ss");

		this.textFieldFullName.setText(employee.getFullName());
		this.textFieldDepartment
				.setText(this.mainApplication.getUser().getEmploymentHistory().getDepartment() == null ? ""
						: this.mainApplication.getUser().getEmploymentHistory().getDepartment().getDepartmentName());
		this.textFieldEmployeeCode.setText(String.valueOf(employee.getEmployeeCode()));
		this.textFieldFirstName.setText(employee.getFirstName());
		this.textFieldMiddleName.setText(employee.getMiddleName());
		this.textFieldLastName.setText(employee.getSurname());

		this.textFieldOvertimeFrom.setText(DateTimeformat.format(overtimeFiling.getDateTimeFrom()) != null
				? DateTimeformat.format(overtimeFiling.getDateTimeFrom())
				: "");
		this.textFieldOvertimeTo.setText(DateTimeformat.format(overtimeFiling.getDateTimeTo()) != null
				? DateTimeformat.format(overtimeFiling.getDateTimeTo())
				: "");
		this.textFieldWorkDone.setText(overtimeFiling.getWorkDone() != null ? overtimeFiling.getWorkDone() : "");

		this.textFieldDuration
				.setText(overtimeFiling.getTotalOtHours() != null ? overtimeFiling.getTotalOtHours().toString()
						: BigDecimal.ZERO.toString());
		
		if(overtimeFiling.getPrikeyTrail() != null) {
			OvertimeFilingTrail overtimeFilingTrail = new OvertimeFilingTrail();
			overtimeFilingTrail = this.mainApplication.getOvertimeFilingTrailMain().getDataById(overtimeFiling.getPrikeyTrail());
					this.textFieldRemark.setText(overtimeFilingTrail.getRecordStatusDetails().getFirstRemarks());
		}
	}

	@Override
	public void onSetMainApplication() {
		this.buttonDelete.setVisible(false);
		this.buttonDelete.setManaged(false);

		this.tableViewOvertimeFiling.getSelectionModel().selectFirst();

		this.setTableViewItems();
		this.nonEditableFields();
		this.buttonOnKeyPressed();
		this.showDetails(null);

		this.tableViewOvertimeFiling.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						this.getEditPrevValue(newValue);
						this.showDetails(newValue);
					}
				});
		
		this.buttonRemarks.setVisible(false);
		
		this.gridPaneRemarks.setManaged(false);
		this.gridPaneRemarks.setVisible(false);
		
	}

	public void setTableColumnStyle(TableColumn<OvertimeFiling, String> tableColumn) {
		tableColumn.setStyle("-fx-alignment: CENTER");
	}

	public void getEditPrevValue(OvertimeFiling newValue) {
		ObservableList<OvertimeFilingTrail> otFilingTrailValues = FXCollections.observableArrayList();

		otFilingTrailValues.addAll(this.mainApplication.getOvertimeFilingTrailMain()
				.getDataByUserPrikey(this.mainApplication.getUser().getPrimaryKey()));

		OvertimeFilingTrail prevValue = this.mainApplication.getOvertimeFilingTrailMain()
				.getEditPreviousValue(newValue.getPrimaryKey());

		otFilingTrailValues.forEach(otFilingTrail -> {
			if (otFilingTrail.getPrimaryKey() == null) {
				return;
			}
			if (otFilingTrail.getPrimaryKey().equals(newValue.getPrimaryKey())
					&& otFilingTrail.getDateTimeFrom().equals(newValue.getDateTimeFrom())
					&& otFilingTrail.getDateTimeTo().equals(newValue.getDateTimeTo())
					&& otFilingTrail.getRecordProcedure().getRecordProcedureCode().compareTo(2) == 0
					&& otFilingTrail.getRecordStatus().getRecordStatusCode().compareTo(0) == 0) {

				newValue.setDateTimeFrom(prevValue.getDateTimeFrom());
				newValue.setDateTimeTo(prevValue.getDateTimeTo());
				newValue.setWorkDone(prevValue.getWorkDone());
				newValue.setTotalOtHours(prevValue.getTotalOtHours());
				this.showDetails(newValue);
			}
			return;
		});
	}

	public void buttonOnKeyPressed() {
		this.buttonAdd.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.handleAdd();
			}
		});
		this.buttonEdit.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.handleEdit();
			}
		});
		this.buttonDelete.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.handleDelete();
			}
		});
	}

	public void handleCollect() {

		this.setTableViewItems();

		if (this.tableViewOvertimeFiling.getItems().isEmpty()) {
			AlertUtil.showInformationAlert("No approved overtime records found.", this.getParentStage());
		} else {
			ProcessingMessage.showProcessingMessage(this.getParentStage());
			this.tableViewOvertimeFiling.refresh();
			this.tableViewOvertimeFiling.getSelectionModel().selectFirst();
			ProcessingMessage.closeProcessingMessage();

		}
	}

	public void clearLabels() {
		this.textFieldFullName.setText("");
		this.textFieldDepartment.setText("");
		this.textFieldEmployeeCode.setText("");
		this.textFieldFirstName.setText("");
		this.textFieldMiddleName.setText("");
		this.textFieldLastName.setText("");
		this.textFieldOvertimeFrom.setText("");
		this.textFieldOvertimeTo.setText("");
		this.textFieldWorkDone.setText("");
		this.textFieldDuration.setText("");
		this.textFieldRemark.setText("");
	}

	public void showEmployeeDetails() {

		if (this.mainApplication.getUser().getEmploymentHistory() != null) {
			Employee employee = this.mainApplication.getUser().getEmploymentHistory().getEmployee();

			this.textFieldFullName.setText(employee.getFullName());
			this.textFieldDepartment
					.setText(this.mainApplication.getUser().getEmploymentHistory().getDepartment() == null ? ""
							: this.mainApplication.getUser().getEmploymentHistory().getDepartment()
									.getDepartmentName());
			this.textFieldEmployeeCode.setText(String.valueOf(employee.getEmployeeCode()));
			this.textFieldFirstName.setText(employee.getFirstName());
			this.textFieldMiddleName.setText(employee.getMiddleName());
			this.textFieldLastName.setText(employee.getSurname());

		} else {
			this.clearLabels();
		}
	}

	public void setTableViewItems() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		this.mainApplication.populateObservableList();

		this.tableViewOvertimeFiling.setItems(this.mainApplication.getObservableListTimeFiling());
		TableColumnUtil.setColumn(this.tableColumnTimeFrom, p -> timeFormat.format(p.getDateTimeFrom()));
		TableColumnUtil.setColumn(this.tableColumnTimeTo, p -> timeFormat.format(p.getDateTimeTo()));
		TableColumnUtil.setColumn(this.tableColumnDateApplied, p -> dateFormat.format(p.getDateFiled()));
	}

	public void nonEditableFields() {
		this.textFieldDepartment.setEditable(false);
		this.textFieldEmployeeCode.setEditable(false);
		this.textFieldFullName.setEditable(false);
		this.textFieldFirstName.setEditable(false);
		this.textFieldMiddleName.setEditable(false);
		this.textFieldLastName.setEditable(false);
		this.textFieldOvertimeFrom.setEditable(false);
		this.textFieldOvertimeTo.setEditable(false);
		this.textFieldWorkDone.setEditable(false);
		this.textFieldDuration.setEditable(false);
		
		this.textFieldRemark.setEditable(false);
	}

	public void compareData(OvertimeFiling overtimeFiling, OvertimeFiling overtimeFilingTrail,
			Integer recordProcedureCode) {

		try {
			CompareDataUtil.compare(overtimeFiling, overtimeFilingTrail, "user.employmentHistory.employee.employeeCode",
					this.textFieldEmployeeCode, recordProcedureCode);
			CompareDataUtil.compare(overtimeFiling, overtimeFilingTrail, "user.employmentHistory.employee.firstName",
					this.textFieldFirstName, recordProcedureCode);
			CompareDataUtil.compare(overtimeFiling, overtimeFilingTrail, "user.employmentHistory.employee.middleName",
					this.textFieldMiddleName, recordProcedureCode);
			CompareDataUtil.compare(overtimeFiling, overtimeFilingTrail, "user.employmentHistory.employee.surname",
					this.textFieldLastName, recordProcedureCode);
			CompareDataUtil.compare(overtimeFiling, overtimeFilingTrail, "dateTimeFrom", this.textFieldOvertimeFrom,
					recordProcedureCode);
			CompareDataUtil.compare(overtimeFiling, overtimeFilingTrail, "dateTimeTo", this.textFieldOvertimeTo,
					recordProcedureCode);
			CompareDataUtil.compare(overtimeFiling, overtimeFilingTrail, "workDone", this.textFieldWorkDone,
					recordProcedureCode);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException
				| IllegalArgumentException | ParseException e) {
			e.printStackTrace();
		}

	}

	public void hideButtons(Boolean status) {
		this.buttonAdd.setVisible(status);
		this.buttonEdit.setVisible(status);
		this.buttonApproval.setVisible(status);
		this.buttonCollect.setVisible(status);
		this.buttonPrint.setVisible(status);
	}

	@Override
	public void onApproval() {
		OvertimeFilingApprovalMain overtimeFilingApprovalMain = new OvertimeFilingApprovalMain();
		overtimeFilingApprovalMain.setUser(this.mainApplication.getUser());
		overtimeFilingApprovalMain.setPrimaryStage(this.mainApplication.getPrimaryStage());
		overtimeFilingApprovalMain.handleShowApprovalWindow();
	}

	@FXML
	public void handlePrint() {
		this.mainApplication.showPrintOvertimeFiling();
	}

	public void handleRemarks() {
		System.out.println("handleRemarks");
//		this.mainApplication.onRemarks();
	}

	public TableView<OvertimeFiling> getTableViewOvertimeFiling() {
		return tableViewOvertimeFiling;
	}

	public void setTableViewOvertimeFiling(TableView<OvertimeFiling> tableViewOvertimeFiling) {
		this.tableViewOvertimeFiling = tableViewOvertimeFiling;
	}

	public GridPane getGridPaneOvertime() {
		return gridPaneOvertime;
	}

	public void setGridPaneOvertime(GridPane gridPaneOvertime) {
		this.gridPaneOvertime = gridPaneOvertime;
	}

	public ValidatedTextField getTextFieldFullName() {
		return textFieldFullName;
	}

	public void setTextFieldFullName(ValidatedTextField textFieldFullName) {
		this.textFieldFullName = textFieldFullName;
	}

	public ValidatedTextField getTextFieldDepartment() {
		return textFieldDepartment;
	}

	public void setTextFieldDepartment(ValidatedTextField textFieldDepartment) {
		this.textFieldDepartment = textFieldDepartment;
	}

	public ValidatedTextField getTextFieldEmployeeCode() {
		return textFieldEmployeeCode;
	}

	public void setTextFieldEmployeeCode(ValidatedTextField textFieldEmployeeCode) {
		this.textFieldEmployeeCode = textFieldEmployeeCode;
	}

	public ValidatedTextField getTextFieldFirstName() {
		return textFieldFirstName;
	}

	public void setTextFieldFirstName(ValidatedTextField textFieldFirstName, String test) {
		this.textFieldFirstName = textFieldFirstName;
		this.textFieldFirstName.setText(test);
	}

	public ValidatedTextField getTextFieldMiddleName() {
		return textFieldMiddleName;
	}

	public void setTextFieldMiddleName(ValidatedTextField textFieldMiddleName) {
		this.textFieldMiddleName = textFieldMiddleName;
	}

	public ValidatedTextField getTextFieldLastName() {
		return textFieldLastName;
	}

	public void setTextFieldLastName(ValidatedTextField textFieldLastName) {
		this.textFieldLastName = textFieldLastName;
	}

	public ValidatedTextField getTextFieldOvertimeFrom() {
		return textFieldOvertimeFrom;
	}

	public void setTextFieldOvertimeFrom(ValidatedTextField textFieldOvertimeFrom) {
		this.textFieldOvertimeFrom = textFieldOvertimeFrom;
	}

	public ValidatedTextField getTextFieldOvertimeTo() {
		return textFieldOvertimeTo;
	}

	public void setTextFieldOvertimeTo(ValidatedTextField textFieldOvertimeTo) {
		this.textFieldOvertimeTo = textFieldOvertimeTo;
	}

	public ValidatedTextField getTextFieldWorkDone() {
		return textFieldWorkDone;
	}

	public void setTextFieldWorkDone(ValidatedTextField textFieldWorkDone) {
		this.textFieldWorkDone = textFieldWorkDone;
	}

	public ValidatedTextField getTextFieldDuration() {
		return textFieldDuration;
	}

	public void setTextFieldDuration(ValidatedTextField textFieldDuration) {
		this.textFieldDuration = textFieldDuration;
	}

	public ValidatedTextField getTextFieldDateApplied() {
		return textFieldDateApplied;
	}

	public void setTextFieldDateApplied(ValidatedTextField textFieldDateApplied) {
		this.textFieldDateApplied = textFieldDateApplied;
	}

	public Button getButtonRemarks() {
		return buttonRemarks;
	}

	public void setButtonRemarks(Button buttonRemarks) {
		this.buttonRemarks = buttonRemarks;
	}
	
	public GridPane getGridPaneRemarks() {
		return gridPaneRemarks;
	}

	public void setGridPaneRemarks(GridPane gridPaneRemarks) {
		this.gridPaneRemarks = gridPaneRemarks;
	}

	public ValidatedTextField getTextFieldRemark() {
		return textFieldRemark;
	}

	public void setTextFieldRemark(ValidatedTextField textFieldRemark) {
		this.textFieldRemark = textFieldRemark;
	}



	@FXML
	private Button buttonAdd;
	@FXML
	private Button buttonEdit;
	@FXML
	private Button buttonDelete;
	@FXML
	private Button buttonApproval;
	@FXML
	private Button buttonPrint;
	@FXML
	private GridPane gridPaneOvertime;
	@FXML
	private TableView<OvertimeFiling> tableViewOvertimeFiling;
	@FXML
	private TableColumn<OvertimeFiling, String> tableColumnTimeFrom;
	@FXML
	private TableColumn<OvertimeFiling, String> tableColumnTimeTo;
	@FXML
	private TableColumn<OvertimeFiling, String> tableColumnDateApplied;
	@FXML
	private ValidatedTextField textFieldFullName;
	@FXML
	private ValidatedTextField textFieldDepartment;
	@FXML
	private ValidatedTextField textFieldEmployeeCode;
	@FXML
	private ValidatedTextField textFieldFirstName;
	@FXML
	private ValidatedTextField textFieldMiddleName;
	@FXML
	private ValidatedTextField textFieldLastName;
	@FXML
	private ValidatedTextField textFieldOvertimeFrom;
	@FXML
	private ValidatedTextField textFieldOvertimeTo;
	@FXML
	private ValidatedTextField textFieldWorkDone;
	@FXML
	private ValidatedTextField textFieldDuration;
	@FXML
	private ValidatedTextField textFieldDateApplied;
	@FXML
	private Button buttonCollect;
	@FXML
	private Button buttonBypass;
	@FXML
	private Button buttonRemarks;
	
	@FXML
	private GridPane gridPaneRemarks;
	@FXML
	private ValidatedTextField textFieldRemark;

}
