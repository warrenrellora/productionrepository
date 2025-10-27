package ph.com.lserv.production.employeescheduleuploading.view;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ph.com.lbpsc.production.ReportMain;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.masterclass.MasterController;
import ph.com.lbpsc.production.overtimetype.model.OvertimeType;
import ph.com.lbpsc.production.position.model.Position;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.ProgressUtil;
import ph.com.lbpsc.production.util.ReportUtil;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.employeescheduleuploadingovertimebreakdown.model.EmployeeScheduleUploadingOvertimeBreakdown;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;

public class PrintEmployeeScheduleUploadingController extends MasterController<EmployeeScheduleUploadingMain> {
	Stage stage;
	Client selectedClient;
	Department selectedDepartment;
	ObservableList<Client> obsListClient = FXCollections.observableArrayList();
	ObservableList<Department> obsListDepartment = FXCollections.observableArrayList();
	ObservableList<Employee> obsListEmployee = FXCollections.observableArrayList();
	ObservableList<Integer> obsListEmpID = FXCollections.observableArrayList();
	Date payFrom;
	Date payTo;
	Date cutoffFrom;
	Date cutoffTo;
	List<EmployeeScheduleUploading> dataCollectedList = new ArrayList<>();
	List<EmployeeScheduleUploading> addedDataList = new ArrayList<>();

	List<Integer> overtimeTypeCdListDistinct = new ArrayList<>();
	List<OvertimeType> otDefaultObjectList = new ArrayList<>();

	HashMap<Integer, HashMap<Integer, Integer>> overallTotalMinPerEmployeeHashMap = new HashMap<>();
	HashMap<Integer, Integer> totalMinPerOvertimeTypeHashMap = new HashMap<>();

	HashMap<Integer, List<BigDecimal>> totalHrsHashMap = new HashMap<>();
	List<Integer> empIdList = new ArrayList<>();

	List<EmployeeScheduleUploading> biometricsReportList = new ArrayList<>();
	OvertimeType prevOvertimeType = new OvertimeType();

	public boolean isValid() {

		if (this.selectedClient == null && this.selectedDepartment == null
				&& (this.comboBoxEmployee.getValue() == null || this.comboBoxEmployee.getValue().isEmpty())) {
			AlertUtil.showInformationAlert("Please select the needed data to collect.", this.getStage());
			return false;
		}

		return true;
	}

	public void generateReport(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport, InputStream report) {
		ObservableList<EmployeeScheduleUploading> sortObsList = FXCollections.observableArrayList();

		sortObsList.setAll(biometricsReportList);
		biometricsReportList.clear();

		ObservableListUtil.sort(sortObsList, p -> p.getEmploymentHistory().getEmployee().getSurname());
		biometricsReportList.addAll(sortObsList);

		if (sortObsList.size() != 0) {

			parameterReport.put(JRParameter.REPORT_CONNECTION, new ReportMain().getDatabaseConnection());

			JRBeanCollectionDataSource jRBeanCollectionDataSource = new JRBeanCollectionDataSource(
					biometricsReportList);

			ReportUtil.showReportBeanCollection(report, parameterReport, jRBeanCollectionDataSource);
		} else {
			AlertUtil.showDataNotFound(this.getStage());
		}
	}

	public List<EmployeeScheduleUploading> sortDataList(List<EmployeeScheduleUploading> copyFinalizedBiometricsList) {
		ObservableList<EmployeeScheduleUploading> sortObsList = FXCollections.observableArrayList();
		sortObsList.setAll(copyFinalizedBiometricsList);
		ObservableListUtil.sort(sortObsList, p -> this.mainApplication.getDateFromTimestamp(p.getTimeInEntry()));

		copyFinalizedBiometricsList.clear();
		copyFinalizedBiometricsList.addAll(sortObsList);

		return copyFinalizedBiometricsList;
	}

	public List<EmployeeScheduleUploading> formatDetailsOvertimeList(Integer employeeCode,
			List<EmployeeScheduleUploading> biometricsReportList) {
		List<EmployeeScheduleUploading> bioPerEmployeeList = new ArrayList<>();
		List<EmployeeScheduleUploading> bioPerEmployeeProcessedList = new ArrayList<>();
		List<EmployeeScheduleUploading> resultList = new ArrayList<>();
		int indexHeader = 0;

		this.setOvertimeHeader();

		List<BigDecimal> otBreakdownTotalHrsList = new ArrayList<>();

		// added default null values(columns for the dynamic table)
		// for each overtime value as 'initialization'
		for (int index = 0; index < this.mainApplication.getOvertimeHeaderList().size(); index++) {
			otBreakdownTotalHrsList.add(null);
		}

		// to be filled by the 'foreach' loop below
		bioPerEmployeeList = biometricsReportList.stream()
				.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeCode))
				.collect(Collectors.toList());

		for (EmployeeScheduleUploading employeeScheduleUploading : bioPerEmployeeList) {

			List<EmployeeScheduleUploadingOvertimeBreakdown> employeeScheduleUploadingOvertimeBreakdownList = new ArrayList<>();

			for (int index = 0; index < this.mainApplication.getOvertimeHeaderList().size(); index++) {
				employeeScheduleUploadingOvertimeBreakdownList.add(null);
			}

			if (!employeeScheduleUploading.getOvertimeBreakdownList().isEmpty()
					&& employeeScheduleUploading.getOvertimeBreakdownList().size() != 0
					&& employeeScheduleUploading.getOvertimeBreakdownList() != null) {

				for (EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown : employeeScheduleUploading
						.getOvertimeBreakdownList()) {

					for (OvertimeType overtimeType : this.mainApplication.getOvertimeHeaderList()) {
						if (employeeScheduleUploadingOvertimeBreakdown != null) {

							if (overtimeType.getPrimaryKey().equals(
									employeeScheduleUploadingOvertimeBreakdown.getOvertimeType().getPrimaryKey())) {
								indexHeader = this.mainApplication.getOvertimeHeaderList().indexOf(overtimeType);

								employeeScheduleUploadingOvertimeBreakdownList.set(indexHeader,
										employeeScheduleUploadingOvertimeBreakdown);

								if (otBreakdownTotalHrsList != null
										&& otBreakdownTotalHrsList.get(indexHeader) != null) {

									BigDecimal totalHrs = otBreakdownTotalHrsList.get(indexHeader);
									BigDecimal totalHrsEntry = new BigDecimal(
											employeeScheduleUploadingOvertimeBreakdown.getTotalMin())
													.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);

									totalHrs = totalHrs.add(totalHrsEntry);

									otBreakdownTotalHrsList.set(indexHeader, totalHrs);

								} else {
									BigDecimal initialTotalHrs = new BigDecimal(
											employeeScheduleUploadingOvertimeBreakdown.getTotalMin());
									BigDecimal totalHrsFinal = initialTotalHrs.divide(new BigDecimal(60), 2,
											RoundingMode.HALF_UP);

									otBreakdownTotalHrsList.set(indexHeader, totalHrsFinal);
								}
							}
						}
					}
				}
			}
			employeeScheduleUploading.setOvertimeBreakdownList(employeeScheduleUploadingOvertimeBreakdownList);
		}

		this.totalHrsHashMap.put(employeeCode, otBreakdownTotalHrsList);
		bioPerEmployeeProcessedList.addAll(bioPerEmployeeList);

		resultList = bioPerEmployeeProcessedList.stream().collect(Collectors.toList());
		ObservableList<EmployeeScheduleUploading> sortByDateObsList = FXCollections.observableArrayList();

		sortByDateObsList.setAll(resultList);
		ObservableListUtil.sort(sortByDateObsList, p -> p.getEmploymentHistory().getEmployee().getEmployeeFullName());
		resultList.clear();
		resultList.addAll(sortByDateObsList);

		return resultList;
	}

	public void computeHrsAndMinsEachOvertimePerBio(List<EmployeeScheduleUploading> bioPerEmployeeList,
			List<Integer> overtimeTypePrikeyList) {
		// this method only applicable to some client timesheet report
		for (EmployeeScheduleUploading employeeScheduleUploading : bioPerEmployeeList) {
			int hrsND = 0;
			int minsND = 0;
			int hrsROT = 0;
			int minsROT = 0;
			int hrsSOT = 0;
			int minsSOT = 0;

			if (employeeScheduleUploading.getOvertimeBreakdownList() != null
					&& !employeeScheduleUploading.getOvertimeBreakdownList().isEmpty()) {

				for (EmployeeScheduleUploadingOvertimeBreakdown employeeScheduleUploadingOvertimeBreakdown : employeeScheduleUploading
						.getOvertimeBreakdownList()) {
					for (Integer overtimeTypePrikey : overtimeTypePrikeyList) {

						if (employeeScheduleUploadingOvertimeBreakdown != null) {

							if (overtimeTypePrikey.equals(
									employeeScheduleUploadingOvertimeBreakdown.getOvertimeType().getPrimaryKey())) {
								BigDecimal initialTotalMins = new BigDecimal(
										employeeScheduleUploadingOvertimeBreakdown.getTotalMin());

								int hrs = initialTotalMins.intValue();
								hrs = hrs / 60;
								int mins = initialTotalMins.intValue();
								mins = mins % 60;

								switch (overtimeTypePrikey) {
								case 1:
									// ot10 -- night pay
									hrsND = hrsND + hrs;
									minsND = minsND + mins;
									break;
								case 5:
								case 15:
								case 6:
									// ot100 -- reg holiday 8 hrs
									// ot200 -- reg holiday after 8 hrs
									// ot125 -- reg day after 8 hours / early ot
									hrsROT = hrsROT + hrs;
									minsROT = minsROT + mins;
									break;
								case 7:
								case 3:
								case 10:
								case 11:
								case 19:
									// TODO double holidays
									// ot130 -- sunday or restday
									// ot160 -- rest day holiday 8 hrs
									// ot338 -- rest day holiday after 8 hrs
									// ot 30 -- rest day 8 hrs
									// ot 169 -- rest day after 8 hrs
									hrsSOT = hrsSOT + hrs;
									minsSOT = minsSOT + mins;
									break;
								case 9:
									// custom ot150 as ot30/ot169
									if (this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
											.getClientPrikeyBreakdownOT30OT169List().contains(employeeScheduleUploading
													.getClient().getCompany().getCompanyCode())) {
										hrsSOT = hrsSOT + hrs;
										minsSOT = minsSOT + mins;
									}
								default:
									break;
								}
							}
						}
					}
				}
			}
			employeeScheduleUploading.setHrsOfOvertimeNightPay(hrsND);
			employeeScheduleUploading.setMinsOfOvertimeNightPay(minsND);
			employeeScheduleUploading.setHrsOfOvertimeRegDay(hrsROT);
			employeeScheduleUploading.setMinsOfOvertimeRegDay(minsROT);
			employeeScheduleUploading.setHrsOfOvertimeRestDay(hrsSOT);
			employeeScheduleUploading.setMinsOfOvertimeRestDay(minsSOT);
		}
	}

	public void setOvertimeHeader() {
		this.mainApplication.getOvertimeHeaderList().clear();

		overtimeTypeCdListDistinct = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
				.getAllOvertimeTypeDistinctByPayFromPayToClientCd(payFrom, payTo, "LBPSC");

		if (overtimeTypeCdListDistinct.size() != 0) {
			overtimeTypeCdListDistinct.forEach(prikey -> {
				this.mainApplication.getOvertimeHeaderList()
						.add(this.mainApplication.getOvertimeTypeMain().getOvertimeTypeById(prikey));
			});
		}

		if (this.mainApplication.getOvertimeHeaderList().size() == 0) {
			this.mainApplication.getOvertimeHeaderList().addAll(this.otDefaultObjectList);
		} else {

			for (OvertimeType overtimeType : this.mainApplication.getOvertimeHeaderList()) {
				String[] splitString = overtimeType.getOvertimeName().split("%");
				String overtimeName = splitString[0];

				overtimeType.setOvertimeName(overtimeName);
			}

			List<OvertimeType> otDefaultListCopy = new ArrayList<>();
			otDefaultListCopy.addAll(this.otDefaultObjectList);

			Integer countNonDefaultOvertime = 0;

			for (OvertimeType overtimeType : this.mainApplication.getOvertimeHeaderList()) {

				OvertimeType overtimeTypeDefault = this.otDefaultObjectList.stream()
						.filter(p -> p.getOvertimeName().equals(overtimeType.getOvertimeName())).findAny().orElse(null);

				if (overtimeTypeDefault != null) {
					otDefaultListCopy.remove(overtimeTypeDefault);
				} else {
					countNonDefaultOvertime += 1;
				}

			}

			if (otDefaultListCopy.size() != 0) {

				if (countNonDefaultOvertime > 0) {
					for (int i = 0; i < countNonDefaultOvertime; i++) {
						otDefaultListCopy.remove(otDefaultListCopy.size() - 1);
					}

					otDefaultListCopy.addAll(this.mainApplication.getOvertimeHeaderList());
					this.mainApplication.getOvertimeHeaderList().clear();
					this.mainApplication.getOvertimeHeaderList().addAll(otDefaultListCopy);

					Collections.sort(this.mainApplication.getOvertimeHeaderList(),
							(a, b) -> a.getPrimaryKey().compareTo(b.getPrimaryKey()));
				} else {
					this.mainApplication.getOvertimeHeaderList().clear();
					this.mainApplication.getOvertimeHeaderList().addAll(this.otDefaultObjectList);
				}

			} else {
				// exceeded ot type > 13
			}
		}
	}

	public void handleCancel() {
		stage.close();
	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub
	}

	public void setCutoffByDepartment(Department newValue) {
		List<EmployeeScheduleUploading> bioList = this.mainApplication
				.getDataByClientDepartmentPayFromPayTo(this.selectedClient, newValue, this.payFrom, this.payTo, 0);

		if (bioList.isEmpty()) {
			bioList = this.mainApplication.getDataByClientDepartmentPayFromPayTo(this.selectedClient, newValue,
					this.payFrom, this.payTo, 1);
		}

		if (bioList != null && !bioList.isEmpty()) {
			EmployeeScheduleUploading employeeScheduleUploading = bioList.get(0);
			this.cutoffFrom = employeeScheduleUploading.getCutoffFrom();
			this.cutoffTo = employeeScheduleUploading.getCutoffTo();
		} else {
			this.cutoffFrom = null;
			this.cutoffTo = null;
		}
	}

	public void setCutoffByClient(Client newValue) {
		List<EmployeeScheduleUploading> bioList = this.mainApplication.getDataByClientPayFromPayTo(newValue,
				this.payFrom, this.payTo);

		if (bioList.size() != 0) {
			EmployeeScheduleUploading employeeScheduleUploading = bioList.get(0);
			this.cutoffFrom = employeeScheduleUploading.getCutoffFrom();
			this.cutoffTo = employeeScheduleUploading.getCutoffTo();
		} else {
			this.cutoffFrom = null;
			this.cutoffTo = null;
		}
	}

	public void setCutoffByEmployee(Employee newValue) {

		if (newValue == null) {
			// this.labelCutoffFrom.setText("");
			// this.labelCutoffTo.setText("");
			return;
		}

		List<EmployeeScheduleUploading> bioList = this.mainApplication
				.getDataByEmployeeIdPayFrom(newValue.getEmployeeCode(), this.payFrom);

		if (bioList.size() != 0) {
			EmployeeScheduleUploading employeeScheduleUploading = bioList.get(0);

			this.cutoffFrom = employeeScheduleUploading.getCutoffFrom();
			this.cutoffTo = employeeScheduleUploading.getCutoffTo();

			// this.labelCutoffFrom.setText(new
			// SimpleDateFormat("MM-dd-yyyy").format(this.cutoffFrom));
			// this.labelCutoffTo.setText(new
			// SimpleDateFormat("MM-dd-yyyy").format(this.cutoffTo));
		} else {
			this.cutoffFrom = null;
			this.cutoffTo = null;

			// this.labelCutoffFrom.setText("");
			// this.labelCutoffTo.setText("");
		}
	}

	public void setFieldListener() {
		this.comboBoxClient.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedClient = newValue;
				this.comboBoxDepartment.setDisable(false);

				this.setCutoffByClient(newValue);

				this.obsListEmployee.setAll(this.mainApplication.populateEmployee(newValue, null));
				this.comboBoxEmployee.setItems(this.obsListEmployee, p -> p.getEmployeeFullName());

				this.obsListDepartment.clear();
				List<String> deptCodeList = this.obsListEmployee.stream().map(p -> p.getDepartment()).distinct()
						.collect(Collectors.toList());

				deptCodeList.forEach(deptCode -> {
					this.obsListDepartment.add(mainApplication.getDepartmentMain().getDepartmentByCode(deptCode));
				});

				this.comboBoxDepartment.setItems(this.obsListDepartment, p -> p.getDepartmentName());

				this.obsListEmpID.setAll(
						this.obsListEmployee.stream().map(p -> p.getEmployeeCode()).collect(Collectors.toList()));
				this.comboBoxEmpID.setItems(this.obsListEmpID, p -> String.valueOf(p));

			} else {
				this.selectedClient = null;
				this.comboBoxDepartment.setDisable(true);

				this.obsListDepartment.clear();
				this.comboBoxDepartment.setItems(null);

				this.obsListEmployee.setAll(this.mainApplication.populateEmployee(null, null));
				this.comboBoxEmployee.setItems(this.obsListEmployee, p -> p.getEmployeeFullName());

				this.obsListEmpID.setAll(
						this.obsListEmployee.stream().map(p -> p.getEmployeeCode()).collect(Collectors.toList()));
				this.comboBoxEmpID.setItems(this.obsListEmpID, p -> String.valueOf(p));

				this.cutoffFrom = this.mainApplication.getCutoffDateFrom();
				this.cutoffTo = this.mainApplication.getCutoffDateTo();

				// this.labelCutoffFrom.setText(new
				// SimpleDateFormat("MM-dd-yyyy").format(this.cutoffFrom));
				// this.labelCutoffTo.setText(new
				// SimpleDateFormat("MM-dd-yyyy").format(this.cutoffTo));
				// this.labelCutoffFrom.setText("");
				// this.labelCutoffTo.setText("");
			}
		});

		this.comboBoxDepartment.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedDepartment = newValue;

				this.obsListEmployee.setAll(this.mainApplication.populateEmployee(this.selectedClient, newValue));
				this.comboBoxEmployee.setItems(this.obsListEmployee, p -> p.getEmployeeFullName());

				this.obsListEmpID.setAll(
						this.obsListEmployee.stream().map(p -> p.getEmployeeCode()).collect(Collectors.toList()));
				this.comboBoxEmpID.setItems(this.obsListEmpID, p -> String.valueOf(p));

			} else {
				this.selectedDepartment = null;

				this.obsListEmployee.setAll(this.mainApplication.populateEmployee(this.selectedClient, null));
				this.comboBoxEmployee.setItems(this.obsListEmployee, p -> p.getEmployeeFullName());

				this.obsListEmpID.setAll(
						this.obsListEmployee.stream().map(p -> p.getEmployeeCode()).collect(Collectors.toList()));
				this.comboBoxEmpID.setItems(this.obsListEmpID, p -> String.valueOf(p));
			}
		});

		this.comboBoxEmployee.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			this.setCutoffByEmployee(newValue);
			if (newValue != null) {
				this.comboBoxEmpID.setValue(this.obsListEmpID.stream().filter(p -> p.equals(newValue.getEmployeeCode()))
						.findFirst().get().toString());
			} else {
				this.comboBoxEmpID.setValue(null);
			}
		});

		this.comboBoxEmpID.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.toString().trim().length() == 6) {
				this.comboBoxEmployee.setValue(this.obsListEmployee.stream()
						.filter(p -> p.getEmployeeCode().equals(newValue)).findFirst().get().getEmployeeFullName());
			} else {
				this.comboBoxEmployee.setValue(null);
			}
		});

	}

	public void initializeFields() {
		this.obsListClient.setAll(this.mainApplication.getObsListClient());
		this.obsListEmployee.setAll(this.mainApplication.getEmployeeObservableList());
		this.obsListEmpID.setAll(this.mainApplication.getObsListEmpID());
		// this.cutoffFrom = this.mainApplication.getCutoffDateFrom();
		// this.cutoffTo = this.mainApplication.getCutoffDateTo();

		this.payFrom = this.mainApplication.getPayPeriodDateFrom();
		this.payTo = this.mainApplication.getPayPeriodDateTo();

		this.labelPayFrom.setText(new SimpleDateFormat("MM-dd-yyyy").format(this.payFrom));
		this.labelPayTo.setText(new SimpleDateFormat("MM-dd-yyyy").format(this.payTo));

		this.comboBoxClient.setItems(this.obsListClient, p -> p.getClientName());
		this.comboBoxEmpID.setItems(this.obsListEmpID, p -> String.valueOf(p));
		this.comboBoxEmployee.setItems(this.obsListEmployee, p -> p.getEmployeeFullName());

		// this.labelCutoffFrom.setText(new
		// SimpleDateFormat("MM-dd-yyyy").format(this.cutoffFrom));
		// this.labelCutoffTo.setText(new
		// SimpleDateFormat("MM-dd-yyyy").format(this.cutoffTo));

		this.comboBoxDepartment.setDisable(true);
		this.selectedClient = null;
		this.selectedDepartment = null;

		// this.obsListEmployee
		// .setAll(this.mainApplication.populateEmployee(this.selectedClient,
		// this.selectedDepartment));
		// this.comboBoxEmployee.setItems(this.obsListEmployee, p ->
		// p.getEmployeeFullName());
	}

	public void printEmployee(Map<String, Object> parameterReport) {
		Employee employee = this.comboBoxEmployee.getValueObject();
		List<EmployeeScheduleUploading> entryByEmpList = new ArrayList<>();

		boolean isIntegral = employee.getClient().compareTo("LBPSC") == 0 ? true : false;

		this.empIdList.add(employee.getEmployeeCode());

		entryByEmpList
				.addAll(this.mainApplication.getDataByEmployeeIdPayFrom(employee.getEmployeeCode(), this.payFrom));

		if (isIntegral) {
			this.empIdList.forEach(employeeCode -> {
				this.biometricsReportList.addAll(this.formatDetailsOvertimeList(employeeCode, entryByEmpList));
			});
		} else {
			this.biometricsReportList.addAll(entryByEmpList);
		}

		this.generateReportByClientFormat(biometricsReportList, parameterReport);
	}

	public void printClient(boolean isIntegral, Map<String, Object> parameterReport) {
		List<EmployeeScheduleUploading> bioPerClientPayFromPayToList = new ArrayList<>();

		this.empIdList.addAll(this.mainApplication
				.getAllEmployeeIdByClientCodePayFromPayTo(this.selectedClient.getClientCode(), payFrom, payTo));

		bioPerClientPayFromPayToList
				.addAll(this.mainApplication.getDataByClientPayFromPayToAll(this.selectedClient, payFrom, payTo));

		if (isIntegral) {
			Task<Void> taskPrintPerClient = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					int counter = 0;
					for (Integer employeeCode : empIdList) {
						List<EmployeeScheduleUploading> bioPerEmployeeList = new ArrayList<>();
						bioPerEmployeeList = bioPerClientPayFromPayToList.stream().filter(
								p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeCode))
								.collect(Collectors.toList());

						biometricsReportList.addAll(formatDetailsOvertimeList(employeeCode, bioPerEmployeeList));

						counter = counter + 1;
						updateProgress(ProgressUtil.getProgressValue(counter, empIdList.size()), 1D);
						updateMessage(ProgressUtil.getMessageValue("Processing...\n", counter, empIdList.size()));
					}

					counter = 0;
					Platform.runLater(() -> {
						generateReportByClientFormat(biometricsReportList, parameterReport);
					});
					return null;
				}
			};
			ProgressUtil.showProcessInterface(this.getStage(), taskPrintPerClient, false);

		} else {
			this.biometricsReportList.addAll(bioPerClientPayFromPayToList);
			this.generateReportByClientFormat(this.biometricsReportList, parameterReport);
		}
	}

	public void printDepartment(boolean isIntegral, Map<String, Object> parameterReport) {
		this.empIdList.addAll(this.mainApplication.getAllEmployeeIdByClientCodeDepartmentPayFromPayTo(
				this.selectedClient.getClientCode(), this.selectedDepartment.getDepartmentCode(), payFrom, payTo));

		List<EmployeeScheduleUploading> bioByDepartmentList = new ArrayList<>();

		bioByDepartmentList.addAll(this.mainApplication.getDataByClientDepartmentPayFromPayTo(this.selectedClient,
				this.selectedDepartment, payFrom, payTo, 0));
		bioByDepartmentList.addAll(this.mainApplication.getDataByClientDepartmentPayFromPayTo(this.selectedClient,
				this.selectedDepartment, payFrom, payTo, 1));

		if (isIntegral) {
			this.empIdList.forEach(employeeCode -> {
				this.biometricsReportList.addAll(this.formatDetailsOvertimeList(employeeCode, bioByDepartmentList));
			});
		} else {
			this.biometricsReportList.addAll(bioByDepartmentList);
		}

		this.generateReportByClientFormat(biometricsReportList, parameterReport);
	}

	public void handlePrint() {
		if (this.isValid()) {
			this.biometricsReportList.clear();
			this.empIdList.clear();

			Map<String, Object> parameterReport = new HashMap<String, Object>();

			String pathImage = System.getenv("LOCALAPPDATA").concat(
					File.separator + "IFMIS" + File.separator + "app" + File.separator + "resources" + File.separator);
			String payPeriod = this.mainApplication.getPayPeriod(this.payFrom, this.payTo);

			parameterReport.put("pathImage", pathImage);
			parameterReport.put("payPeriod", payPeriod);
			parameterReport.put("overallTotalMinPerEmployeeHashMap", this.overallTotalMinPerEmployeeHashMap);
			parameterReport.put("totalMinPerOvertimeTypeHashMap", this.totalMinPerOvertimeTypeHashMap);

			parameterReport.put("totalHrsHashMap", this.totalHrsHashMap);

			if (this.selectedClient != null) {
				boolean isIntegral = this.selectedClient.getClientCode().compareTo("LBPSC") == 0 ? true : false;
				if (this.selectedDepartment != null) {
					if (this.comboBoxEmployee.getValueObject() != null) {
						this.printEmployee(parameterReport);
						return;
					}
					this.printDepartment(isIntegral, parameterReport);
					return;
				}

				if (this.comboBoxEmployee.getValueObject() != null) {
					this.printEmployee(parameterReport);
					return;
				} else {
					this.printClient(isIntegral, parameterReport);
					return;
				}

			} else {
				if (this.comboBoxEmployee.getValueObject() != null) {
					this.printEmployee(parameterReport);
					return;
				}
			}
		}
	}

	public void generateReportMIAA(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingMIAA.jasper");

		this.setScheduleParameterDefault(parameterReport);
		this.setNightDiffParameterBio(biometricsReportList, parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportByClientFormat(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {

		Client client = new Client();

		if (this.selectedClient != null) {
			client = this.selectedClient;
		} else {
			if (this.comboBoxEmployee.getValueObject() != null) {
				EmploymentHistory employmentHistory = new EmploymentHistory();
				employmentHistory = this.mainApplication.getEmploymentHistoryMain()
						.getEmploymentHistoryMaxByEmployeeCode(
								this.comboBoxEmployee.getValueObject().getEmployeeCode());
				client = employmentHistory.getClient();
			}
		}

		if (client != null) {
			switch (client.getCompany().getCompanyCode()) {
			case 75: // lserv
				this.generateReportLSERV(biometricsReportList, parameterReport);
				break;
			case 80: // miaa
				this.generateReportMIAA(biometricsReportList, parameterReport);
				break;
			case 189:
				this.generateReportPDIC(biometricsReportList, parameterReport);
				this.generateReportPDICAudited(biometricsReportList, parameterReport);
				break;
			case 215:
				this.generateReportGMRMCAC(biometricsReportList, parameterReport);
				break;
			case 16: // bsp
			case 315:
			case 387:
				this.generateReportBSP(biometricsReportList, parameterReport);
				this.generateReportBSPAudited(biometricsReportList, parameterReport);
				break;
			case 323: // phlshss
				this.generateReportPHLSHSS(biometricsReportList, parameterReport);
				break;
			case 479: // doe
			case 586:
			case 183: // dole1
			case 393: // pgc
				this.generateReportDOE(biometricsReportList, parameterReport);
				break;
			case 654: // themactan
				this.generateReportTheMactan(biometricsReportList, parameterReport);
				break;
			case 337: // csc
				this.generateReportCSC(biometricsReportList, parameterReport);
				break;
			case 106: // PFDAJAN
				this.generateReportBSPAudited(biometricsReportList, parameterReport);
				break;
			case 448: // caap
			case 452:
			case 449:
			case 450:
			case 451:
			case 447:
			case 587:
			case 588:
			case 589:
			case 590:
			case 591:
			case 592:
			case 593:
			case 594:
			case 595:
			case 596:
			case 500: // neda
			case 330: // phlcomcom
			case 696: // caapgenc2
			case 695:
			case 697:
			case 698:
			case 699:
				this.generateReportCAAP(biometricsReportList, parameterReport);
				break;
			case 405: // sss
			case 406:
			case 407:
			case 408:
			case 409:
			case 410:
			case 411:
			case 412:
			case 413:
			case 414:
			case 415:
			case 416:
			case 417:
			case 418:
			case 419:
			case 420:
			case 421:
			case 422:
			case 423:
			case 424:
			case 425:
			case 426:
			case 427:
			case 428:
			case 429:
			case 430:
			case 431:
			case 432:
			case 433:
			case 434:
			case 435:
			case 436:
			case 437:
			case 438:
			case 439:
			case 456:
			case 457:
			case 458:
			case 459:
			case 460:
			case 461:
			case 462:
			case 463:
			case 464:
			case 465:
			case 466:
			case 467:
			case 468:
			case 469:
			case 470:
			case 471:
			case 472:
			case 473:
			case 482:
			case 492:
			case 493:
			case 494:
			case 502:
			case 503:
			case 539:
			case 551:
			case 552:
			case 555:
			case 558:
			case 559:
			case 564:
			case 580:
			case 581:
			case 600:
			case 601:
			case 602:
			case 603:
			case 604:
			case 605:
			case 606:
			case 607:
			case 611:
			case 616:
			case 617:
			case 618:
			case 619:
			case 620:
			case 621:
			case 622:
			case 623:
			case 624:
			case 625:
			case 626:
				this.generateReportSSS(biometricsReportList, parameterReport);
				break;
			case 665:
				this.generateReportNPARKS(biometricsReportList, parameterReport);
				break;
			case 526: // newport
				this.generateReportNEWPORT(biometricsReportList, parameterReport);
				break;
			case 585:
				this.generateReportZAMCMC(biometricsReportList, parameterReport);
				break;
			default:
				this.generateReportDefault(biometricsReportList, parameterReport);
				break;
			}
		}
	}

	public void generateReportNPARKS(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingNPARK.jasper");

		this.computeSummaryClientStandard(parameterReport);

		String payPeriod = "For the period of " + new SimpleDateFormat("MMMM d").format(this.payFrom) + "-"
				+ new SimpleDateFormat("d, yyyy").format(this.payTo);

		parameterReport.put("payPeriod", payPeriod);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportGMRMCAC(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingGMRMCAC.jasper");

		this.computeTotalNoOfHrsPerEntry(biometricsReportList, parameterReport);

		this.setScheduleParameterDefault(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void computeTotalNoOfHrsPerEntry(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {

		HashMap<Integer, String> grandTotalHashMap = new HashMap<>();

		for (Integer employeeCode : this.empIdList) {
			List<EmployeeScheduleUploading> bioPerEmployeeList = biometricsReportList.stream()
					.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeCode))
					.collect(Collectors.toList());

			if (bioPerEmployeeList != null && !bioPerEmployeeList.isEmpty()) {
				Integer grandTotal = 0;
				DecimalFormat decimalFormat = new DecimalFormat("##");

				for (EmployeeScheduleUploading bio : bioPerEmployeeList) {
					if (bio.getTimeInEntry() != null && bio.getTimeOutEntry() != null) {
						Integer totalNoOfMins = this.mainApplication.computeTotalMinsEntry(bio.getTimeInEntry(),
								bio.getTimeOutEntry());

						if (totalNoOfMins >= 540) {
							totalNoOfMins = totalNoOfMins - 60;
						}

						Integer hrs = totalNoOfMins / 60;
						Integer mins = totalNoOfMins % 60;

						String hrsFormatted = hrs < 10 ? "0" + String.valueOf(hrs) : decimalFormat.format(hrs);
						String minsFormatted = mins < 10 ? "0" + String.valueOf(mins) : decimalFormat.format(mins);

						String totalNoOfHrsFormatted = hrsFormatted + ":" + minsFormatted;

						bio.setTotalHrs(hrs);
						bio.setTotalMins(mins);

						bio.setTotalNoOfHrs(totalNoOfHrsFormatted);

						grandTotal = grandTotal + totalNoOfMins;
					}
				}
				String grandTotalFormatted = "";

				Integer grandTotalHrs = grandTotal / 60;
				Integer grandTotalMins = grandTotal % 60;

				String grandTotalHrsFormatted = grandTotalHrs < 10 ? "0".concat(decimalFormat.format(grandTotalHrs))
						: decimalFormat.format(grandTotalHrs);
				String grandTotalMinsFormatted = grandTotalMins < 10 ? "0".concat(decimalFormat.format(grandTotalMins))
						: decimalFormat.format(grandTotalMins);

				grandTotalFormatted = grandTotalHrsFormatted + ":" + grandTotalMinsFormatted;
				grandTotalHashMap.put(employeeCode, grandTotalFormatted);
			}
		}

		parameterReport.put("grandTotalHashMap", grandTotalHashMap);

	}

	public void generateReportZAMCMC(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingZAMCMC.jasper");

		this.computeSummaryClientStandard(parameterReport);
		this.setScheduleParameterDefault(parameterReport);

		String payPeriod = "For the period of " + new SimpleDateFormat("MMMM d").format(this.payFrom).toUpperCase()
				+ "-" + new SimpleDateFormat("d, yyyy").format(this.payTo);
		parameterReport.put("payPeriod", payPeriod);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void setNightDiffParameterBio(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {

		for (Integer empId : empIdList) {
			List<EmployeeScheduleUploading> bioList = biometricsReportList.stream()
					.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(empId))
					.collect(Collectors.toList());

			if (!bioList.isEmpty()) {

				for (EmployeeScheduleUploading biometric : bioList) {
					if (biometric.getOvertimeBreakdownList() != null
							&& !biometric.getOvertimeBreakdownList().isEmpty()) {

						for (EmployeeScheduleUploadingOvertimeBreakdown otBreakdown : biometric
								.getOvertimeBreakdownList()) {
							Integer nightDiffPrikey = 1;
							if (otBreakdown != null) {
								if (otBreakdown.getOvertimeType().getPrimaryKey().equals(nightDiffPrikey)) {
									biometric.setInitialTotalMinsNightDiff(otBreakdown.getTotalMin());
								}
							}
						}

					}
				}

			}

		}
	}

	public void generateReportPDICAudited(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingPDICAudited.jasper");

		this.setScheduleParameterDefault(parameterReport);
		this.computeSummaryClientStandard(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportDefault(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingClientStandardDefault.jasper");

		this.setScheduleParameterDefault(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportBSPAudited(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingBSPAudited.jasper");

		String payPeriod = "For the period of " + new SimpleDateFormat("MMMM d").format(this.payFrom) + "-"
				+ new SimpleDateFormat("d, yyyy").format(this.payTo);
		parameterReport.put("payPeriod", payPeriod);

		this.computeSummaryClientStandard(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportBSP(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingBSP.jasper");

		String payPeriod = "For the period of " + new SimpleDateFormat("MMMM d").format(this.payFrom) + "-"
				+ new SimpleDateFormat("d, yyyy").format(this.payTo);
		parameterReport.put("payPeriod", payPeriod);

		this.computeSummaryClientStandard(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportPDIC(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingPDIC.jasper");

		this.setScheduleParameterDefault(parameterReport);
		this.computeSummaryClientStandard(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void computeSummaryClientStandard(Map<String, Object> parameterReport) {
		HashMap<Integer, Integer> totalWorkingDaysByEmpIdHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalHolidaysByEmpIdHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalAbsentsByEmpIdHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalLatesByEmpIdHashMap = new HashMap<>();
		HashMap<Integer, Integer> frequencyOfLatesByEmpIdHashMap = new HashMap<>();

		HashMap<Integer, BigDecimal> NDHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> ROTHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> SOTHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> SOTEHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> LOTHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> LOTEHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> RSOTHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> RSOTEHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> RLOTHashMap = new HashMap<>();
		HashMap<Integer, BigDecimal> RLOTEHashMap = new HashMap<>();

		ObservableList<EmploymentHistory> empHistoryObsList = FXCollections.observableArrayList();

		for (Integer employeeID : this.empIdList) {
			EmploymentHistory employmentHistory = this.mainApplication.getEmploymentHistoryMain()
					.getEmploymentHistoryMaxByEmployeeCode(employeeID);
			empHistoryObsList.add(employmentHistory);
		}

		ObservableListUtil.sort(empHistoryObsList, p -> p.getEmployee().getFullName());

		for (EmploymentHistory employmentHistory : empHistoryObsList) {
			Integer employeeCode = employmentHistory.getEmployee().getEmployeeCode();

			List<Integer> overtimeTypePrikeyList = new ArrayList<>();
			overtimeTypePrikeyList = this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
					.getAllOvertimeTypeDistinctByPayFromPayToClientCd(payFrom, payTo,
							employmentHistory.getClient().getClientCode());

			boolean isDaily = employmentHistory.getEmploymentConfiguration().getIsDaily();

			Integer numberOfRegularDays = 0;
			Integer numberOfHolidays = 0;
			Integer frequencyOfLate = 0;
			Integer totalLate = 0;
			Integer totalAbsent = 0;

			if (isDaily) {
				numberOfRegularDays = this.mainApplication.countWorkingDaysByEmpIdPayFromPayTo(employeeCode, payFrom,
						payTo);
			}

			numberOfHolidays = this.mainApplication.countHolidaysByEmpIdPayFrom(employeeCode, payFrom);
			frequencyOfLate = this.mainApplication.countFrequencyOfLateByEmpIdPayFrom(employeeCode, payFrom);
			totalLate = this.mainApplication.computeTotalLateByEmpIdPayFrom(employeeCode, payFrom);
			totalAbsent = this.mainApplication.countTotalAbsentByEmpIdPayFrom(employeeCode, payFrom);

			frequencyOfLatesByEmpIdHashMap.put(employeeCode, frequencyOfLate == null ? 0 : frequencyOfLate);
			totalLatesByEmpIdHashMap.put(employeeCode, totalLate == null ? 0 : totalLate);
			totalAbsentsByEmpIdHashMap.put(employeeCode, totalAbsent == null ? 0 : totalAbsent);
			totalHolidaysByEmpIdHashMap.put(employeeCode, numberOfHolidays == null ? 0 : numberOfHolidays);
			totalWorkingDaysByEmpIdHashMap.put(employeeCode, numberOfRegularDays == null ? 0 : numberOfRegularDays);

			List<EmployeeScheduleUploading> bioByEmployeeList = new ArrayList<>();
			bioByEmployeeList.addAll(biometricsReportList.stream()
					.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeCode))
					.collect(Collectors.toList()));

			this.computeHrsAndMinsEachOvertimePerBio(bioByEmployeeList, overtimeTypePrikeyList);

			this.computeAllOvertimeSummaryClientStandard(employeeCode, bioByEmployeeList, NDHashMap, ROTHashMap,
					SOTHashMap, SOTEHashMap, LOTHashMap, LOTEHashMap, RSOTHashMap, RSOTEHashMap, RLOTHashMap,
					RLOTEHashMap);

		}

		parameterReport.put("totalWorkingDaysByEmpIdHashMap", totalWorkingDaysByEmpIdHashMap);
		parameterReport.put("totalHolidaysByEmpIdHashMap", totalHolidaysByEmpIdHashMap);
		parameterReport.put("totalLatesByEmpIdHashMap", totalLatesByEmpIdHashMap);
		parameterReport.put("totalAbsentsByEmpIdHashMap", totalAbsentsByEmpIdHashMap);
		parameterReport.put("frequencyOfLatesByEmpIdHashMap", frequencyOfLatesByEmpIdHashMap);
		parameterReport.put("NDHashMap", NDHashMap);
		parameterReport.put("ROTHashMap", ROTHashMap);
		parameterReport.put("SOTHashMap", SOTHashMap);
		parameterReport.put("SOTEHashMap", SOTEHashMap);
		parameterReport.put("LOTHashMap", LOTHashMap);
		parameterReport.put("LOTEHashMap", LOTEHashMap);
		parameterReport.put("RSOTHashMap", RSOTHashMap);
		parameterReport.put("RSOTEHashMap", RSOTEHashMap);
		parameterReport.put("RLOTHashMap", RLOTHashMap);
		parameterReport.put("RLOTEHashMap", RLOTEHashMap);
	}

	public void computeHrsOvertimeSummary(BigDecimal overtimeTypeSummaryTotal) {
		overtimeTypeSummaryTotal = overtimeTypeSummaryTotal.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
	}

	public void computeAllOvertimeSummaryClientStandard(Integer employeeCode,
			List<EmployeeScheduleUploading> biometricsReportList, HashMap<Integer, BigDecimal> NDHashMap,
			HashMap<Integer, BigDecimal> ROTHashMap, HashMap<Integer, BigDecimal> SOTHashMap,
			HashMap<Integer, BigDecimal> SOTEHashMap, HashMap<Integer, BigDecimal> LOTHashMap,
			HashMap<Integer, BigDecimal> LOTEHashMap, HashMap<Integer, BigDecimal> RSOTHashMap,
			HashMap<Integer, BigDecimal> RSOTEHashMap, HashMap<Integer, BigDecimal> RLOTHashMap,
			HashMap<Integer, BigDecimal> RLOTEHashMap) {

		BigDecimal ND = BigDecimal.ZERO;
		BigDecimal ROT = BigDecimal.ZERO;
		BigDecimal SOT = BigDecimal.ZERO;
		BigDecimal SOTE = BigDecimal.ZERO;
		BigDecimal LOT = BigDecimal.ZERO;
		BigDecimal LOTE = BigDecimal.ZERO;
		BigDecimal RSOT = BigDecimal.ZERO;
		BigDecimal RSOTE = BigDecimal.ZERO;
		BigDecimal RLOT = BigDecimal.ZERO;
		BigDecimal RLOTE = BigDecimal.ZERO;

		for (EmployeeScheduleUploading employeeScheduleUploading : biometricsReportList) {
			if (employeeScheduleUploading.getOvertimeBreakdownList() != null) {
				for (EmployeeScheduleUploadingOvertimeBreakdown overtimeBreakdown : employeeScheduleUploading
						.getOvertimeBreakdownList()) {
					if (overtimeBreakdown != null) {
						switch (overtimeBreakdown.getOvertimeType().getPrimaryKey()) {
						case 1: // ot10
							ND = ND.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setND(overtimeBreakdown.getTotalMin());
							break;
						case 3: // ot30
						case 7: // ot130
							SOT = SOT.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setSOT(overtimeBreakdown.getTotalMin());
							break;
						case 5: // ot100
							LOT = LOT.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setLOT(overtimeBreakdown.getTotalMin());
							break;
						case 6: // ot125
							ROT = ROT.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setROT(overtimeBreakdown.getTotalMin());
							break;
						case 9: // ot150
							// custom config ot150 as ot30/ot169
							if (this.mainApplication.getEmployeeScheduleUploadingOvertimeBreakdownMain()
									.getClientPrikeyBreakdownOT30OT169List()
									.contains(employeeScheduleUploading.getClient().getCompany().getCompanyCode())) {
								RSOT = RSOT.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
								employeeScheduleUploading.setSOT(overtimeBreakdown.getTotalMin());
							} else {
								RSOT = RSOT.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
								employeeScheduleUploading.setRSOT(overtimeBreakdown.getTotalMin());
							}
							break;
						case 10: // ot160
							RLOT = RLOT.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setRLOT(overtimeBreakdown.getTotalMin());
							break;
						case 11: // ot169
							SOTE = SOTE.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setSOTE(overtimeBreakdown.getTotalMin());
							break;
						case 14: // ot195
							RSOTE = RSOTE.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setRSOTE(overtimeBreakdown.getTotalMin());
							break;
						case 18: // ot260
							LOTE = LOTE.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setLOTE(overtimeBreakdown.getTotalMin());
							break;
						case 19: // ot338
							RLOTE = RLOTE.add(new BigDecimal(overtimeBreakdown.getTotalMin()));
							employeeScheduleUploading.setRLOTE(overtimeBreakdown.getTotalMin());
						default:
							break;
						}
					}
				}
			}
		}

		// RSOT = SOT.add(LOT);
		// RLOT = ROT.add(LOT); // not sure
		// RLOTE = ROT.add(LOTE); // not sure

		ND = ND.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		ROT = ROT.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		SOT = SOT.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		SOTE = SOTE.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		LOT = LOT.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		LOTE = LOTE.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		RSOT = RSOT.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		RSOTE = RSOTE.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		RLOT = RLOT.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);
		RLOTE = RLOTE.divide(new BigDecimal(60), 2, RoundingMode.HALF_UP);

		NDHashMap.put(employeeCode, ND);
		ROTHashMap.put(employeeCode, ROT);
		SOTHashMap.put(employeeCode, SOT);
		SOTEHashMap.put(employeeCode, SOTE);
		LOTHashMap.put(employeeCode, LOT);
		LOTEHashMap.put(employeeCode, LOTE);
		RSOTHashMap.put(employeeCode, RSOT);
		RSOTEHashMap.put(employeeCode, RSOTE);
		RLOTHashMap.put(employeeCode, RLOT);
		RLOTEHashMap.put(employeeCode, RLOTE);

	}

	public void generateReportDOE(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingDOE.jasper");

		this.computeSummaryClientStandard(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportLSERV(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingLSERV.jasper");

		parameterReport.put("overtimeHeaderList", this.mainApplication.getOvertimeHeaderList());

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportNEWPORT(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingNEWPORT.jasper");

		this.setScheduleParameterDefault(parameterReport);

		String payPeriod = new SimpleDateFormat("MM/dd/yy").format(payFrom) + " - "
				+ new SimpleDateFormat("MM/dd/yy").format(payTo);

		parameterReport.put("payPeriod", payPeriod);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void setScheduleParameterDefault(Map<String, Object> parameterReport) {
		ObservableList<EmploymentHistory> employmentHistoryObsList = FXCollections.observableArrayList();
		HashMap<Integer, String> scheduleEmployeeHashMap = new HashMap<>();

		for (Integer employeeCode : this.empIdList) {
			EmploymentHistory employmentHistory = new EmploymentHistory();
			employmentHistory = this.mainApplication.getEmploymentHistoryMain()
					.getEmploymentHistoryMaxByEmployeeCode(employeeCode);

			employmentHistoryObsList.add(employmentHistory);
		}

		ObservableListUtil.sort(employmentHistoryObsList, p -> p.getEmployee().getEmployeeFullName());

		for (EmploymentHistory employmentHistory : employmentHistoryObsList) {
			Integer employeeCode = employmentHistory.getEmployee().getEmployeeCode();

			String scheduleTime = "";
			String timeFormat = "HH:mm";

			List<ScheduleEncoding> scheduleEncodingList = this.mainApplication.getScheduleEncodingMain()
					.getAllEmployeeScheduleByEmpId(employeeCode);

			if (!scheduleEncodingList.isEmpty()) {
				scheduleTime = new SimpleDateFormat(timeFormat).format(scheduleEncodingList.get(0).getTimeIn()) + " to "
						+ new SimpleDateFormat(timeFormat).format(scheduleEncodingList.get(0).getTimeOut());
			}

			scheduleEmployeeHashMap.put(employeeCode, scheduleTime);
		}

		parameterReport.put("scheduleEmployeeHashMap", scheduleEmployeeHashMap);
	}

	public void generateReportSSS(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingSSS.jasper");

		this.setScheduleParameterDefault(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportPHLSHSS(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingPHLSHSS.jasper");
		// report =
		// this.mainApplication.getClass().getResourceAsStream("report/EmployeeScheduleUploadingPHLSHSS.jasper");

		this.computeSummaryClientStandard(parameterReport);

		String payPeriod = "For the period of " + new SimpleDateFormat("MMMM d").format(this.payFrom) + "-"
				+ new SimpleDateFormat("d, yyyy").format(this.payTo);

		parameterReport.put("payPeriod", payPeriod);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportCSC(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingCSC.jasper");

		String payPeriod = "For the period of " + new SimpleDateFormat("MMMM d").format(this.payFrom) + "-"
				+ new SimpleDateFormat("d, yyyy").format(this.payTo);

		parameterReport.put("payPeriod", payPeriod);

		this.computeSummaryClientStandard(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportCAAP(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {
		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingCAAP.jasper");

		String payPeriod = "For the period of " + new SimpleDateFormat("MMMM d").format(this.payFrom) + "-"
				+ new SimpleDateFormat("d, yyyy").format(this.payTo);

		parameterReport.put("payPeriod", payPeriod);

		this.computeSummaryClientStandard(parameterReport);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void generateReportTheMactan(List<EmployeeScheduleUploading> biometricsReportList,
			Map<String, Object> parameterReport) {

		InputStream report = null;
		report = ReportMain.class.getResourceAsStream("reports/EmployeeScheduleUploadingTheMactan.jasper");

		String payPeriod = new SimpleDateFormat("MMM dd").format(this.payFrom).toUpperCase() + "-"
				+ new SimpleDateFormat("dd, yyyy").format(this.payTo);

		ObservableList<EmploymentHistory> employmentHistoryObsList = FXCollections.observableArrayList();
		HashMap<Integer, String> positionNameHashMap = new HashMap<>();
		HashMap<Integer, String> scheduleEmployeeHashMap = new HashMap<>();
		HashMap<Integer, String> dayOffEmployeeHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalNightDiffHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalRegularOTHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalRestDayOTHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalExcessRestDayOTHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalLegalHolidayOTHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalExcessLegalHolidayHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalSpecialHolidayHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalExcessSpecialHolidayHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalLegalHolidayRestDayHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalExcessLegalHolidayRestDayHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalSpecialHolidayRestDayHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalExcessSpecialHolidayRestDayHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalLatesByEmpIdHashMap = new HashMap<>();
		HashMap<Integer, Integer> totalAbsentsByEmpIdHashMap = new HashMap<>();

		for (Integer employeeCode : this.empIdList) {
			EmploymentHistory employmentHistory = new EmploymentHistory();
			employmentHistory = this.mainApplication.getEmploymentHistoryMain()
					.getEmploymentHistoryMaxByEmployeeCode(employeeCode);

			employmentHistoryObsList.add(employmentHistory);
		}

		ObservableListUtil.sort(employmentHistoryObsList, p -> p.getEmployee().getEmployeeFullName());

		for (EmploymentHistory employmentHistory : employmentHistoryObsList) {
			Integer employeeCode = employmentHistory.getEmployee().getEmployeeCode();

			Position position = new Position();
			position = this.mainApplication.getPositionMain()
					.getPositionByCode(employmentHistory.getEmployee().getPosition());

			positionNameHashMap.put(employeeCode, position.getPositionName());

			String[] result = this.mainApplication.getWorkDayAndDayOff(employeeCode);
			String scheduleTime = result[0];
			String dayOff = result[1];

			scheduleEmployeeHashMap.put(employeeCode, scheduleTime);
			dayOffEmployeeHashMap.put(employeeCode, dayOff);

			Integer totalLate = this.mainApplication.computeTotalLateByEmpIdPayFrom(employeeCode, payFrom);
			Integer totalAbsent = this.mainApplication.countTotalAbsentByEmpIdPayFrom(employeeCode, payFrom);

			totalLatesByEmpIdHashMap.put(employeeCode, totalLate);
			totalAbsentsByEmpIdHashMap.put(employeeCode, totalAbsent);

			List<EmployeeScheduleUploading> bioByEmployeeList = new ArrayList<>();

			bioByEmployeeList.addAll(biometricsReportList.stream()
					.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeCode))
					.collect(Collectors.toList()));

			int totalNightDiff = 0;
			int totalRegularOT = 0;
			int totalRestDayOT = 0;
			int totalExcessRestDayOT = 0;
			int legalHolidayOT = 0;
			int excessLegalHolidayOT = 0;
			int specialHolidayOT = 0;
			int excessSpecialHolidayOT = 0;
			int legalHolidayRestDayOT = 0;
			int excessLegalHolidayRestDayOT = 0;
			int specialHolidayRestDayOT = 0;
			int excessSpecialHolidayRestDayOT = 0;

			for (EmployeeScheduleUploading employeeScheduleUploading : bioByEmployeeList) {
				int nightDiffPerEntry = 0;

				if (employeeScheduleUploading.getOvertimeBreakdownList() != null) {
					for (EmployeeScheduleUploadingOvertimeBreakdown overtimeBreakdown : employeeScheduleUploading
							.getOvertimeBreakdownList()) {
						if (overtimeBreakdown != null) {
							switch (overtimeBreakdown.getOvertimeType().getPrimaryKey()) {
							case 1:
								// ot10
								nightDiffPerEntry = overtimeBreakdown.getTotalMin();
								employeeScheduleUploading.setInitialTotalMinsNightDiff(nightDiffPerEntry);
								totalNightDiff = totalNightDiff + nightDiffPerEntry;
								break;
							case 6:
								// ot125
								totalRegularOT = totalRegularOT + overtimeBreakdown.getTotalMin();
								break;
							case 7:
								// ot130
								totalRestDayOT = totalRestDayOT + overtimeBreakdown.getTotalMin();
								specialHolidayOT = specialHolidayOT + overtimeBreakdown.getTotalMin();
								break;
							case 11:
								// ot169
								totalExcessRestDayOT = totalExcessRestDayOT + overtimeBreakdown.getTotalMin();
								excessSpecialHolidayOT = excessSpecialHolidayOT + overtimeBreakdown.getTotalMin();
								break;
							case 5:
								// ot100
								legalHolidayOT = legalHolidayOT + overtimeBreakdown.getTotalMin();
								break;
							case 18:
								// ot260
								excessLegalHolidayOT = excessLegalHolidayOT + overtimeBreakdown.getTotalMin();
								break;
							case 10:
								// ot160
								legalHolidayRestDayOT = legalHolidayRestDayOT + overtimeBreakdown.getTotalMin();
								break;
							case 19: // OT338
								excessLegalHolidayRestDayOT = excessLegalHolidayRestDayOT
										+ overtimeBreakdown.getTotalMin();
								break;
							case 4:
								// ot50
								specialHolidayRestDayOT = specialHolidayRestDayOT + overtimeBreakdown.getTotalMin();
								break;
							case 14:
								// ot195
								excessSpecialHolidayRestDayOT = excessSpecialHolidayRestDayOT
										+ overtimeBreakdown.getTotalMin();
								break;
							default:
								break;
							}
						}
					}
				}

			}
			totalNightDiffHashMap.put(employeeCode, totalNightDiff);
			totalRegularOTHashMap.put(employeeCode, totalRegularOT);
			totalRestDayOTHashMap.put(employeeCode, totalRestDayOT);
			totalExcessRestDayOTHashMap.put(employeeCode, totalExcessRestDayOT);
			totalLegalHolidayOTHashMap.put(employeeCode, legalHolidayOT);
			totalExcessLegalHolidayHashMap.put(employeeCode, excessLegalHolidayOT);
			totalSpecialHolidayHashMap.put(employeeCode, specialHolidayOT);
			totalExcessSpecialHolidayHashMap.put(employeeCode, excessSpecialHolidayOT);
			totalLegalHolidayRestDayHashMap.put(employeeCode, legalHolidayRestDayOT);
			totalExcessLegalHolidayRestDayHashMap.put(employeeCode, excessLegalHolidayRestDayOT);
			totalSpecialHolidayRestDayHashMap.put(employeeCode, excessSpecialHolidayRestDayOT);
			totalExcessSpecialHolidayRestDayHashMap.put(employeeCode, excessSpecialHolidayRestDayOT);

		}

		parameterReport.put("payPeriod", payPeriod);
		parameterReport.put("positionNameHashMap", positionNameHashMap);
		parameterReport.put("scheduleEmployeeHashMap", scheduleEmployeeHashMap);
		parameterReport.put("dayOffEmployeeHashMap", dayOffEmployeeHashMap);

		parameterReport.put("totalNightDiffHashMap", totalNightDiffHashMap);
		parameterReport.put("totalRegularOTHashMap", totalRegularOTHashMap);
		parameterReport.put("totalRestDayOTHashMap", totalRestDayOTHashMap);
		parameterReport.put("totalExcessRestDayOTHashMap", totalExcessRestDayOTHashMap);
		parameterReport.put("totalLegalHolidayOTHashMap", totalLegalHolidayOTHashMap);
		parameterReport.put("totalExcessLegalHolidayHashMap", totalExcessLegalHolidayHashMap);
		parameterReport.put("totalSpecialHolidayHashMap", totalSpecialHolidayHashMap);
		parameterReport.put("totalExcessSpecialHolidayHashMap", totalExcessSpecialHolidayHashMap);
		parameterReport.put("totalLegalHolidayRestDayHashMap", totalLegalHolidayRestDayHashMap);
		parameterReport.put("totalExcessLegalHolidayRestDayHashMap", totalExcessLegalHolidayRestDayHashMap);
		parameterReport.put("totalSpecialHolidayRestDayHashMap", totalSpecialHolidayRestDayHashMap);
		parameterReport.put("totalExcessSpecialHolidayRestDayHashMap", totalExcessSpecialHolidayRestDayHashMap);

		parameterReport.put("totalLatesByEmpIdHashMap", totalLatesByEmpIdHashMap);
		parameterReport.put("totalAbsentsByEmpIdHashMap", totalAbsentsByEmpIdHashMap);

		this.generateReport(biometricsReportList, parameterReport, report);
	}

	public void setDefaultOvertimeList() {
		this.otDefaultObjectList.clear();

		List<String> otDefaultList = new ArrayList<>();
		otDefaultList.add("OT10%");
		otDefaultList.add("OT30%");
		otDefaultList.add("OT50%");
		otDefaultList.add("OT100%");
		otDefaultList.add("OT125%");
		otDefaultList.add("OT130%");
		otDefaultList.add("OT150%");
		otDefaultList.add("OT160%");
		otDefaultList.add("OT169%");
		otDefaultList.add("OT195%");
		otDefaultList.add("OT200%");
		otDefaultList.add("OT260%");
		otDefaultList.add("OT338%");

		// otDefaultList.add("OT25%");
		// otDefaultList.add("OT137%");
		// otDefaultList.add("OT175%");

		List<OvertimeType> otTypeList = this.mainApplication.getOvertimeTypeMain().getAllOvertimeType();

		// sorted in order of adding items to this list
		for (String overtimeName : otDefaultList) {
			otDefaultObjectList.addAll(otTypeList.stream().filter(p -> p.getOvertimeName().equals(overtimeName))
					.sorted().collect(Collectors.toList()));

		}

		// ObservableList<OvertimeType> obsListSorted =
		// FXCollections.observableArrayList();
		// obsListSorted.setAll(otDefaultObjectList);
		// ObservableListUtil.sort(obsListSorted, p -> p.getSequenceNumber());
		// otDefaultObjectList.clear();
		// otDefaultObjectList.addAll(obsListSorted);

		for (OvertimeType overtimeType : otDefaultObjectList) {
			String[] splitString = overtimeType.getOvertimeName().split("%");
			String overtimeName = splitString[0];

			overtimeType.setOvertimeName(overtimeName);
		}

	}

	@Override
	public void onSetMainApplication() {
		this.initializeFields();
		this.setFieldListener();
		this.setDefaultOvertimeList();
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private Button buttonCollect;
	@FXML
	private Button buttonCancel;
	@FXML
	private AutoFillComboBox<Client> comboBoxClient;
	@FXML
	private AutoFillComboBox<Department> comboBoxDepartment;
	@FXML
	private AutoFillComboBox<Employee> comboBoxEmployee;
	@FXML
	private AutoFillComboBox<Integer> comboBoxEmpID;
	@FXML
	private Label labelPayFrom;
	@FXML
	private Label labelPayTo;
	// @FXML
	// private Label labelCutoffFrom;
	// @FXML
	// private Label labelCutoffTo;
}
