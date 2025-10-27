 package ph.com.lserv.production.employeescheduleuploading.view;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import ph.com.lbpsc.production.allowance.model.Allowance;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterclass.MasterBrowseController;
import ph.com.lbpsc.production.miscellaneousadjustmentdetails.model.MiscellaneousAdjustmentDetails;
import ph.com.lbpsc.production.overtime.model.Overtime;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.payroll.model.Payroll;
import ph.com.lbpsc.production.payrollclientconfiguration.model.PayrollClientConfiguration;
import ph.com.lbpsc.production.payrollstatusdetails.model.PayrollStatusDetails;
import ph.com.lbpsc.production.payrolltype.model.PayrollType;
import ph.com.lbpsc.production.postauditpayroll.model.PostAuditPayroll;
import ph.com.lbpsc.production.searchv2.Search;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.ModificationType;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.ProgressUtil;
import ph.com.lbpsc.production.util.TableColumnUtil;
import ph.com.lbpsc.production.util.TableViewUtil;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;

public class BrowseEmployeeScheduleUploadingController
		extends MasterBrowseController<EmployeeScheduleUploading, EmployeeScheduleUploadingMain> {

	List<String> rawDataList = new ArrayList<>();
	List<String> listEmployeeId = new ArrayList<>();
	ObservableList<String> filterObservableList = FXCollections.observableArrayList();

	List<Date> listIrregularSchedDates = new ArrayList<>();
	List<Integer> empIdList = new ArrayList<>();

	List<EmployeeScheduleUploading> detailsList = new ArrayList<>();
	List<EmployeeScheduleUploading> retrievedDataExtractedList = new ArrayList<>();
	List<EmployeeScheduleUploading> entriesByEmployeeList = new ArrayList<>();
	List<EmployeeScheduleUploading> entriesByEmployeeListCopy = new ArrayList<>();
	List<EmployeeScheduleUploading> listDetailsPerDate = new ArrayList<>();
	List<EmployeeScheduleUploading> listRegularSched = new ArrayList<>();
	List<EmployeeScheduleUploading> listIrregularSched = new ArrayList<>();
	List<EmployeeScheduleUploading> listNoSched = new ArrayList<>();
	List<EmployeeScheduleUploading> employeeScheduleUploadingWithRegularSchedList = new ArrayList<>();
	List<EmployeeScheduleUploading> employeeScheduleUploadingWithIrregularSchedList = new ArrayList<>();

	List<Employee> extractedEmployeesList = new ArrayList<>();
	List<Employee> allActiveEmployeeList = new ArrayList<>();

	Client selectedClient = new Client();
	Department selectedDepartment = new Department();
	ObservableList<Client> obsListClient = FXCollections.observableArrayList();
	ObservableList<Department> obsListDepartment = FXCollections.observableArrayList();
	ObservableList<Employee> obsListEmployee = FXCollections.observableArrayList();

	ObservableList<Map.Entry<Employee, Boolean>> observableListMapEmployees = FXCollections.observableArrayList();
	ObservableList<Map.Entry<Employee, Boolean>> observableListMapEmployeesCopy = FXCollections.observableArrayList();

	ObservableList<Map.Entry<Employee, Boolean>> observableListMapValidatedEmployees = FXCollections
			.observableArrayList();
	ObservableList<Map.Entry<Employee, Boolean>> observableListMapNotValidatedEmployees = FXCollections
			.observableArrayList();

	Date payFrom, payTo, prevPayFrom, prevPayTo;
	List<Payroll> processedPayrollList = new ArrayList<>();

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEdit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDelete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSearch() {
		if (this.obsListEmployee == null || this.obsListEmployee.isEmpty()) {
			AlertUtil.showDataNotFound(this.mainApplication.getPrimaryStage());
			return;
		}

		Search<Employee> searchDialog = new Search<>();
		searchDialog.setObservableListObject(this.obsListEmployee);

		// searchDialog.addTableColumn("Name", "fullName");
		searchDialog.addTableColumn("Employee ID", "employeeCode");
		searchDialog.addTableColumn("Surname", "surname");
		searchDialog.addTableColumn("First Name", "firstName");
		searchDialog.addTableColumn("Middle Name", "middleName");

		searchDialog.addSearchCriteria("Employee ID", "employeeCode", "######");
		searchDialog.addSearchCriteria("Surname", "surname", "");
		searchDialog.addSearchCriteria("First Name", "firstName", "");
		searchDialog.addSearchCriteria("Middle Name", "middleName", "");
		// searchDialog.addSearchCriteria("Name", "fullName", "");

		Employee employee = searchDialog.showSearchDialog("Search Employees", this.getParentStage());
		if (employee != null) {

			Map.Entry<Employee, Boolean> returnedEmployee = this.observableListMapEmployees.stream()
					.filter(p -> p.getKey().equals(employee)).findFirst().get();
			int index = this.observableListMapEmployees.indexOf(returnedEmployee);

			this.mainApplication.setSelectedEmployee(returnedEmployee.getKey());
			this.tableViewEmployee.requestFocus();
			this.tableViewEmployee.scrollTo(index);
			this.tableViewEmployee.getSelectionModel().select(index);

			// this.clearFields();
//			this.comboBoxFilter.setValue("All");

			// ProcessingMessage.showProcessingMessage(this.getParentStage(), "Extracting
			// data.");
			// this.configTableViewDetails();
			// ProcessingMessage.closeProcessingMessage();
			this.showEmployeeBiometricsDetails();
			this.getEntriesByEmployee();

		}
	}

	public void setFieldListener() {

		this.tableViewEmployee.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.mainApplication.setSelectedEmployee(newValue.getKey());

				this.tableViewEmployee.getSelectionModel().select(newValue);

				this.entriesByEmployeeListCopy.clear();
				this.listRegularSched.clear();
				this.listIrregularSched.clear();

				this.showEmployeeBiometricsDetails();

				this.setTableViewDetails();

//				this.comboBoxFilter.setValue(null);
			}
		});

		this.tableViewDetails.setRowFactory(p -> {
			TableRow<EmployeeScheduleUploading> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getClickCount() == 2) {
					if (this.mainApplication.showEditEmployeeScheduleUploading(row.getItem(), ModificationType.EDIT)) {
						AlertUtil.showSuccessSaveAlert(this.getParentStage());
						this.setTableViewDetails();
					}
				}
			});
			return row;
		});

		this.comboBoxClient.valueObjectProperty().addListener((obs, oldValue, newValue) -> {

			if (newValue != null) {
				this.selectedClient = newValue;
				this.comboBoxDepartment.setDisable(false);
				this.mainApplication.setSelectedClient(newValue);
				this.setTableViewEmployee();

				ObservableList<String> deptNoList = FXCollections.observableArrayList();

				deptNoList.setAll(this.obsListEmployee.stream().map(p -> p.getDepartment()).distinct()
						.collect(Collectors.toList()));

				this.obsListDepartment.clear();

				deptNoList.forEach(deptNo -> {
					this.obsListDepartment.add(mainApplication.getDepartmentMain().getDepartmentByCode(deptNo));
				});
				this.comboBoxDepartment.setItems(this.obsListDepartment, p -> p.getDepartmentName());
//				this.comboBoxFilter.setValue(null);
			} else {
				this.selectedClient = null;
				this.selectedDepartment = null;
				this.mainApplication.setSelectedClient(null);

				this.setTableViewEmployee();

//				this.comboBoxFilter.setValue(null);
				this.comboBoxDepartment.setValue(null);
				this.comboBoxDepartment.setDisable(true);

				this.obsListDepartment.clear();
				this.comboBoxDepartment.setItems(this.obsListDepartment, p -> p.getDepartmentName());

				this.setTableViewEmployee();
			}

		});

		this.comboBoxDepartment.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedDepartment = newValue;
				this.mainApplication.setSelectedDepartment(newValue);

//				this.comboBoxFilter.setValue(null);

				this.setTableViewEmployee();
				this.getEntriesByEmployee();

				if (newValue != oldValue) {
//					this.comboBoxFilter.setValue(null);

					this.setTableViewEmployee();
					this.getEntriesByEmployee();
				}

			} else {
				this.selectedDepartment = null;
				this.mainApplication.setSelectedDepartment(null);

//				this.comboBoxFilter.setValue(null);

				this.setTableViewEmployee();
				this.getEntriesByEmployee();
			}
		});

//		this.comboBoxFilter.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
//			if (newValue != null && newValue != "" && !newValue.isEmpty()) {
//				switch (newValue) {
//				case "All":
//					// reset
//					this.entriesByEmployeeList.clear();
//					this.entriesByEmployeeList.addAll(this.entriesByEmployeeListCopy);
//					this.entriesByEmployeeList.removeAll(this.listNoSched);
//
//					Platform.runLater(() -> {
//						this.setTableViewDetails();
//					});
//
//					break;
//				case "Regular Schedule":
//
//					this.entriesByEmployeeList.clear();
//					this.entriesByEmployeeList.addAll(this.listRegularSched.stream()
//							.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode()
//									.equals(this.mainApplication.getSelectedEmployee().getEmployeeCode()))
//							.collect(Collectors.toList()));
//
//					Platform.runLater(() -> {
//						this.setTableViewDetails();
//					});
//
//					break;
//				case "Irregular Schedule":
//					this.entriesByEmployeeList.clear();
//					this.entriesByEmployeeList.addAll(this.listIrregularSched.stream()
//							.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode()
//									.equals(this.mainApplication.getSelectedEmployee().getEmployeeCode()))
//							.collect(Collectors.toList()));
//
//					Platform.runLater(() -> {
//						this.setTableViewDetails();
//					});
//
//					break;
//				default:
//
//					break;
//				}
//			} else {
//				this.comboBoxFilter.setValue(this.filterObservableList.get(0));
//			}
//		});

		this.datePickerPayFrom.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.datePickerPayTo.setValue(DateUtil.getCutOffDateOfPayTo(DateUtil.parseDate(newValue.toString()))
						.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				this.datePickerPayFrom
						.setValue(DateUtil.getCutOffDate(DateUtil.parseDate(newValue.toString()), 0, false).toInstant()
								.atZone(ZoneId.systemDefault()).toLocalDate());

				if (this.prevPayFrom != null && this.prevPayTo != null) {
					if (!this.prevPayFrom.equals(DateFormatter.toDate(this.datePickerPayFrom.getValue()))
							&& !this.prevPayTo.equals(DateFormatter.toDate(this.datePickerPayTo.getValue()))) {
						this.mainApplication.getEmployeeObservableList().clear();
						this.setTableViewEmployee();
						this.entriesByEmployeeList.clear();
						this.entriesByEmployeeListCopy.clear();
						this.listIrregularSched.clear();
						this.listRegularSched.clear();

						this.mainApplication.getObsListClient().clear();
						this.mainApplication.getObsListEmpID().clear();

						List<String> clientCodeList = mainApplication.getEmployeeObservableList().stream()
								.map(p -> p.getClient()).distinct().collect(Collectors.toList());
						ObservableList<Client> clientList = FXCollections.observableArrayList();
						clientCodeList.forEach(clientCode -> {
							clientList.add(mainApplication.getClientMain().getClientByCode(clientCode));
						});

						comboBoxClient.setItems(clientList, p -> p.getClientName());
						this.setTableViewDetails();
						this.resetFields();
						this.setFieldDisable(true);
					}
				}

				this.prevPayFrom = DateFormatter.toDate(this.datePickerPayFrom.getValue());
				this.prevPayTo = DateFormatter.toDate(this.datePickerPayTo.getValue());

				this.mainApplication.setPayPeriodDateFrom(DateFormatter.toDate(this.datePickerPayFrom.getValue()));
			}
		});

		this.datePickerPayTo.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.datePickerPayFrom
						.setValue(DateUtil.getCutOffDate(DateUtil.parseDate(newValue.toString()), 0, false).toInstant()
								.atZone(ZoneId.systemDefault()).toLocalDate());
				this.datePickerPayTo.setValue(DateUtil.getCutOffDateOfPayTo(DateUtil.parseDate(newValue.toString()))
						.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

				if (this.prevPayFrom != null && this.prevPayTo != null) {
					if (!this.prevPayFrom.equals(DateFormatter.toDate(this.datePickerPayFrom.getValue()))
							&& !this.prevPayTo.equals(DateFormatter.toDate(this.datePickerPayTo.getValue()))) {
						this.mainApplication.getEmployeeObservableList().clear();
						this.setTableViewEmployee();
						this.entriesByEmployeeList.clear();
						this.entriesByEmployeeListCopy.clear();
						this.listIrregularSched.clear();
						this.listRegularSched.clear();

						this.mainApplication.getObsListClient().clear();
						this.mainApplication.getObsListEmpID().clear();

						List<String> clientCodeList = mainApplication.getEmployeeObservableList().stream()
								.map(p -> p.getClient()).distinct().collect(Collectors.toList());
						ObservableList<Client> clientList = FXCollections.observableArrayList();
						clientCodeList.forEach(clientCode -> {
							clientList.add(mainApplication.getClientMain().getClientByCode(clientCode));
						});

						comboBoxClient.setItems(clientList, p -> p.getClientName());
						this.setTableViewDetails();
						this.resetFields();
						this.setFieldDisable(true);
					}
				}

				this.prevPayFrom = DateFormatter.toDate(this.datePickerPayFrom.getValue());
				this.prevPayTo = DateFormatter.toDate(this.datePickerPayTo.getValue());

				this.mainApplication.setPayPeriodDateTo(DateFormatter.toDate(this.datePickerPayTo.getValue()));

			}
		});

	}

	public void showEmployeeBiometricsDetails() {
		if (this.mainApplication.getSelectedEmployee() != null) {
			this.entriesByEmployeeList.clear();
			this.entriesByEmployeeList.addAll(this.mainApplication.getDataByEmployeeIdPayFrom(
					this.mainApplication.getSelectedEmployee().getEmployeeCode(), this.payFrom));
			this.entriesByEmployeeListCopy.addAll(this.entriesByEmployeeList);

			this.listRegularSched.addAll(this.entriesByEmployeeList.stream()
					.filter(entry -> entry.getIsRegularSchedule() != null && entry.getIsRegularSchedule() == 1)
					.collect(Collectors.toList()));
			this.listIrregularSched.addAll(this.entriesByEmployeeList.stream()
					.filter(entry -> entry.getIsRegularSchedule() != null && entry.getIsRegularSchedule() == 0)
					.collect(Collectors.toList()));
		}
	}

	public void getEntriesByEmployee() {
		if (this.mainApplication.getSelectedEmployee() != null) {
			for (EmployeeScheduleUploading entry : this.mainApplication.getListFinalizeBiometricsComplete()) {
				if (entry.getEmploymentHistory().getEmployee().getEmployeeCode()
						.equals(this.mainApplication.getSelectedEmployee().getEmployeeCode())) {
					if (!this.entriesByEmployeeList.contains(entry)) {
						this.entriesByEmployeeList.add(entry);
					}
				}
			}
		}
		this.entriesByEmployeeListCopy.clear();
		this.entriesByEmployeeListCopy.addAll(this.entriesByEmployeeList);
		this.setTableViewDetails();
	}

	public void selectFirstEntryFromTable() {
		if (this.entriesByEmployeeList != null && !this.entriesByEmployeeList.isEmpty()
				&& this.entriesByEmployeeList.size() != 0) {
			this.tableViewDetails.requestFocus();
			this.tableViewDetails.scrollTo(this.entriesByEmployeeList.get(0));
			this.tableViewDetails.getSelectionModel().select(this.entriesByEmployeeList.get(0));
		}

	}

	public void setFieldDisable(Boolean isDisable) {
//		this.comboBoxFilter.setDisable(isDisable);
		this.buttonSearch.setDisable(isDisable);
		// this.buttonReset.setDisable(isDisable);
		this.buttonPrint.setDisable(isDisable);
		this.comboBoxClient.setDisable(isDisable);
		this.comboBoxDepartment.setDisable(isDisable);
		// this.comboBoxEmployeeFilter.setDisable(isDisable);
		this.buttonValidate.setDisable(isDisable);
		this.buttonProcess.setDisable(isDisable);
	}

	public void setTableViewDetails() {
		ObservableList<EmployeeScheduleUploading> empScheduleUploadingObsList = FXCollections.observableArrayList();

		// this.showEmployeeBiometricsDetails();

		ObservableListUtil.sort(this.entriesByEmployeeList,
				p -> new SimpleDateFormat("yyyy-MM-dd").format(p.getDateEntry()));

		empScheduleUploadingObsList.setAll(this.entriesByEmployeeList);

		this.tableViewDetails.setItems(empScheduleUploadingObsList);

		TableViewUtil.refreshTableView(this.tableViewDetails);
		TableColumnUtil.setColumn(this.tableColumnDate,
				p -> new SimpleDateFormat("MMM. dd, yyyy").format((p.getDateEntry())));

		TableColumnUtil.setColumn(this.tableColumnTimeIn,
				p -> p.getTimeInEntry() == null ? "" : this.mainApplication.formatTimeAMPM(p.getTimeInEntry()));
		TableColumnUtil.setColumn(this.tableColumnLunchOut,
				p -> p.getLunchOutEntry() == null ? "" : this.mainApplication.formatTimeAMPM(p.getLunchOutEntry()));
		TableColumnUtil.setColumn(this.tableColumnLunchIn,
				p -> p.getLunchInEntry() == null ? "" : this.mainApplication.formatTimeAMPM(p.getLunchInEntry()));
		TableColumnUtil.setColumn(this.tableColumnTimeOut,
				p -> p.getTimeOutEntry() == null ? "" : this.mainApplication.formatTimeAMPM(p.getTimeOutEntry()));
		TableColumnUtil.setColumn(this.tableColumnDay, p -> p.getDayOfDate());
		TableColumnUtil.setColumn(this.tableColumnRemarks,
				p -> p.getRemarksReference() == null ? "" : p.getRemarksReference().getRemarks());

		// StringUtils.substring(p.getDayOfDate(), 0 , 3);

		this.selectFirstEntryFromTable();

		if (empScheduleUploadingObsList.size() != 0) {
			if (empScheduleUploadingObsList.get(0).getIsValidated().equals(1)) {
				this.tableViewDetails.setDisable(true);
				this.buttonValidate.setDisable(true);
			} else {
				this.tableViewDetails.setDisable(false);
				this.buttonValidate.setDisable(false);
			}
		}
	}

	public void showEmployees() {
		this.tableViewEmployee.setItems(this.observableListMapEmployees);
		TableColumnUtil.setColumn(this.tableColumnEmployeeId,
				p -> p.getKey().getEmployeeCode() == null ? "" : p.getKey().getEmployeeCode());
		TableColumnUtil.setColumn(this.tableColumnEmployeeName,
				p -> p.getKey().getEmployeeFullName() == null ? "" : p.getKey().getEmployeeFullName());

//		this.labelEmpCount.setText("Employee Count: " + this.observableListMapEmployees.size());
	}

	public void setTableViewEmployee() {
		this.obsListEmployee
				.setAll(this.mainApplication.populateEmployee(this.selectedClient, this.selectedDepartment));

		this.observableListMapEmployees.setAll(this.mainApplication.populateHashMapEmployees(this.obsListEmployee));
		this.observableListMapEmployeesCopy.setAll(this.observableListMapEmployees);
		this.observableListMapValidatedEmployees.clear();
		this.observableListMapNotValidatedEmployees.clear();

		ObservableListUtil.sort(this.obsListEmployee, p -> p.getSurname());

		this.showEmployees();

		if (this.mainApplication.getEmployeeObservableList() != null
				|| !this.mainApplication.getEmployeeObservableList().isEmpty()) {
			this.setFieldDisable(false);
		}

		List<Integer> validatedEmployeeList = new ArrayList<>();
		if (payFrom != null && payTo != null) {
			validatedEmployeeList.addAll(mainApplication.getAllValidatedEmployeeIdByPayFromPayTo(payFrom, payTo));
		}

		if (validatedEmployeeList != null && !validatedEmployeeList.isEmpty()) {
			buttonProcess.setDisable(false);
		} else {
			buttonProcess.setDisable(true);
		}

		this.styleColumnColor(validatedEmployeeList);

		TableViewUtil.refreshTableView(this.tableViewEmployee);
		this.tableViewEmployee.refresh();

	}

	public void styleColumnColor(List<Integer> validatedEmployeeList) {
		Callback<TableColumn<Entry<Employee, Boolean>, Boolean>, TableCell<Entry<Employee, Boolean>, Boolean>> cellFactory = new Callback<TableColumn<Entry<Employee, Boolean>, Boolean>, TableCell<Entry<Employee, Boolean>, Boolean>>() {
			@Override
			public TableCell<Entry<Employee, Boolean>, Boolean> call(
					final TableColumn<Entry<Employee, Boolean>, Boolean> param) {
				final TableCell<Entry<Employee, Boolean>, Boolean> cell = new TableCell<Entry<Employee, Boolean>, Boolean>() {

					@Override
					public void updateItem(Boolean item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							// fix for array out of bounds
							setGraphic(null);
							setText(null);
						} else {
							// condition
							setStyle("");
							if (!observableListMapEmployees.isEmpty()) {
								if (validatedEmployeeList.stream()
										.filter(p -> p.equals(param.getTableView().getItems()
												.get(this.getTableRow().getIndex()).getKey().getEmployeeCode()))
										.findAny().isPresent()) {
									setStyle("-fx-background-color:lightgreen;");
								}
							}

						}
					}
				};
				return cell;
			}
		};

		tableColumnValidated.setCellFactory(cellFactory);
	}

	public void handleUpload() {
		this.mainApplication.showUploadEmployeeScheduleUploading();
	}

	public void clearFields() {
//		this.comboBoxFilter.setValue(null);
		this.comboBoxClient.setValue(null);
	}

	public void handlePrint() {
		this.mainApplication.showPrintEmployeeScheduleUploading();
	}

	public void handleValidate() {
		List<Employee> selectedEmployeesFromMap = new ArrayList<>();
		List<EmployeeScheduleUploading> bioByEmployeeList = new ArrayList<>();

		selectedEmployeesFromMap.addAll(this.observableListMapEmployees.stream().filter(p -> p.getValue().equals(true))
				.map(p -> p.getKey()).collect(Collectors.toList()));

		if (selectedEmployeesFromMap != null && !selectedEmployeesFromMap.isEmpty()) {
			if (AlertUtil.showQuestionAlertBoolean("Validate all selected employee?", this.getParentStage())) {

				Task<Void> taskValidateEmployees = new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						int counter = 0;
						for (Employee employee : selectedEmployeesFromMap) {
							bioByEmployeeList.addAll(
									mainApplication.getDataByEmployeeIdPayFrom(employee.getEmployeeCode(), payFrom));

							for (EmployeeScheduleUploading biometric : bioByEmployeeList) {
								biometric.setIsValidated(1);
								biometric.setChangedByUser(mainApplication.getUser() == null ? null
										: mainApplication.getUser().getUserName());
								biometric.setChangedOnDate(new Date());
								biometric.setChangedInComputer(mainApplication.getComputerName());
								biometric.setUser(mainApplication.getUser());
							}

							counter += 1;
							updateProgress(ProgressUtil.getProgressValue(counter, selectedEmployeesFromMap.size()), 1D);
							updateMessage(ProgressUtil.getMessageValue("Validating...\n", counter,
									selectedEmployeesFromMap.size()));
						}

						Platform.runLater(() -> {
							boolean isSuccessValidate = false;
							isSuccessValidate = mainApplication.updateMultipleData(bioByEmployeeList);

							if (isSuccessValidate) {
								AlertUtil.showSuccessSaveAlert(getParentStage());
							} else {
								AlertUtil.showErrorAlert("Error validating.", getParentStage());
							}

							setTableViewEmployee();
							entriesByEmployeeList.clear();
							setTableViewDetails();
							checkBoxEmployee.setSelected(false);
						});
						return null;
					}
				};
				ProgressUtil.showProcessInterface(getParentStage(), taskValidateEmployees, false);
			}
		} else {
			AlertUtil.showInformationAlert("No selected employee/s to validate.", this.getParentStage());
		}
	}

	public void handleProcess() {
		List<Integer> validatedEmployeeIDList = new ArrayList<>();
		validatedEmployeeIDList.addAll(mainApplication.getAllValidatedEmployeeIdByPayFromPayTo(payFrom, payTo));

		if (validatedEmployeeIDList != null && !validatedEmployeeIDList.isEmpty()) {
			if (AlertUtil.showQuestionAlertBoolean("Process validated biometrics for\nPay period:\n"
					+ this.mainApplication.getPayPeriod(payFrom, payTo) + " ?", this.getParentStage())) {

				this.processedPayrollList = new ArrayList<>();
				this.mainApplication.getEmployeeErrorSavingPayrollHashMap().clear();

				Task<Void> taskProcessPayroll = new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						int counter = 0;
						for (Integer employeeCode : validatedEmployeeIDList) {
							try {

								// get emp history by eff date -- for pay rate
								EmploymentHistory employmentHistory = new EmploymentHistory();
								employmentHistory = mainApplication.getEmploymentHistoryMain()
										.getEmploymentHistoryByEmployeeIdAndEffectivityDate(employeeCode, payFrom);

								Payroll existingPayroll = mainApplication.getPayrollMain()
										.getEmployeeRegularPayrollByPayFrom(employmentHistory.getEmployee(), payFrom);

								if (existingPayroll == null) {
									Payroll payroll = processPayroll(employmentHistory);
									processedPayrollList.add(payroll);

								}

								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter, validatedEmployeeIDList.size()),
										1D);
								updateMessage(ProgressUtil.getMessageValue("Processing...\n", counter,
										validatedEmployeeIDList.size()));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						counter = 0;
						for (Payroll payroll : processedPayrollList) {
							modifyDetailsPayroll(payroll, ModificationType.ADD);
							counter += 1;
							updateProgress(ProgressUtil.getProgressValue(counter, processedPayrollList.size()), 1D);
							updateMessage(
									ProgressUtil.getMessageValue("Saving...\n", counter, processedPayrollList.size()));
						}

						counter = 0;
						Platform.runLater(() -> {

							if (mainApplication.getEmployeeErrorSavingPayrollHashMap() != null
									&& !mainApplication.getEmployeeErrorSavingPayrollHashMap().isEmpty()) {
								if (AlertUtil.showQuestionAlertBoolean(
										"Error saving some payroll.\nWould you like to export a report file[.xslx]? ",
										mainApplication.getPrimaryStage())) {
									Integer payrollErrorReport = 3;
									mainApplication.exportExcel(payrollErrorReport);
								}
							}

							mainApplication.getEmployeeErrorSavingPayrollHashMap().clear();
							processedPayrollList.clear();

							AlertUtil.showSuccessSaveAlert(mainApplication.getPrimaryStage());
						});

						return null;
					}
				};
				ProgressUtil.showProcessInterface(getParentStage(), taskProcessPayroll, false);
			}
		} else {
			AlertUtil.showInformationAlert("No validated records to process.", this.getParentStage());
		}
	}

	public void modifyDetailsPayroll(Payroll payroll, ModificationType modificationType) {
		boolean isSuccessSave = false;

		if (this.mainApplication.getPayrollEncodeMain().isUpdatePayrollHeader(payroll)) {
			// create
			isSuccessSave = mainApplication.getPayrollMain().createPayroll(payroll);

			if (isSuccessSave) {
				if (this.mainApplication.getPayrollEncodeMain().isSavePayrollDetailsSuccess(payroll)) {
					if (this.mainApplication.getPayrollEncodeMain().getPayrollFoxProMain().isSavePayrollFoxpro(payroll,
							modificationType)) {
						if (this.mainApplication.getPayrollEncodeMain()
								.isSavePayrollMiscellaneousAdjustmentDetails(payroll)) {
							if (this.mainApplication.getPayrollEncodeMain().isSavePayrollPreviousOvertime(payroll,
									modificationType)) {
							} else {
								mainApplication.getEmployeeErrorSavingPayrollHashMap().put(
										payroll.getEmploymentHistory(), "Error saving to payroll previous overtime.");
								isSuccessSave = false;
							}
						} else {
							mainApplication.getEmployeeErrorSavingPayrollHashMap().put(payroll.getEmploymentHistory(),
									"Error saving to payroll other deduction.");
							isSuccessSave = false;
						}
					} else {
						mainApplication.getEmployeeErrorSavingPayrollHashMap().put(payroll.getEmploymentHistory(),
								"Error saving to previous payroll structure.");
						isSuccessSave = false;
					}
				} else {
					isSuccessSave = false;
				}
			}

			if (!isSuccessSave) {
				mainApplication.getEmployeeErrorSavingPayrollHashMap().put(payroll.getEmploymentHistory(),
						"Error saving payroll.");
			}
		} else {
			mainApplication.getEmployeeErrorSavingPayrollHashMap().put(payroll.getEmploymentHistory(),
					"Error saving payroll header.");
		}

	}

	public Payroll processPayroll(EmploymentHistory employmentHistory) throws Exception {
		Payroll payroll = new Payroll();

		payroll = this.setInitialPayrollProperties(employmentHistory);

		payroll.getPayrollComputation().executeComputePayroll(payroll);

		return payroll;
	}

	public Payroll setInitialPayrollProperties(EmploymentHistory employmentHistory) {

		boolean isDaily = employmentHistory.getEmploymentConfiguration().getIsDaily();
		Payroll payroll = new Payroll();
		PayrollType payrollType = new PayrollType();

		payrollType.setPayrollTypeKey(1);
		payroll.setPayrollType(payrollType);

		int payrollCategoryDefault = 1;

		PayrollClientConfiguration payrollClientConfiguration = new PayrollClientConfiguration();
		payrollClientConfiguration = this.mainApplication.getPayrollEncodeMain().getPayrollClientConfigurationMain()
				.getClientConfigurationByClientCode(employmentHistory.getClient());

		payroll.setPayrollClientConfiguration(payrollClientConfiguration);

		payroll.setPayrollType(payrollType);
		payroll.setPayPeriodFrom(this.payFrom);
		payroll.setPayPeriodTo(this.payTo);
		payroll.setPayrollCategory(payrollCategoryDefault);
		payroll.setUser(this.mainApplication.getUser());
		payroll.setChangedInComputer(this.mainApplication.getComputerName());
		payroll.setChangedOnDate(new Date());
		payroll.setAmountOfSssEmployerShare(BigDecimal.ZERO);
		payroll.setAmountOfSssEmployeeShare(BigDecimal.ZERO);
		payroll.setAmountOfSssEmployeeCompensation(BigDecimal.ZERO);
		payroll.setAmountOfPhilhealthEmployerShare(BigDecimal.ZERO);
		payroll.setAmountOfPhilhealthEmployeeShare(BigDecimal.ZERO);
		payroll.setAmountOfPagibigEmployerShare(BigDecimal.ZERO);
		payroll.setAmountOfPagibigEmployeeShare(BigDecimal.ZERO);
		payroll.setAmountOfSssSalaryLoan(BigDecimal.ZERO);
		payroll.setAmountOfSssCalamityLoan(BigDecimal.ZERO);
		payroll.setAmountOfPagibigMultiPurposeLoan(BigDecimal.ZERO);
		payroll.setAmountOfPagibigCalamityLoan(BigDecimal.ZERO);
		payroll.setAmountOfHmoDeduction(BigDecimal.ZERO);
		payroll.setAmountOfCashAdvance(BigDecimal.ZERO);
		payroll.setAmountOfAllowanceRefund(BigDecimal.ZERO);
		payroll.setAmountOfAllowanceDeduct(BigDecimal.ZERO);
		payroll.setAmountOfPreviousOvertimeAdjustment(BigDecimal.ZERO);
		payroll.setAmountOfNetOvertimePreviousPay(BigDecimal.ZERO);

		payroll.setEmploymentHistory(employmentHistory);
		payroll.setTaxStatus(employmentHistory.getEmployee().getTaxStatus());

		this.checkingPayrollTypeRegular(payroll);

		Integer numberOfRegularDays = 0;
		Integer numberOfHolidays = 0;
		BigDecimal numberOfDaysAbsent = BigDecimal.ZERO;
		Integer minutesOfUndertime = 0;
		Integer hoursOfUndertime = 0;
		HashSet<Overtime> overtimeHashSet = new HashSet<>();

		List<EmployeeScheduleUploading> employeeScheduleUploadingList = new ArrayList<>();
		employeeScheduleUploadingList.addAll(this.mainApplication
				.getDataByEmployeeIdPayFrom(employmentHistory.getEmployee().getEmployeeCode(), payFrom));

		
		numberOfDaysAbsent = new BigDecimal(employeeScheduleUploadingList.stream()
				.filter(p -> p.getRemarksReference() != null && p.getRemarksReference().getPrimaryKey() == 1).count());

		List<Integer> allUndertimeList = employeeScheduleUploadingList.stream().filter(p -> p.getUndertime() != null)
				.map(p -> p.getUndertime()).collect(Collectors.toList());
		for (Integer undertimeMins : allUndertimeList) {
			minutesOfUndertime = minutesOfUndertime + undertimeMins;
		}

		hoursOfUndertime = minutesOfUndertime / 60;
		minutesOfUndertime = minutesOfUndertime % 60;

		if (isDaily) {
			numberOfHolidays = Math
					.toIntExact(employeeScheduleUploadingList.stream().filter(p -> p.getIsHoliday() != 0).count());
			numberOfRegularDays = Math.toIntExact(
					employeeScheduleUploadingList.stream().filter(p -> p.getIsRegularSchedule() == 1).count());
		}

		payroll.setNumberOfRegularDays(numberOfRegularDays);
		payroll.setNumberOfHolidays(numberOfHolidays);
		payroll.setNumberOfDaysAbsent(numberOfDaysAbsent);
		payroll.setMinutesOfUndertime(minutesOfUndertime);
		payroll.setHoursOfUndertime(hoursOfUndertime);

		payroll.setChangedOnDate(this.mainApplication.getDateNow());

		List<EmployeeScheduleUploadingOvertimeBreakdown> overtimeBreakdownList = new ArrayList<>();
		overtimeBreakdownList.addAll(this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
				.getAllDataByEmployeeCodePayFromPayTo(employmentHistory.getEmployee().getEmployeeCode(), payFrom,
						payTo));

		if (overtimeBreakdownList != null && !overtimeBreakdownList.isEmpty()) {

			List<EmployeeScheduleUploadingOvertimeBreakdown> overtimeBreakdownOverallList = new ArrayList<>();
			List<OvertimeType> overtimeTypeClientList = this.mainApplication.getOvertimeTypeMain()
					.getOvertimeTypeByClientCode(employmentHistory.getClient().getClientCode());

			overtimeTypeClientList.forEach(overtimeTypeClient -> {

				List<EmployeeScheduleUploadingOvertimeBreakdown> overtimeBreakdownByOvertimeTypeList = new ArrayList<>();
				overtimeBreakdownByOvertimeTypeList.addAll(overtimeBreakdownList.stream()
						.filter(p -> p.getOvertimeType().getPrimaryKey().equals(overtimeTypeClient.getPrimaryKey()))
						.collect(Collectors.toList()));

				int totalMin = 0;
				for (EmployeeScheduleUploadingOvertimeBreakdown otBreakdown : overtimeBreakdownByOvertimeTypeList) {
					if (otBreakdown.getOvertimeFiling() != null || otBreakdown.getEarlyOvertimeFiling() != null
							|| otBreakdown.getOvertimeFilingClient() != null
							|| otBreakdown.getEarlyOvertimeFilingClient() != null) {
						totalMin = totalMin + otBreakdown.getTotalMin();
					}
				}

				EmployeeScheduleUploadingOvertimeBreakdown overtimeBreakdownOverall = new EmployeeScheduleUploadingOvertimeBreakdown();
				overtimeBreakdownOverall.setOvertimeType(overtimeTypeClient);
				overtimeBreakdownOverall.setTotalMin(totalMin);

				overtimeBreakdownOverallList.add(overtimeBreakdownOverall);
			});

			for (EmployeeScheduleUploadingOvertimeBreakdown overtimeBreakdown : overtimeBreakdownOverallList) {
				if (overtimeBreakdown.getOvertimeType() != null) {
					Overtime overtime = new Overtime();
					Integer hoursOfOvertime = 0;
					Integer minutesOfOvertime = 0;
					OvertimeType overtimeType = new OvertimeType();

					hoursOfOvertime = overtimeBreakdown.getTotalMin() / 60;
					minutesOfOvertime = overtimeBreakdown.getTotalMin() % 60;
					overtimeType = overtimeBreakdown.getOvertimeType();

					overtime.setHoursOfOvertime(hoursOfOvertime);
					overtime.setMinutesOfOvertime(minutesOfOvertime);
					overtime.setOvertimeType(overtimeType);
					overtime.setPayroll(payroll);

					overtimeHashSet.add(overtime);
				}
			}
		}
		List<Overtime> overtimeList = overtimeHashSet.stream().collect(Collectors.toList());
		payroll.setOvertimeList(overtimeList);

		List<Payroll> previousOvertimeList = new ArrayList<>();
		for (Payroll previousOvertime : this.mainApplication.getPayrollEncodeMain()
				.getObservableListPayrollPreviousOvertime()) {
			if (previousOvertime.getAmountOfNetPay() != null
					&& previousOvertime.getAmountOfNetPay().compareTo(BigDecimal.ZERO) != 0) {
				previousOvertimeList.add(previousOvertime);
			}
		}
		payroll.setPreviousOvertimeList(previousOvertimeList);

		List<Allowance> allowanceList = new ArrayList<>();
		allowanceList.addAll(
				this.mainApplication.getPayrollEncodeMain().allowanceList(payroll.getPayrollClientConfiguration()));
		payroll.setAllowanceList(allowanceList);

		List<MiscellaneousAdjustmentDetails> miscellaneousAdjustmentDetailsList = new ArrayList<>();
		miscellaneousAdjustmentDetailsList = this.mainApplication.getPayrollEncodeMain()
				.miscellaneousAdjustmentDetailsList(payroll);
		payroll.setMiscellaneousAdjustmentDetailsList(miscellaneousAdjustmentDetailsList);

		List<PayrollStatusDetails> payrollStatusDetailsList = new ArrayList<>();
		payrollStatusDetailsList = this.mainApplication.getPayrollEncodeMain().payrollStatusDetailsList(payroll);
		payroll.setPayrollStatusDetailsList(payrollStatusDetailsList);

		List<PostAuditPayroll> postAuditPayrollList = new ArrayList<>();
		postAuditPayrollList = this.mainApplication.getPayrollEncodeMain().postAuditPayrollList(payroll);
		payroll.setPostAuditPayrollList(postAuditPayrollList);

		payroll.setChangedOnDate(this.mainApplication.getDateNow());

		this.mainApplication.getPayrollEncodeMain().setPayrollHeaderObject(payroll);
		this.mainApplication.getPayrollEncodeMain().setPayrollComputationObject(payroll);

		return payroll;
	}

	public void checkingPayrollTypeRegular(Payroll payroll) {

		if (payroll.getPayrollType().getPayrollTypeKey().equals(1)) {
			List<Payroll> payrollList = this.mainApplication.getPayrollMain().getFilteredPayroll(
					payroll.getEmploymentHistory().getEmployee().getEmployeeCode(), null, null, null, null, null, null,
					payroll.getPayPeriodFrom(), payroll.getPayPeriodTo(), null, null, null);

			if (payrollList != null && payrollList.size() > 0) {
				boolean isExists = false;

				for (Payroll checkingPayroll : payrollList) {
					if (checkingPayroll.getActualEmploymentConfiguration().getPrimaryKey()
							.equals(payroll.getActualEmploymentConfiguration().getPrimaryKey())) {
						isExists = true;
						break;
					}
				}
				if (!isExists) {
					payroll.setPayrollType(
							this.mainApplication.getPayrollEncodeMain().getPayrollTypeMain().getPayrollTypeByID(4));
				}
			}
		}
	}

	// public void handleReset() {
	// if (AlertUtil.showQuestionAlertBoolean("Reset\n\nAre you sure?",
	// this.getParentStage())) {
	// this.resetFields();
	// }
	// }

	public void resetFields() {
		this.clearFields();

		this.mainApplication.getListFinalizeBiometricsComplete().clear();
		this.extractedEmployeesList.clear();
		this.empIdList.clear();
		this.listRegularSched.clear();
		this.listIrregularSched.clear();
		this.listNoSched.clear();
		this.retrievedDataExtractedList.clear();
		this.employeeScheduleUploadingWithRegularSchedList.clear();
		this.employeeScheduleUploadingWithIrregularSchedList.clear();

		this.obsListClient.clear();
		this.comboBoxClient.setItems(null);
		this.obsListDepartment.clear();
		this.comboBoxDepartment.setItems(null);

		this.mainApplication.getListFinalizeBiometricsOnlyBio().clear();
		this.mainApplication.getEmployeeObservableList().clear();
		this.mainApplication.getEmployeeScheduleUploadingList().clear();

		this.comboBoxClient.setValue(null);
		this.comboBoxDepartment.setValue(null);

		// this.datePickerPayFrom.setValue(null);
		// this.datePickerPayTo.setValue(null);

		this.datePickerPayFrom.setDisable(false);
		this.datePickerPayTo.setDisable(false);
		this.buttonCollect.setDisable(false);
		this.buttonUpload.setDisable(false);

		this.selectedClient = null;
		this.selectedDepartment = null;

		this.entriesByEmployeeList.clear();
		this.entriesByEmployeeListCopy.clear();

		// this.existingDataList.clear();
		// this.newEntrySaveList.clear();
		// this.updateEntrySaveList.clear();
		// this.deleteEntrySaveList.clear();

		// this.mainApplication.setPayPeriodDateFrom(null);
		// this.mainApplication.setPayPeriodDateTo(null);
		this.mainApplication.setCutoffDateFrom(null);
		this.mainApplication.setCutoffDateTo(null);

		this.setTableViewEmployee();
		this.setTableViewDetails();

		// this.setFieldDisable(true);
	}

	public boolean isValid() {
		if (this.datePickerPayFrom.getValue() == null && this.datePickerPayTo.getValue() == null) {
			return false;
		}
		return true;
	}

	public void handleCollect() {
		this.resetFields();

		if (this.isValid()) {
			// this.comboBoxEmployeeFilter.setValue("All");
			this.payFrom = this.mainApplication.getPayPeriodDateFrom();
			this.payTo = this.mainApplication.getPayPeriodDateTo();

			ObservableList<Client> clientList = FXCollections.observableArrayList();
			mainApplication.getObsListEmpID().setAll(mainApplication.getAllEmployeeIdByPayFromPayTo(payFrom, payTo));

			Task<Void> taskExtractDataFromList = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					int counter = 0;

					mainApplication.getEmployeeObservableList().clear();

					for (Integer empId : mainApplication.getObsListEmpID()) {
						Employee employee = new Employee();
						employee = mainApplication.getEmployeeMain().getEmployeeByID(empId);

						mainApplication.getEmployeeObservableList().add(employee);

						counter += 1;
						updateProgress(ProgressUtil.getProgressValue(counter, mainApplication.getObsListEmpID().size()),
								1D);
						updateMessage(ProgressUtil.getMessageValue("Collecting...\n", counter,
								mainApplication.getObsListEmpID().size()));
					}

					Platform.runLater(() -> {
						if (mainApplication.getEmployeeObservableList().size() != 0) {
							setTableViewEmployee();
							comboBoxClient.setValue(null);

							// datePickerPayFrom.setDisable(true);
							// datePickerPayTo.setDisable(true);

							List<String> clientCodeList = mainApplication.getEmployeeObservableList().stream()
									.map(p -> p.getClient()).distinct().collect(Collectors.toList());

							// clientList.setAll(retrievedDataExtractedList.stream().map(p ->
							// p.getClient()).distinct()
							// .collect(Collectors.toList()));
							clientCodeList.forEach(clientCode -> {
								clientList.add(mainApplication.getClientMain().getClientByCode(clientCode));
							});

							comboBoxClient.setItems(clientList, p -> p.getClientName());
							mainApplication.getObsListClient().setAll(clientList);

							// setTableViewDetails();
							// buttonCollect.setDisable(true);
							// buttonUpload.setDisable(true);
							// observableListMapEmployeesCopy.setAll(observableListMapEmployees);

						} else {
							resetFields();
							setFieldDisable(true);
							AlertUtil.showInformationAlert("No data found.\nPay Period Date:\n"
									+ new SimpleDateFormat("MMM. dd, yyyy")
											.format(mainApplication.getPayPeriodDateFrom())
									+ " - " + new SimpleDateFormat("MMM. dd, yyyy")
											.format(mainApplication.getPayPeriodDateTo()),
									getParentStage());

						}
					});
					return null;
				}

			};
			ProgressUtil.showProcessInterface(getParentStage(), taskExtractDataFromList, false);
		} else {
			setFieldDisable(true);
			AlertUtil.showInformationAlert("Pick Pay Period dates before retrieving.", getParentStage());
			// resetFields();
			return;
		}
	}

	public void setRegularSchedule(EmployeeScheduleUploading employeeScheduleUploading) {

		mainApplication.getListFinalizeBiometricsComplete().add(employeeScheduleUploading);
	}

	@Override
	public void modifyDetails(EmployeeScheduleUploading employeeScheduleUploading, ModificationType modificationType) {

	}

	@Override
	public void showDetails(EmployeeScheduleUploading employeeScheduleUploading) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMainApplication() {
		this.obsListClient
				.setAll(this.mainApplication.populateClient(DateFormatter.toDate(this.datePickerPayFrom.getValue()),
						DateFormatter.toDate(this.datePickerPayTo.getValue())));
		this.comboBoxClient.setItems(this.obsListClient, p -> p.getClientName());

		this.mainApplication.setSelectedEmployee(null);
		this.selectedClient = null;
		this.selectedDepartment = null;

		this.setFieldDisable(true);
//		this.labelEmpCount.setText("Employee Count: ");
		this.setFieldListener();

		this.filterObservableList.add("All");
		this.filterObservableList.add("Regular Schedule");
		this.filterObservableList.add("Irregular Schedule");
		// this.filterObservableList.add("Without Schedule");

//		this.comboBoxFilter.setItems(this.filterObservableList, p -> p, true);
//		this.comboBoxFilter.setValue("All");

		this.allActiveEmployeeList.addAll(this.mainApplication.getEmployeeMain().getAllActiveEmployee(
				this.mainApplication.getPayPeriodDateFrom(), this.mainApplication.getPayPeriodDateTo()));

		this.tableColumnEmployeeCheckbox.setGraphic(this.checkBoxEmployee);
		this.checkBoxEmployee.setTooltip(new Tooltip("Select All\r\nUnselect All"));
		this.tableViewEmployee.setEditable(true);

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
			});
			return property;
		});

		this.tableColumnEmployeeCheckbox
				.setCellFactory(CheckBoxTableCell.forTableColumn(this.tableColumnEmployeeCheckbox));

		this.checkBoxEmployee.selectedProperty().addListener((obs, oldValue, newValue) -> {
			this.observableListMapEmployees.forEach(object -> {
				object.setValue(newValue);
			});
			TableViewUtil.refreshTableView(this.tableViewEmployee);
		});
		
		
//		this.comboBoxFilter.setVisible(false);
	}

	@FXML
	private Button buttonUpload;
	@FXML
	private Button buttonPrint;
	@FXML
	private Button buttonSearch;
	// @FXML
	// private Button buttonReset;
	@FXML
	private Button buttonCollect;
	@FXML
	private Button buttonValidate;
	@FXML
	private Button buttonProcess;
	@FXML
	private TableView<Map.Entry<Employee, Boolean>> tableViewEmployee;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, Boolean> tableColumnEmployeeCheckbox;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeId;
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, String> tableColumnEmployeeName;
	@FXML
	private TableView<EmployeeScheduleUploading> tableViewDetails;
	@FXML
	private TableColumn<EmployeeScheduleUploading, String> tableColumnDate;
	@FXML
	private TableColumn<EmployeeScheduleUploading, String> tableColumnTimeIn;
	@FXML
	private TableColumn<EmployeeScheduleUploading, String> tableColumnLunchOut;
	@FXML
	private TableColumn<EmployeeScheduleUploading, String> tableColumnLunchIn;
	@FXML
	private TableColumn<EmployeeScheduleUploading, String> tableColumnTimeOut;
	@FXML
	private TableColumn<EmployeeScheduleUploading, String> tableColumnDay;
	@FXML
	private TableColumn<EmployeeScheduleUploading, String> tableColumnRemarks;
//	@FXML
//	private Label labelEmpCount;
	@FXML
	private AutoFillComboBox<Client> comboBoxClient;
//	@FXML
//	private AutoFillComboBox<String> comboBoxFilter;
	@FXML
	private FormattedDatePicker datePickerPayFrom;
	@FXML
	private FormattedDatePicker datePickerPayTo;
	@FXML
	private AutoFillComboBox<Department> comboBoxDepartment;
	@FXML
	private CheckBox checkBoxEmployee = new CheckBox();
	@FXML
	private TableColumn<Map.Entry<Employee, Boolean>, Boolean> tableColumnValidated;
}
