package ph.com.lserv.production.employeescheduleencoding.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.validatedtextfield.ValidatedTextField;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.masterclass.MasterBrowseController;
import ph.com.lbpsc.production.searchv2.Search;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.ProgressUtil;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lbpsc.production.util.TableViewUtil;
import ph.com.lserv.production.employeescheduleencoding.EmployeeScheduleEncodingMain;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;
import ph.com.lserv.production.employeescheduleencodingovertime.model.EmployeeScheduleEncodingOvertime;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;

public class BrowseEmployeeScheduleEncodingController
		extends MasterBrowseController<EmployeeScheduleEncoding, EmployeeScheduleEncodingMain> {
	List<ValidatedTextField> textFieldList = new ArrayList<>();
	List<CheckBox> checkboxList = new ArrayList<>();
	EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();
	Node lastSelectedTable = null;

	List<EmployeeScheduleEncoding> scheduleToBeAddedList = new ArrayList<>();
	List<EmployeeScheduleEncoding> scheduleToBeUpdatedList = new ArrayList<>();

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdd() {
		// this.modifyDetails(new EmployeeScheduleEncoding(), ModificationType.ADD);
	}

	@Override
	public void onEdit() {

	}

	@Override
	public void onDelete() {
		boolean successDelete = false;

		if (this.lastSelectedTable.equals(this.tableViewIrregular)) {

			EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = this.tableViewIrregular
					.getSelectionModel().getSelectedItem().getKey();

			if (employeeScheduleEncodingIrregular != null) {

				List<EmployeeScheduleEncodingOvertime> isWithOvertimeIrregularList = new ArrayList<>();

				isWithOvertimeIrregularList = this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
						.getAllOvertimeIrregularByEmployeeIdAndPrikeyIrregularSchedule(
								employeeScheduleEncodingIrregular, employeeScheduleEncodingIrregular
										.getEmploymentHistory().getEmployee().getEmployeeCode());

				if (isWithOvertimeIrregularList != null && !isWithOvertimeIrregularList.isEmpty()) {

					if (this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
							.deleteOvertimeByPrikeyIrregularSchedule(employeeScheduleEncodingIrregular.getPrimaryKey(),
									employeeScheduleEncodingIrregular.getEmploymentHistory().getEmployee()
											.getEmployeeCode())) {

						successDelete = this.mainApplication.getEmployeeScheduleEncodingIrregularMain()
								.deleteMainObject(employeeScheduleEncodingIrregular);
					}
				} else {
					successDelete = this.mainApplication.getEmployeeScheduleEncodingIrregularMain()
							.deleteMainObject(employeeScheduleEncodingIrregular);
				}
			}
		}

		if (this.lastSelectedTable.equals(this.tableViewOvertime)) {

			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime = this.tableViewOvertime
					.getSelectionModel().getSelectedItem();

			if (employeeScheduleEncodingOvertime != null) {
				successDelete = this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
						.deleteMainObject(employeeScheduleEncodingOvertime);
			}
		}

		if (this.lastSelectedTable.equals(this.tableViewOvertimeIrregular)) {

			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime = this.tableViewOvertimeIrregular
					.getSelectionModel().getSelectedItem();

			if (employeeScheduleEncodingOvertime != null) {
				successDelete = this.mainApplication.getEmployeeScheduleEncodingOvertimeMain()
						.deleteMainObject(employeeScheduleEncodingOvertime);
			}

		}

		if (successDelete) {
			AlertUtil.showSuccessDeleteAlert(this.getParentStage());
		} else {
			AlertUtil.showErrorAlert("Data not deleted.", this.getParentStage());
		}

		this.setTableViews();

	}

	@Override
	public void onSearch() {
		this.mainApplication.getEmployeeObservableList().clear();

		this.mainApplication.getObservableListEmployeesByClientDepartment().forEach(object -> {
			this.mainApplication.getEmployeeObservableList().add(object.getKey());
		});

		Search<Employee> searchDialog = new Search<>();

		if (this.mainApplication.getEmployeeObservableList() == null
				&& this.mainApplication.getEmployeeObservableList().isEmpty()) {
			AlertUtil.showNoDataAlert(this.getParentStage());
			return;
		}

		searchDialog.setObservableListObject(this.mainApplication.getEmployeeObservableList());

		// searchDialog.addTableColumn("Employee ID", "getEmployeeCode");
		// searchDialog.addTableColumn("Employee Name", "getEmployeeFullName");
		//
		// searchDialog.addSearchCriteria("Employee ID", "getEmployeeCode", "######");
		// searchDialog.addSearchCriteria("Employee Name", "getEmployeeFullName", "");

		searchDialog.addTableColumn("Employee ID", "employeeCode");
		searchDialog.addTableColumn("Surname", "surname");
		searchDialog.addTableColumn("First Name", "firstName");
		searchDialog.addTableColumn("Middle Name", "middleName");

		searchDialog.addSearchCriteria("Employee ID", "employeeCode", "######");
		searchDialog.addSearchCriteria("Surname", "surname", "");
		searchDialog.addSearchCriteria("First Name", "firstName", "");
		searchDialog.addSearchCriteria("Middle Name", "middleName", "");

		Employee employee = searchDialog.showSearchDialog("Search Employees", this.getParentStage());

		if (employee != null) {

			Map.Entry<Employee, Boolean> returnedEmployee = this.mainApplication
					.getObservableListEmployeesByClientDepartment().stream()
					.filter(object -> object.getKey().equals(employee)).findFirst().get();

			int index = this.mainApplication.getObservableListEmployeesByClientDepartment().indexOf(returnedEmployee);

			this.setTableViews();
			this.tableViewEmployee.requestFocus();
			this.tableViewEmployee.getSelectionModel().select(index);
			this.tableViewEmployee.scrollTo(index);
		}
	}

	@Override
	public void modifyDetails(EmployeeScheduleEncoding employeeScheduleEncoding, ModificationType modificationType) {
		if (this.mainApplication.showEditRegularEmployeeScheduleEncoding(employeeScheduleEncoding, modificationType)) {
			boolean successSave = false;
			List<EmployeeScheduleEncoding> scheduleToBeAddedList = new ArrayList<>();

			if (modificationType.equals(ModificationType.ADD)) {
				for (EmployeeScheduleEncoding selectedEmployee : this.mainApplication
						.getEmployeeScheduleEncodingToBeSaveList()) {
					EmployeeScheduleEncoding employeeScheduleEncodingExisting = new EmployeeScheduleEncoding();

					if (selectedEmployee.getEmploymentHistory() == null
							|| selectedEmployee.getEmploymentHistory().getEmployee() == null) {
						continue;
					}

					employeeScheduleEncodingExisting = this.mainApplication.getEmployeeScheduleByEmployeeID(
							selectedEmployee.getEmploymentHistory().getEmployee().getEmployeeCode());

					if (employeeScheduleEncodingExisting != null) {

						if (employeeScheduleEncodingExisting.getScheduleEncodingReference().getPrimaryKeyReference()
								.compareTo(selectedEmployee.getScheduleEncodingReference()
										.getPrimaryKeyReference()) != 0) {
							selectedEmployee.setPrimaryKey(employeeScheduleEncodingExisting.getPrimaryKey());
							successSave = this.mainApplication.updateMainObject(selectedEmployee);
						}
					} else {
						scheduleToBeAddedList.add(selectedEmployee);
					}
				}

				if (!scheduleToBeAddedList.isEmpty()) {
					successSave = this.mainApplication.createMultipleData(scheduleToBeAddedList);
				}
			}

			if (successSave) {
				AlertUtil.showSuccessSaveAlert(this.getParentStage());
				this.setTableViews();
				this.checkBoxEmployee.setSelected(false);
				this.mainApplication.populateEmployee();
				this.setTableViewEmployee();
				this.menuButtonAdd.setDisable(true);
			} else {
				AlertUtil.showErrorAlert("Data not saved.", this.getParentStage());
			}

		}
	}

	public void saveRegularSchedule() {
		Task<Void> taskSaveRegularSchedule = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				// TODO Auto-generated method stub
				try {
					int counter = 0;

					if (!mainApplication.getObjectToModifySelectedObjectList().isEmpty()) {
						for (ScheduleEncoding scheduleEncoding : mainApplication
								.getObjectToModifySelectedObjectList()) {

							mainApplication.getScheduleEncodingMain().createMainObject(scheduleEncoding);
							counter += 1;
							updateProgress(ProgressUtil.getProgressValue(counter,
									mainApplication.getObjectToModifySelectedObjectList().size()), 1D);
							updateMessage(ProgressUtil.getMessageValue("New Schedule Entry.\nSaving", counter,
									mainApplication.getObjectToModifySelectedObjectList().size()));
						}
						// if (mainApplication.getScheduleEncodingMain()
						// .createMultipleMainObject(mainApplication.getObjectToModifySelectedObjectList()))
						// {
						// }
					}

					counter = 0;
					for (EmployeeScheduleEncoding employeeScheduleEncoding : mainApplication
							.getEmployeeScheduleEncodingToBeSaveList()) {
						if (employeeScheduleEncoding == null) {
							counter += 1;
							continue;
						}

						if (employeeScheduleEncoding.getEmploymentHistory() == null
								|| employeeScheduleEncoding.getEmploymentHistory().getEmployee() == null) {
							continue;
						}

						modifyDetailsRegular(employeeScheduleEncoding, ModificationType.ADD);
						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter,
								mainApplication.getEmployeeScheduleEncodingToBeSaveList().size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("Please wait.\nModifying", counter,
								mainApplication.getEmployeeScheduleEncodingToBeSaveList().size()));
					}

					counter = 0;
					for (EmployeeScheduleEncoding employeeScheduleEncoding : scheduleToBeAddedList) {
						if (employeeScheduleEncoding.getEmploymentHistory() == null
								|| employeeScheduleEncoding.getEmploymentHistory().getEmployee() == null) {
							counter += 1;
							continue;
						}
						// mainApplication.createMultipleData(scheduleToBeAddedList);

						mainApplication.createMainObject(employeeScheduleEncoding);

						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter, scheduleToBeAddedList.size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("New schedule.\nSaving", counter,
								scheduleToBeAddedList.size()));
					}

					counter = 0;
					for (EmployeeScheduleEncoding employeeScheduleEncoding : scheduleToBeUpdatedList) {
						if (employeeScheduleEncoding.getEmploymentHistory() == null
								|| employeeScheduleEncoding.getEmploymentHistory().getEmployee() == null) {
							counter += 1;
							continue;
						}

						mainApplication.updateMainObject(employeeScheduleEncoding);

						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter, scheduleToBeUpdatedList.size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("Updated schedule.\nSaving", counter,
								scheduleToBeUpdatedList.size()));
					}
					counter = 0;
					Platform.runLater(() -> {
						AlertUtil.showSuccessSaveAlert(getParentStage());
						clearFields();
						setTableViewEmployee();
						checkBoxEmployee.setSelected(false);
						menuButtonAdd.setDisable(true);
						// setTableViews();
						// mainApplication.populateEmployee();
						String errorMessage = "";
						if (!mainApplication.getModifyDetailsErrorAddList().isEmpty()) {
							errorMessage = "Error Saving new schedule for:\r\n";
							for (EmployeeScheduleEncoding employeeScheduleEncoding : mainApplication
									.getModifyDetailsErrorAddList()) {
								String fullName = employeeScheduleEncoding.getEmploymentHistory().getEmployee()
										.getEmployeeFullName();
								errorMessage = errorMessage + "• " + fullName + ".\r\n";
							}
							AlertUtil.showErrorAlert(errorMessage, mainApplication.getPrimaryStage());
						}

						if (!mainApplication.getModifyDetailsErrorUpdateList().isEmpty()) {
							errorMessage = "Error Saving updated schedule for:\r\n";
							for (EmployeeScheduleEncoding employeeScheduleEncoding : mainApplication
									.getModifyDetailsErrorUpdateList()) {
								String fullName = employeeScheduleEncoding.getEmploymentHistory().getEmployee()
										.getEmployeeFullName();
								errorMessage = errorMessage + "• " + fullName + ".\r\n";
							}
							AlertUtil.showErrorAlert(errorMessage, mainApplication.getPrimaryStage());
						}
					});
					mainApplication.getModifyDetailsErrorAddList().clear();
					mainApplication.getModifyDetailsErrorUpdateList().clear();
					scheduleToBeAddedList.clear();
					scheduleToBeUpdatedList.clear();
					mainApplication.getObjectToModifySelectedObjectList().clear();
					mainApplication.getEmployeeScheduleEncodingToBeSaveList().clear();
				} catch (Exception e) {
					// TODO: handle exception
				}

				return null;
			}
		};
		ProgressUtil.showProcessInterface(getParentStage(), taskSaveRegularSchedule, false);
	}

	public void modifyDetailsRegular(EmployeeScheduleEncoding employeeScheduleEncoding,
			ModificationType modificationType) {
		boolean successAdd = true;
		boolean successEdit = true;
		if (modificationType.equals(ModificationType.ADD)) {

			EmployeeScheduleEncoding employeeScheduleEncodingExisting = new EmployeeScheduleEncoding();

			employeeScheduleEncodingExisting = this.mainApplication.getEmployeeScheduleByEmployeeID(
					employeeScheduleEncoding.getEmploymentHistory().getEmployee().getEmployeeCode());

			if (employeeScheduleEncodingExisting != null) {

				if (employeeScheduleEncodingExisting.getScheduleEncodingReference().getPrimaryKeyReference().compareTo(
						employeeScheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference()) != 0) {
					employeeScheduleEncoding.setPrimaryKey(employeeScheduleEncodingExisting.getPrimaryKey());
					successEdit = this.scheduleToBeUpdatedList.add(employeeScheduleEncoding);
				}
			} else {
				successAdd = scheduleToBeAddedList.add(employeeScheduleEncoding);
			}

		}

		if (!successAdd) {
			this.mainApplication.getModifyDetailsErrorAddList().add(employeeScheduleEncoding);
		}

		if (!successEdit) {
			this.mainApplication.getModifyDetailsErrorUpdateList().add(employeeScheduleEncoding);
		}
		// return successSave;
	}

	@FXML
	public void handleRegular() {

		this.mainApplication.getSelectedEmployeesFromTableView();
		if (this.mainApplication.showEditRegularEmployeeScheduleEncoding(new EmployeeScheduleEncoding(),
				ModificationType.ADD)) {
			this.saveRegularSchedule();
		}

	}

	@FXML
	public void handleIrregular() throws IOException {
		ProcessingMessage.showProcessingMessage(this.getParentStage(), "Loading.");

		if (this.isWithSelectedEmployees()) {
			EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();

			if (this.mainApplication.showEditIrregularEmployeeScheduleEncoding(employeeScheduleEncodingIrregular,
					ModificationType.ADD)) {
				this.clearFields();
				this.setTableViewEmployee();

				this.checkBoxEmployee.setSelected(false);
				this.menuButtonAdd.setDisable(true);
			}
			ProcessingMessage.closeProcessingMessage();
			TableViewUtil.refreshTableView(this.tableViewIrregular);
		} else {
			ProcessingMessage.closeProcessingMessage();
		}
	}

	@FXML
	public void handleOvertime() {

		if (this.isWithSelectedEmployees()) {
			EmployeeScheduleEncodingOvertime employeeScheduleEncodingOvertime = new EmployeeScheduleEncodingOvertime();
			this.mainApplication.setIsAddingRegularOvertime(true);

			if (this.mainApplication.showEditOvertimeEmployeeScheduleEncoding(employeeScheduleEncodingOvertime,
					ModificationType.ADD)) {
				this.clearFields();
				this.setTableViewEmployee();
				this.checkBoxEmployee.setSelected(false);
				this.menuButtonAdd.setDisable(true);
			}
			TableViewUtil.refreshTableView(this.tableViewOvertime);
		}
	}

	@FXML
	public void handleOvertimeIrregular() {
		Boolean invalid = false;
		this.mainApplication.setIsAddingRegularOvertime(false);
		this.mainApplication.getSelectedEmployeesFromTableView();
		this.mainApplication.getSelectedIrregularSchedule();

		if (this.isWithSelectedEmployees()) {
			if (this.mainApplication.getSelectedEmployeesObservableList().size() > 1) {
				AlertUtil.showInformationAlert("Add Irregular Overtime Schedule:\nSelect only 1 employee.",
						this.getParentStage());
				return;
			}

			if (this.mainApplication.getSelectedIrregularScheduleObservableList().isEmpty()) {
				AlertUtil.showInformationAlert("Add Irregular Overtime Schedule:\nSelect 1 irregular schedule.",
						this.getParentStage());
				return;
			}

			if (this.mainApplication.getSelectedIrregularScheduleObservableList().size() >= 2) {
				AlertUtil.showInformationAlert("Add Irregular Overtime Schedule:\nSelect only 1 irregular schedule.",
						this.getParentStage());
				return;
			}

			for (Map.Entry<Employee, Boolean> employee : this.mainApplication.getSelectedEmployeesObservableList()) {
				if (!employee.getKey().equals(this.mainApplication.getSelectedEmployee())) {
					AlertUtil.showErrorAlert(
							"Add Irregular Overtime Schedule:\nSelect schedule from the selected employee.",
							this.getParentStage());
					invalid = true;
				}
			}

			if (invalid) {
				return;
			}

			this.mainApplication.setIsAddingRegularOvertime(false);

			if (this.mainApplication.showEditOvertimeEmployeeScheduleEncoding(new EmployeeScheduleEncodingOvertime(),
					ModificationType.ADD)) {
				this.clearFields();
				this.setTableViewEmployee();
				this.checkBoxEmployee.setSelected(false);
				this.menuButtonAdd.setDisable(true);
			}
			TableViewUtil.refreshTableView(this.tableViewOvertime);

		}

	}

	public void handleEditOvertime() {
		// this.mainApplication.getEmployeeScheduleEncodingOvertimeMain().showSetEmployeeScheduleEncodingOvertime(
		// new EmployeeScheduleEncodingOvertime(), ModificationType.ADD,
		// this.getParentStage());
	}

	public void handleRetrieve() {
		this.clearFields();
		this.checkBoxEmployee.setSelected(false);

		if (this.comboBoxClient.getValueObject() != null) {
			if (!this.comboBoxClient.getValueObject().getClientName().isEmpty()
					|| this.comboBoxClient.getValueObject().getClientName() != "") {
				ProcessingMessage.showProcessingMessage(this.getParentStage());
				this.mainApplication.setSelectedClient(this.comboBoxClient.getValueObject());

				if (this.comboBoxDepartment.getValueObject() != null) {
					this.mainApplication.setSelectedDepartment(this.comboBoxDepartment.getValueObject());
				} else {
					this.mainApplication.setSelectedDepartment(null);
				}

				this.mainApplication.populateEmployee();
				this.setTableViewEmployee();

				if (this.mainApplication.getObservableListEmployeesByClientDepartment() != null
						&& !this.mainApplication.getObservableListEmployeesByClientDepartment().isEmpty()) {
					this.tableViewEmployee.getSelectionModel().selectFirst();
				}

				ProcessingMessage.closeProcessingMessage();

			} else {
				AlertUtil.showInformationAlert("Please select a client.", this.getParentStage());
				return;
			}
		} else {
			AlertUtil.showInformationAlert("Please select a client.", this.getParentStage());
			return;
		}
	}

	public boolean isWithSelectedEmployees() {
		this.mainApplication.getSelectedEmployeesObservableList().clear();
		this.mainApplication.getEmployeeScheduleEncodingToBeSaveList().clear();

		this.mainApplication.getObservableListEmployeesByClientDepartment().forEach(row -> {
			if (row.getValue() == true) {
				this.mainApplication.getSelectedEmployeesObservableList().add(row);
			}
		});

		if (this.mainApplication.getSelectedEmployeesObservableList().isEmpty()) {
			AlertUtil.showInformationAlert("Please select any employee/s", this.getParentStage());
			return false;
		}

		return true;
	}

	@Override
	public void showDetails(EmployeeScheduleEncoding employeeScheduleEncoding) {

	}

	public void clearFields() {
		this.mainApplication.getAllRegularScheduleObservableList().clear();
		this.tableViewRegular.setItems(this.mainApplication.getAllRegularScheduleObservableList());

		this.mainApplication.getAllIrregularScheduleObservableList().clear();
		this.tableViewIrregular.setItems(this.mainApplication.getAllIrregularScheduleObservableList());

		this.mainApplication.getAllOvertimeScheduleObservableList().clear();
		this.tableViewOvertime.setItems(this.mainApplication.getAllOvertimeScheduleObservableList());

		this.mainApplication.getAllOvertimeScheduleIrregularObservableList().clear();
		this.tableViewIrregular.setItems(this.mainApplication.getAllIrregularScheduleObservableList());

		if (this.mainApplication.getSelectedClient() == null) {
			this.mainApplication.getEmployeeObservableList().clear();
			this.mainApplication.getObservableListEmployeesByClientDepartment().clear();
			this.tableViewEmployee.setItems(this.mainApplication.getObservableListEmployeesByClientDepartment());
		}

		this.titledPaneRegularSchedule.setText("Regular Schedule");

		// this.setTableViews();
		TableViewUtil.refreshTableView(this.tableViewRegular);
		this.buttonDelete.setDisable(true);
		// this.checkBoxEmployee.setSelected(false);
		// this.textFieldOffset.clear();
	}

	public void setEditableFields() {
		this.textFieldList.forEach(textField -> {
			textField.setEditable(false);
			textField.setStyle("-fx-text-box-border: black;");
		});

	}

	public void setFieldList() {
		// this.textFieldList.add(textFieldOffset);
		// this.textFieldList.add(textFieldClient);
		// this.textFieldList.add(textFieldEmployee);
	}

	public void setFieldListener() {

		this.comboBoxClient.valueObjectProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue != null) {

				this.mainApplication.setSelectedClient(newValue);
				this.comboBoxDepartment.setDisable(false);
				this.mainApplication.populateDepartment();
				this.comboBoxDepartment.setItems(this.mainApplication.getDepartmentByClientObservableList(),
						p -> p.getDepartmentName());

				if (newValue != oldValue && oldValue != null) {
					this.clearFields();
					this.menuButtonAdd.setDisable(true);
					this.buttonSearch.setDisable(true);
					this.checkBoxEmployee.setSelected(false);
					this.mainApplication.getObservableListEmployeesByClientDepartment().clear();
					this.tableViewEmployee
							.setItems(this.mainApplication.getObservableListEmployeesByClientDepartment());
				}

			} else {
				// this.mainApplication.setSelectedClient(null);
				this.mainApplication.getObservableListEmployeesByClientDepartment().clear();
				this.tableViewEmployee.setItems(this.mainApplication.getObservableListEmployeesByClientDepartment());
				this.comboBoxDepartment.setDisable(true);
				this.comboBoxDepartment.setValue(null);
				this.menuButtonAdd.setDisable(true);
				this.buttonSearch.setDisable(true);
				this.checkBoxEmployee.setSelected(false);
				this.clearFields();
			}
		});

		this.comboBoxDepartment.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (this.mainApplication.getSelectedClient() != null) {
				if (newValue != null) {
					this.mainApplication.setSelectedDepartment(newValue);
					this.setTableViews();

					if (newValue != oldValue) {
						this.clearFields();
						this.menuButtonAdd.setDisable(true);
						this.buttonSearch.setDisable(true);
						this.checkBoxEmployee.setSelected(false);
						this.mainApplication.getObservableListEmployeesByClientDepartment().clear();
						this.tableViewEmployee
								.setItems(this.mainApplication.getObservableListEmployeesByClientDepartment());
					}
				} else {
					this.clearFields();
					this.mainApplication.setSelectedDepartment(null);
					this.menuButtonAdd.setDisable(true);
					this.buttonSearch.setDisable(true);
					this.checkBoxEmployee.setSelected(false);
					this.mainApplication.getObservableListEmployeesByClientDepartment().clear();
					this.tableViewEmployee
							.setItems(this.mainApplication.getObservableListEmployeesByClientDepartment());
				}
			}

		});

		this.tableViewIrregular.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				this.lastSelectedTable = this.tableViewIrregular;
			}
		});

		this.tableViewOvertime.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				this.lastSelectedTable = this.tableViewOvertime;
			}
		});

		this.tableViewOvertimeIrregular.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				this.lastSelectedTable = this.tableViewOvertimeIrregular;
			}
		});

		this.tableViewEmployee.focusedProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				this.buttonDelete.setDisable(true);
			}
		});

		this.tableViewRegular.setRowFactory(p -> {
			TableRow<ScheduleEncoding> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				this.buttonDelete.setDisable(true);
			});
			return row;
		});

		this.tableViewIrregular.setRowFactory(p -> {
			TableRow<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> row = new TableRow<>();
			row.setOnMouseClicked(event -> {

				this.buttonDelete.setDisable(false);

				if (event.getClickCount() == 2 && !row.isEmpty()) {

					this.mainApplication.getSelectedEmployeesObservableList().clear();
					this.mainApplication.getSelectedEmployeesObservableList()
							.add(this.tableViewEmployee.getSelectionModel().getSelectedItem());

					// if (this.isWithSelectedEmployees()) {
					ProcessingMessage.showProcessingMessage(this.getParentStage(), "Loading.");
					if (this.mainApplication.showEditIrregularEmployeeScheduleEncoding(
							this.tableViewIrregular.getSelectionModel().getSelectedItem().getKey(),
							ModificationType.EDIT)) {
						this.mainApplication.populateTableViewIrregularObservableList();
						this.setTableViewIrregularSched();
						this.mainApplication.populateEmployee();
						this.setTableViewEmployee();
						this.menuButtonAdd.setDisable(true);
					}
					ProcessingMessage.closeProcessingMessage();
					// }

				}
			});
			return row;
		});

		this.tableViewOvertime.setRowFactory(p -> {
			TableRow<EmployeeScheduleEncodingOvertime> row = new TableRow<>();
			row.setOnMouseClicked(event -> {

				this.buttonDelete.setDisable(false);

				if (event.getClickCount() == 2 && !row.isEmpty()) {

					this.mainApplication.getSelectedEmployeesObservableList().clear();
					this.mainApplication.getSelectedEmployeesObservableList()
							.add(this.tableViewEmployee.getSelectionModel().getSelectedItem());
					this.mainApplication.setIsAddingRegularOvertime(true);
					// if (this.isWithSelectedEmployees()) {
					if (this.mainApplication.showEditOvertimeEmployeeScheduleEncoding(
							this.tableViewOvertime.getSelectionModel().getSelectedItem(), ModificationType.EDIT)) {
						this.mainApplication.populateRegularOvertimeSchedule();
						this.setTableViewOvertimeSched();
						this.mainApplication.populateEmployee();
						this.setTableViewEmployee();
						this.menuButtonAdd.setDisable(true);
					}
					// }
				}
			});
			return row;
		});

		this.tableViewOvertimeIrregular.setRowFactory(p -> {
			TableRow<EmployeeScheduleEncodingOvertime> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				this.buttonDelete.setDisable(false);

				if (event.getClickCount() == 2 && !row.isEmpty()) {
					this.mainApplication.getSelectedEmployeesObservableList().clear();
					this.mainApplication.getSelectedEmployeesObservableList()
							.add(this.tableViewEmployee.getSelectionModel().getSelectedItem());
					this.mainApplication.setIsAddingRegularOvertime(false);

					if (this.mainApplication.showEditOvertimeEmployeeScheduleEncoding(
							this.tableViewOvertimeIrregular.getSelectionModel().getSelectedItem(),
							ModificationType.EDIT)) {
						this.mainApplication.populateRegularOvertimeSchedule();
						this.setTableViewOvertimeSched();
						this.mainApplication.populateEmployee();
						this.setTableViewEmployee();
						this.menuButtonAdd.setDisable(true);
					}
				}
			});
			return row;
		});

		this.tableViewEmployee.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			this.mainApplication.setSelectedEmployee(null);
			this.clearFields();
			if (newValue != null) {
				this.mainApplication
						.setSelectedEmployee(this.tableViewEmployee.getSelectionModel().getSelectedItem().getKey());
				this.setTableViews();
			} else {
				this.clearFields();
			}
		});

		this.tableViewIrregular.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				EmployeeScheduleEncodingIrregular selectedItem = this.tableViewIrregular.getSelectionModel()
						.getSelectedItem().getKey();

				this.mainApplication.populateIrregularOvertimeSchedule(selectedItem);
				this.setTableViewOvertimeIrregularSched();
			} else {
				this.clearFields();
			}

		});

	}

	public void setFields() {
		this.mainApplication.populateClient();
		this.comboBoxClient.setItems(this.mainApplication.getClientObservableList(), p -> p.getClientName());

		// this.textFieldOffset.setStyle("-fx-text-box-border: black;");
		this.comboBoxDepartment.setDisable(true);
		this.buttonDelete.setDisable(true);
		this.menuButtonAdd.setDisable(true);
		this.buttonSearch.setDisable(true);

		this.tableColumnEmployeeCheckbox.setGraphic(this.checkBoxEmployee);
		this.checkBoxEmployee.setTooltip(new Tooltip("Select All\r\nUnselect All"));
		this.tableViewEmployee.setEditable(true);

		this.tableColumnIrregularCheckBox.setGraphic(this.checkBoxIrregular);
		this.checkBoxIrregular.setTooltip(new Tooltip("Select All\r\nUnselect All"));
		this.tableViewIrregular.setEditable(true);

	}

	public void setTableViews() {
		this.setTableViewRegularSched();
		this.setTableViewIrregularSched();
		this.setTableViewOvertimeSched();
		// this.setTableViewOvertimeIrregularSched();
		// this.setTableViewEmployee();
	}

	public void setTableViewEmployee() {
		this.mainApplication.populateObservableListEmployees();

		if (this.mainApplication.getObservableListEmployeesByClientDepartment().size() != 0) {
			this.buttonSearch.setDisable(false);
		} else {
			this.buttonSearch.setDisable(true);
		}

		this.tableViewEmployee.setItems(this.mainApplication.getObservableListEmployeesByClientDepartment());

		this.tableColumnEmployeeId.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey().getEmployeeCode() != null) {
				property.setValue(film.getValue().getKey().getEmployeeCode().toString());
			}
			return property;
		});

		this.tableColumnEmployeeName.setCellValueFactory(film -> {
			SimpleStringProperty property = new SimpleStringProperty();
			if (film.getValue().getKey().getEmployeeFullName() != null) {
				property.setValue(film.getValue().getKey().getEmployeeFullName());
			}
			return property;
		});

		this.checkBoxEmployee.selectedProperty().addListener((obs, oldValue, newValue) -> {
			this.mainApplication.getObservableListEmployeesByClientDepartment().forEach(object -> {
				object.setValue(newValue);
				if (object.getValue().equals(true)) {
					this.menuButtonAdd.setDisable(false);
				} else {
					this.menuButtonAdd.setDisable(true);
				}
			});
			TableViewUtil.refreshTableView(this.tableViewEmployee);
		});

		this.tableColumnEmployeeCheckbox.setCellValueFactory(c -> {
			SimpleBooleanProperty property = new SimpleBooleanProperty();
			property.setValue(c.getValue().getValue());

			final CheckBoxTableCell<String, Boolean> checkBoxTableCell = new CheckBoxTableCell<String, Boolean>();

			checkBoxTableCell.setSelectedStateCallback(new Callback<Integer, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(Integer index) {
					return property;
				}
			});

			property.addListener((obs, oldValue, newValue) -> {
				c.getValue().setValue(newValue);
				this.tableViewEmployee.getSelectionModel().select(c.getValue());
				TableViewUtil.refreshTableView(this.tableViewEmployee);

				if (this.mainApplication.getObservableListEmployeesByClientDepartment()
						.filtered(C -> C.getValue().equals(true)).size() >= 1) {
					this.menuButtonAdd.setDisable(false);
				} else {
					this.menuButtonAdd.setDisable(true);
				}

			});
			return property;
		});

		this.tableColumnEmployeeCheckbox
				.setCellFactory(CheckBoxTableCell.forTableColumn(this.tableColumnEmployeeCheckbox));

	}

	public void setTableViewRegularSched() {
		EmployeeScheduleEncoding employeeScheduleEncoding = new EmployeeScheduleEncoding();
		List<ScheduleEncoding> scheduleEncodingImportList = new ArrayList<>();

		if (this.mainApplication.getSelectedEmployee() != null) {

			scheduleEncodingImportList.addAll(this.mainApplication.getScheduleEncodingMain()
					.getAllScheduleUploadedByEmployeeId(this.mainApplication.getSelectedEmployee().getEmployeeCode()));

			employeeScheduleEncoding = this.mainApplication
					.getEmployeeScheduleByEmployeeID(this.mainApplication.getSelectedEmployee().getEmployeeCode());

			if (employeeScheduleEncoding == null && scheduleEncodingImportList.isEmpty()) {
				this.clearFields();
				return;
			}

			this.mainApplication.populateTableViewRegularObservableList();

			if (!scheduleEncodingImportList.isEmpty()) {
				this.titledPaneRegularSchedule.setText("Regular Schedule: FROM IMPORT");
			} else {
				this.titledPaneRegularSchedule.setText("Regular Schedule : ("
						+ employeeScheduleEncoding.getScheduleEncodingReference().getScheduleName() + ")");
			}

			this.tableViewRegular.setItems(this.mainApplication.getAllRegularScheduleObservableList());
			TableColumnUtil.setColumn(this.tableColumnRegularDay, p -> p.getScheduleDay());
			TableColumnUtil.setColumn(this.tableColumnRegularTimeIn,
					p -> new SimpleDateFormat("HH:mm").format(p.getTimeIn()));
			TableColumnUtil.setColumn(this.tableColumnRegularTimeOut,
					p -> new SimpleDateFormat("HH:mm").format(p.getTimeOut()));
			TableColumnUtil.setColumn(this.tableColumnRegularLunchIn,
					p -> p.getLunchIn() == null ? "" : new SimpleDateFormat("HH:mm").format(p.getLunchIn()));
			TableColumnUtil.setColumn(this.tableColumnRegularLunchOut,
					p -> p.getLunchOut() == null ? "" : new SimpleDateFormat("HH:mm").format(p.getLunchOut()));
			TableColumnUtil.setColumn(this.tableColumnRegularTotalMin, p -> p.getTotalMinPerDay());
			TableColumnUtil.setColumn(this.tableColumnRegularOffset, p -> p.getOffsetAllowed());

			if (employeeScheduleEncoding != null) {
				if (employeeScheduleEncoding.getScheduleEncodingReference() == null) {
					this.mainApplication.getAllRegularScheduleObservableList().clear();
					// this.textFieldOffset.clear();
					this.tableViewRegular.setItems(this.mainApplication.getAllRegularScheduleObservableList());
					return;
				}

				// this.mainApplication.getAllRegularScheduleObservableList().forEach(p -> {
				// this.textFieldOffset.setText(String.valueOf(p.getOffsetAllowed()));
				// });

			}
		}
	}

	public void setTableViewIrregularSched() {
		if (this.mainApplication.getSelectedEmployee() != null) {
			this.mainApplication.populateTableViewIrregularObservableList();

			if (!this.mainApplication.getAllIrregularScheduleObservableList().isEmpty()
					&& this.mainApplication.getAllIrregularScheduleList() != null) {

				ObservableListUtil.sort(this.mainApplication.getAllIrregularScheduleObservableList(),
						p -> new SimpleDateFormat("yyyy-MM-dd").format(p.getKey().getDateSchedule()));

				this.tableViewIrregular.setItems(this.mainApplication.getAllIrregularScheduleObservableList());

				this.tableColumnIrregularDate.setCellValueFactory(film -> {
					SimpleStringProperty property = new SimpleStringProperty();
					if (film.getValue().getKey().getDateSchedule() != null) {
						property.setValue(new SimpleDateFormat("MMM. dd, yyyy")
								.format(film.getValue().getKey().getDateSchedule()));
					}
					return property;
				});

				this.tableColumnIrregularTimeIn.setCellValueFactory(film -> {
					SimpleStringProperty property = new SimpleStringProperty();
					if (film.getValue().getKey().getTimeIn() != null) {
						property.setValue(new SimpleDateFormat("HH:mm").format(film.getValue().getKey().getTimeIn()));
					}
					return property;
				});

				this.tableColumnIrregularTimeOut.setCellValueFactory(film -> {
					SimpleStringProperty property = new SimpleStringProperty();
					if (film.getValue().getKey().getTimeOut() != null) {
						property.setValue(new SimpleDateFormat("HH:mm").format(film.getValue().getKey().getTimeOut()));
					}
					return property;
				});

				this.tableColumnIrregularLunchIn.setCellValueFactory(film -> {
					SimpleStringProperty property = new SimpleStringProperty();
					if (film.getValue().getKey().getLunchIn() != null) {
						property.setValue(new SimpleDateFormat("HH:mm").format(film.getValue().getKey().getLunchIn()));
					}
					return property;
				});

				this.tableColumnIrregularLunchOut.setCellValueFactory(film -> {
					SimpleStringProperty property = new SimpleStringProperty();
					if (film.getValue().getKey().getLunchOut() != null) {
						property.setValue(new SimpleDateFormat("HH:mm").format(film.getValue().getKey().getLunchOut()));
					}
					return property;
				});

				this.tableColumnIrregularTotalMinPerDay.setCellValueFactory(film -> {
					SimpleStringProperty property = new SimpleStringProperty();
					if (film.getValue().getKey().getTotalMinPerDay() != null) {
						property.setValue(String.valueOf(film.getValue().getKey().getTotalMinPerDay()));
					}
					return property;
				});

				this.tableColumnIrregularOffset.setCellValueFactory(film -> {
					SimpleStringProperty property = new SimpleStringProperty();
					if (film.getValue().getKey().getOffsetAllowed() != null) {
						property.setValue(String.valueOf(film.getValue().getKey().getOffsetAllowed()));
					}
					return property;
				});

				this.checkBoxIrregular.selectedProperty().addListener((obs, oldValue, newValue) -> {
					this.mainApplication.getAllIrregularScheduleObservableList().forEach(object -> {
						object.setValue(newValue);
					});
					TableViewUtil.refreshTableView(this.tableViewIrregular);
				});

				this.tableColumnIrregularCheckBox.setCellValueFactory(c -> {
					SimpleBooleanProperty property = new SimpleBooleanProperty();
					property.setValue(c.getValue().getValue());

					final CheckBoxTableCell<String, Boolean> checkBoxTableCell = new CheckBoxTableCell<String, Boolean>();

					checkBoxTableCell.setSelectedStateCallback(new Callback<Integer, ObservableValue<Boolean>>() {
						@Override
						public ObservableValue<Boolean> call(Integer index) {
							return property;
						}
					});

					property.addListener((obs, oldValue, newValue) -> {
						c.getValue().setValue(newValue);
						// this.tableViewIrregular.getSelectionModel().getSelectedItem().getKey();
						this.tableViewIrregular.getSelectionModel().select(c.getValue());
						TableViewUtil.refreshTableView(this.tableViewIrregular);

					});
					return property;
				});

				this.tableColumnIrregularCheckBox
						.setCellFactory(CheckBoxTableCell.forTableColumn(this.tableColumnIrregularCheckBox));

			}

		} else {
			this.clearFields();
		}

	}

	public void setTableViewOvertimeSched() {

		if (this.mainApplication.getSelectedEmployee() != null) {
			this.mainApplication.populateRegularOvertimeSchedule();

			this.tableViewOvertime.setItems(this.mainApplication.getAllOvertimeScheduleObservableList());
			TableColumnUtil.setColumn(this.tableColumnOTName, p -> p.getOvertimeType().getOvertimeName());
			TableColumnUtil.setColumn(this.tableColumnOTStart,
					p -> new SimpleDateFormat("HH:mm").format(p.getTimeStart()));
			TableColumnUtil.setColumn(this.tableColumnOTEnd, p -> new SimpleDateFormat("HH:mm").format(p.getTimeEnd()));

		} else {
			this.clearFields();
		}
	}

	public void setTableViewOvertimeIrregularSched() {

		if (this.mainApplication.getSelectedEmployee() != null) {
			this.tableViewOvertimeIrregular
					.setItems(this.mainApplication.getAllOvertimeScheduleIrregularObservableList());
			TableColumnUtil.setColumn(this.tableColumnOTNameIrregular, p -> p.getOvertimeType().getOvertimeName());
			TableColumnUtil.setColumn(this.tableColumnOTStartIrregular,
					p -> new SimpleDateFormat("HH:mm").format(p.getTimeStart()));
			TableColumnUtil.setColumn(this.tableColumnOTEndIrregular,
					p -> new SimpleDateFormat("HH:mm").format(p.getTimeEnd()));
		} else {
			this.clearFields();
		}

	}

	@Override
	public void onSetMainApplication() {
		this.setFields();
		this.setEditableFields();
		this.setFieldList();
		this.setFieldListener();

	}

	@FXML
	private Button buttonCollect;
	@FXML
	private MenuButton menuButtonAdd;
	@FXML
	private MenuItem menuItemRegular;
	@FXML
	private MenuItem menuItemIrregular;
	@FXML
	private MenuItem menuItemOvertime;
	@FXML
	private MenuItem menuItemOvertimeIrregular;
	@FXML
	private Button buttonDelete;
	@FXML
	private Button buttonSearch;
	// @FXML
	// private ValidatedTextField textFieldOffset;
	@FXML
	private CheckBox checkBoxIrregular = new CheckBox();
	@FXML
	private TableView<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>> tableViewIrregular;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, Boolean> tableColumnIrregularCheckBox;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, String> tableColumnIrregularDate;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, String> tableColumnIrregularTimeIn;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, String> tableColumnIrregularTimeOut;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, String> tableColumnIrregularLunchIn;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, String> tableColumnIrregularLunchOut;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, String> tableColumnIrregularTotalMinPerDay;
	@FXML
	private TableColumn<Map.Entry<EmployeeScheduleEncodingIrregular, Boolean>, String> tableColumnIrregularOffset;
	@FXML
	private CheckBox checkBoxEmployee = new CheckBox();
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, Boolean> tableColumnEmployeeCheckbox;
	@FXML
	private TableView<Map.Entry<Employee, Boolean>> tableViewEmployee;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeId;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeName;
	@FXML
	private TableView<ScheduleEncoding> tableViewRegular;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnRegularDay;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnRegularTimeIn;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnRegularTimeOut;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnRegularLunchIn;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnRegularLunchOut;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnRegularTotalMin;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnRegularOffset;
	@FXML
	private TableView<EmployeeScheduleEncodingOvertime> tableViewOvertime;
	@FXML
	private TableColumn<EmployeeScheduleEncodingOvertime, String> tableColumnOTName;
	@FXML
	private TableColumn<EmployeeScheduleEncodingOvertime, String> tableColumnOTStart;
	@FXML
	private TableColumn<EmployeeScheduleEncodingOvertime, String> tableColumnOTEnd;
	@FXML
	private AutoFillComboBox<Client> comboBoxClient;
	@FXML
	private Button buttonRetrieve;
	@FXML
	private TableColumn<ScheduleEncoding, String> tableColumnIrregularTotalMin;
	@FXML
	private AutoFillComboBox<Department> comboBoxDepartment;
	@FXML
	private TitledPane titledPaneRegularSchedule;
	@FXML
	private TableView<EmployeeScheduleEncodingOvertime> tableViewOvertimeIrregular;
	@FXML
	private TableColumn<EmployeeScheduleEncodingOvertime, String> tableColumnOTNameIrregular;
	@FXML
	private TableColumn<EmployeeScheduleEncodingOvertime, String> tableColumnOTStartIrregular;
	@FXML
	private TableColumn<EmployeeScheduleEncodingOvertime, String> tableColumnOTEndIrregular;

}
