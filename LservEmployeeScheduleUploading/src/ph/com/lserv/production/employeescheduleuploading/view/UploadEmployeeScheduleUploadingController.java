package ph.com.lserv.production.employeescheduleuploading.view;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ph.com.lbpsc.production.client.model.Client;
import ph.com.lbpsc.production.custom.controls.autofillcombobox.AutoFillComboBox;
import ph.com.lbpsc.production.custom.controls.formatteddatepicker.FormattedDatePicker;
import ph.com.lbpsc.production.department.model.Department;
import ph.com.lbpsc.production.employee.model.Employee;
import ph.com.lbpsc.production.employmenthistory.model.EmploymentHistory;
import ph.com.lbpsc.production.importexportfile.ImportExportFileMain;
import ph.com.lbpsc.production.masterclass.MasterController;
import ph.com.lbpsc.production.user.model.User;
import ph.com.lbpsc.production.util.AlertUtil;
import ph.com.lbpsc.production.util.DateFormatter;
import ph.com.lbpsc.production.util.DateUtil;
import ph.com.lbpsc.production.util.ObservableListUtil;
import ph.com.lbpsc.production.util.ProcessingMessage;
import ph.com.lbpsc.production.util.ProgressUtil;
import ph.com.lserv.production.employeescheduleencoding.model.EmployeeScheduleEncoding;
import ph.com.lserv.production.employeescheduleencodingirregular.model.EmployeeScheduleEncodingIrregular;
import ph.com.lserv.production.employeescheduleuploading.EmployeeScheduleUploadingMain;
import ph.com.lserv.production.employeescheduleuploading.model.EmployeeScheduleUploading;
import ph.com.lserv.production.overtimefiling.model.OvertimeFiling;
import ph.com.lserv.production.overtimefilingclient.model.OvertimeFilingClient;
import ph.com.lserv.production.scheduleencoding.model.ScheduleEncoding;

public class UploadEmployeeScheduleUploadingController extends MasterController<EmployeeScheduleUploadingMain> {
	Stage stage;

	List<Employee> extractedEmployeesList = new ArrayList<>();

	List<String> biometricsDataList = new ArrayList<>();
	HashSet<Integer> empIdList = new HashSet<>();
	List<Integer> noEmpIdList = new ArrayList<>();

	List<Date> listIrregularSchedDates = new ArrayList<>();

	List<EmployeeScheduleUploading> listDetailsPerDate = new ArrayList<>();
	List<EmployeeScheduleUploading> listRegularSched = new ArrayList<>();
	List<EmployeeScheduleUploading> listIrregularSched = new ArrayList<>();
	List<EmployeeScheduleUploading> listNoSched = new ArrayList<>();
	List<EmployeeScheduleUploading> employeeScheduleUploadingWithRegularSchedList = new ArrayList<>();
	List<EmployeeScheduleUploading> employeeScheduleUploadingWithIrregularSchedList = new ArrayList<>();
	List<EmployeeScheduleUploading> existingDataList = new ArrayList<>();
	List<EmployeeScheduleUploading> newEntrySaveList = new ArrayList<>();
	List<EmployeeScheduleUploading> updateEntrySaveList = new ArrayList<>();
	List<EmployeeScheduleUploading> deleteEntrySaveList = new ArrayList<>();
	List<EmployeeScheduleUploading> bioToBeSaveList = new ArrayList<>();
	List<EmployeeScheduleUploading> listTimeInEntries = new ArrayList<>();
	List<EmployeeScheduleUploading> listTimeInEntriesToBeRemove = new ArrayList<>();

	Client selectedClient = new Client();
	Department selectedDepartment = new Department();

	Boolean isSaveSuccess = true;
	Boolean isUpdateSuccess = true;
	Boolean isDeleteSuccess = true;
	Boolean isOvertimeBreakdownSuccess = true;

	ObservableList<Client> obsListClient = FXCollections.observableArrayList();
	ObservableList<Department> obsListDepartment = FXCollections.observableArrayList();
	ObservableList<Employee> obsListEmployee = FXCollections.observableArrayList();

	EmployeeScheduleUploading prevTimeOut = new EmployeeScheduleUploading();

	Date payFrom, payTo, cutoffFrom, cutoffTo;

	public void handleImport() {

		if ((this.datePickerPayFrom.getValue() != null && this.datePickerPayTo.getValue() != null)
				&& (this.datePickerCutoffFrom.getValue() != null && this.datePickerCutoffTo.getValue() != null)) {
			if (!this.biometricsDataList.isEmpty()) {
				this.biometricsDataList.clear();
			}

			ImportExportFileMain importExportFileMain = new ImportExportFileMain();
			try {
				importExportFileMain.getRootLayoutImportExportFile(false, "txt",
						this.mainApplication.getPrimaryStage());

				if (importExportFileMain.getFileLocation() != null | importExportFileMain.getFileName() != null) {
					this.resetFields();

					String fileName = importExportFileMain.getFileLocation() + "\\" + importExportFileMain.getFileName()
							+ ".txt";

					String[] fileImported = importExportFileMain.getFileName().split("[.]");
					String fileExtension = fileImported.length >= 1 ? fileImported[fileImported.length - 1] : "";

					if (fileImported.length > 1 && fileExtension.compareTo("txt") != 0) {
						AlertUtil.showInformationAlert("Invalid file: '" + importExportFileMain.getFileName()
								+ "'\nImport only valid '.txt' biometrics file\nor remove all extra period '.' in file name.",
								this.getStage());
						return;
					}

					String regexAlphabet = ".*[a-zA-Z]+.*";

					List<String> rawDataList = Files.readAllLines(Paths.get(fileName), StandardCharsets.ISO_8859_1);

					ProcessingMessage.showProcessingMessage(this.getStage(), "Reading biometrics.");

					int entryCheckerCounter = 0;
					for (String biometricsEntry : rawDataList) {
						entryCheckerCounter += 1;
						if (biometricsEntry.isEmpty()) {
							// skip this line entry
							continue;
						} else {
							if (biometricsEntry.matches(regexAlphabet)) {
								String getAllAlphabet = biometricsEntry.replaceAll("[^A-Za-z]+", "");
								if (getAllAlphabet.compareTo("BIO") != 0 && getAllAlphabet.compareTo("ARMS") != 0) {
									AlertUtil.showInformationAlert("Invalid Time Entry:\n'" + biometricsEntry
											+ "'\n on Line number: " + entryCheckerCounter, this.getStage());
									ProcessingMessage.closeProcessingMessage();
									return;
								}
							}

							String[] checkEntryFormat = biometricsEntry.split("\\s+");
							if (checkEntryFormat.length <= 1) {
								AlertUtil.showInformationAlert("Invalid file: '" + importExportFileMain.getFileName()
										+ ".txt'\nImport only valid '.txt' biometrics file", this.getStage());
								ProcessingMessage.closeProcessingMessage();
								return;
							}
							biometricsDataList.addAll(Arrays.asList(biometricsEntry));
						}
					}
					ProcessingMessage.closeProcessingMessage();

					if (!empIdList.isEmpty()) {
						this.resetFields();
					}

					this.extractBiometricsData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			AlertUtil.showInformationAlert("Pick dates for both Cutoff and Pay Period before importing.",
					this.getStage());
			return;
		}
	}

	public void checkDuplicateEntries(EmployeeScheduleUploading resultEmployeeScheduleUploading) {
		if (mainApplication.getEmployeeScheduleUploadingList().size() == 0) {
			mainApplication.getEmployeeScheduleUploadingList().add(resultEmployeeScheduleUploading);
		} else {
			// double check if already added
			boolean isAlreadySaved = mainApplication.getEmployeeScheduleUploadingList().stream()
					.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(
							resultEmployeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode())
							&& p.getDateEntry().equals(resultEmployeeScheduleUploading.getDateEntry())
							&& p.getTimeEntry().equals(resultEmployeeScheduleUploading.getTimeEntry()))
					.findFirst().isPresent();

			if (!isAlreadySaved) {
				mainApplication.getEmployeeScheduleUploadingList().add(resultEmployeeScheduleUploading);
			}
		}
	}

	public String extractEmpId(String entry) {
		String output = "";

		output = entry.length() == 4 ? "10".concat(entry)
				: entry.length() == 5 ? "1".concat(entry)
						: entry.length() > 5 ? entry
								: entry.length() == 1 ? "10000".concat(entry)
										: entry.length() == 2 ? "1000".concat(entry)
												: entry.length() == 3 ? "100".concat(entry) : "";

		return output;
	}

	public String extractBioArms(String[] dataEntry) {
		String output = null;

		if (dataEntry.length > 7) {
			if (dataEntry[7] != null || !dataEntry[7].isEmpty()) {
				output = dataEntry[7];
			}
		}

		return output;
	}

	public void extractBiometricsData() {
		String empIdFormat = "\\b\\d{0,6}\\b";
		String dateFormat = "((18|19|20)[0-9]{2}[\\-.]"
				+ "(0[13578]|1[02])[\\-.](0[1-9]|[12][0-9]|3[01]))|(18|19|20)[0-9]{2}[\\-.]"
				+ "(0[469]|11)[\\-.](0[1-9]|[12][0-9]|30)|(18|19|20)[0-9]{2}[\\-.](02)[\\-.]"
				+ "(0[1-9]|1[0-9]|2[0-8])|(((18|19|20)(04|08|[2468][048]|[13579][26]))|2000)" + "[\\-.](02)[\\-.]29";
		String timeFormat = "^(2[0-3]|[01]?[0-9]):([0-5]?[0-9]):([0-5]?[0-9])$";

		Task<Void> taskExtractDataFromList = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				List<Integer> empIdActiveList = new ArrayList<>();
				List<Integer> empIdValidatedList = new ArrayList<>();

				empIdActiveList
						.addAll(mainApplication.getEmployeeMain().getAllActiveEmployeeID(new Date(), new Date()));
				empIdValidatedList.addAll(mainApplication.getAllValidatedEmployeeIdByPayFromPayTo(payFrom, payTo));
				empIdActiveList.removeAll(empIdValidatedList);

				try {
					extractedEmployeesList.clear();
					mainApplication.getEmployeeObservableList().clear();
					mainApplication.getListEmpIdNoSched().clear();
					existingDataList.clear();
					newEntrySaveList.clear();
					updateEntrySaveList.clear();
					deleteEntrySaveList.clear();

					int counter = 0;
					int entryCheckerCounter = 0;
					
					System.out.println("fixed caap report / fixed reupload");
					
					counter = 0;
					if (biometricsDataList != null && !biometricsDataList.isEmpty()) {

						Calendar date = Calendar.getInstance();
						date.setTime(cutoffFrom);

						for (String entry : biometricsDataList) {
							entryCheckerCounter += 1;
							String[] dataEntry = entry.trim().split("\\s+");

							String bioId = dataEntry[0];
							String empId = extractEmpId(dataEntry[0]);
							String dateEntry = dataEntry[1];
							String timeEntry = dataEntry[2];
							String bioArms = extractBioArms(dataEntry);

							if (DateUtil.isDateBetween(DateUtil.parseDate(dateEntry), cutoffFrom, cutoffTo)) {
								Boolean isValidEmpIdFormat = empId.matches(empIdFormat);
								Boolean isValidDateFormat = dateEntry.matches(dateFormat);
								Boolean isValidTimeFormat = timeEntry.matches(timeFormat);

								if (!isValidEmpIdFormat || !isValidDateFormat || !isValidTimeFormat) {
									showAlertMessageInvalidEntry(isValidEmpIdFormat, isValidDateFormat,
											isValidTimeFormat, empId, dateEntry, timeEntry, entryCheckerCounter);
									break;
								}

								Boolean isEmployeeBiometricsValidated = false;
								isEmployeeBiometricsValidated = empIdValidatedList.stream()
										.filter(p -> p.equals(Integer.valueOf(empId))).findAny().isPresent();

								if (!isEmployeeBiometricsValidated) {
									EmployeeScheduleUploading employeeScheduleUploading = new EmployeeScheduleUploading();
									EmployeeScheduleUploading resultEmployeeScheduleUploading = new EmployeeScheduleUploading();

									EmploymentHistory employmentHistoryMax = mainApplication.getEmploymentHistoryMain()
											.getEmploymentHistoryMaxByEmployeeCode(Integer.valueOf(empId));

									if (employmentHistoryMax != null) {
										employeeScheduleUploading.setEmploymentHistory(employmentHistoryMax);
									} else {
										continue;
									}
									employeeScheduleUploading.setEmployee(
											employeeScheduleUploading.getEmploymentHistory().getEmployee());

									if (selectedClient != null) {
										if (selectedDepartment != null) {
											Department department = new Department();
											department = mainApplication.getDepartmentMain().getDepartmentByCode(
													employeeScheduleUploading.getEmployee().getDepartment());
											if (department != null && selectedDepartment.getDepartmentCode()
													.equals(department.getDepartmentCode())) {
												resultEmployeeScheduleUploading = createEmployeeScheduleUploading(
														employeeScheduleUploading, empId, dateEntry, timeEntry, bioId,
														bioArms);

												// checkDuplicateEntries(resultEmployeeScheduleUploading);
												mainApplication.getEmployeeScheduleUploadingList()
														.add(resultEmployeeScheduleUploading);

												if (!empIdList.contains(Integer.valueOf(empId))) {
													empIdList.add(Integer.valueOf(empId));
												}
											}
											continue;
										}
										Client client = new Client();
										// client = mainApplication.getClientMain()
										// .getClientByCode(employeeScheduleUploading.getEmployee().getClient());
										client = employeeScheduleUploading.getEmploymentHistory().getClient();
										if (client != null
												&& selectedClient.getClientName().equals(client.getClientName())) {

											resultEmployeeScheduleUploading = createEmployeeScheduleUploading(
													employeeScheduleUploading, empId, dateEntry, timeEntry, bioId,
													bioArms);

											// checkDuplicateEntries(resultEmployeeScheduleUploading);
											mainApplication.getEmployeeScheduleUploadingList()
													.add(resultEmployeeScheduleUploading);

											if (!empIdList.contains(Integer.valueOf(empId))) {
												empIdList.add(Integer.valueOf(empId));
											}
										}
										continue;

									} else {
										resultEmployeeScheduleUploading = createEmployeeScheduleUploading(
												employeeScheduleUploading, empId, dateEntry, timeEntry, bioId, bioArms);

										// checkDuplicateEntries(resultEmployeeScheduleUploading);
										mainApplication.getEmployeeScheduleUploadingList()
												.add(resultEmployeeScheduleUploading);

										if (!empIdList.contains(Integer.valueOf(empId))) {
											empIdList.add(Integer.valueOf(empId));
										}
										continue;
									}
								}
							}
						}

						if (empIdList != null && !empIdList.isEmpty()) {
							counter = 0;
							for (Integer empIdFromList : empIdList) {
								Employee employee = mainApplication.getEmployeeMain().getEmployeeByID(empIdFromList);

								if (employee != null) {
									mainApplication.getEmployeeObservableList().add(employee);
								} else {
									if (!noEmpIdList.contains(empIdFromList)) {
										noEmpIdList.add(empIdFromList);
									}
								}

								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter, empIdList.size()), 1D);
								updateMessage(
										ProgressUtil.getMessageValue("Processing 1 / 2\n", counter, empIdList.size()));
							}

							counter = 0;
							for (EmployeeScheduleUploading employeeScheduleUploading : mainApplication
									.getEmployeeScheduleUploadingList()) {

								Employee employee = new Employee();
								employee = mainApplication.getEmployeeObservableList().stream()
										.filter(p -> p.getEmployeeCode()
												.equals(employeeScheduleUploading.getEmployeeId()))
										.map(p -> p).findFirst().orElse(null);

								if (employee != null) {
									EmploymentHistory employmentHistoryMax = mainApplication.getEmploymentHistoryMain()
											.getEmploymentHistoryMaxByEmployeeCode(employee.getEmployeeCode());

									String dayOfDate = Instant
											.ofEpochMilli(employeeScheduleUploading.getDateEntry().getTime())
											.atZone(ZoneId.systemDefault()).toLocalDate().getDayOfWeek().toString();

									if (employee.getEmployeeCode().equals(employeeScheduleUploading.getEmployeeId())) {
										employeeScheduleUploading.setEmployee(employee);
										employeeScheduleUploading
												.setPayPeriod(mainApplication.getPayPeriod(payFrom, payTo));
										employeeScheduleUploading
												.setDayOfDate(mainApplication.capitalizeFirstChar(dayOfDate));
										employeeScheduleUploading.setPayFrom(payFrom);
										employeeScheduleUploading.setPayTo(payTo);
										employeeScheduleUploading.setCutoffFrom(cutoffFrom);
										employeeScheduleUploading.setCutoffTo(cutoffTo);

										if (employmentHistoryMax != null) {
											employeeScheduleUploading.setEmploymentHistory(employmentHistoryMax);
										}
									}
								}
								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter,
										mainApplication.getEmployeeScheduleUploadingList().size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Processing 2 / 2\n", counter,
										mainApplication.getEmployeeScheduleUploadingList().size()));
							}

							mainApplication.getEntryToBeRemoveList().clear();
							mainApplication.getListEmpIdNoSched().clear();
							counter = 0;
							for (Integer empIdFromList : empIdList) {

								mainApplication.getEmployeeSchedule(empIdFromList,
										mainApplication.getEmployeeScheduleUploadingList());

								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter, empIdList.size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Processing. 1/ 5\nGetting schedule...\n",
										counter, empIdList.size()));
							}

							mainApplication.getEmployeeScheduleUploadingList()
									.removeAll(mainApplication.getEntryToBeRemoveList());
							empIdList.removeAll(mainApplication.getListEmpIdNoSched());

							listDetailsPerDate.clear();
							mainApplication.getListFinalizeBiometricsComplete().clear();
							mainApplication.getListFinalizeBiometricsOnlyBio().clear();
							listRegularSched.clear();
							listIrregularSched.clear();
							listIrregularSchedDates.clear();
							listNoSched.clear();
							employeeScheduleUploadingWithRegularSchedList.clear();
							employeeScheduleUploadingWithIrregularSchedList.clear();

							ObservableList<EmployeeScheduleUploading> tempList = FXCollections.observableArrayList();

							tempList.setAll(mainApplication.getEmployeeScheduleUploadingList());
							mainApplication.getEmployeeScheduleUploadingList().clear();
							ObservableListUtil.sort(tempList,
									p -> new SimpleDateFormat("MM/dd/yyyy").format(p.getDateEntry()));
							mainApplication.getEmployeeScheduleUploadingList().addAll(tempList);

							counter = 0;
							for (EmployeeScheduleUploading employeeScheduleUploading : mainApplication
									.getEmployeeScheduleUploadingList()) {
								checkBiometricsScheduleType(employeeScheduleUploading);

								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter,
										mainApplication.getEmployeeScheduleUploadingList().size()), 1D);
								updateMessage(
										ProgressUtil.getMessageValue("Processing. 2 / 5\nGetting schedule type...\n",
												counter, mainApplication.getEmployeeScheduleUploadingList().size()));
							}

							counter = 0;
							for (EmployeeScheduleUploading employeeScheduleUploading : employeeScheduleUploadingWithRegularSchedList) {
								setRegularSchedule(employeeScheduleUploading);
								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter,
										employeeScheduleUploadingWithRegularSchedList.size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Processing. 3 / 5\nRegular schedules...\n",
										counter, employeeScheduleUploadingWithRegularSchedList.size()));
							}

							counter = 0;
							for (EmployeeScheduleUploading employeeScheduleUploadingIrregular : employeeScheduleUploadingWithIrregularSchedList) {
								setIrregularSchedule(employeeScheduleUploadingIrregular);
								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter,
										employeeScheduleUploadingWithIrregularSchedList.size()), 1D);
								updateMessage(
										ProgressUtil.getMessageValue("Processing. 4 / 5\nIrregular Schedules...\n",
												counter, employeeScheduleUploadingWithIrregularSchedList.size()));
							}

							listIrregularSchedDates.addAll(employeeScheduleUploadingWithIrregularSchedList.stream()
									.map(p -> p.getDateEntry()).collect(Collectors.toList()));

							counter = 0;

							List<Integer> empIdNoSchedList = listNoSched.stream()
									.map(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode()).distinct()
									.collect(Collectors.toList());

							for (Integer empId : empIdNoSchedList) {
								setEntryNoSched(empId);

								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter,
										employeeScheduleUploadingWithIrregularSchedList.size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Processing. 5 / 5\nOvertime Schedules...\n",
										counter, employeeScheduleUploadingWithIrregularSchedList.size()));
							}

							listRegularSched.addAll(mainApplication.getListFinalizeBiometricsOnlyBio());
							listRegularSched.removeAll(employeeScheduleUploadingWithIrregularSchedList);

							List<Date> distinctList = new ArrayList<>();
							distinctList = listIrregularSchedDates.stream().distinct().collect(Collectors.toList());

							counter = 0;
							for (EmployeeScheduleUploading data : mainApplication.getListFinalizeBiometricsOnlyBio()) {
								if (distinctList.contains(data.getDateEntry())) {
									if (!listIrregularSched.contains(data)) {
										listIrregularSched.add(data);
									}
								}
								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter,
										mainApplication.getListFinalizeBiometricsOnlyBio().size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Please wait...\n", counter,
										mainApplication.getListFinalizeBiometricsOnlyBio().size()));
							}

							counter = 0;

							for (Integer employeeCode : empIdList) {

								if (mainApplication.getListFinalizeBiometricsComplete()
										.addAll(mainApplication.setNoEntryDates(employeeCode,
												mainApplication.getListFinalizeBiometricsOnlyBio()))) {
									counter += 1;
									updateProgress(ProgressUtil.getProgressValue(counter, empIdList.size()), 1D);
									updateMessage(ProgressUtil.getMessageValue("Completing entries 1/2\n", counter,
											empIdList.size()));
								}
							}

							setBioToBeSaveList();
							setExistingDataList(empIdValidatedList);

							counter = 0;
							for (EmployeeScheduleUploading employeeScheduleUploading : mainApplication
									.getListFinalizeBiometricsComplete()) {

								if (mainApplication.setRemarksEntry(employeeScheduleUploading,
										employeeScheduleUploading.getDateEntry())) {
									counter += 1;
									updateProgress(ProgressUtil.getProgressValue(counter,
											mainApplication.getListFinalizeBiometricsComplete().size()), 1D);
									updateMessage(ProgressUtil.getMessageValue("Completing entries 2/2\n Please wait\n",
											counter, mainApplication.getListFinalizeBiometricsComplete().size()));
								}
							}

							counter = 0;
							for (Integer empId : empIdList) {

								List<EmployeeScheduleUploading> bioPerEmployeeList = bioToBeSaveList.stream().filter(
										p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(empId))
										.collect(Collectors.toList());

								modifyDetailsToBeSave(bioPerEmployeeList);
								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter, empIdList.size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Modifying details 1/2...\n", counter,
										empIdList.size()));
							}

							if (updateEntrySaveList.size() != 0) {
								if (!mainApplication.updateMultipleData(updateEntrySaveList)) {
									isUpdateSuccess = false;
								}
							}

							if (newEntrySaveList.size() != 0) {
								if (!mainApplication.createMultipleData(newEntrySaveList)) {
									isSaveSuccess = false;
								}
							}

							deleteEntrySaveList.removeAll(updateEntrySaveList);
							deleteEntrySaveList.removeAll(newEntrySaveList);

							if (deleteEntrySaveList.size() != 0) {
								if (!mainApplication.deleteMultipleData(deleteEntrySaveList)) {
									isDeleteSuccess = false;
								}
							}

							counter = 0;
							List<Integer> empIdUploadedList = new ArrayList<>();

							empIdUploadedList.addAll(mainApplication.getAllEmployeeIdByPayFromPayTo(payFrom, payTo));

							handleInvalidEntries(empIdUploadedList, empIdActiveList);

							for (Integer employeeCode : empIdUploadedList) {
								handleNullEntries(employeeCode);
								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter, empIdUploadedList.size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Modifying details 2/2...\n", counter,
										empIdUploadedList.size()));
							}

							counter = 0;
							for (Integer empId : empIdList) {

								mainApplication.computeOvertimeBreakdown(empId, payFrom, payTo);

								counter += 1;
								updateProgress(ProgressUtil.getProgressValue(counter, empIdList.size()), 1D);
								updateMessage(ProgressUtil.getMessageValue("Saving...\n", counter, empIdList.size()));
							}
						}

						counter = 0;
						Platform.runLater(() -> {
							if (isSaveSuccess && isUpdateSuccess && isDeleteSuccess) {
								if (mainApplication.getEmployeeScheduleUploadingList() != null
										&& !mainApplication.getEmployeeScheduleUploadingList().isEmpty()) {

									mainApplication.getRawEmployeeObservableList()
											.setAll(mainApplication.getEmployeeObservableList());

									List<Employee> employeeWithoutSched = new ArrayList<>();

									extractedEmployeesList.addAll(mainApplication.getEmployeeObservableList());

									for (Integer empId : mainApplication.getListEmpIdNoSched()) {

										employeeWithoutSched.addAll(extractedEmployeesList.stream()
												.filter(p -> p.getEmployeeCode().equals(empId))
												.collect(Collectors.toList()));

									}

									extractedEmployeesList.removeAll(employeeWithoutSched);
									mainApplication.getEmployeeObservableList().setAll(extractedEmployeesList);
									obsListEmployee.setAll(mainApplication.getEmployeeObservableList());

									if (mainApplication.getListEmpIdNoSched() != null
											&& mainApplication.getListEmpIdNoSched().size() != 0) {
										if (AlertUtil.showQuestionAlertBoolean(
												"Employees without schedule found.\nDo you want to export?",
												getStage())) {
											mainApplication.exportExcel(1);
										}
									}

									if (!mainApplication.getEmployeesWithoutOvertimeConfigHashMap().isEmpty()
											|| mainApplication.getEmployeesWithoutOvertimeConfigHashMap().size() > 0) {
										// generate report
										if (AlertUtil.showQuestionAlertBoolean(
												"Employees without overtime config found.\nDo you want to export?",
												getStage())) {
											mainApplication.exportExcel(2);
										}
									}
									AlertUtil.showSuccessSaveAlert(getStage());
									stage.close();

								} else {

									if (mainApplication.getListEmpIdNoSched() != null
											&& !mainApplication.getListEmpIdNoSched().isEmpty()) {
										if (AlertUtil.showQuestionAlertBoolean(
												"Employees without schedule found.\nDo you want to export?",
												getStage())) {
											mainApplication.exportExcel(1);
										}
									}

									if (!mainApplication.getEmployeesWithoutOvertimeConfigHashMap().isEmpty()
											|| mainApplication.getEmployeesWithoutOvertimeConfigHashMap().size() > 0) {
										// generate report
										if (AlertUtil.showQuestionAlertBoolean(
												"Employees without overtime config found.\nDo you want to export?",
												getStage())) {
											mainApplication.exportExcel(2);
										}
									}

									AlertUtil.showInformationAlert(
											"No data found.\nCutoff Date:\n"
													+ new SimpleDateFormat("MMM. dd, yyyy").format(cutoffFrom) + " - "
													+ new SimpleDateFormat("MMM. dd, yyyy").format(cutoffTo),
											getStage());

									resetFields();
								}

							} else {
								AlertUtil.showErrorAlert("Upload Failed.", getStage());
								resetFields();
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		ProgressUtil.showProcessInterface(getStage(), taskExtractDataFromList, false);
	}

	public boolean isDateEntryExistInNextPayPeriod(Date dateEntryToBeSave,
			EmployeeScheduleUploading employeeScheduleUploading) {
		boolean returnValue = false;

		LocalDate localDatePayFrom = this.payFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		int payFromDayEntry = localDatePayFrom.getDayOfMonth();
		int nextPayFromDay = 16;
		int nextPayFromMonth = localDatePayFrom.getMonthValue();
		int nextPayFromYear = localDatePayFrom.getYear();

		if (payFromDayEntry > 15 && payFromDayEntry <= 31) {
			nextPayFromDay = 1;
			if (nextPayFromMonth == 12) {
				nextPayFromMonth = 1;
				nextPayFromYear = nextPayFromYear + 1;
			} else {
				nextPayFromMonth = nextPayFromMonth + 1;
			}
		}

		Date nextPayPeriodFrom = DateUtil.parseDate(nextPayFromYear, nextPayFromMonth, nextPayFromDay);

		List<EmployeeScheduleUploading> employeeScheduleUploadingListByEmpPayFromList = new ArrayList<>();

		employeeScheduleUploadingListByEmpPayFromList.addAll(this.mainApplication.getDataByEmployeeIdPayFrom(
				employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode(), nextPayPeriodFrom));

		boolean isDateExisted = employeeScheduleUploadingListByEmpPayFromList.stream()
				.filter(p -> p.getDateEntry().equals(dateEntryToBeSave)).findFirst().isPresent();

		returnValue = isDateExisted;

		return returnValue;
	}

	public boolean isDateEntryExistInPrevPayPeriod(Date dateEntryToBeSave,
			EmployeeScheduleUploading employeeScheduleUploading) {
		boolean returnValue = false;

		LocalDate localDatePayFrom = this.payFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		int payFromDayEntry = localDatePayFrom.getDayOfMonth();
		int prevPayFromDay = 1;
		int prevPayFromMonth = localDatePayFrom.getMonthValue();
		int prevPayFromYear = localDatePayFrom.getYear();

		if (payFromDayEntry <= 15) {
			prevPayFromDay = 16;
			if (prevPayFromMonth == 12) {
				prevPayFromMonth = 1;
				prevPayFromYear = prevPayFromYear - 1;
			} else {
				prevPayFromMonth = prevPayFromMonth - 1;
			}
		}

		Date prevPayPeriodFrom = DateUtil.parseDate(prevPayFromYear, prevPayFromMonth, prevPayFromDay);

		List<EmployeeScheduleUploading> employeeScheduleUploadingListByEmpPayFromList = new ArrayList<>();

		employeeScheduleUploadingListByEmpPayFromList.addAll(this.mainApplication.getDataByEmployeeIdPayFrom(
				employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode(), prevPayPeriodFrom));

		boolean isDateExisted = employeeScheduleUploadingListByEmpPayFromList.stream()
				.filter(p -> p.getDateEntry().equals(dateEntryToBeSave)).findFirst().isPresent();

		returnValue = isDateExisted;
		return returnValue;
	}

	public void setListToBeSave(EmployeeScheduleUploading employeeScheduleUploading) {
		Date dateEntryToBeSave = employeeScheduleUploading.getDateEntry();
		Integer employeeIdEntry = employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode();

		List<EmployeeScheduleUploading> existingDataList = new ArrayList<>();
		existingDataList.addAll(this.mainApplication.getDataByEmployeeIdPayFrom(employeeIdEntry, this.payFrom));

		// boolean isDateAlreadySaved =
		// ObservableListUtil.getObject(this.existingDataList,
		// p ->
		// p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeIdEntry)
		// && p.getPayFrom().equals(this.payFrom) && p.getPayTo().equals(this.payTo)
		// && p.getDateEntry().compareTo(dateEntryToBeSave) == 0);
		// boolean isDateAlreadySaved = this.existingDataList.stream()
		// .filter(p ->
		// p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeIdEntry)
		// && p.getPayFrom().equals(this.payFrom) && p.getPayTo().equals(this.payTo)
		// && p.getDateEntry().compareTo(dateEntryToBeSave) == 0)
		// .findFirst().isPresent();
		EmployeeScheduleUploading isDateAlreadySaved = this.mainApplication
				.getDataByEmpIdDateEntryPayFrom(employeeIdEntry, dateEntryToBeSave, this.payFrom);

		boolean isDateEntryExistInNextPayPeriod = true;
		isDateEntryExistInNextPayPeriod = this.isDateEntryExistInNextPayPeriod(dateEntryToBeSave,
				employeeScheduleUploading);

		boolean isDateEntryExistInPrevPayPeriod = true;
		isDateEntryExistInPrevPayPeriod = this.isDateEntryExistInPrevPayPeriod(dateEntryToBeSave,
				employeeScheduleUploading);

		if (existingDataList.size() == 0) {
			if (!isDateEntryExistInNextPayPeriod && !isDateEntryExistInPrevPayPeriod) {
				this.newEntrySaveList.add(employeeScheduleUploading);
				return;
			}
		}

		List<EmployeeScheduleUploading> existingDataPerEmployeePerPayPeriodList = new ArrayList<>();
		existingDataPerEmployeePerPayPeriodList
				.addAll(this.mainApplication.getDataByEmployeeIdPayFrom(employeeIdEntry, this.payFrom));

		// existingDataPerEmployeePerPayPeriodList = this.existingDataList.stream()
		// .filter(p ->
		// p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeIdEntry)
		// && p.getPayFrom().equals(this.payFrom) && p.getPayTo().equals(this.payTo))
		// .collect(Collectors.toList());

		if (isDateAlreadySaved == null) {
			if (!isDateEntryExistInNextPayPeriod && !isDateEntryExistInPrevPayPeriod) {
				if (dateEntryToBeSave.compareTo(this.cutoffFrom) >= 0
						&& dateEntryToBeSave.compareTo(this.cutoffTo) <= 0) {

					Boolean isAlreadyOnNewList = this.newEntrySaveList.stream()
							.filter(p -> p.getDateEntry().equals(dateEntryToBeSave)
									&& p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeIdEntry))
							.findAny().isPresent();

					if (!isAlreadyOnNewList) {
						this.newEntrySaveList.add(employeeScheduleUploading);
					}
				}
			}
		} else {
			existingDataPerEmployeePerPayPeriodList.forEach(p -> {
				if (!this.deleteEntrySaveList.contains(p)) {
					this.deleteEntrySaveList.add(p);
				}
			});
			// this.deleteEntrySaveList.addAll(existingDataPerEmployeePerPayPeriodList);

			if (!existingDataPerEmployeePerPayPeriodList.isEmpty() && existingDataPerEmployeePerPayPeriodList.stream()
					.filter(p -> p.getDateEntry().equals(dateEntryToBeSave)).findFirst().get() != null) {

				EmployeeScheduleUploading existingEmployeeScheduleUploading = existingDataPerEmployeePerPayPeriodList
						.stream().filter(p -> p.getDateEntry().equals(dateEntryToBeSave)).findFirst().get();

				this.deleteEntrySaveList.remove(existingEmployeeScheduleUploading);

				Date dateEntryExisting = existingEmployeeScheduleUploading.getDateEntry();

				if (existingEmployeeScheduleUploading.getDateEntry().compareTo(this.cutoffFrom) >= 0
						&& existingEmployeeScheduleUploading.getDateEntry().compareTo(this.cutoffTo) <= 0) {

					existingEmployeeScheduleUploading.setTimeInEntry(employeeScheduleUploading.getTimeInEntry());
					existingEmployeeScheduleUploading.setLunchOutEntry(employeeScheduleUploading.getLunchOutEntry());
					existingEmployeeScheduleUploading.setLunchInEntry(employeeScheduleUploading.getLunchInEntry());
					existingEmployeeScheduleUploading.setTimeOutEntry(employeeScheduleUploading.getTimeOutEntry());
					existingEmployeeScheduleUploading.setUndertime(employeeScheduleUploading.getUndertime());

					existingEmployeeScheduleUploading.setUser(this.mainApplication.getUser());
					existingEmployeeScheduleUploading.setChangedOnDate(new Date());
					existingEmployeeScheduleUploading.setChangedInComputer(this.mainApplication.getComputerName());
					existingEmployeeScheduleUploading.setChangedByUser(this.mainApplication.getUser() == null ? null
							: this.mainApplication.getUser().getUserName());

					existingEmployeeScheduleUploading.setCutoffFrom(this.cutoffFrom);
					existingEmployeeScheduleUploading.setCutoffTo(this.cutoffTo);
					existingEmployeeScheduleUploading
							.setRemarksReference(employeeScheduleUploading.getRemarksReference());
					existingEmployeeScheduleUploading.setIsHoliday(employeeScheduleUploading.getIsHoliday());
					existingEmployeeScheduleUploading.setIsRestDay(employeeScheduleUploading.getIsRestDay());
					existingEmployeeScheduleUploading
							.setAllowedOvertime(employeeScheduleUploading.getAllowedOvertime());
					existingEmployeeScheduleUploading
							.setIsRegularSchedule(employeeScheduleUploading.getIsRegularSchedule());
					existingEmployeeScheduleUploading
							.setScheduleEncoding(employeeScheduleUploading.getScheduleEncoding());
					existingEmployeeScheduleUploading.setEmployeeScheduleEncodingIrregular(
							employeeScheduleUploading.getEmployeeScheduleEncodingIrregular());

					Boolean isAlreadyOnUpdateList = this.updateEntrySaveList.stream()
							.filter(p -> p.getDateEntry().equals(dateEntryExisting) && p.getEmploymentHistory()
									.getEmployee().getEmployeeCode().equals(existingEmployeeScheduleUploading
											.getEmploymentHistory().getEmployee().getEmployeeCode()))
							.findFirst().isPresent();

					existingEmployeeScheduleUploading.setPayPeriod(this.mainApplication.getPayPeriod(payFrom, payTo));
					existingEmployeeScheduleUploading.setSchedule(employeeScheduleUploading.getSchedule());
					existingEmployeeScheduleUploading.setBioArms(employeeScheduleUploading.getBioArms());

					if (!isAlreadyOnUpdateList) {
						this.updateEntrySaveList.add(existingEmployeeScheduleUploading);
					}
				}
			}

			if (isDateEntryExistInNextPayPeriod || isDateEntryExistInPrevPayPeriod) {
				EmployeeScheduleUploading employeeScheduleUploadingExisted = this.mainApplication
						.getDataByEmpIdDateEntryPayFrom(
								employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode(),
								dateEntryToBeSave, this.payFrom);

				if (employeeScheduleUploadingExisted != null) {
					if (!this.deleteEntrySaveList.contains(employeeScheduleUploadingExisted)) {
						this.deleteEntrySaveList.add(employeeScheduleUploadingExisted);
					}
				}
			}
		}
	}

	public void modifyDetailsToBeSave(List<EmployeeScheduleUploading> employeeScheduleUploadingList) {
		for (EmployeeScheduleUploading employeeScheduleUploading : employeeScheduleUploadingList) {
			employeeScheduleUploading.setUser(this.mainApplication.getUser());
			employeeScheduleUploading.setChangedOnDate(new Date());
			employeeScheduleUploading.setChangedInComputer(this.mainApplication.getComputerName());
			employeeScheduleUploading.setChangedByUser(
					this.mainApplication.getUser() == null ? null : this.mainApplication.getUser().getUserName());

			this.setListToBeSave(employeeScheduleUploading);
		}
	}

	public void setExistingDataList(List<Integer> empIdValidatedList) {
		this.existingDataList.clear();

		if (this.selectedClient != null) {
			if (this.selectedDepartment != null) {
				this.existingDataList = this.mainApplication.getDataByClientDepartmentPayFromPayTo(this.selectedClient,
						this.selectedDepartment, this.payFrom, this.payTo, 0);
				return;
			}
			this.existingDataList = this.mainApplication.getDataByClientPayFromPayTo(this.selectedClient, this.payFrom,
					this.payTo);
			return;
		} else {
			this.existingDataList = this.mainApplication.getDataByPayFromPayTo(this.payFrom, this.payTo);

			return;
		}
	}

	public void setBioToBeSaveList() {
		List<EmployeeScheduleUploading> bioList = new ArrayList<>();
		this.bioToBeSaveList.clear();

		bioList.addAll(mainApplication.getListFinalizeBiometricsComplete());

		if (this.selectedClient != null) {
			if (this.selectedDepartment != null) {
				bioToBeSaveList = bioList.stream().filter(
						p -> p.getDepartment().getDepartmentCode().equals(this.selectedDepartment.getDepartmentCode()))
						.collect(Collectors.toList());
				return;
			}
			bioToBeSaveList = bioList.stream()
					.filter(p -> p.getClient().getClientCode().equals(this.selectedClient.getClientCode()))
					.collect(Collectors.toList());
			return;
		} else {
			bioToBeSaveList = bioList;
		}
	}

	public void showAlertMessageInvalidEntry(Boolean isValidEmpIdFormat, Boolean isValidDateFormat,
			Boolean isValidTimeFormat, String empId, String dateEntry, String timeEntry, int entryCheckerCounter) {
		Platform.runLater(() -> {
			this.showInvalidEntries(isValidEmpIdFormat, isValidDateFormat, isValidTimeFormat, empId, dateEntry,
					timeEntry, entryCheckerCounter);
		});
	}

	public synchronized void showInvalidEntries(Boolean isValidEmpIdFormat, Boolean isValidDateFormat,
			Boolean isValidTimeFormat, String empId, String dateEntry, String timeEntry, int entryCheckerCounter) {

		if (!isValidEmpIdFormat) {
			AlertUtil.showInformationAlert(
					"Invalid Employee ID Entry: " + empId + "\non Line number: " + entryCheckerCounter,
					this.getStage());

			this.resetFields();
			return;
		}
		if (!isValidDateFormat) {
			AlertUtil.showInformationAlert(
					"Invalid Date Entry: " + dateEntry + "\non Line number: " + entryCheckerCounter, this.getStage());
			this.resetFields();
			return;
		}
		if (!isValidTimeFormat) {
			AlertUtil.showInformationAlert(
					"Invalid Time Entry: " + timeEntry + "\non Line number: " + entryCheckerCounter, this.getStage());
			this.resetFields();
			return;
		}
	}

	public void setRegularSchedule(EmployeeScheduleUploading employeeScheduleUploading) {
		List<EmployeeScheduleUploading> bioPerEmployeeByDatesList = new ArrayList<>();
		List<EmployeeScheduleUploading> lunchEntryList = new ArrayList<>();

		if (this.employeeScheduleUploadingWithRegularSchedList.size() == 0
				&& this.employeeScheduleUploadingWithRegularSchedList == null) {
			return;
		}

		// collect all entries by employee & each date in this iteration
		bioPerEmployeeByDatesList.addAll(this.employeeScheduleUploadingWithRegularSchedList.stream()
				.filter(p -> p.getEmployee().getEmployeeCode().equals(employeeScheduleUploading.getEmployeeId())
						&& p.getDateEntry().equals(employeeScheduleUploading.getDateEntry()))
				.collect(Collectors.toList()));

		ObservableList<EmployeeScheduleUploading> bioPerEmployeeByDatesSortedList = FXCollections.observableArrayList();
		bioPerEmployeeByDatesSortedList.setAll(bioPerEmployeeByDatesList);
		ObservableListUtil.sort(bioPerEmployeeByDatesSortedList, p -> p.getTimeEntry().toString());

		bioPerEmployeeByDatesList.clear();
		bioPerEmployeeByDatesList.addAll(bioPerEmployeeByDatesSortedList);

		this.listTimeInEntries.clear();
		this.listTimeInEntriesToBeRemove.clear();

		// Separate each entries
		EmployeeScheduleUploading timeIn = new EmployeeScheduleUploading();
		EmployeeScheduleUploading timeOut = new EmployeeScheduleUploading();
		EmployeeScheduleUploading lunchOut = new EmployeeScheduleUploading();
		EmployeeScheduleUploading lunchIn = new EmployeeScheduleUploading();

		if (bioPerEmployeeByDatesList != null && !bioPerEmployeeByDatesList.isEmpty()) {
			timeIn = bioPerEmployeeByDatesList.get(0);
			timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() - 1);

			ObservableListUtil.sort(bioPerEmployeeByDatesList, p -> p.getDateEntry().toString());

			for (EmployeeScheduleUploading employeeScheduleUploadingByEmployeeAndDate : bioPerEmployeeByDatesList) {
				String dayOfDate = employeeScheduleUploadingByEmployeeAndDate.getDayOfDate();

				ScheduleEncoding scheduleEncodingImported = this.mainApplication.getScheduleEncodingMain()
						.getScheduleImportedByEmpIdAndDay(employeeScheduleUploading.getEmployeeId(), dayOfDate);
				EmployeeScheduleEncoding employeeScheduleEncoding = new EmployeeScheduleEncoding();
				employeeScheduleEncoding = null;
				ScheduleEncoding scheduleEncoding = new ScheduleEncoding();
				scheduleEncoding = null;

				if (scheduleEncodingImported != null) {
					scheduleEncoding = scheduleEncodingImported;
				} else {
					// use schedule by client encoded
					// collect all schedule of current employee
					employeeScheduleEncoding = this.mainApplication.getEmployeeScheduleEncodingMain()
							.getEmployeeScheduleByEmployeeID(
									employeeScheduleUploadingByEmployeeAndDate.getEmployee().getEmployeeCode());

					scheduleEncoding = this.mainApplication.getScheduleEncodingMain()
							.getAllScheduleByPrikeyReferenceClientAndScheduleDay(
									employeeScheduleEncoding.getScheduleEncodingReference().getPrimaryKeyReference(),
									dayOfDate);
				}

				if (employeeScheduleEncoding != null || scheduleEncodingImported != null) {

					Calendar timeInReference = Calendar.getInstance();
					Calendar lunchOutReference = Calendar.getInstance();
					Calendar lunchInReference = Calendar.getInstance();
					Calendar timeOutReference = Calendar.getInstance();

					Calendar timeInReferenceEnd = Calendar.getInstance();

					if (scheduleEncoding != null) {
						timeIn = bioPerEmployeeByDatesList.get(0);
						timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() - 1);

						Integer offsetAllowed = scheduleEncoding.getOffsetAllowed();

						timeInReference.setTime(scheduleEncoding.getTimeIn());
						timeOutReference.setTime(scheduleEncoding.getTimeOut());

						timeInReferenceEnd.setTime(timeInReference.getTime());
						timeInReferenceEnd.add(Calendar.MINUTE, offsetAllowed);

						bioPerEmployeeByDatesList.forEach(entry -> {
							Time bioEntryDate = entry.getTimeEntry();
							Calendar bioEntry = Calendar.getInstance();
							bioEntry.setTime(bioEntryDate);

							if (bioEntry.getTime().before(timeInReferenceEnd.getTime())) {
								// if (bioEntry.getTime().before(timeInReferenceEnd.getTime())
								// && bioEntry.getTime().before(timeInReference.getTime())) {
								if (!this.listTimeInEntries.contains(entry)) {
									this.listTimeInEntries.add(entry);
								}
							}
						});

						Boolean isBreakExempted = scheduleEncoding.getIsBreakExempted().equals(1) ? true : false;

						if (!isBreakExempted) {
							lunchOutReference.setTime(scheduleEncoding.getLunchOut());
							lunchInReference.setTime(scheduleEncoding.getLunchIn());

							EmployeeScheduleUploading result[] = this.setTimeEntriesWithLunch(timeIn, timeOut, lunchIn,
									lunchOut, employeeScheduleUploadingByEmployeeAndDate, timeInReferenceEnd,
									timeInReference, lunchOutReference, lunchInReference, timeOutReference,
									lunchEntryList, bioPerEmployeeByDatesList);

							timeIn = result[0];
							lunchOut = result[1];
							lunchIn = result[2];
							timeOut = result[3];
						} else {

							timeIn = bioPerEmployeeByDatesList.get(0);
							timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() - 1);

							EmployeeScheduleUploading[] results = this.setTimeEntriesWithoutLunch(timeIn, timeOut,
									bioPerEmployeeByDatesList, timeInReference, timeInReferenceEnd, timeOutReference,
									employeeScheduleUploadingByEmployeeAndDate);

							timeIn = results[0];
							timeOut = results[1];
							lunchIn = null;
							lunchOut = null;

						}

					} else {
						timeIn = null;
						lunchOut = null;
						lunchIn = null;
						timeOut = null;

						// if (!this.listNoSched.contains(employeeScheduleUploading)) {
						// this.listNoSched.add(employeeScheduleUploading);
						// }
					}
				}
			}
		}

		// already added in irregular schedule list
		// if
		// (this.employeeScheduleUploadingWithIrregularSchedList.contains(employeeScheduleUploading))
		// {
		// continue;
		// }

		// finalize biometrics data and check if already added in regular schedule list
		if (this.setFinalBiometricsData(employeeScheduleUploading, timeIn, timeOut, lunchOut, lunchIn)) {
			return;
		}

		// add in regular schedule list
		this.mainApplication.getListFinalizeBiometricsOnlyBio().add(employeeScheduleUploading);

	}

	public boolean setFinalBiometricsData(EmployeeScheduleUploading employeeScheduleUploading,
			EmployeeScheduleUploading timeIn, EmployeeScheduleUploading timeOut, EmployeeScheduleUploading lunchOut,
			EmployeeScheduleUploading lunchIn) {

		Date dateEntry = employeeScheduleUploading.getDateEntry();

		Time timeInEntry = timeIn == null ? null : timeIn.getTimeEntry();
		Time timeOutEntry = timeOut == null ? null : timeOut.getTimeEntry();
		Time lunchInEntry = lunchIn == null ? null : lunchIn.getTimeEntry();
		Time lunchOutEntry = lunchOut == null ? null : lunchOut.getTimeEntry();

		Date timeOutEntryDate = timeOut == null ? null : timeOut.getDateEntry();

		employeeScheduleUploading
				.setTimeInEntry(this.mainApplication.entryToTimestampConverter(dateEntry, timeInEntry));
		employeeScheduleUploading
				.setTimeOutEntry(this.mainApplication.entryToTimestampConverter(timeOutEntryDate, timeOutEntry));
		employeeScheduleUploading
				.setLunchInEntry(this.mainApplication.entryToTimestampConverter(dateEntry, lunchInEntry));
		employeeScheduleUploading
				.setLunchOutEntry(this.mainApplication.entryToTimestampConverter(dateEntry, lunchOutEntry));
		employeeScheduleUploading.setIsValidated(0);

		this.mainApplication.computeUndertime(employeeScheduleUploading);

		// this.mainApplication.setRemarksEntry(employeeScheduleUploading, dateEntry,
		// true);
		// this.mainApplication.checkDateRestDay(employeeScheduleUploading, dateEntry,
		// true);
		// this.mainApplication.checkDateHoliday(employeeScheduleUploading, dateEntry);
		// this.mainApplication.checkDateNoLogs(employeeScheduleUploading, dateEntry);
		// this.mainApplication.checkDateLeaveFiling(employeeScheduleUploading);

		// after all checking / process, add to final processed list to be ready for
		// uploading/saving to database
		if (this.mainApplication.getListFinalizeBiometricsOnlyBio().size() == 0) {
			this.mainApplication.getListFinalizeBiometricsOnlyBio().add(employeeScheduleUploading);
			return true;
		}

		// double check if already added
		for (EmployeeScheduleUploading savedEmployeeScheduleEncoding : this.mainApplication
				.getListFinalizeBiometricsOnlyBio()) {

			if (savedEmployeeScheduleEncoding.getEmploymentHistory().getEmployee().getEmployeeCode()
					.equals(employeeScheduleUploading.getEmploymentHistory().getEmployee().getEmployeeCode())
					&& savedEmployeeScheduleEncoding.getDateEntry().equals(employeeScheduleUploading.getDateEntry())) {
				if (!savedEmployeeScheduleEncoding.getTimeEntry().equals(employeeScheduleUploading.getTimeEntry())) {
					int index = this.mainApplication.getListFinalizeBiometricsOnlyBio()
							.indexOf(savedEmployeeScheduleEncoding);
					this.mainApplication.getListFinalizeBiometricsOnlyBio().set(index, employeeScheduleUploading);
					return true;
				}
			}
		}

		return false;
	}

	public void setIrregularSchedule(EmployeeScheduleUploading employeeScheduleUploadingWithIrregular) {
		if (this.employeeScheduleUploadingWithIrregularSchedList.size() == 0
				|| this.employeeScheduleUploadingWithIrregularSchedList == null) {
			return;
		}

		EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();
		List<EmployeeScheduleUploading> lunchEntryList = new ArrayList<>();
		List<EmployeeScheduleUploading> bioPerEmployeeByDatesList = new ArrayList<>();

		EmployeeScheduleUploading timeIn = new EmployeeScheduleUploading();
		EmployeeScheduleUploading timeOut = new EmployeeScheduleUploading();
		EmployeeScheduleUploading lunchOut = new EmployeeScheduleUploading();
		EmployeeScheduleUploading lunchIn = new EmployeeScheduleUploading();

		employeeScheduleEncodingIrregular = this.mainApplication.getEmployeeScheduleEncodingIrregularMain()
				.getEmployeeIrregularScheduleByDateAndEmployeeID(
						new SimpleDateFormat("yyyy-MM-dd")
								.format(employeeScheduleUploadingWithIrregular.getDateEntry()),
						employeeScheduleUploadingWithIrregular.getEmployeeId());

		for (EmployeeScheduleUploading emp : this.employeeScheduleUploadingWithIrregularSchedList) {
			if (emp.getEmploymentHistory().getEmployee().getEmployeeCode().compareTo(
					employeeScheduleEncodingIrregular.getEmploymentHistory().getEmployee().getEmployeeCode()) == 0) {
				if (emp.getDateEntry().equals(employeeScheduleEncodingIrregular.getDateSchedule())) {
					if (!bioPerEmployeeByDatesList.contains(emp)) {
						bioPerEmployeeByDatesList.add(emp);
					}
				}
			}
		}

		timeIn = bioPerEmployeeByDatesList.get(0);
		timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() - 1);

		this.listTimeInEntries.clear();

		for (EmployeeScheduleUploading employeeScheduleEncodingByEmployeeAndDate : bioPerEmployeeByDatesList) {

			Calendar timeInReference = Calendar.getInstance();
			Calendar lunchOutReference = Calendar.getInstance();
			Calendar lunchInReference = Calendar.getInstance();
			Calendar timeOutReference = Calendar.getInstance();

			Calendar timeInReferenceEnd = Calendar.getInstance();

			Integer offsetAllowed = employeeScheduleEncodingIrregular.getOffsetAllowed();

			timeInReference.setTime(employeeScheduleEncodingIrregular.getTimeIn());
			timeOutReference.setTime(employeeScheduleEncodingIrregular.getTimeOut());

			timeInReferenceEnd.setTime(timeInReference.getTime());
			timeInReferenceEnd.add(Calendar.MINUTE, offsetAllowed);

			bioPerEmployeeByDatesList.forEach(entry -> {
				if (entry.getTimeEntry() == null) {
					return;
				}

				Time bioEntryDate = entry.getTimeEntry();
				Calendar bioEntry = Calendar.getInstance();
				bioEntry.setTime(bioEntryDate);

				if (bioEntry.getTime().before(timeInReferenceEnd.getTime())
						&& bioEntry.getTime().before(timeInReference.getTime())) {

					if (!this.listTimeInEntries.contains(entry)) {
						this.listTimeInEntries.add(entry);
					}
				}
			});

			Boolean isBreakExempted = employeeScheduleEncodingIrregular.getIsBreakExempted().equals(1) ? true : false;

			if (!isBreakExempted) {

				lunchOutReference.setTime(employeeScheduleEncodingIrregular.getLunchOut());
				lunchInReference.setTime(employeeScheduleEncodingIrregular.getLunchIn());

				EmployeeScheduleUploading result[] = this.setTimeEntriesWithLunch(timeIn, timeOut, lunchIn, lunchOut,
						employeeScheduleEncodingByEmployeeAndDate, timeInReferenceEnd, timeInReference,
						lunchOutReference, lunchInReference, timeOutReference, lunchEntryList,
						bioPerEmployeeByDatesList);

				timeIn = result[0];
				lunchOut = result[1];
				lunchIn = result[2];
				timeOut = result[3];

			} else {

				timeIn = bioPerEmployeeByDatesList.get(0);
				timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() - 1);

				EmployeeScheduleUploading[] results = this.setTimeEntriesWithoutLunch(timeIn, timeOut,
						bioPerEmployeeByDatesList, timeInReference, timeInReferenceEnd, timeOutReference,
						employeeScheduleEncodingByEmployeeAndDate);

				timeIn = results[0];
				timeOut = results[1];
				lunchIn = null;
				lunchOut = null;

			}

		}

		// finalize biometrics data and check if already added in final biometrics list
		if (this.setFinalBiometricsData(employeeScheduleUploadingWithIrregular, timeIn, timeOut, lunchOut, lunchIn)) {
			return;
		}

		// else add in final biometrics list
		this.mainApplication.getListFinalizeBiometricsOnlyBio().add(employeeScheduleUploadingWithIrregular);

	}

	public void checkBiometricsScheduleType(EmployeeScheduleUploading employeeScheduleUploading) {
		String dayOfDate = employeeScheduleUploading.getDayOfDate();

		this.mainApplication.checkDateRestDay(employeeScheduleUploading, employeeScheduleUploading.getDateEntry());

		EmployeeScheduleEncoding employeeScheduleEncodingRegular = new EmployeeScheduleEncoding();
		EmployeeScheduleEncodingIrregular employeeScheduleEncodingIrregular = new EmployeeScheduleEncodingIrregular();

		employeeScheduleEncodingRegular = this.mainApplication.getEmployeeScheduleEncodingMain()
				.getEmployeeScheduleByEmployeeID(employeeScheduleUploading.getEmployeeId());

		employeeScheduleEncodingIrregular = this.mainApplication.getEmployeeScheduleEncodingIrregularMain()
				.getEmployeeIrregularScheduleByDateAndEmployeeID(
						new SimpleDateFormat("yyyy-MM-dd").format(employeeScheduleUploading.getDateEntry()),
						employeeScheduleUploading.getEmployeeId());

		ScheduleEncoding scheduleEncoding = new ScheduleEncoding();
		scheduleEncoding = null;

		List<ScheduleEncoding> scheduleEncodingImportedList = new ArrayList<>();
		scheduleEncodingImportedList.addAll(this.mainApplication.getScheduleEncodingMain()
				.getAllScheduleUploadedByEmployeeId(employeeScheduleUploading.getEmployeeId()));

		if (!scheduleEncodingImportedList.isEmpty()) {
			scheduleEncoding = scheduleEncodingImportedList.stream().filter(p -> p.getScheduleDay().equals(dayOfDate))
					.findFirst().orElse(null);

		} else {
			if (employeeScheduleEncodingRegular != null) {
				scheduleEncoding = this.mainApplication.getScheduleEncodingMain()
						.getAllScheduleByPrikeyReferenceClientAndScheduleDay(
								employeeScheduleEncodingRegular.getScheduleEncodingReference().getPrimaryKeyReference(),
								dayOfDate);
			}
		}

		if (scheduleEncoding == null && employeeScheduleEncodingIrregular == null) {
			// possible overtime on rest day hence no reg sched(in day) and irreg sched
			// added on reg sched list to breakdown overtime using overtime config sched
			employeeScheduleUploading.setIsRegularSchedule(0);
			if (!this.listNoSched.contains(employeeScheduleUploading)) {
				this.listNoSched.add(employeeScheduleUploading);
				return;
			}

			// if
			// (!this.employeeScheduleUploadingWithRegularSchedList.contains(employeeScheduleUploading))
			// {
			// this.employeeScheduleUploadingWithRegularSchedList.add(employeeScheduleUploading);
			// return;
			// }

		}

		if (employeeScheduleEncodingIrregular != null) {
			employeeScheduleUploading.setIsRegularSchedule(0);
			employeeScheduleUploading.setEmployeeScheduleEncodingIrregular(employeeScheduleEncodingIrregular);
			if (!this.employeeScheduleUploadingWithIrregularSchedList.contains(employeeScheduleUploading)) {
				this.employeeScheduleUploadingWithIrregularSchedList.add(employeeScheduleUploading);
				return;
			}
		}

		if (scheduleEncoding != null) {
			employeeScheduleUploading.setIsRegularSchedule(1);
			employeeScheduleUploading.setScheduleEncoding(scheduleEncoding);

			if (!this.employeeScheduleUploadingWithRegularSchedList.contains(employeeScheduleUploading)) {
				this.employeeScheduleUploadingWithRegularSchedList.add(employeeScheduleUploading);
				return;
			}
		}
	}

	public void setEntryNoSched(Integer employeeCode) {
		if (!this.listNoSched.isEmpty() && this.listNoSched.size() > 0 && this.listNoSched != null) {
			List<EmployeeScheduleUploading> bioPerEmployeeList = new ArrayList<>();
			List<Date> dateEntryFromBioPerEmployeeList = new ArrayList<>();

			Calendar timeInReference = Calendar.getInstance();
			Calendar lunchOutReference = Calendar.getInstance();
			Calendar lunchInReference = Calendar.getInstance();
			Calendar timeOutReference = Calendar.getInstance();

			Calendar timeInReferenceEnd = Calendar.getInstance();

			bioPerEmployeeList.addAll(this.listNoSched.stream()
					.filter(p -> p.getEmploymentHistory().getEmployee().getEmployeeCode().equals(employeeCode))
					.collect(Collectors.toList()));

			dateEntryFromBioPerEmployeeList.addAll(
					bioPerEmployeeList.stream().map(p -> p.getDateEntry()).distinct().collect(Collectors.toList()));

			for (Date dateEntry : dateEntryFromBioPerEmployeeList) {
				List<EmployeeScheduleUploading> bioPerEmployeeByDateList = new ArrayList<>();
				List<EmployeeScheduleUploading> lunchEntryList = new ArrayList<>();

				bioPerEmployeeByDateList.addAll(bioPerEmployeeList.stream()
						.filter(p -> p.getDateEntry().equals(dateEntry)).collect(Collectors.toList()));

				ObservableList<EmployeeScheduleUploading> bioPerEmployeeByDatesSortedList = FXCollections
						.observableArrayList();
				bioPerEmployeeByDatesSortedList.setAll(bioPerEmployeeByDateList);
				ObservableListUtil.sort(bioPerEmployeeByDatesSortedList, p -> p.getTimeEntry().toString());

				// Separate each entries
				EmployeeScheduleUploading timeIn = new EmployeeScheduleUploading();
				EmployeeScheduleUploading timeOut = new EmployeeScheduleUploading();
				EmployeeScheduleUploading lunchOut = new EmployeeScheduleUploading();
				EmployeeScheduleUploading lunchIn = new EmployeeScheduleUploading();

				if (bioPerEmployeeByDatesSortedList != null && !bioPerEmployeeByDatesSortedList.isEmpty()) {
					timeIn = bioPerEmployeeByDatesSortedList.get(0);
					timeOut = bioPerEmployeeByDatesSortedList.get(bioPerEmployeeByDatesSortedList.size() - 1);

					Calendar timeInSchedEnd = Calendar.getInstance();
					timeInSchedEnd.setTime(timeIn.getTimeEntry());

					ObservableListUtil.sort(bioPerEmployeeByDatesSortedList, p -> p.getDateEntry().toString());

					for (EmployeeScheduleUploading employeeScheduleUploading : bioPerEmployeeByDatesSortedList) {
						// possible rest day entries with lunch
						EmployeeScheduleEncoding employeeScheduleEncoding = this.mainApplication
								.getEmployeeScheduleEncodingMain().getEmployeeScheduleByEmployeeID(employeeCode);

						if (employeeScheduleEncoding == null) {
							continue;
						}

						List<ScheduleEncoding> scheduleEncodingList = this.mainApplication.getScheduleEncodingMain()
								.getAllScheduleByPrikeyReferenceClient(employeeScheduleEncoding
										.getScheduleEncodingReference().getPrimaryKeyReference());

						if (scheduleEncodingList != null && !scheduleEncodingList.isEmpty()) {
							ScheduleEncoding scheduleEncoding = scheduleEncodingList.get(0);
							boolean isLunchExempted = scheduleEncoding.getIsBreakExempted() == 1 ? true : false;

							timeInReference.setTime(scheduleEncodingList.get(0).getTimeIn());
							if (scheduleEncodingList.get(0).getLunchOut() != null) {
								lunchOutReference.setTime(scheduleEncodingList.get(0).getLunchOut());
							} else {
								lunchOutReference = null;
							}
							if (scheduleEncodingList.get(0).getLunchIn() != null) {
								lunchInReference.setTime(scheduleEncodingList.get(0).getLunchIn());
							} else {
								lunchInReference = null;
							}
							timeOutReference.setTime(scheduleEncodingList.get(0).getTimeOut());

							Integer offsetAllowed = scheduleEncodingList.get(0).getOffsetAllowed();
							timeInReferenceEnd.setTime(timeInReference.getTime());
							timeInReferenceEnd.add(Calendar.MINUTE, offsetAllowed);

							if (isLunchExempted) {
								EmployeeScheduleUploading result[] = this.setTimeEntriesWithoutLunch(timeIn, timeOut,
										bioPerEmployeeByDatesSortedList, timeInReference, timeInReferenceEnd,
										timeOutReference, employeeScheduleUploading);

								timeIn = result[0];
								lunchOut = null;
								lunchIn = null;
								timeOut = result[1];

							} else {
								EmployeeScheduleUploading result[] = this.setTimeEntriesWithLunch(timeIn, timeOut,
										lunchIn, lunchOut, employeeScheduleUploading, timeInReferenceEnd,
										timeInReference, lunchOutReference, lunchInReference, timeOutReference,
										lunchEntryList, bioPerEmployeeByDatesSortedList);

								timeIn = result[0];
								lunchOut = result[1];
								lunchIn = result[2];
								timeOut = result[3];
							}

						}

						if (this.setFinalBiometricsData(employeeScheduleUploading, timeIn, timeOut, lunchOut,
								lunchIn)) {
							continue;
						}

						this.mainApplication.getListFinalizeBiometricsOnlyBio().add(employeeScheduleUploading);
					}
				}
			}
		}
	}

	public EmployeeScheduleUploading createEmployeeScheduleUploading(
			EmployeeScheduleUploading employeeScheduleUploading, String empId, String dateEntry, String timeEntry,
			String bioId, String bioArms) {
		employeeScheduleUploading.setEmployeeId(Integer.valueOf(empId));
		employeeScheduleUploading.setDateEntry(DateUtil.parseDate(dateEntry));

		Timestamp entry = this.mainApplication.entryToTimestampConverter(DateUtil.parseDate(dateEntry),
				Time.valueOf(timeEntry));

		employeeScheduleUploading.setTimeEntry(Time.valueOf(new SimpleDateFormat("HH:mm:ss").format(entry)));
		employeeScheduleUploading.setBioId(bioId);

		EmploymentHistory employmentHistoryMax = mainApplication.getEmploymentHistoryMain()
				.getEmploymentHistoryMaxByEmployeeCode(Integer.valueOf(empId));

		if (employmentHistoryMax != null) {
			employeeScheduleUploading.setEmploymentHistory(employmentHistoryMax);
		}

		employeeScheduleUploading.setEmployee(employeeScheduleUploading.getEmploymentHistory().getEmployee());

		Client client = new Client();
		client = mainApplication.getClientMain().getClientByCode(employeeScheduleUploading.getEmployee().getClient());
		employeeScheduleUploading.setClient(client);

		Department department = new Department();
		department = mainApplication.getDepartmentMain()
				.getDepartmentByCode(employeeScheduleUploading.getEmployee().getDepartment());
		employeeScheduleUploading.setDepartment(department);

		employeeScheduleUploading.setBioArms(bioArms);

		return employeeScheduleUploading;
	}

	public void resetFields() {

		this.mainApplication.getListFinalizeBiometricsComplete().clear();
		this.extractedEmployeesList.clear();
		this.empIdList.clear();
		this.listRegularSched.clear();
		this.listIrregularSched.clear();
		this.listNoSched.clear();

		this.mainApplication.getListFinalizeBiometricsOnlyBio().clear();
		this.mainApplication.getEmployeeObservableList().clear();
		this.mainApplication.getEmployeeScheduleUploadingList().clear();
		this.mainApplication.getOvertimeHeaderList().clear();

		this.existingDataList.clear();
		this.newEntrySaveList.clear();
		this.updateEntrySaveList.clear();
		this.deleteEntrySaveList.clear();

	}

	public EmployeeScheduleUploading[] setTimeEntriesWithLunch(EmployeeScheduleUploading timeIn,
			EmployeeScheduleUploading timeOut, EmployeeScheduleUploading lunchIn, EmployeeScheduleUploading lunchOut,
			EmployeeScheduleUploading employeeScheduleEncodingByEmployeeAndDate, Calendar timeInReferenceEnd,
			Calendar timeInReference, Calendar lunchOutReference, Calendar lunchInReference, Calendar timeOutReference,
			List<EmployeeScheduleUploading> lunchEntryList, List<EmployeeScheduleUploading> bioPerEmployeeByDatesList) {
		EmployeeScheduleUploading[] results = new EmployeeScheduleUploading[4];
		results[0] = new EmployeeScheduleUploading();
		results[1] = new EmployeeScheduleUploading();
		results[2] = new EmployeeScheduleUploading();
		results[3] = new EmployeeScheduleUploading();

		try {
			Calendar timeEntryCal = Calendar.getInstance();

			Time timeEntrySet = employeeScheduleEncodingByEmployeeAndDate.getTimeEntry();
			timeEntryCal.setTime(timeEntrySet);

			if (this.mainApplication.getClientPrikeyLunchEntrySameConfigList()
					.contains(employeeScheduleEncodingByEmployeeAndDate.getClient().getCompany().getCompanyCode())) {
				lunchOutReference.add(Calendar.MINUTE, -60);
				if ((timeEntryCal.getTime().after(lunchOutReference.getTime())
						|| timeEntryCal.getTime().equals(lunchOutReference.getTime()))
						&& (timeEntryCal.getTime().before(lunchInReference.getTime())
								|| timeEntryCal.getTime().equals(lunchInReference.getTime())
								|| timeEntryCal.getTime().before(timeOutReference.getTime()))) {
					lunchEntryList.add(employeeScheduleEncodingByEmployeeAndDate);
				}
			} else {
				if (timeEntryCal.getTime().after(timeInReferenceEnd.getTime())
						&& (timeEntryCal.getTime().before(lunchInReference.getTime())
								|| timeEntryCal.getTime().equals(lunchInReference.getTime())
								|| timeEntryCal.getTime().before(timeOutReference.getTime()))) {
					lunchEntryList.add(employeeScheduleEncodingByEmployeeAndDate);
				}
			}

			if (lunchEntryList != null && !lunchEntryList.isEmpty()) {
				lunchOut = lunchEntryList.get(0);
				lunchIn = lunchEntryList.get(lunchEntryList.size() - 1);
			}

			Calendar timeInEntryCal = Calendar.getInstance();
			Calendar timeOutEntryCal = Calendar.getInstance();
			Calendar lunchInEntryCal = Calendar.getInstance();
			Calendar lunchOutEntryCal = Calendar.getInstance();

			if (timeIn != null) {
				if (timeIn.getTimeEntry() != null) {
					Time timeInSet = timeIn.getTimeEntry();
					timeInEntryCal.setTime(timeInSet);
				} else {
					timeInEntryCal = null;
				}
			}

			if (timeOut != null) {
				if (timeOut.getTimeEntry() != null) {
					Time timeOutSet = timeOut.getTimeEntry();
					timeOutEntryCal.setTime(timeOutSet);
				} else {
					timeOutEntryCal = null;
				}
			}

			if (lunchOut != null) {
				if (lunchOut.getTimeEntry() != null) {
					Time lunchOutSet = lunchOut.getTimeEntry();
					lunchOutEntryCal.setTime(lunchOutSet);
				} else {
					lunchOutEntryCal = null;
				}
			}

			if (lunchIn != null) {
				if (lunchIn.getTimeEntry() != null) {
					Time lunchInSet = lunchIn.getTimeEntry();
					lunchInEntryCal.setTime(lunchInSet);
				} else {
					lunchInEntryCal = null;
				}
			}

			Boolean isSameTimeInOutHr = false;
			if (timeInEntryCal != null && timeOutEntryCal != null) {
				isSameTimeInOutHr = Integer.valueOf(timeInEntryCal.get(Calendar.HOUR_OF_DAY))
						.compareTo(Integer.valueOf(timeOutEntryCal.get(Calendar.HOUR_OF_DAY))) == 0;
			}

			if (bioPerEmployeeByDatesList.size() == 1) {

				timeIn = bioPerEmployeeByDatesList.get(0);
				lunchOut = null;
				lunchIn = null;
				timeOut = null;

			} else {

				if (timeInEntryCal.getTime().before(timeInReferenceEnd.getTime())) {
					timeIn = bioPerEmployeeByDatesList.get(0);
				}

				if (timeOutEntryCal.getTime().equals(timeOutReference.getTime())
						|| timeOutEntryCal.getTime().after(timeOutReference.getTime())) {
					timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() - 1);
				}

				if (isSameTimeInOutHr) {
					timeIn = bioPerEmployeeByDatesList.get(0);
					timeOut = null;
					if (timeOutEntryCal.getTime().equals(timeOutReference.getTime())
							|| timeOutEntryCal.getTime().after(timeOutReference.getTime())) {
						timeIn = null;
						timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() - 1);
					}
				}

				if (timeInEntryCal.getTime().after(timeInReferenceEnd.getTime())) {
					timeIn = null;
					lunchOut = bioPerEmployeeByDatesList.get(0);
				}

				if (lunchEntryList != null && !lunchEntryList.isEmpty()) {

					if (lunchInEntryCal.getTime().equals(timeOutEntryCal.getTime())) {
						lunchIn = lunchEntryList.get(lunchEntryList.size() - 1);
						timeOut = null;
					}

					if (lunchOutEntryCal.getTime().after(timeInReferenceEnd.getTime())
							&& lunchOutEntryCal.getTime().before(lunchOutReference.getTime())
							&& !lunchOutEntryCal.getTime().equals(lunchInEntryCal.getTime())) {
						lunchOut = lunchEntryList.get(0);
					}

					// if (lunchInEntryCal.getTime().equals(lunchInReference.getTime())
					// || (lunchInEntryCal.getTime().after(lunchInReference.getTime())
					// && lunchInEntryCal.getTime().before(timeOutReference.getTime()))) {
					// lunchIn = lunchEntryList.get(lunchEntryList.size() - 1);
					// }

					if (timeInEntryCal.getTime().equals(lunchOutEntryCal.getTime())) {
						if (timeInEntryCal.getTime().before(timeInReferenceEnd.getTime())) {
							timeIn = lunchEntryList.get(0);
							lunchOut = null;
						} else {
							timeIn = null;
							lunchOut = lunchEntryList.get(0);
						}
					}

					if (lunchOutEntryCal.getTime().equals(lunchInEntryCal.getTime())) {
						lunchOut = lunchEntryList.get(0);
						lunchIn = null;

						if (lunchOutEntryCal.getTime().equals(lunchInReference.getTime())
								|| (lunchOutEntryCal.getTime().after(lunchInReference.getTime())
										&& lunchOutEntryCal.getTime().before(timeOutReference.getTime()))) {
							lunchOut = null;
							lunchIn = lunchEntryList.get(lunchEntryList.size() - 1);
						}
					}

					// if (timeInEntryCal.getTime().equals(lunchOutEntryCal.getTime())
					// && !lunchInEntryCal.getTime().equals(timeOutEntryCal.getTime())
					// && !lunchOutEntryCal.getTime().equals(lunchInEntryCal.getTime())) {
					// timeIn = null;
					// lunchOut = lunchEntryList.get(0);
					// lunchIn = lunchEntryList.get(lunchEntryList.size() - 1);
					// timeOut = bioPerEmployeeByDatesList.get(bioPerEmployeeByDatesList.size() -
					// 1);
					// }

					if (this.mainApplication.getClientPrikeyLunchEntrySameConfigList().contains(
							employeeScheduleEncodingByEmployeeAndDate.getClient().getCompany().getCompanyCode())) {
						if (lunchEntryList.size() == 1) {
							lunchOut = lunchEntryList.get(0);
							lunchIn = lunchOut;

							if (timeIn != null && timeIn.equals(lunchOut)) {
								timeIn = null;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		EmployeeScheduleUploading[] resultsOvertime = this.checkDateOvertime(employeeScheduleEncodingByEmployeeAndDate,
				timeInReferenceEnd, timeIn, timeOut);

		results[0] = resultsOvertime[0];
		results[1] = lunchOut;
		results[2] = lunchIn;
		results[3] = resultsOvertime[1];

		return results;
	}

	public EmployeeScheduleUploading[] setTimeEntriesWithoutLunch(EmployeeScheduleUploading timeIn,
			EmployeeScheduleUploading timeOut, List<EmployeeScheduleUploading> bioPerEmployeeByDatesList,
			Calendar timeInReference, Calendar timeInReferenceEnd, Calendar timeOutReference,
			EmployeeScheduleUploading employeeScheduleEncodingByEmployeeAndDate) {
		EmployeeScheduleUploading[] results = new EmployeeScheduleUploading[2];
		results[0] = new EmployeeScheduleUploading();
		results[1] = new EmployeeScheduleUploading();
		try {
			Calendar timeInCal = Calendar.getInstance();
			Calendar timeOutEntryCal = Calendar.getInstance();
			Boolean isSameTimeInOutHr = false;

			if (timeIn != null) {
				Time timeInSet = timeIn.getTimeEntry();
				timeInCal.setTime(timeInSet);
			} else {
				timeInCal = null;
			}

			if (timeOut != null) {
				Time timeOutSet = timeOut.getTimeEntry();
				timeOutEntryCal.setTime(timeOutSet);
			} else {
				timeOutEntryCal = null;
			}

			if (timeInCal != null && timeOutEntryCal != null) {
				isSameTimeInOutHr = Integer.valueOf(timeInCal.get(Calendar.HOUR_OF_DAY))
						.compareTo(Integer.valueOf(timeOutEntryCal.get(Calendar.HOUR_OF_DAY))) == 0;
			}

			if (bioPerEmployeeByDatesList.size() == 1) {
				timeIn = bioPerEmployeeByDatesList.get(0);
				timeOut = null;

				if (timeInCal.getTime().after(timeInReferenceEnd.getTime())
						&& timeOutEntryCal.getTime().after(timeInReferenceEnd.getTime())) {

					Timestamp timeOutTimestamp = this.mainApplication.entryToTimestampConverter(
							employeeScheduleEncodingByEmployeeAndDate.getDateEntry(),
							employeeScheduleEncodingByEmployeeAndDate.getTimeEntry());

					Date timeInEndRefDate = timeInReferenceEnd.getTime();
					String time = new SimpleDateFormat("HH:mm:ss").format(timeInEndRefDate);

					Timestamp timeInEndRef = this.mainApplication.entryToTimestampConverter(
							employeeScheduleEncodingByEmployeeAndDate.getDateEntry(), Time.valueOf(time));

					Integer timeDiff = this.mainApplication.computeTotalMinsEntry(timeInEndRef, timeOutTimestamp);
					if (timeDiff > 120) {
						timeIn = null;
						timeOut = bioPerEmployeeByDatesList.get(0);
					}
				}

			} else {
				if (isSameTimeInOutHr && timeInCal.getTime().before(timeInReferenceEnd.getTime())
						&& timeOutEntryCal.getTime().before(timeInReferenceEnd.getTime())) {
					timeIn = bioPerEmployeeByDatesList.get(0);
					timeOut = null;
				}

				if (isSameTimeInOutHr && timeInCal.getTime().after(timeInReferenceEnd.getTime())
						&& timeOutEntryCal.getTime().after(timeInReferenceEnd.getTime())) {
					timeIn = null;
					timeOut = bioPerEmployeeByDatesList.get(0);
				}

				if (timeIn == null) {
					if (timeOutEntryCal.getTime().after(timeInReferenceEnd.getTime())
							&& timeOutEntryCal.getTime().before(timeOutReference.getTime())) {
						timeIn = null;
						timeOut = bioPerEmployeeByDatesList.get(0);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		EmployeeScheduleUploading[] resultOvertime = this.checkDateOvertime(employeeScheduleEncodingByEmployeeAndDate,
				timeInReferenceEnd, timeIn, timeOut);

		results[0] = resultOvertime[0];
		results[1] = resultOvertime[1];

		return results;
	}

	public EmployeeScheduleUploading[] checkDateOvertime(
			EmployeeScheduleUploading employeeScheduleEncodingByEmployeeAndDate, Calendar timeInReferenceEnd,
			EmployeeScheduleUploading timeIn, EmployeeScheduleUploading timeOut) {

		EmployeeScheduleUploading[] results = new EmployeeScheduleUploading[2];
		results[0] = new EmployeeScheduleUploading();
		results[1] = new EmployeeScheduleUploading();
		boolean isIntegralEmployee = employeeScheduleEncodingByEmployeeAndDate.getEmploymentHistory().getClient()
				.getClientCode().equals("LBPSC") ? true : false;

		List<OvertimeFiling> overtimeFilingList = new ArrayList<>();
		List<OvertimeFilingClient> overtimeFilingClientList = new ArrayList<>();

		if (isIntegralEmployee) {
			User user = new User();
			user = this.mainApplication.getUserMain()
					.getUserByEmployeeId(employeeScheduleEncodingByEmployeeAndDate.getEmployeeId());

			if (user != null) {
				Integer prikeyUser = user.getPrimaryKey();

				overtimeFilingList = this.mainApplication.getOvertimeFilingMain()
						.getAllOvertimeByOvertimeFromAndPrikeyUser(
								new SimpleDateFormat("yyyy-MM-dd")
										.format(employeeScheduleEncodingByEmployeeAndDate.getDateEntry()).concat("%"),
								prikeyUser);

				if (overtimeFilingList != null && !overtimeFilingList.isEmpty() && overtimeFilingList.size() != 0) {
					OvertimeFiling overtimeFiling = new OvertimeFiling();
					// fix allowed overtime to this day if there's ot next day
					if (overtimeFilingList.size() == 1) {
						overtimeFiling = overtimeFilingList.get(0);
					} else {
						ObservableList<OvertimeFiling> sortObsList = FXCollections.observableArrayList();
						sortObsList.setAll(overtimeFilingList);

						overtimeFiling = overtimeFilingList.get(overtimeFilingList.size() - 1);
					}

					if (overtimeFiling.getBypassDateTimeTo() != null) {
						employeeScheduleEncodingByEmployeeAndDate.setAllowedOvertime(
								this.mainApplication.timestampToTime(overtimeFiling.getBypassDateTimeTo()));
					} else {
						employeeScheduleEncodingByEmployeeAndDate.setAllowedOvertime(
								this.mainApplication.timestampToTime(overtimeFiling.getDateTimeTo()));
					}

					// fix time out for this day from the time in next day based from ot filing
					Date OTEndDate = DateUtil
							.parseDate(overtimeFiling.getDateTimeTo().toLocalDateTime().toLocalDate().toString());

					Calendar OTEndDateCal = Calendar.getInstance();
					OTEndDateCal.setTime(overtimeFiling.getDateTimeTo());

					List<EmployeeScheduleUploading> tempListBioNextDay = new ArrayList<>();
					List<EmployeeScheduleUploading> listTimeInEntriesNextDay = new ArrayList<>();

					if (!OTEndDate.equals(employeeScheduleEncodingByEmployeeAndDate.getDateEntry())) {

						tempListBioNextDay.addAll(this.mainApplication.getEmployeeScheduleUploadingList().stream()
								.filter(p -> p.getDateEntry().equals(OTEndDate)).collect(Collectors.toList()));

						tempListBioNextDay.forEach(entry -> {
							Time bioEntryDate = entry.getTimeEntry();
							Calendar bioEntry = Calendar.getInstance();
							bioEntry.setTime(bioEntryDate);

							if (bioEntry.getTime().before(timeInReferenceEnd.getTime())) {
								if (!listTimeInEntriesNextDay.contains(entry)) {
									listTimeInEntriesNextDay.add(entry);
								}
							}
						});

						if (listTimeInEntriesNextDay != null && !listTimeInEntriesNextDay.isEmpty()) {
							ObservableList<EmployeeScheduleUploading> sortEntriesPerDateByTimeObsList = FXCollections
									.observableArrayList();

							sortEntriesPerDateByTimeObsList.setAll(listTimeInEntriesNextDay);
							listTimeInEntriesNextDay.clear();

							ObservableListUtil.sort(sortEntriesPerDateByTimeObsList, p -> p.getTimeEntry().toString());
							listTimeInEntriesNextDay.addAll(sortEntriesPerDateByTimeObsList);

							timeOut = listTimeInEntriesNextDay.get(listTimeInEntriesNextDay.size() - 2);
							timeOut.setDateEntry(OTEndDate);
						}
					}

				} else {
					// next loop, next day with ot from prev day, fix time in
					overtimeFilingList.clear();
					this.listTimeInEntriesToBeRemove.clear();

					overtimeFilingList = this.mainApplication.getOvertimeFilingMain()
							.getAllOvertimeByOvertimeToAndPrikeyUser(new SimpleDateFormat("yyyy-MM-dd")
									.format(employeeScheduleEncodingByEmployeeAndDate.getDateEntry()).concat("%"),
									prikeyUser);

					if (!overtimeFilingList.isEmpty() && overtimeFilingList != null) {
						if (!this.listTimeInEntries.isEmpty() && this.listTimeInEntries.size() != 0
								&& this.listTimeInEntries != null) {
							timeIn = this.listTimeInEntries.get(listTimeInEntries.size() - 1);
						} else {
							timeIn = null;
						}
					}

				}
			}
		} else {
			overtimeFilingClientList = this.mainApplication.getOvertimeFilingClientMain()
					.getDataByEmpIdAndOvertimeDateFrom(employeeScheduleEncodingByEmployeeAndDate.getEmploymentHistory()
							.getEmployee().getEmployeeCode(), employeeScheduleEncodingByEmployeeAndDate.getDateEntry());

			if (overtimeFilingClientList != null && !overtimeFilingClientList.isEmpty()
					&& overtimeFilingClientList.size() != 0) {
				OvertimeFilingClient overtimeFilingClient = new OvertimeFilingClient();

				if (overtimeFilingClientList.size() == 1) {
					overtimeFilingClient = overtimeFilingClientList.get(0);
				} else {
					overtimeFilingClient = overtimeFilingClientList.get(overtimeFilingClientList.size() - 1);
				}

				employeeScheduleEncodingByEmployeeAndDate
						.setAllowedOvertime(this.mainApplication.timestampToTime(overtimeFilingClient.getDateTimeTo()));

				Date OTEndDate = DateUtil
						.parseDate(overtimeFilingClient.getDateTimeTo().toLocalDateTime().toLocalDate().toString());

				Calendar OTEndDateCal = Calendar.getInstance();
				OTEndDateCal.setTime(overtimeFilingClient.getDateTimeTo());

				List<EmployeeScheduleUploading> tempListBioNextDay = new ArrayList<>();
				List<EmployeeScheduleUploading> listTimeInEntriesNextDay = new ArrayList<>();

				if (!OTEndDate.equals(employeeScheduleEncodingByEmployeeAndDate.getDateEntry())) {

					tempListBioNextDay.addAll(this.mainApplication.getEmployeeScheduleUploadingList().stream()
							.filter(p -> p.getDateEntry().equals(OTEndDate)).collect(Collectors.toList()));

					tempListBioNextDay.forEach(entry -> {
						Time bioEntryDate = entry.getTimeEntry();
						Calendar bioEntry = Calendar.getInstance();
						bioEntry.setTime(bioEntryDate);

						if (bioEntry.getTime().before(timeInReferenceEnd.getTime())) {
							if (!listTimeInEntriesNextDay.contains(entry)) {
								listTimeInEntriesNextDay.add(entry);
							}
						}
					});

					if (listTimeInEntriesNextDay != null && !listTimeInEntriesNextDay.isEmpty()) {

						ObservableList<EmployeeScheduleUploading> sortEntriesPerDateByTimeObsList = FXCollections
								.observableArrayList();

						sortEntriesPerDateByTimeObsList.setAll(listTimeInEntriesNextDay);
						listTimeInEntriesNextDay.clear();

						ObservableListUtil.sort(sortEntriesPerDateByTimeObsList, p -> p.getTimeEntry().toString());
						listTimeInEntriesNextDay.addAll(sortEntriesPerDateByTimeObsList);

						timeOut = listTimeInEntriesNextDay.get(listTimeInEntriesNextDay.size() - 2);
						timeOut.setDateEntry(OTEndDate);
					}
				}
			} else {
				overtimeFilingClientList.clear();
				this.listTimeInEntriesToBeRemove.clear();

				overtimeFilingClientList = this.mainApplication.getOvertimeFilingClientMain()
						.getDataByEmpIdAndOvertimeDateTo(employeeScheduleEncodingByEmployeeAndDate
								.getEmploymentHistory().getEmployee().getEmployeeCode(),
								employeeScheduleEncodingByEmployeeAndDate.getDateEntry());
				if (!overtimeFilingClientList.isEmpty() && overtimeFilingClientList != null) {
					if (!this.listTimeInEntries.isEmpty() && this.listTimeInEntries.size() != 0
							&& this.listTimeInEntries != null) {
						timeIn = this.listTimeInEntries.get(listTimeInEntries.size() - 1);
					} else {
						timeIn = null;
					}
				}
			}
		}

		results[0] = timeIn;
		results[1] = timeOut;

		return results;
	}

	public void handleNullEntries(Integer employeeCode) {
		// after checking each bio to be saved if already existed to the next payperiod
		// check the undeleted entries -- if all entries left are null entries (timeIn &
		// timeOut)
		boolean isAllEntryNull = false;

		List<EmployeeScheduleUploading> list = new ArrayList<>();
		list.addAll(this.mainApplication.getDataByEmployeeIdPayFrom(employeeCode, this.payFrom));

		for (EmployeeScheduleUploading bioByEmpPayFrom : list) {
			if (bioByEmpPayFrom.getTimeInEntry() != null && bioByEmpPayFrom.getTimeOutEntry() != null) {
				isAllEntryNull = false;
			}
		}

		if (isAllEntryNull) {
			this.mainApplication.deleteMultipleData(list);
		}
	}

	public void handleInvalidEntries(List<Integer> empIdUploadedList, List<Integer> empIdActiveList) {
		// final checking of entries...
		List<Integer> empIdUploadedListCopy = new ArrayList<>();

		empIdUploadedListCopy.addAll(empIdUploadedList);
		empIdUploadedListCopy.removeAll(this.empIdList);

		List<Integer> activeEmpIdUploadedList = new ArrayList<>();
		List<Integer> validEmpIdUploadedList = new ArrayList<>();
		List<String> clientCodeList = new ArrayList<>();
		clientCodeList.addAll(this.mainApplication.getAllClientCdByPayFrom(payFrom));

		if (this.selectedClient != null) {
			clientCodeList.remove(this.selectedClient.getClientCode());

			if (this.selectedDepartment != null) {
				validEmpIdUploadedList.addAll(this.mainApplication.getAllEmpIdExcludingSelectedDeptPay(
						this.selectedDepartment.getDepartmentCode(), this.selectedClient.getClientCode(), payFrom));

				empIdUploadedListCopy.removeAll(validEmpIdUploadedList);
			}
		}
		clientCodeList.forEach(clientCode -> {
			List<Integer> empIdPerClientList = this.mainApplication.getAllEmployeeIdByClientCodePayFromPayTo(clientCode,
					payFrom, payTo);

			empIdUploadedListCopy.removeAll(empIdPerClientList);
		});

		empIdActiveList.forEach(p -> {
			if (empIdUploadedListCopy.stream().filter(c -> c.equals(p)).findFirst().isPresent()) {
				activeEmpIdUploadedList.add(p);
			}
		});

		for (Integer empId : activeEmpIdUploadedList) {
			List<EmployeeScheduleUploading> employeeScheduleUploadingLeftList = mainApplication
					.getDataByEmployeeIdPayFrom(empId, payFrom);
			mainApplication.deleteMultipleData(employeeScheduleUploadingLeftList);
		}
	}

	@FXML
	public void handleCancel() {
		this.stage.close();
	}

	@Override
	public void configureAccess() {
		// TODO Auto-generated method stub

	}

	public void setFieldListener() {

		this.comboBoxClient.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedClient = newValue;

				this.obsListDepartment.setAll(this.mainApplication.populateDepartment(newValue));
				this.comboBoxDepartment.setItems(this.obsListDepartment, p -> p.getDepartmentName());
				this.comboBoxDepartment.setDisable(false);
			} else {
				this.selectedClient = null;
				this.selectedDepartment = null;

				this.obsListDepartment.clear();
				this.comboBoxDepartment.setItems(this.obsListDepartment, p -> p.getDepartmentName());
				this.comboBoxDepartment.setDisable(true);
			}
		});

		this.comboBoxDepartment.valueObjectProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.selectedDepartment = newValue;
			} else {
				this.selectedDepartment = null;
			}
		});

		this.datePickerPayFrom.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.datePickerPayTo.setValue(DateUtil.getCutOffDateOfPayTo(DateUtil.parseDate(newValue.toString()))
						.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				this.datePickerPayFrom
						.setValue(DateUtil.getCutOffDate(DateUtil.parseDate(newValue.toString()), 0, false).toInstant()
								.atZone(ZoneId.systemDefault()).toLocalDate());

				this.datePickerCutoffFrom.setValue(this.datePickerPayFrom.getValue());
				this.datePickerCutoffTo.setValue(this.datePickerPayTo.getValue());

				this.payFrom = DateFormatter.toDate(this.datePickerPayFrom.getValue());
			} else {
				this.payTo = null;
			}
		});

		this.datePickerPayTo.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				this.datePickerPayFrom
						.setValue(DateUtil.getCutOffDate(DateUtil.parseDate(newValue.toString()), 0, false).toInstant()
								.atZone(ZoneId.systemDefault()).toLocalDate());
				this.datePickerPayTo.setValue(DateUtil.getCutOffDateOfPayTo(DateUtil.parseDate(newValue.toString()))
						.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

				this.datePickerCutoffFrom.setValue(this.datePickerPayFrom.getValue());
				this.datePickerCutoffTo.setValue(this.datePickerPayTo.getValue());

				this.payTo = DateFormatter.toDate(this.datePickerPayTo.getValue());
			} else {
				this.payTo = null;
			}
		});

		this.datePickerCutoffFrom.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {

				try {
					this.cutoffFrom = new SimpleDateFormat("yyyy-MM-dd").parse(newValue.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}

			} else {
				this.cutoffFrom = null;
			}
		});

		this.datePickerCutoffTo.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {

				try {
					this.cutoffTo = new SimpleDateFormat("yyyy-MM-dd").parse(newValue.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}

			} else {
				this.cutoffTo = null;
			}
		});

	}

	@Override
	public void onSetMainApplication() {
		this.setFieldListener();

		this.obsListClient.setAll(this.mainApplication.populateClient(null, null));
		this.comboBoxClient.setItems(this.obsListClient, p -> p.getClientName());

		this.selectedClient = null;
		this.selectedDepartment = null;

		this.existingDataList.clear();
		this.newEntrySaveList.clear();
		this.updateEntrySaveList.clear();
		this.deleteEntrySaveList.clear();

	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private Button buttonImport;
	@FXML
	private Button buttonCancel;
	@FXML
	private AutoFillComboBox<Client> comboBoxClient;
	@FXML
	private AutoFillComboBox<Department> comboBoxDepartment;
	@FXML
	private FormattedDatePicker datePickerPayFrom;
	@FXML
	private FormattedDatePicker datePickerPayTo;
	@FXML
	private FormattedDatePicker datePickerCutoffFrom;
	@FXML
	private FormattedDatePicker datePickerCutoffTo;
}
